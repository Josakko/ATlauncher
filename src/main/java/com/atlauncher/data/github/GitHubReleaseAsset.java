package com.atlauncher.data.github;

import com.google.gson.annotations.SerializedName;

public class GitHubReleaseAsset {
    public String url;
    public String name;

    @SerializedName("content_type")
    public String contentType;

    public int size;

    @SerializedName("browser_download_url")
    public String downloadUrl;
}
