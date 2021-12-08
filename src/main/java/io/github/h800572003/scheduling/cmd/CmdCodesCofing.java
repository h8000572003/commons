package io.github.h800572003.scheduling.cmd;

import org.springframework.context.annotation.Configuration;

/**
 * 命令定義檔案
 * 
 * @author 6407
 *
 */
@Configuration
public class CmdCodesCofing {
//	private final IPropertiesService propertiesService;
//	private final ISchedulingManager schedulingManager;
//
//	public enum NameCodes {
//		COMMAND
//	}
//
//	public CmdCodesCofing(IPropertiesService propertiesService, ISchedulingManager schedulingManager, ApplicationContext applicationContext) {
//		this.schedulingManager = schedulingManager;
//		this.propertiesService = propertiesService;
//		List<IProperties> properties = this.getCmmand();
//		IPropertiesRepository propertiesRepository = propertiesSaveFactory.getSaveType(this);
//		final MyCacheLoaderInCludeDefHolder loader = new MyCacheLoaderInCludeDefHolder(propertiesRepository,
//				properties);
//		final PropertiesInNoCacheHolder holder = new PropertiesInNoCacheHolder(CmdCodesCofing.NameCodes.COMMAND.name(),
//				propertiesRepository, loader);
//
//		propertiesService.addCode(CmdCodesCofing.NameCodes.COMMAND.name(), "命令", holder);
//
//	}

	public static enum Codes {
		UP("啟動服務"), //
		DOWN("關閉服務"), //
		START("啟動全部，加入{CODE}可針對單項啟動"), //
		INTERUPT("中斷，加入{CODE}可針對單項中斷"), //
		REFRESH("重新整理，必須加入{CODE}單項重新整理"), //
		WATCH("查看資訊"), //
		GETPRO("取得代碼清單，加入{CATEGORY}可取得該項代碼資料"), //
		CLEAR("清除快取，加入{CATEGORY}可清除該筆"), //
		UPPRO("更新代碼內容，加入{CATEGORY},{KEY},{VALUE1},{VALUE2},{VALUE3}使用逗點分開資料"), //
		DEPRO("刪除代碼內容，加入{CATEGORY},{KEY}"), //

		;

		final String name;

		private Codes(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

//	public List<IProperties> getCmmand() {
//		List<IProperties> properties = new ArrayList<>();
//		for (Codes code : Codes.values()) {
//			properties.add(this.create(code, "", "", "N"));//
//		}
//
//		// for (Code code : propertiesService.getCategory()) {
//		// properties.add(this.create(ExCodes.CLEAR_.name, ExCodes.CLEAR_.name,
//		// code, "-", "-", "-"));//
//		// }
//		;
//
//		return properties;
//	}
//
//	private IProperties create(Codes codes, String... value) {
//		MyProperties myProperties = new MyProperties();
//		myProperties.setValue1("");
//		myProperties.setValue2("");
//		myProperties.setValue3("Y");
//		myProperties.setKey(codes.name());
//		if (value.length >= 1) {
//			myProperties.setValue1(value[0]);
//		}
//		if (value.length >= 2) {
//			myProperties.setValue2(value[1]);
//		}
//		if (value.length >= 3) {
//			myProperties.setValue3(value[2]);
//		}
//		myProperties.setMeno(codes.getName());
//		return myProperties;
//	}

//	@Override
//	public SaveTypeCodes getSaveTypeCodes() {
//		return SaveTypeCodes.BASECODE;
//	}
}
