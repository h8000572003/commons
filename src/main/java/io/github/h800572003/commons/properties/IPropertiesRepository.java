package io.github.h800572003.commons.properties;

import java.util.List;
import java.util.Optional;

public interface IPropertiesRepository {

	List<IProperties> getProperties(String category);

	Optional<IProperties> getValue(String category, String key);

	int update(String category, IProperties properties);

	int insert(String category, IProperties properties);

	int delete(String category, String key);

	int delete(String category);
}
