package io.github.h800572003.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import io.github.h800572003.exception.ApBusinessException;
import org.apache.commons.lang3.StringUtils;

public class DateUtlis {
	static final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	static 	final  public String YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
	static final  public String YYYMMDD = "yyyyMMdd";

	public static boolean afterNow(Date date, int type, int amount) {
		Calendar statTime = Calendar.getInstance();
		statTime.setTimeInMillis(date.getTime());
		statTime.add(type, +amount);
		return statTime.getTime().after(new Date());
	}

	public static LocalDateTime paresrNullEqaulNow(String value) {
		if (StringUtils.isBlank(value)) {
			return LocalDateTime.now();
		} else {
			return LocalDateTime.parse(value, formatter);
		}

	}

	public static String getText() {
		return getText(YYYY_MM_DD_HH_MM_SS, new Date());
	}

	public static String getText(String format, Date date) {
		if (date == null) {
			return "";
		}
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}

	public static Date parse(String yyyymmdd) {
		return parse("yyyy/MM/dd", yyyymmdd);
	}

	public static Date parse(String format, String yyyymmdd) {
		try {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			return simpleDateFormat.parse(yyyymmdd);
		} catch (final ParseException var2) {
			throw new ApBusinessException("日期格式錯誤:" + yyyymmdd);
		}
	}

	/**
	 * 取得今天日期
	 * 
	 * @return [yyyyMMdd]格式 20220222
	 */ 
	public static String today() {
		final SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyyMMdd");
		final Date date = new Date();
		return sdf.format(date);
	}

}
