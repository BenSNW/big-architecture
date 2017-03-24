package hx.nlp.parser.temporal;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

/**
 * Created by zhipeng.wang on 03/21 2017.
 */
public interface TemporalTokensParser {

//	default Class<T> getExpressionType() {
//		return (Class<T>)
//			((ParameterizedType) this.getClass().getGenericInterfaces()[0])
//				.getActualTypeArguments()[0];
//	}

	TemporalExpression parse(List<CoreLabel> tokens) throws RuntimeException;
}
