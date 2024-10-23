package com.myth.earth.restful.core.psi;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.myth.earth.restful.consts.JaxRsPackageConst;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.enums.JaxRsHttpMethod;
import com.myth.earth.restful.kits.PsiKit;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.utils.DataExtractUtil;
import com.myth.earth.restful.utils.ParamInfoPsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * jax-rs psi 工具类
 *
 * @author changan
 * @date 2023-04-28 9:06
 */
public class JaxRsPsiUtil {

    /**
     * 获取请求类型
     *
     * @param psiMethod 方法信息
     * @return 请求类型
     */
    @NotNull
    public static HttpMethod getMethodType(@NotNull PsiMethod psiMethod) {
        List<PsiAnnotation> methodAnnotations = PsiKit.getMethodAnnotations(psiMethod);
        for (PsiAnnotation methodAnnotation : methodAnnotations) {
            String qualifiedName = methodAnnotation.getQualifiedName();
            JaxRsHttpMethod httpMethod = JaxRsHttpMethod.getMethodByQualifiedName(qualifiedName);
            if (Objects.nonNull(httpMethod)) {
                return httpMethod.getMethod();
            }
        }
        return HttpMethod.REQUEST;
    }

    /**
     * 获取方法入参数据 <br>
     * <ol>
     * <li>表单参数：javax.ws.rs.QueryParam</li>
     * <li>Form参数：javax.ws.rs.FormParam</li>
     * <li>非表单、json 类型为一个对象，非基本数据类型</li>
     * <li>非表单、text，没有javax.ws.rs.QueryParam注解</li>
     * </ol>
     *
     * @param psiMethod 方法信息
     * @return 接口参数内容
     */
    @NotNull
    public static List<ParamInfo> analyzeInputParam(@NotNull PsiMethod psiMethod) {
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        List<ParamInfo> paramInfos = Lists.newArrayListWithCapacity(psiParameters.length);
        // 获取方法上的
        for (PsiParameter psiParameter : psiParameters) {
            if (DataExtractUtil.isExcludeParameter(psiParameter)) {
                continue;
            }
            ParamInfo paramInfo = ParamInfoPsiUtil.buildInputParam(psiParameter);
            // 将QueryParam注解的value作为参数名
            Optional.ofNullable(PsiKit.getPropertyFromAnnotation(psiParameter, JaxRsPackageConst.PARAM_QUERY_PARAM, "value")).ifPresent(paramInfo::setName);
            // 将FormParam注解的value作为参数名
            Optional.ofNullable(PsiKit.getPropertyFromAnnotation(psiParameter, JaxRsPackageConst.PARAM_FORM_PARAM, "value")).ifPresent(paramInfo::setName);
            // 将PathParam注解的value作为参数名
            Optional.ofNullable(PsiKit.getPropertyFromAnnotation(psiParameter, JaxRsPackageConst.PARAM_PATH_PARAM, "value")).ifPresent(paramInfo::setName);
            paramInfos.add(paramInfo);
        }
        return paramInfos;
    }
}
