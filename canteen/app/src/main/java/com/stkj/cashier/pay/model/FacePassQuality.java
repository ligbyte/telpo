package com.stkj.cashier.pay.model;

import mcv.facepass.types.FacePassPose;

public class FacePassQuality {
    public int flag;
    public float blur;
    public float brightness;
    public float deviation;
    public float edgefaceComp;
    public FacePassPose pose;
    public FacePassQualityCheck facePassQualityCheck;

    public FacePassQuality(int flag, float blur, float brightness, float deviation, float edgefacecomp, FacePassPose pose) {
        this.flag = flag;
        this.blur = blur;
        this.brightness = brightness;
        this.deviation = deviation;
        this.edgefaceComp = edgefacecomp;
        this.pose = pose;
        this.facePassQualityCheck = new FacePassQualityCheck(flag);
    }
}
