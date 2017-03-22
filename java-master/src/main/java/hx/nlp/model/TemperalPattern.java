package hx.nlp.model;

import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

/**
 * Created by zhipeng.wang on 03/22 2017.
 */
public abstract class TemperalPattern implements TemperalExpression {

	protected String expression;
	protected TokenSequencePattern pattern;

	protected TemperalPattern(String pattern) {

	}

}
