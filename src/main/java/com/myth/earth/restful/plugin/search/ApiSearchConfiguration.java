package com.myth.earth.restful.plugin.search;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.myth.earth.restful.enums.HttpMethod;
import org.jetbrains.annotations.NotNull;


@State(name = "ApiSearchConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class ApiSearchConfiguration extends ChooseByNameFilterConfiguration<HttpMethod> {

    public static ApiSearchConfiguration getInstance(@NotNull Project project) {
        return project.getService(ApiSearchConfiguration.class);
    }

    @Override
    protected String nameForElement(HttpMethod type) {
        return type.name();
    }
}
