package hx.nlp;

import java.time.LocalDateTime;

/**
 * Created by zhipeng.wang on 3/20 020.
 */
public interface DateTimeParser {

    String getText();

    LocalDateTime parse();
}
