package org.jojo.flow.model.flowChart.modules;

import java.io.Serializable;

import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DOMable;

public interface InternalConfig extends Serializable, DOMable {
    @Override
    DOM getDOM();
}
