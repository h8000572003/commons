package io.github.h800572003.commons.properties;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.cache.CacheLoader;

public class MyCacheLoaderBySourceHolder extends CacheLoader<String, List<IProperties>> {

	private final IPropertiesRepository propertiesRepository;

	public MyCacheLoaderBySourceHolder(IPropertiesRepository propertiesRepository) {
		super();
		this.propertiesRepository = propertiesRepository;
	}

	@Override
	public List<IProperties> load(String key) throws Exception {

		return this.propertiesRepository.getProperties(key).stream().sorted(Comparator.comparing(IProperties::getKey))
				.collect(Collectors.toList());
	}
}