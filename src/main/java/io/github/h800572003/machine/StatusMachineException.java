package io.github.h800572003.machine;

import java.text.MessageFormat;

public class StatusMachineException extends RuntimeException {

	// ================================================
	// == [Enumeration constants] Block Start
	// == [Enumeration constants] Block End
	// ================================================
	// == [static variables] Block Start

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// == [static variables] Block Stop
	// ================================================
	// == [instance variables] Block Start

	// == [instance variables] Block Stop
	// ================================================
	// == [static Constructor] Block Start
	public StatusMachineException(String pattern, Object... arguments) {
		super(MessageFormat.format(pattern, arguments));
	}

	// public PclmsBusinessExecpetion(String string) {
	// super(string);
	// }

	public StatusMachineException(String string, Throwable throwable) {
		super(string, throwable);
	}
	// == [static Constructor] Block Stop
	// ================================================
	// == [Constructors] Block Start
	// == [Constructors] Block Stop
	// ================================================
	// == [Static Method] Block Start
	// == [Static Method] Block Stop
	// ================================================
	// == [Accessor] Block Start
	// == [Accessor] Block Stop
	// ================================================
	// == [Overrided JDK Method] Block Start (Ex. toString / equals+hashCode)
	// == [Overrided JDK Method] Block Stop
	// ================================================
	// == [Method] Block Start
	// ####################################################################
	// ## [Method] sub-block :
	// ###################################################################
	// == [Method] Block Stop
	// ================================================
	// == [Inner Class] Block Start
	// == [Inner Class] Block Stop
	// ================================================

}