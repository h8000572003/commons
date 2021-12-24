package io.github.h800572003.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.github.h800572003.exception.ApBusinessException;

/**
 * 產生隨機資料
 * 
 * @author 6407
 *
 */
public class MockitObject {

	private Map<Class<?>, Supplier<?>> map = Maps.newHashMap();
	private Map<String, Supplier<?>> customer = Maps.newHashMap();
	private Set<String> nameSet = Sets.newHashSet();

	/**
	 * 註冊轉換器
	 * 
	 * @param pClass
	 * @param supplier
	 */
	public void register(Class<?> pClass, Supplier<?> supplier) {
		this.map.put(pClass, supplier);
	}

	public void exclude(String name) {
		this.nameSet.add(name);
	}

	/**
	 * 註冊轉換器
	 * 
	 * @param name
	 * @param supplier
	 */
	public void registerName(String name, Supplier<?> supplier) {
		this.customer.put(name, supplier);
	}

	public MockitObject() {
		this.register(Integer.class, () -> new Random().nextInt());
		this.register(int.class, () -> new Random().nextInt());
		this.register(short.class, () -> new Random().nextInt());
		this.register(boolean.class, () -> new Random().nextBoolean());
		this.register(Boolean.class, () -> new Random().nextBoolean());
		this.register(String.class, () -> new Random().nextInt() + "");

		this.register(Double.class, () -> new Random().nextDouble());
		this.register(double.class, () -> new Random().nextDouble());
		this.register(Float.class, () -> new Random().nextFloat());
		this.register(float.class, () -> new Random().nextFloat());

		this.register(long.class, () -> new Random().nextLong());
		this.register(Long.class, () -> new Random().nextLong());
		this.register(BigDecimal.class, () -> BigDecimal.valueOf(new Random().nextLong()));
	}

	public Object random(Object t) {

		Method[] methods = t.getClass().getMethods();
		for (Method method : methods) {

			boolean startsWith = method.getName().startsWith("set");
			if (startsWith) {
				Parameter[] parameters = method.getParameters();
				if (parameters.length == 1) {
					Class<?> type = parameters[0].getType();
					try {
						String uncapitalize = StringUtils.uncapitalize(method.getName().replace("set", ""));
						if (this.nameSet.contains(uncapitalize)) {
							continue;
						}
						if (customer.containsKey(uncapitalize)) {
							method.invoke(t, customer.get(uncapitalize).get());
						} else {
							if (!map.containsKey(type)) {
								throw new ApBusinessException("class {0}無對應隨機規則，請註冊規則", type);
							}
							method.invoke(t, map.get(type).get());

						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new ApBusinessException("class {0}資料異常", type, e);
					}
				}
			}
		}
		return t;

	}

}
