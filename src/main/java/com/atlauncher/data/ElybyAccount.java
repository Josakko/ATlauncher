package com.atlauncher.data;

import java.awt.BorderLayout;
import java.util.Optional;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.gettext.GetText;

import com.atlauncher.data.elyby.Profile;
import com.atlauncher.gui.dialogs.LoginWithElyByDialog;
import com.atlauncher.managers.AccountManager;
import com.atlauncher.managers.DialogManager;
import com.atlauncher.managers.LogManager;
import com.atlauncher.utils.ElyByAuth;
import com.atlauncher.utils.ElyByAuthAPI;
import com.atlauncher.utils.Utils;
import com.atlauncher.App;
import com.atlauncher.builders.HTMLBuilder;
import com.atlauncher.constants.Constants;
import com.atlauncher.data.elyby.ElybyLoginResponse;
import com.atlauncher.data.elyby.OauthTokenResponse;

public class ElybyAccount extends AbstractAccount {
    /**
     * The access token.
     */
    public String accessToken = null;

    /**
     * The account's password to login to ely.by servers
     */
    public transient String password;

    /**
     * The encrypted password
     */
    public String encryptedPassword;

    /*
     * To remember the password or not
     */
    public boolean remember;

    /*
     * In case user authenticated with password and username 
     */
    public String clientToken;

    /**
     * If the user must login again. This is usually the result of a failed
     * accessToken refresh.
     */
    public boolean mustLogin;

    public ElybyAccount(OauthTokenResponse oauthTokenResponse, Profile profile) {
        this.clientToken = null;
        update(oauthTokenResponse, profile);
    }
    
    public ElybyAccount(ElybyLoginResponse authResponse, String username, String password, boolean remember) {
        update(authResponse, username, password, remember);
        this.clientToken = authResponse.getClientToken();
    }

    public void update(ElybyLoginResponse authResponse, String username, String password, boolean remember) {
        this.remember = remember;
        if (remember) {
            setPassword(password);
        }

        this.accessToken = authResponse.getAccessToken();
        this.minecraftUsername = authResponse.getSelectedProfile().getName();
        this.uuid = authResponse.getSelectedProfile().getId().toString();
        this.username = username;
        this.mustLogin = false;
    }

    public void update(OauthTokenResponse oauthTokenResponse, Profile profile) {
        this.accessToken = oauthTokenResponse.accessToken;
        this.minecraftUsername = profile.username;
        this.uuid = profile.uuid;
        this.username = profile.username;
        this.mustLogin = false;
    }

    /**
     * Sets the password for this Account.
     *
     * @param password The password for the Account
     */
    public void setPassword(String password) {
        this.password = password;
        this.encryptedPassword = Utils.encrypt(this.password);
    }

    /**
     * Sets this Account to remember or not remember the password.
     *
     * @param remember True if the password should be remembered, False if it
     *                 shouldn't be remembered
     */
    public void setRemember(boolean remember) {
        this.remember = remember;
        if (!this.remember) {
            this.password = "";
            this.encryptedPassword = "";
        }
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getSessionToken() {
       return  String.format("token:%s:%s", this.getAccessToken(), this.getUUIDNoDashes()); // return accessToken;
    }

    @Override
    public String getUserType() { 
        return "mojang";
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
        String newUsername = getCurrentUsername();
        if (newUsername != this.username) {
            this.username = newUsername;
        }
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
        if (StringUtils.isBlank(this.clientToken)) {
            return oauthLogin();
        }
        
        ElybyLoginResponse response = this.login();
        if (response != null && !response.hasError() && !this.mustLogin) {
            return true;
        }

        if (this.mustLogin && ensureAccountIsLoggedIn()) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getSkinUrl() {
        return Constants.ELYBY_SKIN_TEXTURE_URL + this.minecraftUsername + ".png";
    }

    private ElybyLoginResponse login() {
        ElybyLoginResponse response = null;

        if (StringUtils.isNotBlank(accessToken)) {
            LogManager.info("Trying to login with access token.");
            response = ElyByAuth.login(this, false);
        }

        if (response != null && (!response.hasError() || response.isOffline())) {
            this.mustLogin = false;
            return response;
        }

        if (response == null || (response.hasError() && !response.isOffline())) {
            LogManager.info("Invalid access token, trying to get another one!");

            if (!this.remember) {
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                // #. {0} is the Minecraft username
                JLabel passwordLabel = new JLabel(GetText.tr("Enter password for {0}", this.minecraftUsername));

                JPasswordField passwordField = new JPasswordField();
                panel.add(passwordLabel, BorderLayout.NORTH);
                panel.add(passwordField, BorderLayout.CENTER);

                int ret = DialogManager.confirmDialog().setTitle(GetText.tr("Enter Password")).setContent(panel).show();

                String passwordEntry = new String(passwordField.getPassword());
                if (ret == DialogManager.OK_OPTION) {
                    if (!Utils.isEntryValid(passwordEntry)) {
                        LogManager.error("Aborting login for " + this.minecraftUsername + ", no password entered");
                        App.launcher.setMinecraftLaunched(false);
                        this.mustLogin = true;
                        return null;
                    }

                    this.password = passwordEntry;
                } else {
                    LogManager.error("Aborting login for " + this.minecraftUsername);
                    App.launcher.setMinecraftLaunched(false);
                    this.mustLogin = true;
                    return null;
                }
            }
        }
        LogManager.info("Trying to login with password");
        response = ElyByAuth.login(this, true);

        if (response.hasError() && !response.isOffline()) {
            LogManager.error(response.getErrorMessage());

            DialogManager
                    .okDialog().setTitle(
                            GetText.tr("Error Logging In"))
                    .setContent(new HTMLBuilder().center().text(GetText.tr("Couldn't login to ely.by")
                            + "<br/><br/>" + response.getErrorMessage()).build())
                    .setType(DialogManager.ERROR).show();

            App.launcher.setMinecraftLaunched(false);
            this.mustLogin = true;
            return null;
        }

        if (!response.isOffline() && !response.canPlayOnline()) {
            this.mustLogin = true;
            return null;
        }

        if (!response.isOffline() && !response.hasError()) {
            this.uuid = response.getSelectedProfile().getId().toString();
            this.accessToken = response.getAccessToken();
            AccountManager.saveAccounts();
        }

        this.mustLogin = false;
        return response;
    }

    private boolean oauthLogin() {
        boolean ret = false;

        if (StringUtils.isNotBlank(accessToken)) {
            LogManager.info("Checking the access token.");
            ret = ElyByAuthAPI.validateAccessToken(accessToken);
        }

        if (!ret) {
            LogManager.warn("Access token not valid, must relogin!");
            this.mustLogin = false;
        } else {
            LogManager.info("Access token valid.");
        }

        return ret;
    }
}
