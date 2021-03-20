package cn.jj.base.common.view.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.lang.ref.WeakReference;

public abstract class BasePopup<T extends BasePopup> {
    public static final float DIM_AMOUNT_NOT_EXIST = -1f;

    final PopupWindow mWindow;
    final Context mContext;
    private WindowManager mWindowManager;
    private WeakReference<View> mAttachedViewRf;
    private float mDimAmount = DIM_AMOUNT_NOT_EXIST;
    private PopupWindow.OnDismissListener mDismissListener;

    private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            dismiss();
        }
    };

    public BasePopup(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindow = new PopupWindow(context);
        initWindow();
    }

    private void initWindow() {
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setFocusable(true);
        mWindow.setTouchable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setOnDismissListener(() -> {
            BasePopup.this.onDismiss();
            if (mDismissListener != null) {
                mDismissListener.onDismiss();
            }
        });
    }

    public T dimAmount(float dimAmount) {
        mDimAmount = dimAmount;
        return (T) this;
    }

    public T setFocusable(boolean focusable) {
        mWindow.setFocusable(focusable);
        return (T) this;
    }

    public T dismissIfOutsideTouch(boolean dismissIfOutsideTouch) {
        mWindow.setOutsideTouchable(dismissIfOutsideTouch);
        return (T) this;
    }

    public T onDismiss(PopupWindow.OnDismissListener listener) {
        mDismissListener = listener;
        return (T) this;
    }

    private void removeOldAttachStateChangeListener() {
        if (mAttachedViewRf != null) {
            View oldAttachedView = mAttachedViewRf.get();
            if (oldAttachedView != null) {
                oldAttachedView.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
            }
        }
    }

    public View getDecorView() {
        View decorView = null;
        try {
            if (mWindow.getBackground() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView = (View) mWindow.getContentView().getParent();
                } else {
                    decorView = mWindow.getContentView();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView = (View) mWindow.getContentView().getParent().getParent();
                } else {
                    decorView = (View) mWindow.getContentView().getParent();
                }
            }
        } catch (Exception ignore) {

        }

        return decorView;
    }

    protected void showAtLocation(@NonNull View parent, int x, int y) {
        if (!ViewCompat.isAttachedToWindow(parent)) {
            return;
        }
        removeOldAttachStateChangeListener();
        parent.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
        mAttachedViewRf = new WeakReference<>(parent);
        mWindow.showAtLocation(parent, Gravity.NO_GRAVITY, x, y);

        if (mDimAmount != DIM_AMOUNT_NOT_EXIST) {
            updateDimAmount(mDimAmount);
        }
    }

    private void updateDimAmount(float dimAmount) {
        View decorView = getDecorView();
        if (decorView != null) {
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) decorView.getLayoutParams();
            p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = dimAmount;
            modifyWindowLayoutParams(p);
            mWindowManager.updateViewLayout(decorView, p);
        }
    }

    protected void modifyWindowLayoutParams(WindowManager.LayoutParams lp) {

    }

    protected void onDismiss() {

    }

    public final void dismiss() {
        removeOldAttachStateChangeListener();
        mAttachedViewRf = null;
        mWindow.dismiss();
    }
}
