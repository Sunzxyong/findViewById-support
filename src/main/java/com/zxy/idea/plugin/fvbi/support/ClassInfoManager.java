package com.zxy.idea.plugin.fvbi.support;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.*;

/**
 * Created by zhengxiaoyong on 2018/10/04.
 */
public class ClassInfoManager {

    private volatile static ClassInfoManager sInstance;

    /**
     * key:class,v:methods.
     */
    private Map<String, List<String>> mClassMethodMappingTable = new HashMap<>();

    /**
     * key:class,v:obj.
     */
    private Map<String, PsiClass> mClassObjectMappingTable = new HashMap<>();

    /**
     * class name.
     */
    private List<String> mClassNames = new ArrayList<>();

    private ClassInfoManager() {
    }

    public static ClassInfoManager getInstance() {
        if (sInstance == null) {
            synchronized (ClassInfoManager.class) {
                if (sInstance == null) {
                    sInstance = new ClassInfoManager();
                }
            }
        }
        return sInstance;
    }

    public List<String> findMethodFromTable(String className) {
        if (StringUtil.isEmpty(className))
            return new ArrayList<>();
        return mClassMethodMappingTable.get(className);
    }

    public List<String> getAllClassNames() {
        return new ArrayList<>(mClassNames);
    }

    public PsiClass findClassFromTable(String className) {
        if (StringUtil.isEmpty(className))
            return null;
        return mClassObjectMappingTable.get(className);
    }

    public void clear() {
        mClassObjectMappingTable.clear();
        mClassMethodMappingTable.clear();
        mClassNames.clear();
    }

    public void collectClassInfo(PsiClass clazz) {
        clear();

        if (clazz == null)
            return;

        findInnerClassRecursive(clazz);

        for (Map.Entry<String, PsiClass> entry : mClassObjectMappingTable.entrySet()) {
            String className = entry.getKey();
            PsiClass classObj = entry.getValue();
            if (StringUtil.isEmpty(className) || classObj == null)
                continue;
            mClassMethodMappingTable.put(className, parseMethodFromClass(classObj));
        }
    }

    private void findInnerClassRecursive(PsiClass clazz) {
        if (filterClass(clazz))
            return;
        mClassNames.add(clazz.getName());
        mClassObjectMappingTable.put(clazz.getName(), clazz);

        PsiClass[] innerClasses = clazz.getInnerClasses();
        for (int i = 0; i < innerClasses.length; i++) {
            PsiClass innerClass = innerClasses[i];
            findInnerClassRecursive(innerClass);
        }
    }

    private boolean filterClass(PsiClass clazz) {
        return clazz == null || clazz.isInterface() || clazz.isEnum() || clazz.isAnnotationType() || clazz.hasModifierProperty("abstract") || clazz.getMethods().length == 0;
    }

    private List<String> parseMethodFromClass(PsiClass clazz) {
        PsiMethod[] methods = clazz.getMethods();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            PsiMethod method = methods[i];
            if (method == null)
                continue;
            list.add(method.getName());
        }
        return list;
    }

}
