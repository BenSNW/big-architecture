package hx.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.MultiPatternMatcher;
import edu.stanford.nlp.ling.tokensregex.SequenceMatchResult;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import hx.nlp.parser.QueryParser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zhipeng.wang on 03/24 2017.
 */
public class TemporalParserDemo {

	public static void main(String[] args) {

		String text = "大概在去年8月7号到今年9月的那段美好时光里他一直在炒股；昨天到今天上午八点左右;在上一年的第二个季度；前年3月份的那段时光；今年三月的第一个星期三；" +
				"自从今年九月份以来，股市不断震荡；这个系统是3天前重装的；最近的这几天一直在看论文;今年春节期间每克黄金涨价了不少;" +
				"楼市在过去的5年里出现了大量的泡沫；多年来他一直坚持早睡早起；最近几天早上;四月初八；十八年来；半个小时前；一刻钟前；" +
				"一天前；3点钟前；3个小时；3周；3个礼拜；3天；清晨6点钟的时候；大概七点左右；早上七点之前送到；去年的第二季度；去年至今；" +
				"大概从去年8月到今年9月这段时间里；过去的十八个月一直在涨；自从去年起；在16年3月份；国庆节；圣诞节；十一期间；五一；" +
				"楼市在过去的5年里出现了大量的泡沫;去年上半年；昨天前几个小时；前几天早上;春秋时期";
		StanfordCoreNLP annotatorPipeline = new StanfordCoreNLP("chinese-parser.properties");
		Annotation annotation = annotatorPipeline.process(text);

		for (CoreMap sentence: annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

			List<TokenSequencePattern> patterns = Stream.of(
					"[{tag:AD}]? [{tag:P}]? (?$from [{tag:NT}]{1,3}) [{tag:AD}]? [{tag:/P|CC/}] (?$to [{tag:NT}]{1,3}) ([{tag:DEG}]? []{0,3} /时间|时候|时期|时光/)? [{tag:LC}]?",

					"[{tag:/P|DT|AD/}]? (?$date ([ {tag:NT}|{word:/过年|春节|国庆节?|清明节?|端午节?/} ] [{tag:DEG}]?){1,2}) ([{tag:/LC|AD/}])?",     // since until or during pattern

					"[{tag:/P|DT/}]? (?$date [{tag:NT}]{1,2}) ([{tag:LC}])? [{tag:DEG}]? [{tag:DT}]? ([{tag:/CD|OD/}] [{tag:M}]? /分钟|刻钟|小时|天|日|星期[一二三四五六日]?|礼拜[一二三四五六日]?|月份?|季度?/ | [{tag:/M|JJ/}]? /时间|时候|时期|时光/) [{tag:LC}]?",         // A during B pattern

					"([{tag:/LC|DT|NT|JJ/}])? ([{tag:/CD|OD/}] [{tag:M}]? /分钟|刻钟|小时|天|日|星期[一二三四五六日]?|礼拜[一二三四五六日]?|月份?|季度?|年/) ([{tag:/LC|JJ/}])? [{tag:DEG}]? ([{tag:NT}]{0,2})")
				.map(TokenSequencePattern::compile).collect(Collectors.toList());

			MultiPatternMatcher multiPatternMatcher = TokenSequencePattern.getMultiPatternMatcher(patterns);
			List<SequenceMatchResult<CoreMap>> matches = multiPatternMatcher.findNonOverlapping(tokens);
			matches.forEach( matchResult -> {
				System.out.println(matchResult.group());
				matchResult.groupNodes().forEach(label -> System.out.println(label.toShorterString()));
			});

			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
			new QueryParser(tree).parse();

		}
	}
}
