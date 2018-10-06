package com.zxy.idea.plugin.fvbi.support;

import java.util.Objects;

/**
 * Created by zhengxiaoyong on 2018/09/30.
 */
public class ViewInfo {

    public String type;

    public String id;

    public boolean selected = true;

    public static ViewInfo create() {
        return new ViewInfo();
    }

    public ViewInfo type(String type) {
        this.type = type;
        return this;
    }

    public ViewInfo id(String id) {
        this.id = id;
        return this;
    }

    public ViewInfo selected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public static String forShortType(String type) {
        int index = type.lastIndexOf(".");
        if (index == -1)
            return type;
        return type.substring(index + 1, type.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewInfo info = (ViewInfo) o;
        return Objects.equals(id, info.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
