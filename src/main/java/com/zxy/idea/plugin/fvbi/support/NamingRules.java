package com.zxy.idea.plugin.fvbi.support;

import com.intellij.openapi.util.text.StringUtil;

/**
 * Created by zhengxiaoyong on 2018/10/05.
 */
public class NamingRules {

    public static String forName(String id) {
        if (StringUtil.isEmpty(id))
            return "";
        boolean isAddPrefixM = ConfigCenter.getInstance().isAddPrefixM();

        String name;
        if (isAddPrefixM) {
            name = "m" + StringUtil.capitalize(transform(id));
        } else {
            name = transform(id);
        }

        return name;
    }

    private static String transform(String id) {
        if (id.contains("_")) {
            String[] values = id.split("_");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (StringUtil.isEmpty(value))
                    continue;
                if (i == 0) {
                    builder.append(value);
                } else {
                    builder.append(StringUtil.capitalize(value));
                }
            }
            return builder.toString();
        }
        return id;
    }

}
