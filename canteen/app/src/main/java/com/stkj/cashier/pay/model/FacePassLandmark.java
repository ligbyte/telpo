package com.stkj.cashier.pay.model;

import mcv.facepass.types.FacePassPoint2f;

public class FacePassLandmark {
    public float score;
    public FacePassPoint2f[] points;

    public FacePassLandmark(float score, FacePassPoint2f[] points) {
        this.score = score;
        this.points = points;
    }
}
