package com.jj.base.common.view.toast;

import android.widget.Toast;

import androidx.annotation.NonNull;

public interface BadTokenListener {

    void onBadTokenCaught(@NonNull Toast toast);
}
