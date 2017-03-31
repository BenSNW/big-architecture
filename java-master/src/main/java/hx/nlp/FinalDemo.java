package hx.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.tokensregex.MultiPatternMatcher;
import edu.stanford.nlp.ling.tokensregex.SequenceMatchResult;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.Redwood;
import hx.nlp.parser.temporal.FromToPatternParser;
import hx.nlp.util.CoreNLPUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zhipeng.wang on 03/30 2017.
 */
public class FinalDemo {

	/** A logger for this class */
	private static Redwood.RedwoodChannels log = Redwood.channels(CoreNLPDemo.class);

	private static final String[] TEXT = new String[] { "工行昨天的股价", "工行最近几年的股价",
			"工商银行昨天的股价", "中国工商银行上周的股价", "工商银行3天前的股价", "工商银行3月5日到3月10日的股价走势",
			"工商银行3月份的股价走势", "工行第二季度的股价", "工商银行早上8点的股价", "工商银行到3月5日为止前一个月的股价",
			"工商银行的资金流入", "工商银行的股价和市盈率分别是多少", "资金流入前10位",
			"工商银行涨停了", "今天涨停的公司", "连续3天涨停的公司", "涨幅超过百分之三的公司",
			"603991市值三亿美元", "603991市值多少钱", "603991是哪家公司",
			"工商银行好不好", "工商银行呢，这个怎么样？", "有推荐的吗", "有什么推荐的", "有没有什么推荐的" };

	public static void main(String[] args) {

		String propsFile = "chinese-parser.properties";
		StanfordCoreNLP annotatorPipeline = new StanfordCoreNLP(propsFile);

		System.out.println(annotatorPipeline.getProperties().toString());

		Stream.of(TEXT).forEach(text -> {
			Annotation annotation = annotatorPipeline.process(text);
			annotation.set(CoreAnnotations.DocDateAnnotation.class, "2017-03-23");
//            annotation.keySet().forEach(System.out::println);

			CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);

			SemanticGraph semanticGraph = sentence.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
			System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.LIST));
			System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.XML));
			System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.READABLE));
			System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.RECURSIVE));
//            System.out.println(semanticGraph.toString(CoreLabel.OutputFormat.VALUE_MAP));
//            semanticGraph.prettyPrint();

			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

			List<TokenSequencePattern> patterns = Stream.of(
					"[{tag:AD}]? [{tag:P}]? (?$from [{tag:NT}]{1,3}) [{tag:AD}]? [{tag:/P|CC/}] (?$to [{tag:NT}]{1,3}) ([{tag:DEG}]? []{0,3} /时间|时候|时期|时光/)? [{tag:LC}]?",

					"[{tag:/P|DT|AD/}]? (?$date ([ {tag:NT}|{word:/过年|春节|国庆节?|清明节?|端午节?/} ] [{tag:DEG}]?){1,2}) ([{tag:/LC|AD/}])?",     // since until or during pattern

					"[{tag:/P|DT/}]? (?$date [{tag:NT}]{1,2}) ([{tag:LC}])? [{tag:DEG}]? [{tag:DT}]? ([{tag:/CD|OD/}] [{tag:M}]? /分钟|刻钟|小时|天|日|星期|礼拜|月份?|季度?/ | [{tag:/M|JJ/}]? /时间|时候|时期|时光/) [{tag:LC}]?",         // A during B pattern

					"([{tag:/LC|DT|NT|JJ/}])? ([{tag:/CD|OD/}] [{tag:M}]? /分钟|刻钟|小时|天|日|星期|礼拜|月份?|季度?|年/) ([{tag:/LC|JJ/}])? [{tag:DEG}]? ([{tag:NT}]{0,2})")
					.map(TokenSequencePattern::compile).collect(Collectors.toList());
			MultiPatternMatcher multiPatternMatcher = TokenSequencePattern.getMultiPatternMatcher(patterns);
			List<SequenceMatchResult<CoreMap>> matches = multiPatternMatcher.findNonOverlapping(tokens);
			matches.forEach( matchResult -> {
				System.out.println(sentence + " -> " + matchResult.group());
				matchResult.groupNodes().forEach(label -> System.out.println(
						label.get(CoreAnnotations.NamedEntityTagAnnotation.class) + "-"
								+ label.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class)));
			});

			List<CoreLabel> matchedTokens = (List<CoreLabel>) matches.get(0).groupNodes();

			for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				// Print out words, lemma, ne, and normalized ne
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
				String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
				System.out.println("token: " + "word="+word + ", lemma="+lemma + ", pos=" + pos + ", ne=" + ne + ", normalized=" + normalized);
			}

			List<CoreMap> entities = sentence.get(CoreAnnotations.MentionsAnnotation.class);
			for (CoreMap entity: entities) {
				System.out.println(entity + ": " + entity.get(CoreAnnotations.NamedEntityTagAnnotation.class)
						+ "-" + entity.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class)
						+ "-" + entity.get(CoreAnnotations.NumericValueAnnotation.class));
//                entity.keySet().forEach((Class key) -> System.out.println(key + "-" + entity.get(key)));
				entity.get(CoreAnnotations.TokensAnnotation.class).forEach(token -> {
					String word = token.getString(CoreAnnotations.TextAnnotation.class);
					String pos = token.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
					String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
					String nner = token.getString(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
					int index = token.get(CoreAnnotations.IndexAnnotation.class);
					System.out.println(index + "\t" + word + "\t " + pos + "\t " + ner + "\t" + nner);
				});
			}


		});

	}

}
