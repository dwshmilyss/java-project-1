package com.linkflow.analysis.presto.security.util;

import io.naza.frw.exception.BaseExceptionCode;

/**
 * Created by likai.yu on 2020/10/15
 */
public enum ExceptionCode implements BaseExceptionCode {

    S_00009("S00009", "HTTP error, detail [{}]");

    private String code;
    private String templateMessage;

    ExceptionCode(String code, String templateMessage) {
        this.code = code;
        this.templateMessage = templateMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTemplateMessage() {
        return templateMessage;
    }
}
