package io.github.h800572003.properties;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheLoader;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesInNoCacheHolder implements IPropertieServiceHolder {

	private final IPropertiesRepository propertiesRepository;

	private final String category;
	private final CacheLoader<String, List<IProperties>> cacheLoader;

	public PropertiesInNoCacheHolder(String category, IPropertiesRepository propertiesRepository,
			CacheLoader<String, List<IProperties>> cacheLoader) {
		super();
		this.propertiesRepository = propertiesRepository;
		this.category = category;
		this.cacheLoader = cacheLoader;
	}

	@Override
	public List<IProperties> getProperties() {
		return loader();
	}

	private List<IProperties> loadByOutside() {
		try {
			return cacheLoader.load(category);
		} catch (Exception e) {
			throw new ApBusinessException("CATEGORY:{0} 參數錯誤", category);
		}
	}

	private synchronized List<IProperties> loader() {
		return this.loadByOutside();
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
//		holder.remove();
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
