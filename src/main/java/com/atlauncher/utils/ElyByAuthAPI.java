package com.atlauncher.utils;

import java.io.IOException;

import com.atlauncher.Gsons;
import com.atlauncher.constants.Constants;
import com.atlauncher.data.elyby.AuthResponse;
import com.atlauncher.data.elyby.OauthTokenResponse;
import com.atlauncher.data.elyby.Profile;
import com.atlauncher.network.Download;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.Response;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ElyByAuthAPI {
    public static OauthTokenResponse getAccessToken(String authCode) {
        RequestBody data = new FormBody.Builder()
                .add("client_id", Constants.ELYBY_LOGIN_CLIENT_ID)
                .add("client_secret", Constants.ELYBY_LOGIN_SECRET_KEY)
                .add("redirect_uri", Constants.ELYBY_LOGIN_REDIRECT_URL)
                .add("grant_type", "authorization_code")
                .add("code", authCode).build();

        OauthTokenResponse oauthTokenResponse = Download.build().setUrl(Constants.ELYBY_AUTH_TOKEN_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(data)
                .asClass(OauthTokenResponse.class, Gsons.DEFAULT);

        return oauthTokenResponse;
    }

    public static Profile getProfile(String accessToken) throws IOException {
        Profile profile = Download.build().setUrl(Constants.ELYBY_TOKEN_INFO_URL)
                .header("Authorization", "Bearer " + accessToken).asClassWithThrow(Profile.class);

        return profile;
    }

    public static AuthResponse login(String username, String password, String clientToken, boolean requestUser) throws AuthenticationException {
        RequestBody data = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("clientToken", clientToken)
                .add("requestUser", Boolean.valueOf(requestUser).toString())
                .build();

        AuthResponse response = ElyByAuthUtils.sendReq(Constants.ELYBY_AUTH_URL, data, AuthResponse.class);
        
        return response;
    }

    public static AuthResponse refreshToken(String accessToken, String clientToken, boolean requestUser) throws AuthenticationException {
        RequestBody data = new FormBody.Builder()
                .add("accessToken", accessToken)
                .add("clientToken", clientToken)
                .add("requestUser", Boolean.valueOf(requestUser).toString())
                .build();

        AuthResponse response = ElyByAuthUtils.sendReq(Constants.ELYBY_REFRESH_URL, data, AuthResponse.class);

        return response;
    }

    public static boolean validateAccessToken(String accessToken) {
        RequestBody data = new FormBody.Builder().add("accessToken", accessToken).build();
        
        try {
            ElyByAuthUtils.sendReq(Constants.ELYBY_VALIDATE_URL, data, Response.class);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }
}
