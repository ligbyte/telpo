package com.stkj.deviceinterface.callback;

public interface OnReadWeightListener {

    void onReadWeightData(String data, String unit);

    void onReadWeightError(String message);
}