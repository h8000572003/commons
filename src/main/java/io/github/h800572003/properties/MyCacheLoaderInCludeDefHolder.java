package io.github.h800572003.properties;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCacheLoaderInCludeDefHolder extends CacheLoader<String, List<IProperties>> {

	private final IPropertiesRepository propertiesRepository;
	private final List<IProperties> def;

	public MyCacheLoaderInCludeDefHolder(IPropertiesRepository propertiesRepository, List<IProperties> def) {
		super();
		this.propertiesRepository = propertiesRepository;
		this.def = def;
	}

	@Override
	public List<IProperties> load(String key) throws Exception {
		try {
			final List<IProperties> fromOutside = this.propertiesRepository.getProperties(key);
			final Map<IProperties, IProperties> collect = this.def.stream().collect(Collectors.toMap(i -> i, i -> i));
			fromOutside.forEach(i -> {
				final boolean containsKey = collect.containsKey(i);
				if (containsKey) {
					final IProperties iProperties = collect.get(i);
					if (iProperties instanceof MyProperties && i instanceof MyProperties) {
						final MyProperties myProperties = (MyProperties) iProperties;

						final MyProperties iMy = (MyProperties) i;
						iMy.setMeno(myProperties.getMemo());
					}
				}
			});
			final Set<IProperties> set = Sets.newHashSet(fromOutside);
			set.addAll(this.def);
			return set.stream().sorted(Comparator.comparing(IProperties::getKey))
					 .collect(Collectors.toList());
		} catch (Exception e) {
			log.error("key {},get data error",key, e);
			return this.def;
		}

	}

}
