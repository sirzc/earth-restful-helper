package com.myth.earth.restful.model;

import com.intellij.psi.PsiClass;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PsiClassInfo implements Serializable {
    private static final long                serialVersionUID = 1L;
    /**
     * 类
     */
    private              PsiClass            psiClass;
    /**
     * 类注释 （来源：注解、类注释）
     */
    private              String              classDescribe;
    /**
     * 类上的请求地址
     */
    private              String              classApiPath;
    /**
     * 符合条件的方法信息
     */
    private              List<PsiMethodInfo> psiMethodInfos;
}

