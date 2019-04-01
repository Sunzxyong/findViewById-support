package com.zxy.idea.plugin.fvbi.support;

import com.intellij.ide.util.PropertiesComponent;

/**
 * Created by zhengxiaoyong on 2018/09/30.
 */
public class ConfigCenter {

    private volatile static ConfigCenter sInstance;

    private FileType mFileType;

    private Config mConfig = new Config();

    private ConfigCenter() {
    }

    public static ConfigCenter getInstance() {
        if (sInstance == null) {
            synchronized (ConfigCenter.class) {
                if (sInstance == null) {
                    sInstance = new ConfigCenter();
                }
            }
        }
        return sInstance;
    }

    static final class Key {
        public static final String KEY_VARIABLE_PREFIX_M = "com.zxy.idea.plugin.findViewById-support.KEY_VARIABLE_PREFIX_M";
        public static final String KEY_API_AFTER_V26 = "com.zxy.idea.plugin.findViewById-support.KEY_API_AFTER_V26";
        public static final String KEY_KOTLIN_LAZY = "com.zxy.idea.plugin.findViewById-support.KEY_KOTLIN_LAZY";
    }

    public void putValue(String key, boolean value) {
        PropertiesComponent.getInstance().setValue(key, value + "");
    }

    public boolean getValue(String key, boolean defaultValue) {
        return Boolean.parseBoolean(PropertiesComponent.getInstance().getValue(key, defaultValue + ""));
    }

    public FileType getFileType() {
        return mFileType;
    }

    public void setFileType(FileType type) {
        this.mFileType = type;
    }

    public void setRootView(String rootView) {
        mConfig.setRootView(rootView);
    }

    public void setAddPrefixM(boolean addPrefixM) {
        mConfig.setAddPrefixM(addPrefixM);
    }

    public void setApi26(boolean api26) {
        mConfig.setApi26(api26);
    }

    public void setKotlinLazy(boolean lazy) {
        mConfig.setKotlinLazy(lazy);
    }

    public void setVariable(Config.VARIABLE variable) {
        mConfig.setVariable(variable);
    }

    public void setModifier(Config.MODIFIER modifier) {
        mConfig.setModifier(modifier);
    }

    public void setLanguage(Config.LANGUAGE language) {
        mConfig.setLanguage(language);
    }

    public void setSafetyMode(Config.LazyThreadSafetyMode safetyMode) {
        mConfig.setSafetyMode(safetyMode);
    }

    public String getRootView() {
        return mConfig.getRootView();
    }

    public boolean isAddPrefixM() {
        return mConfig.isAddPrefixM();
    }

    public boolean isApi26() {
        return mConfig.isApi26();
    }

    public boolean isKotlinLazy() {
        return mConfig.isKotlinLazy();
    }

    public Config.VARIABLE getVariable() {
        return mConfig.getVariable();
    }

    public Config.MODIFIER getModifier() {
        return mConfig.getModifier();
    }

    public Config.LANGUAGE getLanguage() {
        return mConfig.getLanguage();
    }

    public Config.LazyThreadSafetyMode getSafetyMode() {
        return mConfig.getSafetyMode();
    }

    public void reset() {
        mConfig.reset();
    }

}
