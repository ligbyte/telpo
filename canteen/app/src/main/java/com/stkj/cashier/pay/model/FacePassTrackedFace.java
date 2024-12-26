package com.stkj.cashier.pay.model;

import mcv.facepass.types.FacePassLmkOccStatus;
import mcv.facepass.types.FacePassPose;
import mcv.facepass.types.FacePassRCAttribute;
import mcv.facepass.types.FacePassRect;

public class FacePassTrackedFace {
    public long trackId;
    public FacePassRect rect;
    public float lmkScore;
    public FacePassLandmark landmark;
    public FacePassQuality quality;
    public int facePassqualityState;
    public FacePassRCAttribute rcAttr;
    public FacePassLmkOccStatus lmkOccSta;

    public FacePassTrackedFace(long track_id, float left, float top, float right, float bottom, float lmk_score, FacePassLandmark landmark, int flag, float blur, float brightness, float brt_std, float edgefacecomp, float roll, float pitch, float yaw, int facePassqualityState, FacePassRCAttribute rcAttr, boolean valid, boolean eyeocc, boolean noseocc, boolean mouthocc) {
        this.trackId = track_id;
        this.rect = new FacePassRect((int)left, (int)top, (int)right, (int)bottom);
        this.lmkScore = lmk_score;
        this.landmark = landmark;
        FacePassPose fpPose = new FacePassPose(roll, pitch, yaw);
        this.quality = new FacePassQuality(flag, blur, brightness, brt_std, edgefacecomp, fpPose);
        this.facePassqualityState = facePassqualityState;
        this.rcAttr = rcAttr;
        this.lmkOccSta = new FacePassLmkOccStatus(valid, eyeocc, noseocc, mouthocc);
    }
}