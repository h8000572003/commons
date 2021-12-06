package io.github.h800572003.commons.properties;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCacheLoaderInRangDefHolder extends CacheLoader<String, List<IProperties>> {

	private final IPropertiesRepository propertiesRepository;
	private final List<IProperties> def;

	public MyCacheLoaderInRangDefHolder(IPropertiesRepository propertiesRepository, List<IProperties> def) {
		super();
		this.propertiesRepository = propertiesRepository;
		this.def = def;
	}

	@Override
	public List<IProperties> load(String key) throws Exception {
		try {
			final List<IProperties> fromOutside = this.propertiesRepository.getProperties(key);
			Map<String, IProperties> fromOutMap = fromOutside.stream()
					.collect(Collectors.toMap(IProperties::getKey, i -> i));

			List<IProperties> collect = this.def.stream().map(i -> this.createNew(i, fromOutMap))
					.collect(Collectors.toList());

			final Set<IProperties> set = Sets.newHashSet(collect);
			return set.stream().sorted(Comparator.comparing(IProperties::getKey))
					 .collect(Collectors.toList());
		} catch (Exception e) {
			log.error("key {},get data error", key, e);
			return this.def;
		}

	}

	private IProperties createNew(IProperties properties, Map<String, IProperties> fromOutMap) {

		MyProperties myProperties = new MyProperties();
		myProperties.setKey(properties.getKey());
		myProperties.setValue1(properties.getValue1());
		myProperties.setValue2(properties.getValue2());
		myProperties.setValue3(properties.getValue3());
		if (properties instanceof MyProperties) {
			MyProperties pro = (MyProperties) properties;
			myProperties.setMeno(pro.getMemo());
		}
		if (fromOutMap.containsKey(properties.getKey())) {
			IProperties iProperties = fromOutMap.get(properties.getKey());
			myProperties.setValue1(iProperties.getValue1());
			myProperties.setValue2(iProperties.getValue2());
			myProperties.setValue3(iProperties.getValue3());

		}
		return myProperties;
	}

}
