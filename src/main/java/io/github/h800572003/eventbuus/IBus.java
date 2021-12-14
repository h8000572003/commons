package io.github.h800572003.eventbuus;

public interface IBus {

	void register(Object subscriber);

	void unRegister(Object subscriber);

	void post(Object evnet);

	void post(Object evnet, String topic);

	void close();

	String getBusName();
}
