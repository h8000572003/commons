package io.github.h800572003.check;

public class CreateServiceParameter {
	private boolean isBeak;
	private boolean useCommon;

	public CreateServiceParameter(boolean isBeak, boolean useCommon) {
		this.isBeak = isBeak;
		this.useCommon = useCommon;
	}

	public boolean isBeak() {
		return isBeak;
	}

	public void setBeak(boolean isBeak) {
		this.isBeak = isBeak;
	}

	public boolean isUseCommon() {
		return useCommon;
	}

	public void setUseCommon(boolean useCommon) {
		this.useCommon = useCommon;
	}
}