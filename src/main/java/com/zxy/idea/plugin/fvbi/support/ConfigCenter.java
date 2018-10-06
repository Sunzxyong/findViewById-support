package com.zxy.idea.plugin.fvbi.support;

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

    public void reset() {
        mConfig.reset();
    }

}
