package com.stkj.infocollect.base.callback;

/**
 * 语音回调监听
 */
public interface TTSVoiceListener {
    default void onVoiceEnd() {

    }

    default void onVoiceStart() {

    }

    default void onVoiceError() {

    }
}
