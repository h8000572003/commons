package io.github.h800572003.properties;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheLoader;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesInCacheHolder implements IPropertieServiceHolder {

	private final IPropertiesRepository propertiesRepository;

	private final String category;
	private final CacheLoader<String, List<IProperties>> cacheLoader;
	private List<IProperties> cacheList;

	public PropertiesInCacheHolder(String category, IPropertiesRepository propertiesRepository,
			CacheLoader<String, List<IProperties>> cacheLoader) {
		super();
		this.propertiesRepository = propertiesRepository;
		this.category = category;
		this.cacheLoader = cacheLoader;
	}

	private PropertiesInCacheHolder initial() {
		if (cacheList == null) {
			loadData();
		}
		return this;
	}

	private synchronized PropertiesInCacheHolder loadData() {
		try {
			if (cacheList != null) {
				return this;
			}
			this.cacheList = cacheLoader.load(category);

		} catch (Exception e) {
			throw new ApBusinessException("CATEGORY:{0} 參數錯誤", category);
		}
		return this;
	}

	@Override
	public synchronized List<IProperties> getProperties() {
		return this.initial().getCacheList();
	}

	public List<IProperties> getCacheList() {
		return cacheList;
	}

	@Override
	public Optional<IProperties> getPropertie(String key) {
		return this.getProperties()//
				.stream()//
				.filter(i -> StringUtils.equals(i.getKey(), key))//
				.findAny();//
	}

	@Override
	public void saveOrUpdate(IProperties properties) {
		if (StringUtils.isBlank(properties.getKey())) {
			throw new ApBusinessException("{0} KEY不得為空值", properties.getKey());
		}
		final Optional<IProperties> value = this.propertiesRepository.getValue(this.category, properties.getKey());
		if (value.isPresent()) {
			final int update = this.propertiesRepository.update(this.category, properties);
			log.info("update size:{}", update);

		} else {
			final int insert = this.propertiesRepository.insert(this.category, properties);
			log.info("insert size:{}", insert);

		}
		this.clear();
	}

	@Override
	public void clear() {
		synchronized (this) {
			this.cacheList = null;
		}
	}

	@Override
	public void delete(String code) {
		final int delete = this.propertiesRepository.delete(this.category, code);
		log.info("delete code:{}", delete);
		clear();
	}

	@Override
	public void delete() {
		final int delete = this.propertiesRepository.delete(this.category);
		log.info("delete code:{}", delete);
		clear();

	}

}
