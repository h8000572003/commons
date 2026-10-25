# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案概要

`io.github.h8000572003:commons` 是發佈到 Maven Central 的通用元件函式庫（jar），以 `spring-boot-starter-parent` 2.7.18 為 parent，但**沒有 auto-configuration**：元件以介面（`I` 開頭）加上靜態工廠類別（複數命名，如 `SchedulingManagers`、`PropertiesServices`、`Backeds`、`BactchManagers`）提供，由使用端自行組裝成 Spring bean。功能清單見 `README.md`，版本紀錄見 `CHANGELOG.md`，版本號在 `pom.xml`。

註解、log 與例外訊息皆為台灣繁體中文。識別字中有許多歷史拼字（`Scheduing`、`Hander`、`Bactch`、`Execpetion`、`Utlis`、`Cofing`、`eventbuus`、`Balank` 等），它們是已發佈的 public API，不要「順手修正」。

## 指令

```bash
mvn compile
mvn test                                        # 全部測試
mvn test -Dtest=SchedulingProgressTest          # 單一類別
mvn test '-Dtest=SpringSchedulingManagerBddTest*'  # 含 @Nested 的類別必須加 *
mvn test '-Dtest=SpringSchedulingManagerBddTest$Up'  # 只跑某個 @Nested 群組
mvn -P release clean verify -DskipTests         # 本機檢查發佈產物（sources、javadoc、GPG 簽章）
```

發佈：source／javadoc／gpg／`central-publishing-maven-plugin` 都只在 `release` profile 中（一般建置不簽章）。正式發佈由 `.github/workflows/release.yml` 手動觸發（`dry-run` 只上傳到 Central Portal 驗證不公開；`release` 只能從 master，會公開發佈並打 tag，tag 無前綴，例如 `0.18.0`）。workflow 使用 GitHub environment `maven-central` 的 secrets：`CENTRAL_TOKEN_USERNAME`、`CENTRAL_TOKEN_PASSWORD`、`MAVEN_GPG_PRIVATE_KEY`、`MAVEN_GPG_PASSPHRASE`。

- surefire 為 2.22.2：`-Dtest=類別名` 不會包含 `@Nested` 內部類別，結果是 0 個測試並且 BUILD FAILURE，必須用萬用字元。
- `maven-compiler-plugin` 設定 `source/target 8`（雖然 `java.version` 是 17），所以只能寫 Java 8 語法：不能用 `var`、`List.of`、text block、switch expression。
- 沒有 lint／formatter 設定。

## 測試

JUnit 5 + AssertJ + Mockito（BDDMockito），另有測試用相依 `io.github.chungtsai:test`（提供 `TestCmdService` 等多執行緒測試工具）。部分舊測試標了 `@Disabled`（例如依賴長時間 sleep 的 `SpringSchedulingManagerTest`）。`SpringSchedulingManagerBddTest` 是排程元件的主要測試，用 `@Nested` 依 method 分組、`@DisplayName` 寫成 Given/When/Then，並用 latch／polling 控制時序而不是 sleep；新增排程測試請沿用這個寫法。測試常直接斷言 `ApBusinessException` 的完整中文訊息，改訊息時要同步改測試。

`ApBusinessException(pattern, args...)` 內部使用 `MessageFormat.format`，所以訊息模板用 `{0}` 佔位，單引號有特殊意義。

## 架構重點

### scheduling（最複雜的模組）

- `ISchedulingManager` → `SpringSchedulingManager`：以 Spring `ThreadPoolTaskScheduler` 管理任務。建構子為 protected，`SchedulingManagers.createSchedulingManager` 為 package-private。
- 任務來源：`ISchedulingRepository` 提供 `IScheduingCron`（`getCon()`）與 `IScheduingDelay`（`getCron()`）定義；值為純數字時視為 fixed delay（毫秒，啟動延遲 `ISpringSchedulingProperites.getDelayStart()` 秒），否則視為 Spring cron 表達式。任務實作 `IScheduingTask` 由 `ApplicationContext.getBean(getPClass())` 取得。
- 每個任務包成 `AbstractSchedulingCronContextHolder`（`SchedulingCronContextHolderDTO`／`SchedulingDelayContextHolderDTO`），它同時是 `ISchedulingItemContext`（給管理端看狀態）與 `IScheduingTaskContext`（給任務本身用：`checkUp()` 協作式中止、`setProgress`、`updateMessage`）。`run()` 以 `synchronized(getPClass())` 序列化同一任務類別的執行。狀態：`IDLE` → `RUNNABLE`（已排程）→ `RUNNNIG` → 結束後回到 `RUNNABLE`／`IDLE`，被取消則為 `DEAD`。
- 執行者權限：`ISpringSchedulingProperites.isExecute()` 決定本機是否為執行者（多台主機只有一台執行）。`start`／`cancel`／`runOnce`／`up` 都經 `ISchedulingContext.ifExecuter` 檢查；非執行者仍可 `propertiesChange()` 載入任務供查詢，`down()` 也能釋放資源。
- 生命週期：`up()` = 載入任務 + `upHook` + `startAll()`；`propertiesChange()` 只載入不啟動，且有任務排程中／執行中時會拒絕；`down()` 期間 `lock` 為 true，其他操作會拋「服務忙碌中」。`down()` 等待 `getCloseTimeout()` 秒後若仍有執行緒，交給 `ISchedulingShutdownPolicy`（預設空白策略＝不處理；`InterruptSchedulingShutdownPolicy` 會中斷並回報殘留），殘留執行緒存活期間 `up()` 會拒絕。
- 擴充點以 setter 或 default method 提供（`setSpringSchedulingHook`、`setShutdownPolicy`、`ISchedulingConfigRepository.newItem` 等）。依維護者的要求，新增功能時採「新增介面／預設實作維持原行為」的擴充方式，不修改既有建構子參數與介面方法簽章。
- `ISchedulingConfigRepository.getCancel` 決定取消時是否中斷執行緒（`SchedulingConfigRepository` 對應 `backend.task.cancel`、`backend.task.interrupt.codes`、`backend.task.double-click-one`）。
- 事件通知走 `IScheduingMonitor`（`MyScheduingMonitors` 為多個 monitor 的組合）。

### cmd

`CmdService` 是「命令代碼 → `ICmdHander`」的註冊表；handler 繼承 `BaseCmdHander`，在建構子中自行 `register`。可用代碼定義在 `CmdCodesCofing.Codes`，各 handler 再委派給 `ISchedulingManager`（UP／DOWN／START／INTERUPT／REFRESH／WATCH）或 `IPropertiesService`（GETPRO／CLEAR／UPPRO／DEPRO）。用途是提供維運用的文字指令介面。

### properties

`PropertiesService` 管理多個分類（category）的代碼資料，每個分類掛一個 `IPropertieServiceHolder`（有快取 `PropertiesInCacheHolder`／無快取 `PropertiesInNoCacheHolder`，搭配 `MyCacheLoader*` 載入器，資料來源為 `IPropertiesRepository`）。資料異動時透過 `PropertiesUpdateListener` 通知（含新增、刪除、清除）。

### 其他模組

`concurrent`（工作池、latch、排序佇列）、`backed`（背景執行服務）、`batch`、`machine`（狀態機）、`event`／`eventbuus`（訊息路由與 EventBus）、`textcut`（固定長度文字切割）、`codegenerate`、`log`（動態調整 logback level）大多彼此獨立，各自以介面 + 工廠提供。
