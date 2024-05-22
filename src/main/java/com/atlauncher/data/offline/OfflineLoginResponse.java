/*
 * ATLauncher - https://github.com/ATLauncher/ATLauncher
 * Copyright (C) 2013-2022 ATLauncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.atlauncher.data.offline;

import com.atlauncher.managers.AccountManager;
import com.mojang.authlib.UserAuthentication;
import com.atlauncher.data.OfflineAccount;

public class OfflineLoginResponse {
    private boolean offline;
    private boolean hasError;
    private String errorMessage;
    private UserAuthentication auth;
    private final String username;

    public OfflineLoginResponse(String username) {
        this.hasError = false;
        this.auth = null;
        this.username = username;
        this.offline = true;
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
            this.setErrorMessage("Login failed!");
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
        OfflineAccount account = (OfflineAccount) AccountManager.getAccountByName(this.username);

        if (account != null) {
            account.store = this.auth.saveForStorage();
        }
    }
}
