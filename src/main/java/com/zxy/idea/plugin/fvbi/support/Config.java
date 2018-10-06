package com.zxy.idea.plugin.fvbi.support;

/**
 * Created by zhengxiaoyong on 2018/09/30.
 */
public class Config {

    private String rootView;

    private boolean isAddPrefixM = true;

    private boolean isApi26 = true;

    private boolean isKotlinLazy = false;

    private VARIABLE variable = VARIABLE.MEMBER;

    private MODIFIER modifier = MODIFIER.PRIVATE;

    private LANGUAGE language = LANGUAGE.JAVA;

    public String getRootView() {
        return rootView;
    }

    public void setRootView(String rootView) {
        this.rootView = rootView;
    }

    public boolean isAddPrefixM() {
        return isAddPrefixM;
    }

    public void setAddPrefixM(boolean addPrefixM) {
        isAddPrefixM = addPrefixM;
    }

    public boolean isApi26() {
        return isApi26;
    }

    public void setApi26(boolean api26) {
        isApi26 = api26;
    }

    public boolean isKotlinLazy() {
        return isKotlinLazy;
    }

    public void setKotlinLazy(boolean lazy) {
        isKotlinLazy = lazy;
    }

    public VARIABLE getVariable() {
        return variable;
    }

    public void setVariable(VARIABLE variable) {
        this.variable = variable;
    }

    public MODIFIER getModifier() {
        return modifier;
    }

    public void setModifier(MODIFIER modifier) {
        this.modifier = modifier;
    }

    public LANGUAGE getLanguage() {
        return language;
    }

    public void setLanguage(LANGUAGE language) {
        this.language = language;
    }

    public void reset() {
        rootView = null;
        isAddPrefixM = true;
        isApi26 = true;
        isKotlinLazy = false;
        variable = VARIABLE.MEMBER;
        modifier = MODIFIER.PRIVATE;
        language = LANGUAGE.JAVA;
    }

    public enum VARIABLE {
        MEMBER,
        LOCAL
    }

    public enum MODIFIER {
        PRIVATE,
        DEFAULT,
        PUBLIC,
        PROTECT
    }

    public enum LANGUAGE {
        JAVA,
        KOTLIN
    }

}
