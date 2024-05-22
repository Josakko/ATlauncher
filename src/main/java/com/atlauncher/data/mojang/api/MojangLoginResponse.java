package com.atlauncher.data.mojang.api;

import com.atlauncher.managers.AccountManager;
import com.atlauncher.data.LoginResponse;
import com.atlauncher.data.MojangAccount;

public class MojangLoginResponse extends LoginResponse {
    public MojangLoginResponse(String username) {
        this.hasError = false;
        this.auth = null;
        this.username = username;
        this.offline = false;
    }

    @Override
    public void save() {
        MojangAccount account = (MojangAccount) AccountManager.getAccountByName(this.username);

        if (account != null) {
            account.store = this.auth.saveForStorage();
        }
    }
}

