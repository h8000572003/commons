package io.github.h800572003.properties;

import java.util.List;
import java.util.Objects;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;

public class PropertiesHolderBuilder {

	private boolean inRange = true;
	private boolean isCache = true;
	private List<IProperties> def = Lists.newArrayList();

	public PropertiesHolderBuilder(boolean inRange, boolean isCache) {
		super();
		this.inRange = inRange;
		this.isCache = isCache;
	}

	public PropertiesHolderBuilder setProperties(List<IProperties> def) {
		Objects.requireNonNull(def);
		this.def = def;
		return this;
	}

	public IPropertieServiceHolder build(String category, IPropertiesRepository propertiesRepository) {
		
		Objects.requireNonNull(category);
		Objects.requireNonNull(propertiesRepository);
		Objects.requireNonNull(def);

		CacheLoader<String, List<IProperties>> loader = new MyCacheLoaderInRangDefHolder(propertiesRepository, def);
		if (!inRange) {
			loader = new MyCacheLoaderInCludeDefHolder(propertiesRepository, def);
		}
		IPropertieServiceHolder holder = new PropertiesInCacheHolder(category, propertiesRepository, loader);
		if (!isCache) {
			holder = new PropertiesInNoCacheHolder(category, propertiesRepository, loader);
		}
		return holder;

	}
}
