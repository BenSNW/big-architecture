package hx.nlp;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Benchun on 2/6/17
 */
public class JiebaSegmentDemo {

    public static void main(String[] args) {

        JiebaSegmenter segmenter = new JiebaSegmenter();
        String[] sentences = new String[] { "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。",
                "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作", "结过婚的和尚未结过婚的",
                "您转的这篇文章很无知", "您转这篇文章很无知"};
        for (String sentence : sentences)
            System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX).toString());

        Stream.of(sentences).map(sentence -> segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX))
                .flatMap(List::stream).forEach(segToken -> System.out.println(segToken.word));

    }

}
