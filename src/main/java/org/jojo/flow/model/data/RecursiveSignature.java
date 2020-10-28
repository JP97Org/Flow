package org.jojo.flow.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jojo.flow.model.api.IDataSignature;

public class RecursiveSignature extends DataSignature {
    /**
     * 
     */
    private static final long serialVersionUID = -1286793948272706994L;
    private final List<IDataSignature> components;
    
    public RecursiveSignature(final RecursiveCheckable data) {
        super(data.getDataId());
        this.components = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            this.components.add(data.get(i).getDataSignature());
        }
    }
    
    private RecursiveSignature(final RecursiveSignature toCopy) {
        super(toCopy.getDataId());
        this.components = toCopy.components.stream()
                .map(x -> x.getCopy())
                .collect(Collectors.toList());
    }
    
    private RecursiveSignature(final int dataId, final List<IDataSignature> components) {
        super(dataId);
        this.components = components;
    }

    @Override
    public DataSignature getCopy() {
        return new RecursiveSignature(this);
    }

    @Override
    public IDataSignature getComponent(int index) {
        return this.components.get(index);
    }

    @Override
    public int size() {
        return this.components.size();
    }
    
    @Override
    public String toString() { //TODO das und ofString mehr testen insb. auch mit deaktivierten und verschiedenen recursives
        final StringBuilder ret = new StringBuilder(toStringDs());
        final String componentsString = this.components.toString();
        int level = -1;
        for (final char c : componentsString.toCharArray()) {
            if (c == '[') {
                level++;
            } else if (c == ']') {
                level--;
            } else if ( c == ',') {
                for (int i = 0; i < level; i++) {
                    ret.append(',');
                }
            }
            ret.append(c);
        }
        return ret.toString().replaceAll(",\\s", ",; ");
    }
    
    private static String getSplitString(final int level) {
        final StringBuilder ret = new StringBuilder();
        for (int i = 0; i <= level; i++) {
            ret.append(",");
        }
        return ret.append(";").toString();
    }
    
    @Override
    public IDataSignature ofString(final String info) {
        return ofString(info, 0);
    }
    
    private IDataSignature ofString(final String infoLevel, int level) {
        final String prepared = infoLevel.substring(1, infoLevel.length() - 1);
        final String splitString = getSplitString(level);
        final String[] split = prepared.split(splitString);
        
        List<IDataSignature> retList = new ArrayList<>();
        final String toStrDs = toStringDs();
        for (final String dsStr : split) {
            IDataSignature local;
            if (dsStr.startsWith(toStrDs + "[")) {
                local = ofString(dsStr.replaceFirst(toStrDs, ""), level + 1);
            } else {
                local = DataSignature.of(dsStr);
            }
            retList.add(local);
        }
        
        return new RecursiveSignature(getDataId(), retList);
    }
}