package io.github.h800572003.properties;

public class PropertiesServices {

	public static PropertiesService create(IPropertiesRepository propertiesRepository) {
		return new PropertiesService(propertiesRepository);
	}

}
