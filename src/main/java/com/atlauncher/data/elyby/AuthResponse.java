package com.atlauncher.data.elyby;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.response.Response;

public class AuthResponse extends Response {
    private String accessToken;
    private String clientToken;
    private GameProfile selectedProfile;
    private GameProfile[] availableProfiles;
    private User user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public GameProfile[] getAvailableProfiles() {
        return availableProfiles;
    }

    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    public User getUser() {
        return user;
    }
}

