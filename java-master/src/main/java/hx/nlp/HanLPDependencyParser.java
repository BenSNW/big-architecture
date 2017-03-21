package hx.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.CRFDependencyParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Benchun on 3/12/17
 */
public class HanLPDependencyParser {

    private static String[] SENTENCES = { "工行3天前的股价", "工行过去十五天的股价",
            "工商银行上周的股价", "工商银行上周3的股价", "工商银行上星期的股价",
            "工商银行上星期五的股价", "工商银行星期五的股价", "工商上个月的股价",
            "工商银行3月5日以前的股价走势", "工商银行到3月5日为止的股价走势",
            "中国工商银行昨天的股价是多少", "工商银行3月5日到3月10日的股价走势",
            "工商银行昨天涨停了", "工商银行连续3天涨停了", "工商银行的资金流入是多少" };

    public static void main(String[] args) {

        Stream.of(SENTENCES).map(HanLP::segment).forEach(terms -> terms.forEach(System.out::println));

        // CRF dependency parser
        Stream.of(SENTENCES).map(CRFDependencyParser::compute).forEach(System.out::println);
        // neural network dependency parser
        Stream.of(SENTENCES).map(HanLP::parseDependency).forEach(System.out::println);

        Stream.of(SENTENCES).map(HanLP::parseDependency).forEach(sentence -> {
            List<CoNLLWord> orgs = new ArrayList<>(2);
            List<CoNLLWord> date = new ArrayList<>(4);
            List<CoNLLWord> normalizedSentence = new ArrayList<>(sentence.word.length);
            for (int i = 0; i < sentence.word.length; i++) {
                CoNLLWord word = sentence.word[i];
                if (isDate(word)) {
                    if (i > 0 && isAssociatedDate(sentence.word[i-1])) {
                        normalizedSentence.remove(normalizedSentence.size()-1);
                        date.add(sentence.word[i-1]);
                    }
                    date.add(word);
                    if (i < sentence.word.length-1 && isAssociatedDate(sentence.word[i+1]))
                        date.add(sentence.word[++i]);
                } else if (isOrg(word)) {
                    orgs.add(word);
                } else {
                    normalizedSentence.add(word);
                }
            }

            System.out.println(date.stream().map(word -> word.LEMMA).collect(Collectors.joining()));
            System.out.println(orgs.stream().map(word -> word.LEMMA).collect(Collectors.joining()));
            System.out.println(normalizedSentence.stream().map(word -> word.LEMMA).collect(Collectors.joining()));
        });
    }

    static boolean isDate(CoNLLWord coNLLWord) {
        return coNLLWord.POSTAG.equals("t") ||
            (coNLLWord.POSTAG.equals("mq") && coNLLWord.LEMMA.matches(".*(日|号|天|周|旬|月份?|季度?|年)"));
    }

    static boolean isAssociatedDate(CoNLLWord coNLLWord) {
        return !isDate(coNLLWord) &&
            coNLLWord.CPOSTAG.matches("(m|v?f)") || coNLLWord.POSTAG.matches("(m|v?f)"); // can not be ORG
    }

    static boolean isOrg(CoNLLWord coNLLWord) {
        return coNLLWord.POSTAG.equals("nt") || coNLLWord.POSTAG.equals("ni");
    }

}
