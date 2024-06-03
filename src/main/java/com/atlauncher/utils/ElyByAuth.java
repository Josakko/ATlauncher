package com.atlauncher.utils;

import com.atlauncher.data.ElybyAccount;
import com.atlauncher.data.elyby.AuthResponse;
import com.atlauncher.data.elyby.ElybyLoginResponse;
import com.atlauncher.managers.LogManager;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;

public class ElyByAuth {
    public static ElybyLoginResponse checkAccount(String username, String password, String clientToken) {
        AuthResponse reqResponse = null;
        ElybyLoginResponse response = new ElybyLoginResponse(username);

        try {
            reqResponse = ElyByAuthAPI.login(username, password, clientToken, true);
            response.setResponse(reqResponse);
        } catch (AuthenticationUnavailableException e) {
            response.setErrorMessage(e.getMessage());
            LogManager.error("Authentication servers unavailable, " + e.getMessage());
            response.setOffline();
        } catch (InvalidCredentialsException e) {
            response.setErrorMessage(e.getMessage());
            LogManager.error("Credentials invalid, " + e.getMessage());
        } catch (AuthenticationException e) {
            response.setErrorMessage(e.getMessage());
            LogManager.error("Authentication failed!");
        }

        if (reqResponse == null && !response.hasError()) {
            response.setErrorMessage("Unknown error occurred, authentication failed!");
            LogManager.error("Authentication failed!");
        }

        return response;
    }

    public static ElybyLoginResponse login(ElybyAccount account, boolean usePassword) {
        ElybyLoginResponse response = null;

        if (Utils.isEntryValid(account.accessToken)) {
            response = accessTokenLogin(account.accessToken, account.clientToken, account.username);
        } else if (usePassword) {
            response = checkAccount(account.username, account.password, account.clientToken);
        } else {
            response = new ElybyLoginResponse(account.username);
            response.setErrorMessage("Invalid access token, use password for login!");
            LogManager.error("Authentication with access token failed, should use password!");
        }

        return response;
    }

    public static ElybyLoginResponse accessTokenLogin(String accessToken, String clientToken, String username) {
        AuthResponse reqResponse = null;
        ElybyLoginResponse response = new ElybyLoginResponse(username);

        try {
            reqResponse = ElyByAuthAPI.refreshToken(accessToken, clientToken, true);
            response.setResponse(reqResponse);
        } catch (AuthenticationUnavailableException e) {
            response.setErrorMessage(e.getMessage());
            LogManager.error("Authentication servers unavailable, " + e.getMessage());
            response.setOffline();
        } catch (InvalidCredentialsException e) {
            response.setErrorMessage(e.getMessage());
            LogManager.error("Credentials invalid, " + e.getMessage());
        } catch (AuthenticationException e) {
            response.setErrorMessage(e.getMessage());
            LogManager.error("Authentication failed!");
        }

        if (reqResponse == null && !response.hasError()) {
            response.setErrorMessage("Unknown error occurred, authentication failed!");
            LogManager.error("Authentication failed!");
        }

        return response;
    }
}

