package it.sella.f24.service;

public class ConfigProperties {
    private static final ConfigProperties config = new ConfigProperties();
    private String lang = "ENGLISH";

    private ConfigProperties() {

    }

    public static ConfigProperties getInstance() {
        return config;
    }


    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}