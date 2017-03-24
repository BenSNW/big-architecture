package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.util.CoreMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhipeng.wang on 03/23 2017.
 */
public class FromToPatternParser extends TemporalTokensPatternParser {

	// /大概/? /自?从/? date /一?直/? /到|至/ endDate /(为止|
	// 左右|前后)/?",
	private static final TokenSequencePattern tokensPattern = TokenSequencePattern.compile(
			"([{tag:AD}])? [{tag:P}]? ([{tag:NT}]{1,4}) [{tag:AD}]? " +
			"[{tag:/P|CC/}] ([{tag:NT}]{1,4}) ([{tag:/AD|LC/}])?");

	private static Pattern datePattern = Pattern.compile("((\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?");

	@Override
	protected TokenSequencePattern getPattern() {
		return tokensPattern;
	}

	@Override
	protected TemporalExpression parseMatchedPattern(TokenSequenceMatcher matcher) {
		System.out.println(matcher.group(1) + "," + matcher.group(4));
		LocalDate from = toLocalDate(matcher.group(2));
		LocalDate to = toLocalDate(matcher.group(3));
		if (from != null && to != null)
			return TemporalExpression.temporalRange(matcher.group(), from, to);

		List<CoreMap> mathedTokens = matcher.groupNodes();
		List<? extends CoreMap> tokens = matcher.elements();
		return null;
	}

	private LocalDate toLocalDate(String text) {
		LocalDate date = LocalDate.now();
		Matcher matcher = datePattern.matcher(text);
		if (!matcher.matches())
			return null;
		if (matcher.group(2) != null)
			date = date.withYear(Integer.valueOf(matcher.group(2)));
		if (matcher.group(4) != null)
			date = date.withMonth(Integer.valueOf(matcher.group(4)));
		if (matcher.group(6) != null)
			date = date.withDayOfMonth(Integer.valueOf(matcher.group(6)));
		return LocalDateTime.of(date, LocalTime.MIN).toLocalDate();
	}


}
