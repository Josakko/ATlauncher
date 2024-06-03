package com.atlauncher.data;

public abstract class LoginResponse {
    protected boolean offline;
    protected boolean hasError;
    protected String errorMessage;
    protected String username;

    public void setOffline() {
        this.offline = true;
    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.hasError = true;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public String getErrorMessage() {
        return (this.errorMessage == null ? "Unknown Error Occurred" : this.errorMessage);
    }

    public String getUsername() {
        return this.username;
    }
}
