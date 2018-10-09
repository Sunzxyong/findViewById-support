package com.zxy.idea.plugin.fvbi.support;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.awt.*;

/**
 * Created by zhengxiaoyong on 2018/09/30.
 */
public class FindViewByIdGenerateAction extends BaseGenerateAction {

    public FindViewByIdGenerateAction() {
        super(null);
    }

    public FindViewByIdGenerateAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        if (!enabledAndVisible(event))
            return;

        assert psiFile != null;
        assert editor != null;
        PsiFile layoutFile = Tools.getLayoutFileFromCaret(psiFile, editor);

        ClassInfoManager.getInstance().collectClassInfo(getPsiClass(editor, psiFile));

        ViewIdManager.getInstance().collectViewId(layoutFile);

        FindViewByIdGenerateDialog dialog = new FindViewByIdGenerateDialog(project);
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        Dimension frameSize = dialog.getPreferredSize();
//        if (frameSize.height > screenSize.height)
//            frameSize.height = screenSize.height;
//        if (frameSize.width > screenSize.width)
//            frameSize.width = screenSize.width;
//        dialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        dialog.pack();
        dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
        dialog.setVisible(true);
    }

    @Override
    public void update(AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(enabledAndVisible(event));
    }

    private boolean enabledAndVisible(AnActionEvent event) {
        Project project = event.getProject();
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        if (project == null || psiFile == null || editor == null)
            return false;

        String fileName = psiFile.getVirtualFile().getName();

        ConfigCenter.getInstance().setFileType(null);
        if (fileName.endsWith(".java")) {
            ConfigCenter.getInstance().setFileType(FileType.JAVA);
        } else if (fileName.endsWith(".kt")) {
            ConfigCenter.getInstance().setFileType(FileType.KOTLIN);
        }

        return (fileName.endsWith(".java") || fileName.endsWith(".kt")) && Tools.canFindLayoutResourceElement(psiFile, editor);
    }

    private PsiClass getPsiClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();

        PsiElement element = file.findElementAt(offset);

        if (element == null)
            element = file.findElementAt(offset - 1);

        if (element == null) {
            return null;
        } else {
            PsiClass target = (PsiClass) PsiTreeUtil.getParentOfType(element, PsiClass.class);
            if (target == null) {
                element = file.findElementAt(offset - 1);
                if (element == null)
                    return null;
                target = (PsiClass) PsiTreeUtil.getParentOfType(element, PsiClass.class);
            }
            return target instanceof SyntheticElement ? null : target;
        }
    }

}
