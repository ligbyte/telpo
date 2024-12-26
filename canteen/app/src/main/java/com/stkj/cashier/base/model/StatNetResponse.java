package com.stkj.cashier.base.model;

import android.text.TextUtils;

public class StatNetResponse<T> {
    private String Code;
    private String Message;
    private String sumConsume;
    private String sumRefund;
    private String sumIncome;
    private T Data;

    public StatNetResponse() {
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public boolean isSuccess() {
        return TextUtils.equals("200", Code);
    }

    public boolean isTokenInvalid() {
        return "401".equals(Code);
    }


    public String getSumConsume() {
        return sumConsume;
    }

    public void setSumConsume(String sumConsume) {
        this.sumConsume = sumConsume;
    }

    public String getSumRefund() {
        return sumRefund;
    }

    public void setSumRefund(String sumRefund) {
        this.sumRefund = sumRefund;
    }

    public String getSumIncome() {
        return sumIncome;
    }

    public void setSumIncome(String sumIncome) {
        this.sumIncome = sumIncome;
    }
}
