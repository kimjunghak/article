package com.jungs.article.model.result;

import lombok.Data;

@Data
public class RestResult {

    private boolean success;

    private Object data;

    public static RestResult success(Object data) {
        RestResult restResult = new RestResult();
        restResult.setSuccess(true);
        restResult.setData(data);
        return restResult;
    }

    public static RestResult fail(String msg) {
        RestResult restResult = new RestResult();
        restResult.setSuccess(false);
        restResult.setData(msg);
        return restResult;
    }
}
