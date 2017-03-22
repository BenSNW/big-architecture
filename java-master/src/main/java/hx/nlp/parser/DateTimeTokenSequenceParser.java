package hx.nlp.parser;

import edu.stanford.nlp.ling.CoreLabel;
import hx.nlp.model.TemperalExpression;

import java.util.List;

/**
 * Created by zhipeng.wang on 03/21 2017.
 */
public interface DateTimeTokenSequenceParser<T extends TemperalExpression> {

	T parse(List<CoreLabel> tokens);
}
