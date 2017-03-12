package hx.spark.dsal.text.nlp;

import com.hankcs.hanlp.HanLP;

import java.util.stream.Stream;

/**
 * Created by Benchun on 3/12/17
 */
public class HanLPDependencyParse {

    private static String[] SENTENCES = { "您转发这篇博客很无知", "您转发的这篇博客很无知" };

    public static void main(String[] args) {

        Stream.of(SENTENCES).map(HanLP::parseDependency)
            .forEach(coNLLSentence -> coNLLSentence.forEach(System.out::println));
    }

}
