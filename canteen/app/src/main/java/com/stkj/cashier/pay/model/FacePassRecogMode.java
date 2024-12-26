package com.stkj.cashier.pay.model;

public enum FacePassRecogMode {
    FP_REG_MODE_DEFAULT(0),
    FP_REG_MODE_FEAT_COMP(1);

    private int value;

    private FacePassRecogMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
