package io.github.h800572003.commons.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostNameUtls {

	public static String getHostName() {
		try {
			return (InetAddress.getLocalHost()).getHostName().toUpperCase();
		} catch (UnknownHostException uhe) {
			String host = uhe.getMessage(); // host = "hostname: hostname"
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0) {
					return host.substring(0, colon);
				}
			}
			return "PC";
		}
	}

	public static String getIp() {
		InetAddress localHost = null;
		try {
			localHost = Inet4Address.getLocalHost();
			String ip = localHost.getHostAddress();
			return ip;
		} catch (UnknownHostException e) {

		}
		return "";

	}
}
