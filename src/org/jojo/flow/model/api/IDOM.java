package org.jojo.flow.model.api;

import java.util.List;
import java.util.Map;

public interface IDOM extends IAPI {

    String NAME_OTHERS = "Others";

    void appendString(String name, String content);

    void appendInt(String name, int content);

    void appendInts(String listName, String name, int[] ids);

    <T extends IDOMable> void appendList(String name, List<T> list);

    void appendCustomDOM(String name, IDOMable domable);

    void appendCustomDOM(IDOMable domable);

    Map<String, Object> getDOMMap();

    String elemGet();
}