package io.github.h800572003.properties;

import java.util.List;
import java.util.Optional;

public interface IPropertieServiceHolder {

	List<IProperties> getProperties();

	Optional<IProperties> getPropertie(String key);

	void saveOrUpdate(IProperties properties);

	void clear();

	void delete(String code);
	
	void delete();

	
	
}