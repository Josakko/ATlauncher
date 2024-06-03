package com.atlauncher.data.mojang.api;

import com.atlauncher.managers.AccountManager;
import com.mojang.authlib.UserAuthentication;
import com.atlauncher.data.LoginResponse;
import com.atlauncher.data.MojangAccount;

public class MojangLoginResponse extends LoginResponse {
    private UserAuthentication auth;

    public MojangLoginResponse(String username) {
        this.hasError = false;
        this.auth = null;
        this.username = username;
        this.offline = false;
    }

    public void setAuth(UserAuthentication auth) {
        this.auth = auth;
    }

    public boolean hasAuth() {
        return (this.auth != null);
    }

    public UserAuthentication getAuth() {
        return this.auth;
    }

    public boolean isValidAuth() {
        if (!this.hasAuth()) {
            return false;
        }

        if (!this.auth.isLoggedIn()) {
            this.setErrorMessage("Response from Mojang wasn't valid!");
        } else if (this.auth.getAuthenticatedToken() == null) {
            this.setErrorMessage("No authentication token returned from Mojang!");
        } else if (auth.getSelectedProfile() == null
                && (this.auth.getAvailableProfiles() == null || this.auth.getAvailableProfiles().length == 0)) {
            this.setErrorMessage("There are no paid copies of Minecraft associated with this account!");
        } else if (this.auth.getSelectedProfile() == null) {
            this.setErrorMessage("No profile selected!");
        }

        return !this.hasError;
    }

    public void save() {
        MojangAccount account = (MojangAccount) AccountManager.getAccountByName(this.username);

        if (account != null) {
            account.store = this.auth.saveForStorage();
        }
    }
}

