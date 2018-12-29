package com.zxy.idea.plugin.fvbi.support;

import com.android.utils.Pair;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;

/**
 * Created by zhengxiaoyong on 2018/10/04.
 */
public class CodeGenerateFactory {

    public static Pair<String, String> generate(Object[][] data) {
        if (data == null || data.length == 0)
            return null;

        if (ConfigCenter.getInstance().getLanguage() == Config.LANGUAGE.JAVA) {
            Config.VARIABLE variable = ConfigCenter.getInstance().getVariable();
            if (variable == Config.VARIABLE.MEMBER) {
                return Pair.of(generateJavaMemberVariable(data), generateJavaMemberFindViewById(data));
            } else if (variable == Config.VARIABLE.LOCAL) {
                return Pair.of("", generateJavaLocalFindViewById(data));
            }
        } else if (ConfigCenter.getInstance().getLanguage() == Config.LANGUAGE.KOTLIN) {
            Config.VARIABLE variable = ConfigCenter.getInstance().getVariable();
            if (variable == Config.VARIABLE.MEMBER) {
                if (ConfigCenter.getInstance().isKotlinLazy()) {
                    return Pair.of(generateKotlinLazyFindViewById(data, Config.VARIABLE.MEMBER), "");
                } else {
                    return Pair.of(generateKotlinMemberVariable(data), generateKotlinMemberFindViewById(data));
                }
            } else if (variable == Config.VARIABLE.LOCAL) {
                if (ConfigCenter.getInstance().isKotlinLazy()) {
                    return Pair.of("", generateKotlinLazyFindViewById(data, Config.VARIABLE.LOCAL));
                } else {
                    return Pair.of("", generateKotlinLocalFindViewById(data));
                }
            }
        }

        return null;
    }

    private static String generateJavaMemberVariable(Object[][] data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String name = (String) data[i][3];
            builder.append(generateJavaMemberVariable(type, name)).append("\n");
        }
        return builder.toString();
    }

    private static String generateJavaMemberFindViewById(Object[][] data) {
        StringBuilder builder = new StringBuilder();
        boolean isApi26 = ConfigCenter.getInstance().isApi26();
        String rootView = ConfigCenter.getInstance().getRootView();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String id = (String) data[i][2];
            String name = (String) data[i][3];
            if (isApi26) {
                builder.append(String.format(CodeStatements.JAVA_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26, name, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id)).append("\n");
            } else {
                builder.append(String.format(CodeStatements.JAVA_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26, name, type, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id)).append("\n");
            }
        }
        return builder.toString();
    }

    private static String generateJavaLocalFindViewById(Object[][] data) {
        StringBuilder builder = new StringBuilder();
        boolean isApi26 = ConfigCenter.getInstance().isApi26();
        String rootView = ConfigCenter.getInstance().getRootView();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String id = (String) data[i][2];
            String name = (String) data[i][3];
            if (isApi26) {
                builder.append(String.format(CodeStatements.JAVA_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26, type, name, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id)).append("\n");
            } else {
                builder.append(String.format(CodeStatements.JAVA_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26, type, name, type, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id)).append("\n");
            }
        }
        return builder.toString();
    }

    private static String generateJavaMemberVariable(String type, String name) {
        Config.MODIFIER modifier = ConfigCenter.getInstance().getModifier();
        StringBuilder builder = new StringBuilder();
        switch (modifier) {
            case PRIVATE:
                builder.append("private").append(" ");
                break;
            case DEFAULT:
                //do nothing.
                break;
            case PUBLIC:
                builder.append("public").append(" ");
                break;
            case PROTECT:
                builder.append("protected").append(" ");
                break;
            default:
                break;
        }

        builder.append(String.format(CodeStatements.JAVA_DECLARE_FIELD_EXPRESSION, type, name));
        return builder.toString();
    }

    private static String generateKotlinMemberVariable(Object[][] data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String name = (String) data[i][3];
            builder.append(generateKotlinMemberVariable(type, name)).append("\n");
        }
        return builder.toString();
    }

    private static String generateKotlinMemberVariable(String type, String name) {
        Config.MODIFIER modifier = ConfigCenter.getInstance().getModifier();
        StringBuilder builder = new StringBuilder();
        switch (modifier) {
            case PRIVATE:
                builder.append("private").append(" ");
                break;
            case DEFAULT:
                //do nothing.
                break;
            case PUBLIC:
                builder.append("internal").append(" ");
                break;
            case PROTECT:
                builder.append("protected").append(" ");
                break;
            default:
                break;
        }

        builder.append(String.format(CodeStatements.KOTLIN_DECLARE_FIELD_EXPRESSION, name, type));
        return builder.toString();
    }

    private static String generateKotlinMemberFindViewById(Object[][] data) {
        StringBuilder builder = new StringBuilder();
        boolean isApi26 = ConfigCenter.getInstance().isApi26();
        String rootView = ConfigCenter.getInstance().getRootView();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String id = (String) data[i][2];
            String name = (String) data[i][3];
            if (isApi26) {
                builder.append(String.format(CodeStatements.KOTLIN_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26, name, StringUtil.isEmpty(rootView) ? "" : rootView + ".", type, id)).append("\n");
            } else {
                builder.append(String.format(CodeStatements.KOTLIN_ASSIGN_VARIABLE_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26, name, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id, type)).append("\n");
            }
        }
        return builder.toString();
    }

    private static String generateKotlinLocalFindViewById(Object[][] data) {
        StringBuilder builder = new StringBuilder();
        boolean isApi26 = ConfigCenter.getInstance().isApi26();
        String rootView = ConfigCenter.getInstance().getRootView();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String id = (String) data[i][2];
            String name = (String) data[i][3];
            if (isApi26) {
                builder.append(String.format(CodeStatements.KOTLIN_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26, name, StringUtil.isEmpty(rootView) ? "" : rootView + ".", type, id)).append("\n");
            } else {
                builder.append(String.format(CodeStatements.KOTLIN_DECLARE_LOCAL_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26, name, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id, type)).append("\n");
            }
        }
        return builder.toString();
    }

    private static String generateKotlinLazyFindViewById(Object[][] data, Config.VARIABLE variable) {
        StringBuilder builder = new StringBuilder();
        boolean isApi26 = ConfigCenter.getInstance().isApi26();
        String rootView = ConfigCenter.getInstance().getRootView();
        Config.MODIFIER modifier = ConfigCenter.getInstance().getModifier();
        Config.LazyThreadSafetyMode safetyMode = ConfigCenter.getInstance().getSafetyMode();
        for (int i = 0; i < data.length; i++) {
            boolean selected = (boolean) data[i][0];
            if (!selected)
                continue;
            String type = (String) data[i][1];
            String id = (String) data[i][2];
            String name = (String) data[i][3];
            if (variable == Config.VARIABLE.MEMBER) {
                switch (modifier) {
                    case PRIVATE:
                        builder.append("private").append(" ");
                        break;
                    case DEFAULT:
                        //do nothing.
                        break;
                    case PUBLIC:
                        builder.append("internal").append(" ");
                        break;
                    case PROTECT:
                        builder.append("protected").append(" ");
                        break;
                    default:
                        break;
                }
            }

            switch (safetyMode) {
                case NONE:
                    builder.append(String.format(CodeStatements.KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_VARIABLE_LAZY, name));
                    break;
                case SYNCHRONIZED:
                    builder.append(String.format(CodeStatements.KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_VARIABLE_LAZY_SYNCHRONIZED, name));
                    break;
                default:
                    break;
            }

            if (isApi26) {
                builder.append(String.format(CodeStatements.KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_AFTER_API26, StringUtil.isEmpty(rootView) ? "" : rootView + ".", type, id)).append("\n");
            } else {
                builder.append(String.format(CodeStatements.KOTLIN_FOR_FINDVIEWBYID_EXPRESSION_BEFORE_API26, StringUtil.isEmpty(rootView) ? "" : rootView + ".", id, type)).append("\n");
            }
        }
        return builder.toString();
    }

}
