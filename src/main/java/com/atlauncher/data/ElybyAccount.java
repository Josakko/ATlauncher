package com.atlauncher.data;

import java.util.Date;
import java.util.Optional;

import org.mini2Dx.gettext.GetText;

import com.atlauncher.data.elyby.Profile;
import com.atlauncher.gui.dialogs.LoginWithElyByDialog;
import com.atlauncher.managers.AccountManager;
import com.atlauncher.managers.DialogManager;
import com.atlauncher.managers.LogManager;
import com.atlauncher.utils.ElyByAuthAPI;
import com.atlauncher.constants.Constants;
import com.atlauncher.data.elyby.OauthTokenResponse;

public class ElybyAccount extends AbstractAccount {
    /**
     * The access token.
     */
    public String accessToken;

    /**
     * The ely oauth token.
     */
    public OauthTokenResponse oauthToken;

    /**
     * The date that the accessToken expires at.
     */
    public Date accessTokenExpiresAt;

    /**
     * If the user must login again. This is usually the result of a failed
     * accessToken refresh.
     */
    public boolean mustLogin;

    public ElybyAccount(OauthTokenResponse oauthTokenResponse, Profile profile) {
        update(oauthTokenResponse, profile);
    }

    public void update(OauthTokenResponse oauthTokenResponse, Profile profile) {
        this.oauthToken = oauthTokenResponse;
        this.accessToken = oauthTokenResponse.accessToken;
        this.minecraftUsername = profile.username;
        this.uuid = profile.uuid;
        this.username = profile.username;
        this.mustLogin = false;

        this.accessTokenExpiresAt = new Date();
        this.accessTokenExpiresAt.setTime(this.accessTokenExpiresAt.getTime() + (oauthTokenResponse.expiresIn * 1000));
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getSessionToken() {
        return accessToken;
    }

    public boolean refreshAccessToken() {
        return refreshAccessToken(false);
    }

    public boolean refreshAccessToken(Boolean force) {
        try {
            if (force || new Date().after(this.accessTokenExpiresAt)) {
                LogManager.info("Access token expired. Attempting to refresh");
                OauthTokenResponse oauthTokenResponse = ElyByAuthAPI.refreshAccessToken(oauthToken.refreshToken);

                if (oauthTokenResponse == null) {
                    mustLogin = true;
                    AccountManager.saveAccounts();
                    LogManager.error("Failed to refresh accessToken");
                    return false;
                }

                this.oauthToken = oauthTokenResponse;
                this.accessTokenExpiresAt = new Date();
                this.accessTokenExpiresAt.setTime(this.accessTokenExpiresAt.getTime() + (oauthTokenResponse.expiresIn * 1000));

                AccountManager.saveAccounts();
            }
        } catch (Exception e) {
            mustLogin = true;
            AccountManager.saveAccounts();

            LogManager.logStackTrace("Exception refreshing accessToken", e);
            return false;
        }

        return true;
    }

    @Override
    public String getUserType() {
        return "mojang";
    }

    @Override
    public String getRealUserType() {
        return "elyby";
    }

    @Override
    public String getCurrentUsername() {
        Profile profile = null;

        try {
            profile = ElyByAuthAPI.getProfile(accessToken);
        } catch (Exception e) {
            LogManager.error("Error getting Minecraft profile");
            return null;
        }

        if (profile == null) {
            LogManager.error("Error getting Minecraft profile");
            return null;
        }

        return Optional.of(profile.username).orElse(null);
    }

    @Override
    public void updateSkinPreCheck() {
        this.refreshAccessToken();
    }

    @Override
    public void changeSkinPreCheck() {
        this.refreshAccessToken();
    }

    public boolean updateProfile(String accessToken) {
        Profile profile = null;

        try {
            profile = ElyByAuthAPI.getProfile(accessToken);
        } catch (Exception e) {
            LogManager.logStackTrace("Failed to get Minecraft profile", e);
            return false;
        }

        if (profile == null) {
            LogManager.error("Failed to get Minecraft profile");
            return false;
        }

        this.minecraftUsername = profile.username;
        this.uuid = profile.uuid;

        return true;
    }

    public boolean ensureAccountIsLoggedIn() {
        boolean hasCancelled = false;
        while (mustLogin) {
            int ret = DialogManager.okCancelDialog().setTitle(GetText.tr("You Must Login Again"))
                    .setContent(GetText.tr("You must login again in order to continue.")).setType(DialogManager.INFO)
                    .show();

            if (ret != 0) {
                hasCancelled = true;
                break;
            }

            new LoginWithElyByDialog(this);
        }

        if (hasCancelled) {
            return false;
        }

        return true;
    }

    public boolean ensureAccessTokenValid() {
        if (!ensureAccountIsLoggedIn()) {
            return false;
        }

        if (!new Date().after(accessTokenExpiresAt)) {
            return true;
        }

        LogManager.info("Access Token has expired. Attempting to refresh it.");

        try {
            return refreshAccessToken();
        } catch (Exception e) {
            LogManager.logStackTrace("Exception while attempting to refresh access token", e);
        }

        return false;
    }

    @Override
    public String getSkinUrl() {
        return Constants.ELYBY_SKIN_TEXTURE_URL + this.minecraftUsername + ".png";
    }   
}
