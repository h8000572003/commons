package io.github.h800572003.commons.batch;

import io.github.h800572003.commons.CancelExecpetion;

public interface IBactchTaskExecuterContext {

	void checkUp() throws CancelExecpetion;

}
