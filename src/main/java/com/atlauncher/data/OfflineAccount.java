package com.atlauncher.data;

import java.util.UUID;
import java.nio.charset.StandardCharsets;

import com.atlauncher.data.offline.OfflineLoginResponse;
import com.atlauncher.managers.LogManager;


public class OfflineAccount extends AbstractAccount {
    public OfflineAccount(String username) {
        this(
                username,
                UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)).toString()
            );
    }

    public OfflineAccount(String username, String uuid) {
        this.username = username;
        this.minecraftUsername = username;
        this.uuid = uuid;
    }


    @Override
    public String getAccessToken() {
        return "null";
    }

    @Override
    public String getSessionToken() {
        return String.format("token:%s:%s", this.getAccessToken(), this.getUUIDNoDashes());
    }

    @Override
    public String getUserType() {
        return "mojang";
    }

    @Override
    public String getCurrentUsername() {
        return this.minecraftUsername;
    }

    public OfflineLoginResponse login() {
        OfflineLoginResponse response = new OfflineLoginResponse(this.minecraftUsername);
        
        LogManager.info("Logged into " + this.minecraftUsername + " offline account.");

        return response;
    }

    @Override
    public void updateSkinPreCheck() {
    }

    @Override
    public void changeSkinPreCheck() {
    }

    @Override
    public String getSkinUrl() {
        return null;
    }
    
}
