package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.util.CoreMap;
import hx.nlp.util.CoreNLPUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by zhipeng.wang on 04/01 2017.
 */
public class QPTemporalParser extends TemporalTokensPatternParser {

	static final TokenSequencePattern tokensPattern = TokenSequencePattern.compile(
			"([{tag:/LC|DT|JJ|NT/}])? ([{tag:/CD|OD/}] [{tag:/CC|PU/}]? [{tag:CD}]?) " +
			"/个/? (/分钟|刻钟|小时|钟头|天|日|周|星期|礼拜|月份?|季度?|年度?/) " +
			"([{tag:/LC|JJ/}])?");

	@Override
	protected TokenSequencePattern getPattern() {
		return tokensPattern;
	}


	@Override
	protected TemporalExpression parseMatchedPattern(TokenSequenceMatcher matcher) {
		System.out.println(matcher.group(0));
		String preMod = matcher.group(1);
		List<CoreMap> cdTokens = matcher.groupNodes(2);
		String nnUnit = matcher.group(3);
		String postMod = matcher.group(4);
		if (preMod == null && cdTokens == null)
			return null;

		IntStream.range(1, 5).mapToObj(matcher::groupNodes).filter(m -> m != null).forEach(tokens -> {
			System.out.println(String.join("", tokens.stream().map(token -> token.get(CoreAnnotations.ValueAnnotation.class)).collect(Collectors.toList())));
			System.out.println(tokens.size());
		});
		System.out.println(cdTokens.size());

		return null;
	}
}
