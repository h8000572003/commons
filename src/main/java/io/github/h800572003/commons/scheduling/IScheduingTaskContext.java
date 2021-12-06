package io.github.h800572003.commons.scheduling;

import io.github.h800572003.commons.CancelExecpetion;

public interface IScheduingTaskContext {

	void checkUp() throws CancelExecpetion;

	void updateMessage(String message);

	void setProgress(int progress);
}
