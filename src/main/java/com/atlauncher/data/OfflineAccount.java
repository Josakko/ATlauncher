package com.atlauncher.data;

import com.atlauncher.utils.Utils;

public class OfflineAccount extends AbstractAccount {
    public OfflineAccount(String username) {
        this(
                username,
                Utils.getOfflineUUID(username)
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
