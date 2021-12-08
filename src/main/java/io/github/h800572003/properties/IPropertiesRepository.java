package io.github.h800572003.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public interface IPropertiesRepository {

	List<IProperties> getProperties(String category);

	Optional<IProperties> getValue(String category, String key);

	int update(String category, IProperties properties);

	int insert(String category, IProperties properties);

	int delete(String category, String key);

	int delete(String category);

	 public static class PropertiesRepositoryInMen implements IPropertiesRepository {

		private final Map<String, List<IProperties>> map = new HashMap<>();

		@Override
		public List<IProperties> getProperties(String category) {
			return this.map.getOrDefault(category, Lists.newArrayList());
		}

		@Override
		public Optional<IProperties> getValue(String category, String key) {
			final List<IProperties> orDefault = this.map.getOrDefault(category, Lists.newArrayList());
			return orDefault.stream().filter(i -> i.getKey().equals(key)).findAny();
		}

		@Override
		public int update(String category, IProperties properties) {

			final Optional<IProperties> value = getValue(category, properties.getKey());
			if (value.isPresent()) {
				final HashSet<IProperties> newHashSet = Sets.newHashSet(properties);
				newHashSet.addAll(this.getProperties(category));
				this.map.put(category, newHashSet.stream().collect(Collectors.toList()));
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public int insert(String category, IProperties properties) {
			final Optional<IProperties> value = getValue(category, properties.getKey());
			if (value.isPresent()) {
				return 0;
			} else {
				final List<IProperties> properties2 = this.getProperties(category);
				properties2.add(properties);
				this.map.put(category, properties2);
				return 1;
			}
		}

		@Override
		public int delete(String category, String key) {
			final List<IProperties> properties2 = getProperties(category);

			final List<IProperties> collect = properties2.stream().filter(i -> !i.getKey().equals(key))
					.collect(Collectors.toList());
			this.map.put(category, collect);
			return properties2.size() != collect.size() ? 1 : 0;
		}

		@Override
		public int delete(String category) {
			final List<IProperties> properties2 = getProperties(category);
			int size = properties2.size();
			properties2.clear();
			this.map.put(category, properties2);
			return size;
		}

	}
}
