package io.github.h800572003.check;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class CheckRoles {

	private CheckRoles() {

	}

	/**
	 * 非null
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotNull(String value) {
		return value != null;
	}

	/**
	 * 非空值
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotBlank(String value) {
		return StringUtils.isNotBlank(value);
	}

	/**
	 * 小於
	 * 
	 * @param value
	 * @param less
	 * @return
	 */
	public static boolean isLessThan(int value, int less) {
		return value < less;
	}

	/**
	 * 大於
	 * 
	 * @param value
	 * @param bigger
	 * @return
	 */
	public static boolean isBiggerThan(int value, int bigger) {
		return value > bigger;
	}

	/**
	 * 範圍內
	 * 
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isBetween(int value, int start, int end) {
		return (value >= start) && (value <= end);
	}

	public static <T> boolean isEqual(T t1, T t2) {
		return t1.equals(t2);
	}

	/**
	 * 小於等於
	 * 
	 * @param value
	 * @param less
	 * @return
	 */
	public static boolean isLessThanOrEqualTo(int value, int less) {
		return value <= less;
	}

	/**
	 * 大於
	 * 
	 * @param value
	 * @param bigger
	 * @return
	 */
	public static boolean isBiggerThanOrEqualTo(int value, int bigger) {
		return value >= bigger;
	}

	/**
	 * 
	 * @param start
	 *            起始天
	 * @param traget
	 *            目的天
	 * @param day
	 * @return
	 */
	public static boolean isLessThan(LocalDate start, LocalDate traget, int day) {
		final long between = ChronoUnit.DAYS.between(start, traget);
		return between < day;
	}

	public static boolean isLessThan(Date start, Date traget, int day) {
		return isLessThan(convertToLocalDateViaMilisecond(start), convertToLocalDateViaMilisecond(traget), day);
	}

	public static boolean isLessThanOrEqualTo(LocalDate start, LocalDate traget, int day) {
		final long between = ChronoUnit.DAYS.between(start, traget);
		return between <= day;
	}

	public static boolean isLessThanOrEqualTo(Date start, Date traget, int day) {
		return isLessThanOrEqualTo(convertToLocalDateViaMilisecond(start), convertToLocalDateViaMilisecond(traget),
				day);
	}

	private static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

}
