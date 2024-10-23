package com.myth.earth.restful.plugin.insight.option;

/**
 * 操作类型枚举
 *
 * @author zhouchao
 * @date 2024-06-19 下午11:47
 */
public class RestfulOptionSetting {

    private boolean showUploadOpenapi  = true;
    private boolean showCopyCurl       = true;
    private boolean showExportMarkdown = true;

    public boolean isShowUploadOpenapi() {
        return showUploadOpenapi;
    }

    public void setShowUploadOpenapi(boolean showUploadOpenapi) {
        this.showUploadOpenapi = showUploadOpenapi;
    }

    public boolean isShowCopyCurl() {
        return showCopyCurl;
    }

    public void setShowCopyCurl(boolean showCopyCurl) {
        this.showCopyCurl = showCopyCurl;
    }

    public boolean isShowExportMarkdown() {
        return showExportMarkdown;
    }

    public void setShowExportMarkdown(boolean showExportMarkdown) {
        this.showExportMarkdown = showExportMarkdown;
    }
}
