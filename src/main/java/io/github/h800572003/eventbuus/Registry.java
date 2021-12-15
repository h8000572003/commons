package io.github.h800572003.eventbuus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Registry {

	Map<String, Queue<Subscriber>> map = Maps.newConcurrentMap();

	void bind(Object subscriber) {
		final List<Method> methods = this.findMethods(subscriber);
		methods.forEach(m -> tirerSubcruber(subscriber, m));
	}

	private List<Method> findMethods(Object subscriber) {
		final List<Method> methods = Lists.newArrayList();
		final Method[] declaredMethods = subscriber.getClass().getDeclaredMethods();

		Stream.of(declaredMethods).filter(///
				i -> i.isAnnotationPresent(Subscribe.class) //
				&& i.getParameterCount() == 1//
				&& i.getModifiers() == Modifier.PUBLIC).forEach(methods::add);//
		return methods;
	}

	void ubbind(Object subscriber) {
	}

	public void tirerSubcruber(Object subscriber, Method method) {

		final Subscribe subscribe = method.getDeclaredAnnotation(Subscribe.class);
		final String topic = subscribe.topic();

		this.map.computeIfAbsent(topic, key -> new ConcurrentLinkedQueue<>());
		this.map.get(topic).add(new Subscriber(subscriber, method));
	}

	public Queue<Subscriber> getQueueByTopic(String topic) {
		return this.map.get(topic);
	}
}
