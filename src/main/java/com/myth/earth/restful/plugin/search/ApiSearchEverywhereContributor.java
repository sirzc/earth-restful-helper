package com.myth.earth.restful.plugin.search;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.SearchEverywherePsiRenderer;
import com.intellij.ide.actions.searcheverywhere.*;
import com.intellij.idea.ActionsBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.Processor;
import com.intellij.util.PsiNavigateUtil;
import com.intellij.util.TextWithIcon;
import com.intellij.util.ui.UIUtil;
import com.myth.earth.restful.core.analyze.processor.ApiSearchRefConvertProcessor;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.model.ApiSearchInfoRef;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApiSearchEverywhereContributor implements WeightedSearchEverywhereContributor<ApiSearchInfoRef> {

    private final AnActionEvent                                           actionEvent;
    private final Project                                                 myProject;
    private final PersistentSearchEverywhereContributorFilter<HttpMethod> myFilter;
    private       List<ApiSearchInfoRef>                                  apiSearchInfoRefs;

    public ApiSearchEverywhereContributor(@NotNull AnActionEvent event) {
        this.actionEvent = event;
        myProject = event.getRequiredData(CommonDataKeys.PROJECT);
        myFilter = createRestfulApiFilter(myProject);
        // apiSearchInfoRefs = ApiSearchRefConvertProcessor.getInstance(myProject).buildApiSearchInfoRefs();
    }

    @NotNull
    @Override
    public String getSearchProviderId() {
        return getClass().getSimpleName();
    }

    @NotNull
    @Override
    public String getGroupName() {
        return "Url";
    }

    @Override
    public int getSortWeight() {
        return 800;
    }

    @Nullable
    @Override
    public String getAdvertisement() {
        return DumbService.isDumb(myProject) ? IdeBundle.message("dumb.mode.results.might.be.incomplete") : null;
    }

    @NotNull
    @Override
    public List<AnAction> getActions(@NotNull Runnable onChanged) {
        return Collections.singletonList(new SearchEverywhereFiltersAction<>(myFilter, onChanged));
    }

    @Override
    public boolean processSelectedItem(@NotNull ApiSearchInfoRef restfulApiRef, int i, @NotNull String s) {
        PsiNavigateUtil.navigate(restfulApiRef.getPsiMethod());
        return true;
    }

    @NotNull
    @Override
    public ListCellRenderer<Object> getElementsRenderer() {
        return new SearchEverywherePsiRenderer(this) {

            @Override
            protected boolean customizeNonPsiElementLeftRenderer(ColoredListCellRenderer renderer, JList list, Object value, int index, boolean selected,
                                                                 boolean hasFocus) {
                Color fgColor = list.getForeground();
                Color bgColor = UIUtil.getListBackground();
                TextAttributes attributes = getNavigationItemAttributes(value);
                SimpleTextAttributes nameAttributes = attributes != null ? SimpleTextAttributes.fromTextAttributes(attributes) : null;
                if (nameAttributes == null) {
                    nameAttributes = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor);
                }

                ItemMatchers itemMatchers = getItemMatchers(list, value);
                ApiSearchInfoRef apiSearchInfoRef = (ApiSearchInfoRef) value;
                HttpMethod httpMethod = apiSearchInfoRef.getHttpMethod();
                String name = apiSearchInfoRef.getApiPath() + " ";
                String locationString = apiSearchInfoRef.getDescription();

                // renderer.append(name, nameAttributes);
                SpeedSearchUtil.appendColoredFragmentForMatcher(name, renderer, nameAttributes, itemMatchers.nameMatcher, bgColor, selected);
                renderer.setIcon(httpMethod.getIcon());

                if (StringUtils.isNotEmpty(locationString)) {
                    locationString = "(" + locationString + ")";
                    FontMetrics fm = list.getFontMetrics(list.getFont());
                    int maxWidth = list.getWidth() - fm.stringWidth(name) - myRightComponentWidth - 36;
                    int fullWidth = fm.stringWidth(locationString);
                    if (fullWidth < maxWidth) {
                        // renderer.append(locationString, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
                        SpeedSearchUtil.appendColoredFragmentForMatcher(locationString, renderer, SimpleTextAttributes.GRAYED_ATTRIBUTES,
                                                                        itemMatchers.nameMatcher, bgColor, selected);
                    } else {
                        int adjustedWidth = Math.max(locationString.length() * maxWidth / fullWidth - 1, 3);
                        locationString = StringUtil.trimMiddle(locationString, adjustedWidth);
                        SpeedSearchUtil.appendColoredFragmentForMatcher(locationString, renderer, SimpleTextAttributes.GRAYED_ATTRIBUTES,
                                                                        itemMatchers.nameMatcher, bgColor, selected);
                    }
                }
                return true;
            }

            @Nullable
            @Override
            protected TextWithIcon getItemLocation(Object value) {
                if (value instanceof ApiSearchInfoRef) {
                    return new TextWithIcon(((ApiSearchInfoRef) value).getClassMethodPath(), AllIcons.Nodes.Method);
                }
                return super.getItemLocation(value);
            }
        };
    }

    @Nullable
    @Override
    public Object getDataForItem(@NotNull ApiSearchInfoRef element, @NotNull String dataId) {
        return null;
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return true;
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean isDumbAware() {
        return DumbService.isDumb(myProject);
    }

    @Override
    public void fetchWeightedElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator,
                                      @NotNull Processor<? super FoundItemDescriptor<ApiSearchInfoRef>> consumer) {
        if (isDumbAware() || !shouldProvideElements(pattern)) {
            return;
        }

        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern + "*", NameUtil.MatchingCaseSensitivity.NONE);
        List<HttpMethod> selectedElements = myFilter.getSelectedElements();
        // 从ALL -> URL Tab或快捷键进入时列表为空
        if (apiSearchInfoRefs == null) {
            // 必须从read线程访问，耗时不能过长
            ApplicationManager.getApplication().runReadAction(() -> {
                apiSearchInfoRefs = ApiSearchRefConvertProcessor.getInstance(myProject).buildApiSearchInfoRefs();
            });
        }

        if (apiSearchInfoRefs != null) {
            for (ApiSearchInfoRef apiSearchInfoRef : apiSearchInfoRefs) {
                if (selectedElements.contains(apiSearchInfoRef.getHttpMethod())) {
                    if (matcher.matches(apiSearchInfoRef.getApiPath()) || matcher.matches(apiSearchInfoRef.getDescription())) {
                        ApplicationManager.getApplication().runReadAction(() -> {
                            if (!consumer.process(new FoundItemDescriptor<>(apiSearchInfoRef, 0))) {
                                return;
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 判断是否应该返回列表元素
     *
     * @param pattern 搜索词
     */
    private boolean shouldProvideElements(String pattern) {
        boolean shouldProvideElements = true;
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(myProject);
        if (seManager.isShown()) {
            // 非URL Tab, 也只有ALL Tab
            if (!getSearchProviderId().equals(seManager.getSelectedTabID())) {
                if (StringUtils.isEmpty(StringUtils.trimToNull(pattern))) {
                    shouldProvideElements = false;
                }
            }
        } else {
            // ALL Tab
            if (ActionsBundle.message("action.SearchEverywhere.text").equals(actionEvent.getPresentation().getText())) {
                if (StringUtils.isEmpty(StringUtils.trimToNull(pattern))) {
                    shouldProvideElements = false;
                }
            }
        }
        return shouldProvideElements;
    }

    public static class Factory implements SearchEverywhereContributorFactory<ApiSearchInfoRef> {
        @NotNull
        @Override
        public SearchEverywhereContributor<ApiSearchInfoRef> createContributor(@NotNull AnActionEvent initEvent) {
            return new ApiSearchEverywhereContributor(initEvent);
        }
    }

    private PersistentSearchEverywhereContributorFilter<HttpMethod> createRestfulApiFilter(Project project) {
        return new PersistentSearchEverywhereContributorFilter<>(Arrays.asList(HttpMethod.values()), ApiSearchConfiguration.getInstance(project),
                                                                 HttpMethod::name, HttpMethod::getIcon);
    }

}
