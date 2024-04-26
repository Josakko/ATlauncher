package com.atlauncher.data.github;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GitHubRelease {
    public String url;

    @SerializedName("html_url")
    public String htmlUrl;

    @SerializedName("tag_name")
    public String tagName;

    @SerializedName("assets")
    public ArrayList<GitHubReleaseAsset> assets;
}
