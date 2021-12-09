package io.github.h800572003.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.github.h800572003.exception.ApBusinessExecpetion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesService implements IPropertiesService {

	private final Map<String, IPropertieServiceHolder> map = new ConcurrentHashMap<>();
	private final List<Code> codes = new ArrayList<>();
	private List<PropertiesUpdateListener> propertiesUpdates = new ArrayList<>();

	public void addListener(PropertiesUpdateListener propertiesUpdateListener) {
		this.propertiesUpdates.add(propertiesUpdateListener);
	}

	public void removeCode(String code) {
		this.map.remove(code);
		final Code myCode = new Code();
		myCode.setKey(code);
		myCode.setValue("");
		this.codes.remove(myCode);
	}

	public void addCode(String code, String name, IPropertieServiceHolder holder) {
		if (this.map.containsKey(code)) {
			throw new ApBusinessExecpetion("重複代碼{0}", code);
		}
		this.map.put(code, holder);
		final Code myCode = new Code();
		myCode.setKey(code);
		myCode.setValue(name);
		this.codes.add(myCode);
	}

	protected PropertiesService(IPropertiesRepository propertiesRepository) {
		super();
	}

	private List<IProperties> getCodeByDefault(String code, List<IProperties> def) {
		if (this.map.containsKey(code)) {

			return this.map.get(code).getProperties();
		} else {
			log.info("code:{} use def value", code);
			return def;
		}
	}

	@Override
	public List<IProperties> getPropertie(String code) {
		java.util.Objects.requireNonNull(code);
		return getCodeByDefault(code, Arrays.asList());
	}

	@Override
	public IProperties getValue(String code, String key) {
		java.util.Objects.requireNonNull(code);
		java.util.Objects.requireNonNull(key);
		if (this.map.containsKey(code)) {
			final Optional<IProperties> propertie = this.map.get(code).getPropertie(key);
			return propertie.orElseGet(() -> new MyProperties());
		} else {
			log.info("code:{} use def value", code);
			return new MyProperties();
		}
	}

	@Override
	public void saveOrUpdate(String category, IProperties properties) {
		this.propertiesUpdates.forEach(i -> i.update(category, properties));
		if (this.map.containsKey(category)) {
			this.map.get(category).saveOrUpdate(properties);
		}

	}

	@Override
	public void clear(String category) {
		if (this.map.containsKey(category)) {
			this.map.get(category).clear();
		}

	}

	@Override
	public void delete(String category, String code) {
		if (this.map.containsKey(category)) {
			this.map.get(category).delete(code);
		}

	}

	@Override
	public List<Code> getCategory() {
		return this.codes;
	}

	@Override
	public void delete(String category) {
		if (this.map.containsKey(category)) {
			this.map.get(category).delete();
		}

	}

	@Override
	public void clear() {
		codes.forEach(i -> this.clear(i.getKey()));
	}

	@Override
	public void update() {
		this.clear();
		codes.forEach(this::loadProperties);

	}

	private void loadProperties(Code code) {
		log.info("loadProperties:" + code.getKey());
		this.getPropertie(code.getKey());
	}

}
