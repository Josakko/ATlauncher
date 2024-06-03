package com.atlauncher.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.atlauncher.network.Download;
import com.google.gson.JsonParseException;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.yggdrasil.response.Response;

import okhttp3.RequestBody;

public class ElyByAuthUtils {
    public static <T extends Response> T sendReq(String url, RequestBody input, Class<T> classOfT) throws AuthenticationException {
        try {
            T res = Download.build().setUrl(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .post(input)
                    .asClassWithThrow(classOfT);

            if (res == null) return null;

            if (StringUtils.isNotBlank(res.getError())) {
                if (res.getError().equals("ForbiddenOperationException")) {
                    throw new InvalidCredentialsException(res.getErrorMessage());
                } else {
                    throw new AuthenticationException(res.getErrorMessage());
                }
            }

            return res;
        } catch (IOException e) {
            throw new InvalidCredentialsException("Invalid credentials, check username and password: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server: " + e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server: " + e.getMessage(), e);
        }
    }

    public static <T extends Response> T sendReq(String url, Class<T> classOfT) throws AuthenticationException {
        try {
            T res = Download.build().setUrl(url).asClassWithThrow(classOfT);

            if (res == null) return null;

            if (StringUtils.isNotBlank(res.getError())) {
                if (res.getError().equals("ForbiddenOperationException")) {
                    throw new InvalidCredentialsException(res.getErrorMessage());
                } else {
                    throw new AuthenticationException(res.getErrorMessage());
                }
            }

            return res;
        } catch (IOException e) {
            throw new InvalidCredentialsException("Invalid credentials, check username and password: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server: " + e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server: " + e.getMessage(), e);
        }
    }
}
