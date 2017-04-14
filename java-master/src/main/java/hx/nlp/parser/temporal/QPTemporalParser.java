package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.util.CoreMap;
import hx.nlp.util.TemporalTokensUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static hx.nlp.util.TemporalTokensUtil.dtMapper;
import static hx.nlp.util.TemporalTokensUtil.parseSingleNumber;
import static hx.nlp.util.TemporalTokensUtil.secondsToChronoUnit;

/**
 * Created by zhipeng.wang on 04/01 2017.
 */
public class QPTemporalParser extends TemporalPatternParser {

	static final TokenSequencePattern tokensPattern = TokenSequencePattern.compile(
			"([{word:/上|下|前面?|后面?|这|那|再?过|最近|自？从/} | {tag:DT}])? " +
			"([{tag:/CD|OD/}] [{tag:/CC|PU/}]? [{tag:CD}]?) " +
			"/个/? (/分钟|刻钟|小时|钟头|天|日|周|星期|礼拜|月份?|季度?|年度?/) " +
			"([{tag:/LC|JJ/}])?");

	@Override
	protected TokenSequencePattern getPattern() {
		return tokensPattern;
	}

	@Override
	protected TemporalExpression parseMatchedPattern(TokenSequenceMatcher matcher) {
		String match = matcher.group();
		System.out.println(matcher.group(0));
		String preMod = matcher.group(1);
		List<CoreMap> cdTokens = matcher.groupNodes(2);
		String cdText = matcher.group(2);
		String ntUnit = matcher.group(3);
		String posMod = matcher.group(4);
		if (preMod == null && cdTokens == null)
			return null;

		int cd = 1;
		long seconds = TemporalTokensUtil.ntUnitMapper.get(ntUnit);
		TemporalExpression tp = new TemporalExpression(matcher.group(0));
		ChronoUnit unit = secondsToChronoUnit(seconds);
		tp.setPrecision(unit);
		if (cdText != null && cdText.startsWith("第")) {
			cd = parseSingleNumber(cdText.substring(1));
			return tp;
		}

		if (cdText != null)
			cd = parseSingleNumber(cdText);
		if (preMod == null && posMod == null) {
			return TemporalExpression.ofLength(match, cd, unit);
		}

		LocalDateTime reference = LocalDateTime.now();
		long totalSeconds = (dtMapper.containsKey(preMod) ? dtMapper.get(preMod) :1) * cd * seconds;
		if (preMod != null) {
			tp.setType(TemporalExpression.TYPE.RANGE);
			LocalDateTime projection = reference.plusSeconds(totalSeconds);
			if (unit.compareTo(ChronoUnit.DAYS) <= 0) {
				tp.setStartTime(projection);
				tp.setEndTime(reference);
			} else {
				switch (unit) {
					case WEEKS:
						projection = projection.with(ChronoField.DAY_OF_WEEK, 1);
						tp.setStartTime(projection);
						tp.setEndTime(projection.plusSeconds(-totalSeconds));
						break;
					case MONTHS:
						projection = projection.with(ChronoField.DAY_OF_MONTH, 1);
						tp.setStartTime(projection);
						reference = projection.plusSeconds(-totalSeconds);
						tp.setEndTime(YearMonth.from(reference).atEndOfMonth().atStartOfDay());
						break;
					case YEARS:
						projection = LocalDate.of(projection.getYear(), 1, 1).atStartOfDay();
						tp.setStartTime(projection);
						reference = projection.plusSeconds(-totalSeconds);
						tp.setEndTime(LocalDate.of(reference.getYear(), 12, 31).atStartOfDay());
						break;
					default:
						throw new RuntimeException(unit.toString());
				}
			}

			tp.reorderTime();
		} else if (posMod != null && dtMapper.containsKey(posMod)) {
			tp.setType(TemporalExpression.TYPE.POINT);
			tp.setStartTime(reference.plusSeconds(dtMapper.get(posMod) * cd * seconds));
		}

		System.out.println(tp);
		return tp;
	}
}
