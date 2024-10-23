package com.myth.earth.restful.kits;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import com.myth.earth.restful.enums.HttpMethod;
import com.myth.earth.restful.enums.RequestType;
import com.myth.earth.restful.model.ParamInfo;
import com.myth.earth.restful.model.PsiMethodInfo;
import com.myth.earth.restful.utils.ParamInfoPsiUtil;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * openapi 操作
 *
 * @date 2023-06-07 18:46
 */
public final class OpenApiKit {

    private OpenApiKit() {
    }

    /**
     * 生成 OperationId
     *
     * @param apiPath api地址
     * @return operation id
     */
    @NotNull
    public static String formatOperationId(@NotNull String apiPath) {
        String target = Arrays.stream(apiPath.split("/")).filter(StringUtils::isNotBlank).collect(Collectors.joining("_"));
        return target.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    /**
     * 生成 OperationId
     *
     * @param apiPath    api地址
     * @param httpMethod 方法类型
     * @return operation id
     */
    @NotNull
    public static String formatOperationId(@NotNull String apiPath, @Nullable HttpMethod httpMethod) {
        String target = Arrays.stream(apiPath.split("/")).filter(StringUtils::isNotBlank).map(StringUtils::capitalize).collect(Collectors.joining());
        return Objects.nonNull(httpMethod) ? target + "Using" + httpMethod.name() : target;
    }

    @NotNull
    public static String formatCommentSchema(@NotNull String schemaName) {
        // return schemaName.replaceAll("[<>]", "0");
        return schemaName.replaceAll("<", "<<").replaceAll(">", ">>");
    }

    @NotNull
    public static String formatCommentSchema(@NotNull PsiType psiType) {
        // return schemaName.replaceAll("[<>]", "0");
        return formatCommentSchema(psiType.getPresentableText());
    }

    /**
     * 生成 pathItem
     *
     * @param apiPath       api路径
     * @param psiMethodInfo api对应的方法信息
     * @return pathItem
     */
    @NotNull
    public static PathItem buildPathItem(@NotNull String apiPath, @NotNull PsiMethodInfo psiMethodInfo, String tag) {
        PathItem pathItem = new PathItem();
        Operation operation = getOperation(apiPath, psiMethodInfo, tag);
        switch (psiMethodInfo.getHttpMethod()) {
            case OPTIONS:
                pathItem.setOptions(operation);
                break;
            case POST:
                pathItem.setPost(operation);
                break;
            case PUT:
                pathItem.setPut(operation);
                break;
            case DELETE:
                pathItem.setDelete(operation);
                break;
            case PATCH:
                pathItem.setPatch(operation);
                break;
            case HEAD:
                pathItem.setHead(operation);
                break;
            case TRACE:
                pathItem.setTrace(operation);
                break;
            case REQUEST:
            case GET:
            default:
                pathItem.setGet(operation);
                break;
        }
        return pathItem;
    }

    @NotNull
    private static Operation getOperation(@NotNull String apiPath, @NotNull PsiMethodInfo psiMethodInfo, String tag) {
        Operation operation = new Operation();
        if (StringUtils.isNotBlank(tag)) {
            operation.setTags(Lists.newArrayList(tag));
        }
        operation.setSummary(psiMethodInfo.getMethodDescribe());
        operation.setOperationId(OpenApiKit.formatOperationId(apiPath, psiMethodInfo.getHttpMethod()));
        List<Parameter> parameters = buildParameters(psiMethodInfo);
        if (CollectionUtils.isNotEmpty(parameters)) {
            operation.setParameters(parameters);
        }
        RequestBody requestBody = buildRequestBody(psiMethodInfo);
        if (Objects.nonNull(requestBody)) {
            operation.setRequestBody(requestBody);
        }
        operation.setResponses(buildResponses(psiMethodInfo));
        return operation;
    }

    @Nullable
    public static RequestBody buildRequestBody(@NotNull PsiMethodInfo psiMethodInfo) {
        List<ParamInfo> requestParams = psiMethodInfo.getRequestParams();
        if (CollectionUtils.isEmpty(requestParams)) {
            return null;
        }

        // 内容
        Content content = new Content();

        // json格式
        if (RequestType.RAW_JSON.equals(psiMethodInfo.getRequestType())) {
            ParamInfo paramInfo = requestParams.stream().findFirst().orElse(null);
            content.put(psiMethodInfo.getRequestType().getDesc(), getMediaType(paramInfo));
        }

        // application/x-www-form-urlencoded 格式
        if (RequestType.X_WWW_FORM_URLENCODED.equals(psiMethodInfo.getRequestType())) {
            Map<String, Schema> paramSchemaMap = Maps.newHashMapWithExpectedSize(requestParams.size());
            List<String> required = Lists.newArrayListWithExpectedSize(requestParams.size());
            for (ParamInfo paramInfo : requestParams) {
                String name = paramInfo.getName();
                Schema<?> value = buildSchema(paramInfo, false, null);
                if (StringUtils.isEmpty(name) || Objects.isNull(value) || paramSchemaMap.containsKey(name)) {
                    continue;
                }
                required.add(name);
                paramSchemaMap.put(name, value);
            }
            ObjectSchema objectSchema = new ObjectSchema();
            objectSchema.setProperties(paramSchemaMap);
            objectSchema.setRequired(required);
            content.put(psiMethodInfo.getRequestType().getDesc(), getMediaType(objectSchema));
        }

        // text/plain
        if (RequestType.RAW_TEXT.equals(psiMethodInfo.getRequestType())) {
            ParamInfo paramInfo = requestParams.stream().findFirst().orElse(null);
            content.put(psiMethodInfo.getRequestType().getDesc(), getMediaType(paramInfo));
        }
        // 请求body
        RequestBody requestBody = new RequestBody();
        // 设置content内容
        requestBody.setContent(content);
        return requestBody;
    }

    public static List<Parameter> buildParameters(@NotNull PsiMethodInfo psiMethodInfo) {
        List<Parameter> parameters = new ArrayList<>();
        List<ParamInfo> queryParams = psiMethodInfo.getQueryParams();
        if (CollectionUtils.isNotEmpty(queryParams)) {
            for (ParamInfo paramInfo : queryParams) {
                Parameter parameter = new Parameter();
                parameter.setName(paramInfo.getName());
                parameter.setIn("query");
                parameter.setDescription(paramInfo.getDescription());
                parameter.setRequired(Optional.ofNullable(paramInfo.getRequired()).orElse(true));
                parameter.setStyle(Parameter.StyleEnum.FORM);
                parameter.setSchema(buildSchema(paramInfo));
                parameters.add(parameter);
            }
        }

        List<ParamInfo> pathParams = psiMethodInfo.getPathParams();
        if (CollectionUtils.isNotEmpty(pathParams)) {
            for (ParamInfo paramInfo : pathParams) {
                Parameter parameter = new Parameter();
                parameter.setName(paramInfo.getName());
                parameter.setIn("path");
                parameter.setDescription(paramInfo.getDescription());
                parameter.setRequired(Optional.ofNullable(paramInfo.getRequired()).orElse(true));
                parameter.setStyle(Parameter.StyleEnum.SIMPLE);
                parameter.setSchema(buildSchema(paramInfo));
                parameters.add(parameter);
            }
        }

        return parameters;
    }

    @Nullable
    private static ApiResponses buildResponses(@NotNull PsiMethodInfo psiMethodInfo) {
        ParamInfo responseParam = psiMethodInfo.getResponseParam();
        if (Objects.isNull(responseParam)) {
            return null;
        }

        Content content = new Content();
        content.put("*/*", getMediaType(responseParam));

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("OK");
        apiResponse.setContent(content);

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.put("200", apiResponse);
        return apiResponses;
    }

    @NotNull
    private static MediaType getMediaType(ParamInfo paramInfo) {
        MediaType mediaType = new MediaType();
        mediaType.setSchema(buildSchema(paramInfo));
        return mediaType;
    }

    @NotNull
    private static MediaType getMediaType(Schema<?> schema) {
        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);
        return mediaType;
    }

    /**
     * 构建Schema：一个类型遍历到底
     *
     * @param paramInfo 参数信息
     * @return schema对象
     */
    public static Schema<?> buildSchema(@NotNull ParamInfo paramInfo) {
        return buildSchema(paramInfo, false, null);
    }

    public static Schema<?> buildSchema(@NotNull ParamInfo paramInfo, boolean anNestedClass, @Nullable Map<String, Schema> schemaWrapper) {
        // 非对象类型
        if (!paramInfo.isAnClass()) {
            String paramType = paramInfo.getParamType();
            Schema<?> schemaByType = OpenApiKit.getSchemaByType(paramType);
            Optional.ofNullable(schemaByType).ifPresent(s -> s.setDescription(paramInfo.getDescription()));
            return schemaByType;
        }

        // list集合
        PsiType psiType = paramInfo.getPsiType();
        if (InheritanceUtil.isInheritor(psiType, CommonClassNames.JAVA_UTIL_COLLECTION)) {
            ArraySchema arraySchema = new ArraySchema();
            arraySchema.setType("array");
            PsiType itemPsiType = PsiUtil.extractIterableTypeParameter(psiType, false);
            Schema<?> schema = Optional.ofNullable(itemPsiType).map(ParamInfoPsiUtil::analyzeOutputParam).map(p -> buildSchema(p, anNestedClass, schemaWrapper))
                                       .orElse(null);
            arraySchema.items(schema);
            // array 节点的描述
            Optional.ofNullable(paramInfo.getDescription()).filter(StringUtils::isNotBlank).ifPresent(arraySchema::setDescription);
            return arraySchema;
        }

        // map集合
        if (InheritanceUtil.isInheritor(psiType, CommonClassNames.JAVA_UTIL_MAP)) {
            ObjectSchema objectSchema = new ObjectSchema();
            // matValueType 可以是：T，也可以是List<T>，Map 类型就无解了
            PsiType matValueType = PsiUtil.substituteTypeParameter(psiType, CommonClassNames.JAVA_UTIL_MAP, 1, false);
            Schema<?> schema =
                    Optional.ofNullable(matValueType).map(ParamInfoPsiUtil::analyzeOutputParam).map(p -> buildSchema(p, anNestedClass, schemaWrapper))
                            .orElse(null);
            objectSchema.additionalItems(schema);
            // map节点的描述
            Optional.ofNullable(paramInfo.getDescription()).filter(StringUtils::isNotBlank).ifPresent(objectSchema::setDescription);
            return objectSchema;
        }

        // 单个对象获取完整引用类型
        String schemaName = OpenApiKit.formatCommentSchema(paramInfo.getParamType());
        if (!anNestedClass && CollectionUtils.isNotEmpty(paramInfo.getChildList())) {
            ObjectSchema wrapperSchema = new ObjectSchema();
            wrapperSchema.setTitle(schemaName);
            Map<String, Schema> childSchema = Maps.newHashMapWithExpectedSize(paramInfo.getChildList().size());
            List<String> requiredNames = Lists.newArrayListWithCapacity(childSchema.size());
            for (ParamInfo info : paramInfo.getChildList()) {
                if (Objects.equals(true, info.getRequired())) {
                    requiredNames.add(info.getName());
                }
                Schema<?> value = buildSchema(info, info.isAnNestedClass(), schemaWrapper);
                if (Objects.nonNull(value) && !childSchema.containsKey(info.getName())) {
                    Optional.ofNullable(info.getExample()).ifPresent(value::setExample);
                    childSchema.put(info.getName(), value);
                }
            }
            wrapperSchema.setRequired(requiredNames);
            wrapperSchema.setProperties(childSchema);
            Optional.ofNullable(paramInfo.getDescription()).filter(StringUtils::isNotBlank).ifPresent(wrapperSchema::setDescription);
            // 只有对象类型需要存放存放映射
            if (schemaWrapper != null) {
                schemaWrapper.putIfAbsent(schemaName, wrapperSchema);
            }
        }

        // 单个对象返回引用
        Schema<?> schema = new Schema<>();
        schema.set$ref(Components.COMPONENTS_SCHEMAS_REF + schemaName);
        return schema;
    }

    public static Schema<?> geSchemaByPsiType(PsiType psiType) {
        return InheritanceUtil.isInheritor(psiType, CommonClassNames.JAVA_UTIL_MAP) ? new ArraySchema() : new ObjectSchema();
    }

    @Nullable
    public static Schema<?> getSchemaByType(String paramType) {
        if (Objects.isNull(paramType)) {
            return null;
        }
        if ("boolean".equals(paramType) || "Boolean".equals(paramType)) {
            return new BooleanSchema();
        }
        if ("int".equals(paramType) || "Integer".equals(paramType) || "short".equals(paramType) || "Short".equals(paramType)) {
            return new IntegerSchema();
        }
        if ("long".equals(paramType) || "Long".equals(paramType) || "double".equals(paramType) || "Double".equals(paramType)) {
            IntegerSchema integerSchema = new IntegerSchema();
            integerSchema.setFormat("int64");
            return integerSchema;
        }
        if ("byte".equals(paramType) || "Byte".equals(paramType) || "char".equals(paramType) || "Character".equals(paramType) || "String".equals(paramType)) {
            return new StringSchema();
        }
        if ("BigDecimal".equals(paramType)) {
            return new NumberSchema();
        }
        if ("Date".equals(paramType)) {
            IntegerSchema integerSchema = new IntegerSchema();
            integerSchema.setFormat("int64");
            return integerSchema;
        }
        return null;
    }
}

