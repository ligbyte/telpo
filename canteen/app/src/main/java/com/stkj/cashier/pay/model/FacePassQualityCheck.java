package com.stkj.cashier.pay.model;

public class FacePassQualityCheck {
    private int qualityFlag;
    public boolean isOcclusionPassed;
    public boolean isBrightnessPassd;
    public boolean isEdgefacePassed;
    public boolean isMinFacePassed;
    public boolean isBlurPassed;
    public boolean isYawPassed;
    public boolean isPitchPassed;
    public boolean isRollPassed;

    public FacePassQualityCheck(int flag) {
        this.qualityFlag = flag;
        this.isOcclusionPassed = this.qualityFlag >> 7 == 1;
        this.isBrightnessPassd = (this.qualityFlag >> 6 & 1) == 1;
        this.isEdgefacePassed = (this.qualityFlag >> 5 & 1) == 1;
        this.isMinFacePassed = (this.qualityFlag >> 4 & 1) == 1;
        this.isBlurPassed = (this.qualityFlag >> 3 & 1) == 1;
        this.isYawPassed = (this.qualityFlag >> 2 & 1) == 1;
        this.isPitchPassed = (this.qualityFlag >> 1 & 1) == 1;
        this.isRollPassed = (this.qualityFlag & 1) == 1;
    }
}
