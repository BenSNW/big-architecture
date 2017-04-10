package hx.nlp.parser.temporal;

import lombok.Data;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Created by zhipeng.wang on 03/22 2017.
 */
@Data
public class TemporalExpression {

	public enum TYPE implements Comparable<TYPE> {
		POINT, RANGE, LENGTH, SET, DEMONSTRATIVE
	}

	protected String expression;
//	private LocalDate date;
//	private LocalTime time;
//	private Period period;
//	private Duration duration;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	private TYPE type;
	// 用于计算匹配程度，只有时间精确到几点整时才认为精度是时，否则作为分，如三点钟，三点多，三点整
	private ChronoUnit precision;

	private byte approximation = 0;     // before after or around，三四天前；三十几天；几百多;3、4天
	private byte repeatFrequence = 0;

	public TemporalExpression(String text) {
		this.expression = text;
	}

	private TemporalExpression(String text, LocalDate date, ChronoUnit unit) {
		this(text, date.atStartOfDay(), unit);
	}

	private TemporalExpression(String text, LocalDateTime dateTime, ChronoUnit unit) {
		expression = text;
		type = TYPE.POINT;
		precision = unit;
		endTime = startTime = dateTime;
	}

	private TemporalExpression(String text, LocalDate from, LocalDate to, ChronoUnit unit) {
		this(text, from.atStartOfDay(), to.atStartOfDay(), unit);
	}

	private TemporalExpression(String text, LocalDateTime from, LocalDateTime to, ChronoUnit unit) {
		expression = text;
		type = TYPE.RANGE;
		precision = unit;
		startTime = from;
		endTime = to;
	}

	public static TemporalExpression temporalPoint(String text, LocalDate date) {
		return new TemporalExpression(text, date.atStartOfDay(), ChronoUnit.DAYS);
	}

	public static TemporalExpression temporalPoint(String text, LocalDateTime dt, ChronoUnit unit) {
		return new TemporalExpression(text, dt, unit);
	}

//	public static TemporalExpression temporalTime(String text, LocalDate date, LocalTime time) {
//		return temporalTime(text, LocalDateTime.of(date, time));
//	}
//
//	public static TemporalExpression temporalTime(String text, LocalTime time) {
//		return temporalTime(text, LocalDateTime.of(LocalDate.now(), time));
//	}
//
//	public static TemporalExpression temporalRange(String text, LocalDate from, int days) {
//		return new TemporalExpression(text, from, from.plusDays(days));
//	}
//
//	public static TemporalExpression temporalDateRange(String text, LocalDate from, LocalDate to) {
//		return new TemporalExpression(text, from, to);
//	}

	public static TemporalExpression temporalRange(String text, LocalDateTime startTime,
	                                               LocalDateTime endTime, ChronoUnit unit) {
		return new TemporalExpression(text, startTime, endTime, unit);
	}

//	public static TemporalExpression ofYear(int year) {
//		return temporalDateRange(year + "年", LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
//	}
//
//	public static TemporalExpression atHour(LocalDate date, int hour) {
//		return temporalTime(hour + "时", date, LocalTime.of(hour, 0, 0));
//	}

	public static TemporalExpression ofLength(String text, float amount, ChronoUnit unit) {
		TemporalExpression tp = new TemporalExpression(text);
		tp.type = TYPE.LENGTH;
		tp.precision = unit;
		tp.startTime = LocalDate.ofEpochDay(0).atStartOfDay();
		tp.endTime = tp.startTime.plusSeconds((long) (amount * unit.getDuration().getSeconds()));
		return tp;
	}

	public void reorderTime() {
		if (startTime != null && endTime != null && startTime.compareTo(endTime) > 0) {
			LocalDateTime temp = startTime;
			startTime = endTime;
			endTime = temp;
		}
	}

	public float match(LocalDateTime ldt) {
		switch (type) {
			case POINT:
				return precision.between(startTime, ldt);
		}
		return 1.0f;
	}

	public TemporalExpression withTime(LocalTime time) {
		type = TYPE.POINT;
		startTime = LocalDateTime.of(startTime.toLocalDate(), time);
		return this;
	}

	public TemporalExpression merge(TemporalExpression temporal) {
		return this;

	}

}
