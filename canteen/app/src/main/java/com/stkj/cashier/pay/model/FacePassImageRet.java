package com.stkj.cashier.pay.model;


import mcv.facepass.FacePassException;
import mcv.facepass.types.FacePassLmkOccStatus;
import mcv.facepass.types.FacePassPose;
import mcv.facepass.types.FacePassRCAttribute;
import mcv.facepass.types.FacePassRect;

public class FacePassImageRet {
    public byte[] image;
    public int width;
    public int height;
    public int facePassImageType;
    public int facePassImageRotation;
    public long trackId;
    public FacePassRect rect;
    public FacePassQuality facePassQuality;
    public FacePassRCAttribute rcAttr;
    public FacePassLmkOccStatus lmkoccsta;
    private static final int[] rot = new int[]{0, 90, 180, 270};
    private static final int[] tp = new int[]{0, 1, 2, 3};

    private boolean checkRotation(int facePassImageRotation) {
        boolean rot_valid = false;

        for(int i = 0; i < rot.length; ++i) {
            if (rot[i] == facePassImageRotation) {
                rot_valid = true;
                break;
            }
        }

        return rot_valid;
    }

    private boolean checkType(int facePassImageType) {
        boolean tp_valid = false;

        for(int i = 0; i < tp.length; ++i) {
            if (tp[i] == facePassImageType) {
                tp_valid = true;
                break;
            }
        }

        return tp_valid;
    }

    public FacePassImageRet(byte[] image, int width, int height, int facePassImageRotation, int facePassImageType) throws FacePassException {
        if (width > 0 && height > 0 && image != null) {
            if (!this.checkRotation(facePassImageRotation)) {
                throw new FacePassException("invalid facePassImageRotation");
            } else if (!this.checkType(facePassImageType)) {
                throw new FacePassException("invalid facePassImageType");
            } else {
                this.image = image;
                this.width = width;
                this.height = height;
                this.facePassImageRotation = facePassImageRotation;
                this.facePassImageType = facePassImageType;
                this.trackId = 0L;
                this.rcAttr = null;
            }
        } else {
            throw new FacePassException("invalid image params");
        }
    }

    public FacePassImageRet(byte[] image, int width, int height, int facePassImageRotation, int facePassImageType, long trackId) throws FacePassException {
        if (width > 0 && height > 0 && image != null) {
            if (!this.checkRotation(facePassImageRotation)) {
                throw new FacePassException("invalid facePassImageRotation");
            } else if (!this.checkType(facePassImageType)) {
                throw new FacePassException("invalid facePassImageType");
            } else {
                this.image = image;
                this.width = width;
                this.height = height;
                this.facePassImageRotation = facePassImageRotation;
                this.facePassImageType = facePassImageType;
                this.trackId = trackId;
                this.rcAttr = null;
            }
        } else {
            throw new FacePassException("invalid image params");
        }
    }

    public FacePassImageRet(byte[] image, int width, int height, int facePassImageRotation, int facePassImageType, float left, float top, float right, float bottom, float roll, float pitch, float yaw, int quality_flag, float blur, float edgefacecomp, float brightness, float brightness_std, long trackId, FacePassRCAttribute rcAttr, FacePassLmkOccStatus lmkoccsta) throws FacePassException {
        if (width > 0 && height > 0 && image != null) {
            if (!this.checkRotation(facePassImageRotation)) {
                throw new FacePassException("invalid facePassImageRotation");
            } else if (!this.checkType(facePassImageType)) {
                throw new FacePassException("invalid facePassImageType");
            } else {
                this.image = image;
                this.width = width;
                this.height = height;
                this.facePassImageRotation = facePassImageRotation;
                this.facePassImageType = facePassImageType;
                this.lmkoccsta = lmkoccsta;
                this.rect = new FacePassRect((int)left, (int)top, (int)right, (int)bottom);
                FacePassPose fpPose = new FacePassPose(roll, pitch, yaw);
                this.facePassQuality = new FacePassQuality(quality_flag, blur, brightness, brightness_std, edgefacecomp, fpPose);
                this.trackId = trackId;
                this.rcAttr = rcAttr;
            }
        } else {
            throw new FacePassException("invalid image params");
        }
    }
}

