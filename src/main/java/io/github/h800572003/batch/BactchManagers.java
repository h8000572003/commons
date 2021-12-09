package io.github.h800572003.batch;

public class BactchManagers {
	public static IBactchManager createSignThreadBactchManager() {
		return new SignThreadBactchManager();
	}

}
