package io.github.h800572003.batch;

import io.github.h800572003.exception.CancelExecpetion;

public interface IBactchTaskExecuterContext {

	void checkUp() throws CancelExecpetion;

}
