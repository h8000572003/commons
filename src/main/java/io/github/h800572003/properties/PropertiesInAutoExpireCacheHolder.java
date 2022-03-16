package io.github.h800572003.properties;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 自動清除
 * 
 * @author 6407
 *
 */
@Slf4j
public class PropertiesInAutoExpireCacheHolder implements IPropertieServiceHolder {
	private final IPropertiesRepository propertiesRepository;

	private final String category;
	private final LoadingCache<String, List<IProperties>> loadingCache;

	public PropertiesInAutoExpireCacheHolder(IPropertiesRepository propertiesRepository, String category,
			CacheLoader<String, List<IProperties>> cacheLoader,
			Supplier<CacheBuilder<String, List<IProperties>>> supplier) {
		super();
		this.propertiesRepository = propertiesRepository;
		this.category = category;
		this.loadingCache = supplier.get().build(cacheLoader);
	}


	@Override
	public List<IProperties> getProperties() {
		try {
			return loadingCache.get(category);
		} catch (Exception e) {
			throw new ApBusinessException("CATEGORY:{0} 參數錯誤", category);
		}
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
		loadingCache.cleanUp();
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
