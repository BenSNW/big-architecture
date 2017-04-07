package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

/**
 * Created by zhipeng.wang on 03/31 2017.
 */
public class NTParser extends TemporalTokensPatternParser {

	static final TokenSequencePattern tokensPattern = TokenSequencePattern.compile(
			"([{tag:AD}])? [{tag:P}]? ([{tag:NT}]{1,4}) [{tag:AD}]? " +
			"[{tag:/P|CC/}] ([{tag:NT}]{1,4}) ([{tag:/AD|LC/}])?");

	@Override
	protected TokenSequencePattern getPattern() {
		return tokensPattern;
	}

	@Override
	protected TemporalExpression parseMatchedPattern(TokenSequenceMatcher matcher) {
		return null;
	}
}
