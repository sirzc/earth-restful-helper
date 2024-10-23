package com.myth.earth.restful.plugin.action;

import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.navigation.ChooseByNameRegistry;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.myth.earth.restful.plugin.search.ApiSearchEverywhereContributor;
import org.jetbrains.annotations.NotNull;

/**
 * api search 搜索
 *
 * @author zhouchao
 * @date 2024-06-01 下午4:49
 */
public class ApiSearchAction extends SearchEverywhereBaseAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        boolean dumb = DumbService.isDumb(project);
        if (!dumb || new ApiSearchEverywhereContributor(e).isDumbAware()) {
            String tabID = ApiSearchEverywhereContributor.class.getSimpleName();
            showInSearchEverywherePopup(tabID, e, true, true);
        }
    }

    @Override
    protected boolean hasContributors(@NotNull DataContext dataContext) {
        return !ChooseByNameRegistry.getInstance().getSymbolModelContributors().isEmpty();
    }
}