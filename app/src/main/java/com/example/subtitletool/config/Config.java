package com.example.subtitletool.config;

import com.example.subtitletool.utils.SpUtils;

public class Config {

    public static final String SETTING_CONFIG = "SettingConfig";
    public static int sLoginMode;

    private static SpUtils sSp = SpUtils.getInstance(SETTING_CONFIG);

    public Config() {
    }

    public static void resetConfig() {
        SpUtils.getInstance(SETTING_CONFIG).clear();
        loadConfig();
    }

    public static void loadConfig() {
    }

    static {
        loadConfig();
    }
}
