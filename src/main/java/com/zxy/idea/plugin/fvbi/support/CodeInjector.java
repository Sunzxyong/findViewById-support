package com.zxy.idea.plugin.fvbi.support;

import com.android.utils.Pair;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * Created by zhengxiaoyong on 2018/10/05.
 */
public class CodeInjector extends WriteCommandAction.Simple {

    private Object[][] mTableData;

    private String mClassName;

    private String mMethodName;

    private Interceptor mInterceptor;

    public CodeInjector(Project project, Object[][] data, String className, String methodName) {
        super(project);
        mTableData = data;
        mClassName = className;
        mMethodName = methodName;
    }

    protected CodeInjector(Project project, PsiFile... files) {
        super(project, files);
    }

    protected CodeInjector(Project project, String commandName, PsiFile... files) {
        super(project, commandName, files);
    }

    protected CodeInjector(Project project, String name, String groupID, PsiFile... files) {
        super(project, name, groupID, files);
    }

    public CodeInjector setInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    @Override
    protected void run() throws Throwable {
        if (mInterceptor != null)
            mInterceptor.beforeExecute();

        if (StringUtil.isEmpty(mClassName) || StringUtil.isEmpty(mMethodName) || mTableData == null || mTableData.length == 0)
            return;

        PsiClass insertClass = ClassInfoManager.getInstance().findClassFromTable(mClassName);
        if (insertClass == null)
            return;

        PsiMethod[] methods = insertClass.findMethodsByName(mMethodName, false);
        if (methods.length == 0)
            return;

        PsiMethod insertMethod = methods[0];
        if (insertMethod == null)
            return;

        Pair<String, String> pair = CodeGenerateFactory.generate(mTableData);
        if (pair == null)
            return;

        if (!StringUtil.isEmpty(pair.getFirst())) {
            insertField(insertClass, pair.getFirst());
        }

        if (!StringUtil.isEmpty(pair.getSecond())) {
            insertMethod(insertMethod, pair.getSecond());
        }

        formatCode(insertClass);

        if (mInterceptor != null)
            mInterceptor.afterExecute();
    }

    private void insertField(PsiClass clazz, String code) {
        if (clazz == null)
            return;
        String[] statements = code.split("\n");
        for (int i = 0; i < statements.length; i++) {
            if (StringUtil.isEmpty(statements[i]))
                continue;
            try {
                clazz.add(JavaPsiFacade.getElementFactory(getProject()).createFieldFromText(statements[i], null));
            } catch (Exception e) {
                //ignore.
            }
        }
    }

    private void insertMethod(PsiMethod method, String code) {
        if (method == null)
            return;
        PsiCodeBlock codeBlock = method.getBody();
        if (codeBlock == null)
            return;
        String[] statements = code.split("\n");
        for (int i = 0; i < statements.length; i++) {
            if (StringUtil.isEmpty(statements[i]))
                continue;
            try {
                codeBlock.add(JavaPsiFacade.getElementFactory(getProject()).createStatementFromText(statements[i], null));
            } catch (Exception e) {
                //ignore.
            }
        }
    }

    private void formatCode(PsiClass clazz) {
        JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(getProject());
        codeStyleManager.optimizeImports(clazz.getContainingFile());
        codeStyleManager.shortenClassReferences(clazz);
        new ReformatCodeProcessor(getProject(), clazz.getContainingFile(), null, false).runWithoutProgress();
    }

    public interface Interceptor {
        void beforeExecute();

        void afterExecute();
    }

}
