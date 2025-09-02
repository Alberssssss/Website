package com.example.demo.config;

public class VersionInfo {
    private final String version;
    private final String buildTime;

    public VersionInfo(String version, String buildTime) {
        this.version = version;
        this.buildTime = buildTime;
    }
    public String getVersion() { return version; }
    public String getBuildTime() { return buildTime; }
}