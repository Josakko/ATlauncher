package com.atlauncher.data.elyby;

import com.google.gson.annotations.SerializedName;

public class OauthTokenResponse {
    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_in")
    public Integer expiresIn;

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("refresh_token")
    public String refreshToken;
}
