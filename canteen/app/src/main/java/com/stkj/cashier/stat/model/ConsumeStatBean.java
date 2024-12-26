package com.stkj.cashier.stat.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ConsumeStatBean {

    private int Code;
    private String Message;
    private List<ConsumeStatItem> consumeStatItems;
    private String sumConsume;
    private String sumRefund;
    private String sumIncome;

    public ConsumeStatBean() {
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public List<ConsumeStatItem> getConsumeStatItems() {
        return consumeStatItems;
    }

    public void setConsumeStatItems(List<ConsumeStatItem> consumeStatItems) {
        this.consumeStatItems = consumeStatItems;
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

    public static class ConsumeStatItem {
        private String income;
        private String consume;
        private String feeType;
        private String refund;

        public ConsumeStatItem() {
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getConsume() {
            return consume;
        }

        public void setConsume(String consume) {
            this.consume = consume;
        }

        public String getFeeType() {
            return feeType;
        }

        public void setFeeType(String feeType) {
            this.feeType = feeType;
        }

        public String getRefund() {
            return refund;
        }

        public void setRefund(String refund) {
            this.refund = refund;
        }
    }
}
