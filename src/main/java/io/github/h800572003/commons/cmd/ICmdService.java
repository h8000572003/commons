package io.github.h800572003.commons.cmd;

public interface ICmdService {

	public String execute(String cmd, String action);

	/**
	 * 註冊命令
	 * 
	 * @param code
	 * @param command
	 */
	void register(String code, ICmdHander command);
}
