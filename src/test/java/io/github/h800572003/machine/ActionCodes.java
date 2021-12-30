package io.github.h800572003.machine;

public enum ActionCodes implements StatusAction {

	RUNN, //
	JUMP, //
	WALK, //
	CRY,//
	INJURIED,//
	;
	@Override
	public String toAction() {
		return name();
	}

}
