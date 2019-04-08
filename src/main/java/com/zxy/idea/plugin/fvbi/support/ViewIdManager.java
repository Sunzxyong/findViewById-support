package com.zxy.idea.plugin.fvbi.support;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.util.*;

/**
 * Created by zhengxiaoyong on 2018/09/30.
 */
public class ViewIdManager {

    private volatile static ViewIdManager sInstance;

    /**
     * layout resources filename.
     */
    private Set<String> mLayoutFileNames = new HashSet<>();

    /**
     * K:id,V:ViewInfo.
     */
    private Map<String, ViewInfo> mViewIdMappingTable = new LinkedHashMap<>();

    private ViewIdManager() {
    }

    public static ViewIdManager getInstance() {
        if (sInstance == null) {
            synchronized (ViewIdManager.class) {
                if (sInstance == null) {
                    sInstance = new ViewIdManager();
                }
            }
        }
        return sInstance;
    }

    public Set<String> getLayoutFileNames() {
        return mLayoutFileNames;
    }

    public Map<String, ViewInfo> getViewIdMappingTable() {
        return mViewIdMappingTable;
    }

    public List<ViewInfo> getViewIdInfoAsList() {
        List<ViewInfo> infoList = new ArrayList<>();
        for (Map.Entry<String, ViewInfo> entry : mViewIdMappingTable.entrySet()) {
            ViewInfo info = entry.getValue();
            if (info == null || StringUtil.isEmpty(info.id) || StringUtil.isEmpty(info.type))
                continue;
            infoList.add(info);
        }
        return infoList;
    }

    public void clear() {
        mLayoutFileNames.clear();
        mViewIdMappingTable.clear();
    }

    public void collectViewId(PsiFile layoutFile) {
        clear();
        searchViewId(layoutFile);
    }

    private void searchViewId(final PsiFile layoutFile) {
        if (layoutFile == null)
            return;
        mLayoutFileNames.add(layoutFile.getName());
        layoutFile.accept(new XmlRecursiveElementVisitor() {

            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);

                if (!(element instanceof XmlTag))
                    return;

                XmlTag tag = (XmlTag) element;

                if (filterTag(tag.getName()))
                    return;

                if ("include".equals(tag.getName())) {
                    XmlAttribute layoutAttribute = tag.getAttribute("layout");

                    if (layoutAttribute != null) {
                        PsiFile includeFile = Tools.resolveLayoutResourceFile(layoutAttribute.getValueElement(), String.format("%s.xml", Tools.getLayoutName(layoutAttribute.getValue())));
                        if (includeFile != null)
                            searchViewId(includeFile);
                    }
                } else {
                    XmlAttribute idAttribute = tag.getAttribute("android:id");
                    if (idAttribute == null)
                        return; // missing android:id attribute

                    String value = idAttribute.getValue();
                    if (value == null || value.length() == 0)
                        return; // empty value

                    String name = tag.getName();

                    XmlAttribute clazz = tag.getAttribute("class");
                    if (clazz != null) {
                        name = clazz.getValue();
                    }

                    String id = Tools.getViewId(value);
                    if (id == null || id.length() == 0 || name == null || name.length() == 0)
                        return;

                    ViewInfo viewInfo = ViewInfo.create().type(name).id(id).selected(true);

                    mViewIdMappingTable.put(id, viewInfo);
                }
            }
        });
    }

    private boolean filterTag(String name) {
        return "view".equals(name) || "tag".equals(name) || "requestFocus".equals(name);
    }

}
