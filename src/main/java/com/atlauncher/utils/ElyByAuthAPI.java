package com.atlauncher.utils;

import java.io.IOException;

import com.atlauncher.Gsons;
import com.atlauncher.constants.Constants;
import com.atlauncher.data.elyby.OauthTokenResponse;
import com.atlauncher.data.elyby.Profile;
import com.atlauncher.data.elyby.Textures;
import com.atlauncher.network.Download;

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

    public static OauthTokenResponse refreshAccessToken(String refreshToken) {
        // ely docs is shit, literally their docs say different from what their server does, luckily its open source 
        // https://github.com/elyby/accounts/blob/master/api/modules/oauth/models/OauthProcess.php#L136#L201
        // ops, it looks like the access tokens now last forever so this is not needed 
        RequestBody data = new FormBody.Builder()
                .add("client_id", Constants.ELYBY_LOGIN_CLIENT_ID)
                .add("client_secret", Constants.ELYBY_LOGIN_SECRET_KEY)
                //.add("scope", String.join(" ", Constants.ELYBY_LOGIN_SCOPES))
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token").build();

        OauthTokenResponse oauthTokenResponse = Download.build()
                .setUrl(Constants.ELYBY_REFRESH_TOKEN_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(data)
        .asClass(OauthTokenResponse.class, Gsons.DEFAULT);

        oauthTokenResponse.refreshToken = refreshToken;
        return oauthTokenResponse;
    }

    public static Profile getProfile(String accessToken) throws IOException {
        Profile profile = Download.build().setUrl(Constants.ELYBY_TOKEN_INFO_URL)
                .header("Authorization", "Bearer " + accessToken).asClassWithThrow(Profile.class);

        return profile;
    }

    public static Textures getTextures(String username) {
        return null;
    }
}
