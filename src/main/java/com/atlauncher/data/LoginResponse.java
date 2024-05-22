package com.atlauncher.data;

import com.mojang.authlib.UserAuthentication;

public abstract class LoginResponse {
    public boolean offline;
    public boolean hasError;
    public String errorMessage;
    public UserAuthentication auth;
    public String username;

    public void setOffline() {
        this.offline = true;
    }

    public void setOnline() {
        this.offline = false;
    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.hasError = true;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public String getErrorMessage() {
        return (this.errorMessage == null ? "Unknown Error Occurred" : this.errorMessage);
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

    public String getUsername() {
        return this.username;
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

    public abstract void save();
}
