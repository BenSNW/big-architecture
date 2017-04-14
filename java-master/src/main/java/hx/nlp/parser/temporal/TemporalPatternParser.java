package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zhipeng.wang on 03/23 2017.
 */
public abstract class TemporalPatternParser implements TemporalTokensParser {

	static Pattern datePattern = Pattern.compile("((\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?");

	static String nnSuffix = "([{tag:DEG}]? []{0,3} /时间|时候|时期|时光/)?";

	@Override
	public TemporalExpression parse(List<CoreLabel> tokens) {
		TokenSequenceMatcher matcher = getPattern().matcher(tokens);
		if (!matcher.find())
			return null;
		System.out.println(matcher.elements() + " -> " + matcher.group());
		matcher.groupNodes().forEach(label -> System.out.println(
				label.get(CoreAnnotations.NamedEntityTagAnnotation.class) + "-"
			  + label.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class)));
		return parseMatchedPattern(matcher);
	}

	public String getPatternString() {
		return getPattern().toString();
	}

	protected abstract TokenSequencePattern getPattern();

	protected abstract TemporalExpression parseMatchedPattern(TokenSequenceMatcher matcher);

}
