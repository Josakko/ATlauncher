package com.atlauncher.data;

import java.util.Optional;

import org.mini2Dx.gettext.GetText;

import com.atlauncher.data.elyby.Profile;
import com.atlauncher.gui.dialogs.LoginWithElyByDialog;
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
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getSessionToken() {
        return accessToken;
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
    }

    @Override
    public void changeSkinPreCheck() {
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

        this.username = profile.username;
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
        return ensureAccountIsLoggedIn();
    }

    @Override
    public String getSkinUrl() {
        return Constants.ELYBY_SKIN_TEXTURE_URL + this.minecraftUsername + ".png";
    }   
}
