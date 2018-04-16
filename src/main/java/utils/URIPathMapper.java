package utils;


import template.ViewData;

import java.util.HashMap;

public class URIPathMapper {

    private static URIPathMapper instance = null;
    private final HashMap<String, ViewData> mappings = new HashMap<>();

    private URIPathMapper() {
    }

    public static URIPathMapper getInstance() {
        if (instance == null) {
            instance = new URIPathMapper();
        }
        return instance;
    }

    public void addMapping(String uri, ViewData template) {
        mappings.put(uri, template);
    }

    public ViewData getMapping(String uri) {
        return mappings.get(uri);
    }

    public boolean hasMapping(String uri) {
        return mappings.containsKey(uri);
    }
}
