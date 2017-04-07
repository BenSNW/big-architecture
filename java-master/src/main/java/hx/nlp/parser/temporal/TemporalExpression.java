package hx.nlp.parser.temporal;

import lombok.Data;

import java.time.*;
import java.time.temporal.TemporalUnit;

/**
 * Created by zhipeng.wang on 03/22 2017.
 */
@Data
public class TemporalExpression {

	public enum TYPE implements Comparable<TYPE> {

		// 精确到分或几点整时才认为是TIME，否则作为TIME_RANGE，如三点钟，三点多，三点整
		DATE(10), TIME(100), DATE_RANGE(50), TIME_RANGE(500), SET(1), DEMONSTRATIVE(1);

		private int precision;

		TYPE(int precision) {
			this.precision = precision;
		}

	}

	protected String expression;
	private LocalDate date;
	private LocalTime time;
//	private Period period;
//	private Duration duration;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	private TYPE type;
	private TemporalUnit precision;     // ChronoUnit

	private byte approximation = 0;     // before after or around，三四天前；三十几天；几百多;3、4天
	private byte repeatFrequence = 0;

	public TemporalExpression(String text) {
		this.expression = text;
	}

	private TemporalExpression(String text, LocalDate date ) {
		expression = text;
		type = TYPE.DATE;
		endTime = startTime = date.atStartOfDay();
	}

	private TemporalExpression(String text, LocalDateTime dateTime ) {
		expression = text;
		type = TYPE.TIME;
		endTime = startTime = dateTime;
	}

	private TemporalExpression(String text, LocalDate from, LocalDate to) {
		expression = text;
		type = TYPE.DATE_RANGE;
		this.startTime = from.atStartOfDay();
		this.endTime = to.atStartOfDay();
	}

	private TemporalExpression(String text, LocalDateTime from, LocalDateTime to) {
		expression = text;
		type = TYPE.TIME_RANGE;
		startTime = from;
		endTime = to;
	}

	private TemporalExpression(String text, LocalDate date, LocalTime from, LocalTime to) {
		this(text, LocalDateTime.of(date, from), LocalDateTime.of(date, to));
	}

	private TemporalExpression(String text, Period period) {
		this.expression = text;
		this.type = TYPE.DATE_RANGE;
		startTime = LocalDateTime.MIN;
		endTime = startTime.plusDays(period.getDays());
	}

	public static TemporalExpression temporalDate(String text, LocalDate date) {
		return new TemporalExpression(text, date);
	}

	public static TemporalExpression temporalTime(String text, LocalDateTime time) {
		return new TemporalExpression(text, time);
	}

	public static TemporalExpression temporalTime(String text, LocalDate date, LocalTime time) {
		return temporalTime(text, LocalDateTime.of(date, time));
	}

	public static TemporalExpression temporalTime(String text, LocalTime time) {
		return temporalTime(text, LocalDateTime.of(LocalDate.now(), time));
	}

	public static TemporalExpression temporalDateRange(String text, LocalDate from, LocalDate to) {
		return new TemporalExpression(text, from, to);
	}

	public static TemporalExpression temporalDateRange(String text, LocalDate from, int days) {
		return new TemporalExpression(text, from, from.plusDays(days));
	}

	public static TemporalExpression temporalTimeRange(String text, LocalDateTime startTime, LocalDateTime endTime) {
		return new TemporalExpression(text, startTime, endTime);
	}

	public static TemporalExpression ofYear(int year) {
		return temporalDateRange(year + "年", LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
	}

	public static TemporalExpression atHour(LocalDate date, int hour) {
		return temporalTime(hour + "时", date, LocalTime.of(hour, 0, 0));
	}

	public TemporalExpression withTime(LocalTime time) {
		type = TYPE.TIME;
		startTime = LocalDateTime.of(startTime.toLocalDate(), time);
		return this;
	}

	public TemporalExpression merge(TemporalExpression temporal) {
		return this;

	}

}
