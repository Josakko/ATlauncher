package com.atlauncher.data;

import java.util.Map;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

import com.atlauncher.data.LoginResponse;
import com.atlauncher.managers.LogManager;


public class OfflineAccount extends AbstractAccount {
    /**
     * This is the store for this username as returned by Mojang.
     */
    public Map<String, Object> store;


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
    public String getRealUserType() {
        return "offline";
    }

    @Override
    public String getCurrentUsername() {
        return this.minecraftUsername;
    }


    public LoginResponse login() {
        LoginResponse response = new LoginResponse(this.minecraftUsername, "offline");
        
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
