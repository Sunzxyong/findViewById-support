package com.zxy.idea.plugin.fvbi.support;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * Created by zhengxiaoyong on 2018/10/04.
 */
public class Tools {

    public static String getViewId(String value) {
        //@+id/line
        if (value == null || value.length() == 0)
            return null;
        if (!value.startsWith("@+id/") && !value.startsWith("@id/"))
            return null;
        String[] result = value.split("/");
        if (result.length != 2)
            return null;
        return result[1];
    }

    public static boolean canFindLayoutResourceElement(PsiFile file, Editor editor) {
        int offset = editor.getCaretModel().getOffset();

        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);

        PsiElement element = findLayoutResourceElement(candidateA);
        if (element == null)
            element = findLayoutResourceElement(candidateB);

        return element != null;
    }

    public static PsiFile getLayoutFileFromCaret(PsiFile file, Editor editor) {
        int offset = editor.getCaretModel().getOffset();

        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);

        PsiFile layout = findLayoutResource(candidateA);
        if (layout != null) {
            return layout;
        }
        return findLayoutResource(candidateB);
    }

    private static PsiElement findLayoutResourceElement(PsiElement element) {
        if (element == null)
            return null;

        PsiElement layout;
        if (ConfigCenter.getInstance().getFileType() == FileType.KOTLIN) {
            //element.getParent().getParent(): R.layout.activity_main
            layout = element.getParent().getParent().getFirstChild();
            if (layout == null) {
                return null; // no file to process
            }
        } else {
            //element.getParent(): R.layout.activity_main
            layout = element.getParent().getFirstChild();
            if (layout == null) {
                return null; // no file to process
            }
        }

        if (!"R.layout".equals(layout.getText())) {
            return null; // not layout file
        }

        return layout;
    }

    private static PsiFile findLayoutResource(PsiElement element) {
        if (element == null) {
            return null; // nothing to be used
        }

        PsiElement layout;
        if (ConfigCenter.getInstance().getFileType() == FileType.KOTLIN) {
            //element.getParent().getParent(): R.layout.activity_main
            layout = element.getParent().getParent().getFirstChild();
            if (layout == null) {
                return null; // no file to process
            }
        } else {
            //element.getParent(): R.layout.activity_main
            layout = element.getParent().getFirstChild();
            if (layout == null) {
                return null; // no file to process
            }
        }

        if (!"R.layout".equals(layout.getText())) {
            return null; // not layout file
        }

        String name = String.format("%s.xml", element.getText());
        return resolveLayoutResourceFile(element, name);
    }

    public static PsiFile resolveLayoutResourceFile(PsiElement element, String layoutName) {
        if (element == null || layoutName == null)
            return null;

        Project project = element.getProject();

        Module module = ModuleUtil.findModuleForPsiElement(element);
        PsiFile[] files = null;
        if (module != null) {
            GlobalSearchScope moduleScope = module.getModuleWithDependenciesAndLibrariesScope(false);
            files = FilenameIndex.getFilesByName(project, layoutName, moduleScope);
        }

        if (files == null || files.length == 0) {
            files = FilenameIndex.getFilesByName(project, layoutName, new EverythingGlobalScope(project));
        }

        if (files == null || files.length == 0) {
            return null; //no matching files
        }

        return files[0];
    }

    public static String getLayoutName(String layout) {
        if (layout == null || !layout.startsWith("@layout/")) {
            return null; // it's not layout identifier
        }

        String[] parts = layout.split("/");
        if (parts.length != 2) {
            return null; // not enough parts
        }

        return parts[1];
    }

}