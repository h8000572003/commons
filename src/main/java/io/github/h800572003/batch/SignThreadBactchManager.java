package io.github.h800572003.batch;

import io.github.h800572003.exception.ApBusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignThreadBactchManager implements IBactchManager {

	private List<IBactchTaskHolder> tasks = new ArrayList<IBactchTaskHolder>();
	private Map<String, IBactchTaskHolder> map = new HashMap<String, IBactchTaskHolder>();

	protected SignThreadBactchManager() {
	}

	public void register(IBactchTaskConfig config, IBactchTaskExecuter bactchTaskExecuter) {
		IBactchTaskHolder create = this.create(config, bactchTaskExecuter);
		if (this.map.containsKey(config.getCode())) {
			throw new ApBusinessException("排程作業代碼重複{0}", config.getCode());
		}
		this.map.put(config.getCode(), create);
		this.tasks.add(create);
	}

	private IBactchTaskHolder create(IBactchTaskConfig config, IBactchTaskExecuter bactchTaskExecuter) {
		BactchTaskHolder task = new BactchTaskHolder(config, bactchTaskExecuter);
		return task;
	}

	@Override
	public boolean isOn() {
		return this.getIsOn();
	}

	@Override
	public void on() {
		this.tasks.forEach(i -> i.on());

	}

	@Override
	public void off() {
		this.tasks.forEach(i -> i.off());
	}

	private boolean getIsOn() {
		return tasks.stream().filter(IBactchTaskHolder::isOn).findAny().isPresent();
	}

	private IBactchTaskHolder initBactchTaskHolder(String code) {
		IBactchTaskHolder orDefault = this.map.getOrDefault(code, null);
		if (orDefault == null) {
			throw new ApBusinessException("無此無服務代碼{0}", code);
		}
		return orDefault;
	}

	@Override
	public void on(String code) {
		this.initBactchTaskHolder(code).on();
	}

	@Override
	public void off(String code) {
		this.initBactchTaskHolder(code).off();

	}

	@Override
	public List<IBactchTaskHolder> getBactchTasks() {
		return tasks;
	}

}
