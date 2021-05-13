package com.zf1976.naming.util;

import java.util.ArrayList;
import java.util.List;

public class InetUtilsProperties {
    public static final String PREFIX = "spring.cloud.inetutils";
    private String defaultHostname = "localhost";
    private String defaultIpAddress = "127.0.0.1";
    private int timeoutSeconds = 1;
    private List<String> ignoredInterfaces = new ArrayList<>();
    private boolean useOnlySiteLocalInterfaces = false;
    private List<String> preferredNetworks = new ArrayList<>();

    public InetUtilsProperties() {
    }

    public static String getPREFIX() {
        return "spring.cloud.inetutils";
    }

    public String getDefaultHostname() {
        return this.defaultHostname;
    }

    public void setDefaultHostname(String defaultHostname) {
        this.defaultHostname = defaultHostname;
    }

    public String getDefaultIpAddress() {
        return this.defaultIpAddress;
    }

    public void setDefaultIpAddress(String defaultIpAddress) {
        this.defaultIpAddress = defaultIpAddress;
    }

    public int getTimeoutSeconds() {
        return this.timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public List<String> getIgnoredInterfaces() {
        return this.ignoredInterfaces;
    }

    public void setIgnoredInterfaces(List<String> ignoredInterfaces) {
        this.ignoredInterfaces = ignoredInterfaces;
    }

    public boolean isUseOnlySiteLocalInterfaces() {
        return this.useOnlySiteLocalInterfaces;
    }

    public void setUseOnlySiteLocalInterfaces(boolean useOnlySiteLocalInterfaces) {
        this.useOnlySiteLocalInterfaces = useOnlySiteLocalInterfaces;
    }

    public List<String> getPreferredNetworks() {
        return this.preferredNetworks;
    }

    public void setPreferredNetworks(List<String> preferredNetworks) {
        this.preferredNetworks = preferredNetworks;
    }
}
