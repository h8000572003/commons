package io.github.h800572003.eventbuus;

public interface EventExceptionxHandler {

	void handle(String message);

	void handle(String string, Exception e);

}
