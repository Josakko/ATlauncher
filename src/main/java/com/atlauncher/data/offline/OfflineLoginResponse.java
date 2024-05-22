package com.atlauncher.data.offline;

import com.atlauncher.managers.AccountManager;
import com.atlauncher.data.LoginResponse;
import com.atlauncher.data.OfflineAccount;

public class OfflineLoginResponse extends LoginResponse {
    public OfflineLoginResponse(String username) {
        this.hasError = false;
        this.auth = null;
        this.username = username;
        this.offline = true;
    }

    @Override
    public void save() {
        OfflineAccount account = (OfflineAccount) AccountManager.getAccountByName(this.username);

        if (account != null) {
            account.store = this.auth.saveForStorage();
        }
    }
}
