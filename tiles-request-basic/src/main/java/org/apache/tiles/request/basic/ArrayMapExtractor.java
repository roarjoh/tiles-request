package org.apache.tiles.request.basic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import org.apache.tiles.request.attribute.HasAddableKeys;
import org.apache.tiles.request.attribute.HasRemovableKeys;

public class ArrayMapExtractor implements HasAddableKeys<String>, HasRemovableKeys<String> {
    
    private Map<String, String[]> baseMap;
    
    /**
     * @param baseMap
     */
    protected ArrayMapExtractor(Map<String, String[]> baseMap) {
        this.baseMap = baseMap;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(baseMap.keySet());
    }

    @Override
    public String getValue(String key) {
        String[] values = baseMap.get(key);
        return values == null ? null : values[0];
    }

    @Override
    public void setValue(String key, String value) {
        String[] data = baseMap.get(key);
        String[] newData;
        if (data == null) {
            newData = new String[] {value};
        } else {
            newData = Arrays.copyOf(data, data.length + 1);
            newData[data.length] = value;
        }
        baseMap.put(key, newData);
    }

    @Override
    public void removeValue(String key) {
        baseMap.remove(key);
    }
}