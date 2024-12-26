package com.stkj.cashier.consumer.callback;

import android.view.SurfaceView;

import com.stkj.cashier.home.ui.widget.Home1TitleLayout;
import com.stkj.cashier.home.ui.widget.HomeTitleLayout;

public interface ConsumerListener {

    default void onCreateFacePreviewView(SurfaceView previewView, SurfaceView irPreviewView) {

    }

    default void onCreateTitleLayout(HomeTitleLayout homeTitleLayout) {

    }

    default void onCreateTitleLayout(Home1TitleLayout home1TitleLayout) {

    }

    default void onConsumerDismiss() {

    }

    default void onConsumerChanged() {

    }
}
