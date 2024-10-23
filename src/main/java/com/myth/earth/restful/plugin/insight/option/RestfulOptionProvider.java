package com.myth.earth.restful.plugin.insight.option;

import com.intellij.codeInsight.hints.*;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.codeInsight.hints.presentation.SequencePresentation;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.SmartList;
import com.myth.earth.restful.core.analyze.processor.ApiSearchRefConvertProcessor;
import com.myth.earth.restful.helper.RestfulOptionHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 行提示扩展
 *
 * @author zhouchao
 * @date 2024-06-19 下午11:44
 */
@SuppressWarnings("all")
public class RestfulOptionProvider implements InlayHintsProvider<RestfulOptionSetting> {

    public static final  String                            RESTFUL_LENS_ID = "EarthRestfulHelperLens";
    private static final SettingsKey<RestfulOptionSetting> KEY             = new SettingsKey<>(RESTFUL_LENS_ID);

    interface InlResult {
        void onClick(@NotNull Editor editor, @NotNull PsiElement element, @NotNull MouseEvent event);

        @NotNull
        String getRegularText();

        static InlResult getInstance(String regularText, RestfulOptionEnum docViewOption) {
            return new InlResult() {
                @Override
                public void onClick(@NotNull Editor editor, @NotNull PsiElement element, @NotNull MouseEvent event) {
                    Project project = editor.getProject();
                    if (project == null) {
                        return;
                    }
                    // 类: 上传openapi
                    if (element instanceof PsiClass) {
                        PsiClass psiClass = (PsiClass) element;
                        RestfulOptionHelper.uploadOpenapi(project, psiClass);
                        return;
                    }
                    // 方法: 1. 上传openapi 2. 生成markdown 3. 复制curl
                    PsiMethod psiMethod = (PsiMethod) element;
                    switch (docViewOption) {
                        case OPENAPI:
                            RestfulOptionHelper.uploadOpenapi(project, psiMethod);
                            break;
                        case MARKDOWN:
                            RestfulOptionHelper.generateMarkdown(project, psiMethod);
                            break;
                        case CURL:
                            RestfulOptionHelper.copyCurl(project, psiMethod);
                            break;
                        default:
                            break;
                    }
                }

                @NotNull
                @Override
                public String getRegularText() {
                    return regularText;
                }
            };
        }
    }

    @Nullable
    @Override
    public InlayHintsCollector getCollectorFor(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull RestfulOptionSetting settings,
                                               @NotNull InlayHintsSink inlayHintsSink) {
        return new FactoryInlayHintsCollector(editor) {
            @Override
            public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
                Project project = editor.getProject();
                if (project == null) {
                    return true;
                }

                if (!(element instanceof PsiMember) || element instanceof PsiTypeParameter) {
                    return true;
                }

                PsiElement prevSibling = element.getPrevSibling();
                if (!(prevSibling instanceof PsiWhiteSpace && prevSibling.textContains('\n'))) {
                    return true;
                }

                PsiMember member = (PsiMember) element;
                if (member.getName() == null) {
                    return true;
                }

                ApiSearchRefConvertProcessor processor = ApiSearchRefConvertProcessor.getInstance(project);
                List<InlResult> hints = new SmartList<>();
                if (settings.isShowUploadOpenapi() && element instanceof PsiClass && processor.supportClass((PsiClass) element)) {
                    hints.add(InlResult.getInstance("导出Openapi", RestfulOptionEnum.OPENAPI));
                }

                if (element instanceof PsiMethod && processor.supportMethod((PsiMethod) element)) {
                    if (settings.isShowUploadOpenapi()) {
                        hints.add(InlResult.getInstance("导出Openapi", RestfulOptionEnum.OPENAPI));
                    }

                    if (settings.isShowCopyCurl()) {
                        hints.add(InlResult.getInstance("复制Curl", RestfulOptionEnum.CURL));
                    }

                    if (settings.isShowExportMarkdown()) {
                        hints.add(InlResult.getInstance("生成Markdown", RestfulOptionEnum.MARKDOWN));
                    }
                }

                if (!hints.isEmpty()) {
                    PresentationFactory factory = getFactory();
                    Document document = editor.getDocument();
                    int offset = getAnchorOffset(element);
                    int line = document.getLineNumber(offset);
                    int startOffset = document.getLineStartOffset(line);
                    int column = offset - startOffset;
                    List<InlayPresentation> presentations = new SmartList<>();
                    presentations.add(factory.textSpacePlaceholder(column, true));
                    for (InlResult inlResult : hints) {
                        presentations.add(createPresentation(factory, element, editor, inlResult));
                        presentations.add(factory.textSpacePlaceholder(1, true));
                    }
                    SequencePresentation shiftedPresentation = new SequencePresentation(presentations);
                    sink.addBlockElement(startOffset, true, true, 300, shiftedPresentation);
                }
                return true;
            }
        };
    }

    private static boolean isDefaultMethod(@NotNull PsiClass aClass, @NotNull PsiMethod method) {
        return method.hasModifierProperty(PsiModifier.DEFAULT) && PsiUtil.getLanguageLevel(aClass).isAtLeast(LanguageLevel.JDK_1_8);
    }

    private static int getAnchorOffset(@NotNull PsiElement element) {
        for (PsiElement child : element.getChildren()) {
            if (!(child instanceof PsiComment) && !(child instanceof PsiWhiteSpace)) {
                return child.getTextRange().getStartOffset();
            }
        }
        return element.getTextRange().getStartOffset();
    }

    @NotNull
    private static InlayPresentation createPresentation(@NotNull PresentationFactory factory, @NotNull PsiElement element, @NotNull Editor editor,
                                                        @NotNull InlResult result) {
        InlayPresentation text = factory.smallTextWithoutBackground(result.getRegularText());
        SmartPsiElementPointer<PsiElement> pointer = SmartPointerManager.createPointer(element);
        return factory.referenceOnHover(text, (event, translated) -> {
            PsiElement actual = pointer.getElement();
            if (actual != null) {
                result.onClick(editor, actual, event);
            }
        });
    }

    @NotNull
    @Override
    public RestfulOptionSetting createSettings() {
        return new RestfulOptionSetting();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Restful option";
    }

    @NotNull
    @Override
    public SettingsKey<RestfulOptionSetting> getKey() {
        return KEY;
    }

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @NotNull
    @Override
    public ImmediateConfigurable createConfigurable(@NotNull RestfulOptionSetting settings) {
        return new RestfulOptionConfigurable(settings);
    }

    @Override
    public boolean isLanguageSupported(@NotNull Language language) {
        return true;
    }

    @Override
    public boolean isVisibleInSettings() {
        return true;
    }

    public static enum RestfulOptionEnum {
        OPENAPI, CURL, MARKDOWN
    }
}

