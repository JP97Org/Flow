package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.Subject;
import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.storeLoad.ConfigDOM;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.DOMable;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;

public class ExternalConfig extends Subject implements Comparable<ExternalConfig>, DOMable {
    private String name;
    private int priority;
    
    public ExternalConfig(final String name, final int priority) {
        this.name = name;
        this.priority = priority;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public void setPriority(final int newPriority) {
        this.priority = newPriority;
        notifyObservers(newPriority);
    }
    
    public Pair<String, Integer> getConfig() {
        return new Pair<>(this.name, this.priority);
    }
    
    @Override
    public int hashCode() {
        return getConfig().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof ExternalConfig) {
            final ExternalConfig oc = (ExternalConfig)other;
            return this.name == oc.name && this.priority == oc.priority;
        }
        return false;
    }

    @Override
    public final int compareTo(final ExternalConfig o) { 
        if (this.priority != o.priority) {
            return Integer.valueOf(this.priority).compareTo(o.priority);
        }
        return this.name.compareTo(o.name);
    }

    public DOM getDOM() {
        final ConfigDOM dom = ConfigDOM.getExternal();
        dom.setName(getName());
        dom.setPriority(getPriority());
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        final Map<String, Object> domMap = dom.getDOMMap();
        final DOM nameDom = (DOM)domMap.get(ConfigDOM.NAME_NAME);
        this.name = nameDom.elemGet();
        final DOM priorityDom = (DOM)domMap.get(ConfigDOM.NAME_PRIORITY);
        this.priority = Integer.parseInt(priorityDom.elemGet());
        notifyObservers();
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        final Map<String, Object> domMap = dom.getDOMMap();
        try {
            ok(domMap.get(ConfigDOM.NAME_NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM nameDom = (DOM)domMap.get(ConfigDOM.NAME_NAME);
            ok(nameDom.elemGet() != null, OK.ERR_MSG_NULL);
            final DOM priorityDom = (DOM)domMap.get(ConfigDOM.NAME_PRIORITY);
            ok(x -> Integer.parseInt(priorityDom.elemGet()), "");
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(new ModelFacade().getFlowChart()).reportWarning(); //TODO affect module
            return false;
        }
    }
}
