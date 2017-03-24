package hx.nlp.util;

import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

/**
 * Created by zhipeng.wang on 03/23 2017.
 */
public class MatcherGroupUtil {

	public static List<? extends CoreMap> getOriginalTokens(TokenSequenceMatcher matcher) {
		return matcher.elements();
	}

	public static List<CoreMap> getMatchedTokens(TokenSequenceMatcher matcher) {
		return matcher.groupNodes();
	}

	public static List<CoreMap> getMatchedTokens(TokenSequenceMatcher matcher, int group) {
		return matcher.groupNodes(group);
	}
}
