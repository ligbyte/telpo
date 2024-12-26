package com.stkj.cashier.setting.model;

public class CheckAppVersionBean {

    private String version;

    private String content;

    private String url;

    /**
     * 系统升级是否强制(0 不强制 1 强制)
     */
    private String versionForce;

    // Getters and Setters
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionForce() {
        return versionForce;
    }

    public void setVersionForce(String versionForce) {
        this.versionForce = versionForce;
    }
}
