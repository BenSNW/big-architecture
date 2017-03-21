package hx.nlp.model;

import java.util.Map;

/**
 * Created by zhipeng.wang on 03/20 2017.
 */
public interface IntentTemplate {

    String type();

    Map<String, SlotTemplate> slots();

}
