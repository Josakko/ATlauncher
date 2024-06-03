package com.atlauncher.data.elyby;

import org.apache.commons.lang3.StringUtils;

import com.atlauncher.data.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

public class ElybyLoginResponse extends LoginResponse {
    public User user;
    private String accessToken;
    private GameProfile selectedProfile;
    private String clientToken;
    private PropertyMap[] properties = null;

    public ElybyLoginResponse(String username) {
        this.hasError = false;
        this.username = username;
        this.offline = false;
    }

    public void setResponse(AuthResponse response) {
        this.user = response.getUser();
        this.properties = response.getUser().getProperties();
        this.accessToken = response.getAccessToken();
        this.selectedProfile = response.getSelectedProfile();
        this.clientToken = response.getClientToken();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    public String getClientToken() {
        return clientToken;
    }

    public User getUser() {
        return user;
    }

    public PropertyMap[] getProps() {
        return properties;
    }

    public String getJsonProps() {
        if (properties == null) {
            return "[]";
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        return gson.toJson(properties);
    }

    public boolean canPlayOnline() {
        return !isOffline() && getSelectedProfile() != null && StringUtils.isNotBlank(accessToken);
    }
}
