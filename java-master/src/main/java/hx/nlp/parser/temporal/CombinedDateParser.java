package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

/**
 * Created by zhipeng.wang on 04/01 2017.
 */
public class CombinedDateParser extends TemporalTokensPatternParser {

	static final TokenSequencePattern tokensPattern = TokenSequencePattern.compile(
			"([{tag:AD}])? [{tag:P}]? ");

	@Override
	protected TokenSequencePattern getPattern() {
		return tokensPattern;
	}


	@Override
	protected TemporalExpression parseMatchedPattern(TokenSequenceMatcher matcher) {
		return null;
	}
}
