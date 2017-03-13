package hx.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dependency.CRFDependencyParser;

import java.util.stream.Stream;

/**
 * Created by Benchun on 3/12/17
 */
public class HanLPDependencyParse {

    private static String[] SENTENCES = { "您转的这篇微博很无知", "您转这篇微博很无知" };

    public static void main(String[] args) {

        Stream.of(SENTENCES).map(HanLP::segment).forEach(terms -> terms.forEach(System.out::println));

        // CRF dependency parser
        Stream.of(SENTENCES).map(CRFDependencyParser::compute).forEach(System.out::println);
        // neural network dependency parser
        Stream.of(SENTENCES).map(HanLP::parseDependency).forEach(System.out::println);
    }

}
