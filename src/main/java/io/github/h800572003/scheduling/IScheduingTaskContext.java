package io.github.h800572003.scheduling;

import io.github.h800572003.exception.CancelExecpetion;

public interface IScheduingTaskContext {

	void checkUp() throws CancelExecpetion;

	void updateMessage(String message);

	void setProgress(int progress);
}
