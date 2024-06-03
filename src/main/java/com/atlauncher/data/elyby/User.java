package com.atlauncher.data.elyby;

import com.mojang.authlib.properties.PropertyMap;

public class User {
    private String id;
    private PropertyMap[] properties;

    public String getId() {
        return id;
    }

    public PropertyMap[] getProperties() {
        return properties;
    }
}
