package com.stkj.cashier.pay.model;

public class FacePassTrackResult {
    public byte[] message;
    public FacePassImageRet[] images;
    public FacePassTrackedFace[] trackedFaces;

    public FacePassTrackResult(byte[] message, FacePassImageRet[] images, FacePassTrackedFace[] trackedFaces) {
        this.message = message;
        this.images = images;
        this.trackedFaces = trackedFaces;
    }
}
