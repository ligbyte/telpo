package com.stkj.cashier.base.ui.fragment;

import android.view.KeyEvent;

import com.stkj.cashier.base.callback.DispatchKeyEventListener;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;

/**
 * 键盘时间分发fragment
 */
public abstract class BaseDispatchKeyEventFragment extends BaseRecyclerFragment implements DispatchKeyEventListener {

    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---BaseDispatchKeyEventFragment--dispatchKeyEvent--activity event: " + event + " this = " + this);
        return false;
    }

}
