package hx.nlp.parser.temporal;

import lombok.Data;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

/**
 * Created by zhipeng.wang on 03/22 2017.
 */
@Data
public class TemporalExpression {

	public enum TYPE { DATE, TIME, RANGE, PERIOD, DURATION, SET }

	protected String expression;
	private LocalDate date;
	private LocalTime time;
	private Period period;
	private Duration duration;

	private TYPE type;
	private TemporalUnit unit;

	private byte approximation = 0; // before after or around
	private byte repeatFrequence = 0;

	public TemporalExpression(String text) {
		this.expression = text;
	}

	private TemporalExpression(String text, LocalDate date ) {
		expression = text;
		this.date = date;
		type = TYPE.DATE;
	}

	private TemporalExpression(String text, LocalTime time ) {
		this(text, LocalDateTime.of(LocalDate.now(), time));
	}

	private TemporalExpression(String text, LocalDateTime dateTime ) {
		expression = text;
		date = dateTime.toLocalDate();
		time = dateTime.toLocalTime();
		type = TYPE.TIME;
	}

	private TemporalExpression(String text, LocalDate from, LocalDate to) {
		expression = text;
		this.type = TYPE.RANGE;
		this.date = from;
		period = Period.between(from ,to);
	}

	private TemporalExpression(String text, Period period) {
		this.expression = text;
		this.type = TYPE.PERIOD;
		this.period = period;
	}

	public static TemporalExpression temporalDate(String text, LocalDate date) {
		return new TemporalExpression(text, date);
	}

	public static TemporalExpression temporalTime(String text, LocalTime time) {
		return new TemporalExpression(text, time);
	}

	public static TemporalExpression temporalTime(String text, LocalDateTime time) {
		return new TemporalExpression(text, time);
	}

	public static TemporalExpression temporalRange(String text, LocalDate from, LocalDate to) {
		return new TemporalExpression(text, from, to);
	}

}
