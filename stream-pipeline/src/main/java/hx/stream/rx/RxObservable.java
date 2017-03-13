package hx.stream.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by Benchun on 12/27/16
 *
 * @see http://www.infoq.com/cn/articles/rxjava-by-example?utm_campaign=rightbar_v2&utm_source=infoq&utm_medium=articles_link&utm_content=link_text
 *
 */
public class RxObservable {

    public static void main(String[] args) {

        Observable.from("the quick brown fox jumped over the lazy dogs".split(" "))
            .flatMap(word -> Observable.from(word.split("")))
            .distinct().sorted()
            .zipWith(Observable.range(1, 26), (string, count) -> String.format("%2d. %s", count, string))
            .subscribe(System.out::println);
        
        Observable.from("the quick brown fox jumped over the lazy dogs".split(" "))
        	.debounce(1, TimeUnit.SECONDS)
        	.subscribe(System.out::println);
        
//        Observable.creat

    }
}
