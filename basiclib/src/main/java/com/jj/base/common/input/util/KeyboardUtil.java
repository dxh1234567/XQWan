package com.jj.base.common.input.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.jj.base.common.input.widget.fullscreen.IPanelHeightTarget;
import com.jj.basiclib.R;


public class KeyboardUtil {

    private static int lastSaveKeyboardHeight = 0;

    public static boolean isKeyboardShowing = false;

    /**
     * 保存键盘高度
     *
     * @param keyboardHeight 键盘高度
     * @return 是否有变化并保存成功
     */
    public static boolean saveKeyboardHeight(final Context context, int keyboardHeight) {
        if (lastSaveKeyboardHeight == keyboardHeight) {
            return false;
        }

        if (keyboardHeight < 0) {
            return false;
        }

        lastSaveKeyboardHeight = keyboardHeight;

        return KeyBoardSharedPreferences.save(context, keyboardHeight);
    }

    public static int getKeyboardHeight(Context context) {
        if (lastSaveKeyboardHeight == 0) {
            lastSaveKeyboardHeight = KeyBoardSharedPreferences
                    .get(context, getMinPanelHeight(context.getResources()));
        }

        return lastSaveKeyboardHeight;
    }

    /**
     * 获取表情面板的高度
     */
    public static int getValidPanelHeight(final Context context) {
        final int maxPanelHeight = getMaxPanelHeight(context.getResources());
        final int minPanelHeight = getMinPanelHeight(context.getResources());

        int validPanelHeight = getKeyboardHeight(context);

        validPanelHeight = Math.max(minPanelHeight, validPanelHeight);
        validPanelHeight = Math.min(maxPanelHeight, validPanelHeight);
        return validPanelHeight;
    }

    private static int maxPanelHeight = 0;
    private static int minPanelHeight = 0;
    private static int minKeyboardHeight = 0;

    public static int getMaxPanelHeight(Resources res) {
        if (maxPanelHeight == 0) {
            maxPanelHeight = res.getDimensionPixelSize(R.dimen.max_panel_height);
        }

        return maxPanelHeight;
    }

    public static int getMinPanelHeight(Resources res) {
        if (minPanelHeight == 0) {
            minPanelHeight = res.getDimensionPixelSize(R.dimen.min_panel_height);
        }

        return minPanelHeight;
    }

    public static int getMinKeyboardHeight(Context context) {
        if (minKeyboardHeight == 0) {
            minKeyboardHeight = context.getResources()
                    .getDimensionPixelSize(R.dimen.min_keyboard_height);
        }
        return minKeyboardHeight;
    }

    /**
     * 弹出键盘
     *
     * @param view EditText
     */
    public static void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    /**
     * 隐藏键盘
     *
     * @param view EditText
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 推荐在 {@link Activity# onCreate(Bundle)} 中调用
     *
     * @param activity view所在的activity
     * @param target   view，其高度将与键盘高度相同
     * @param lis      键盘显示的监听
     * @see #saveKeyboardHeight(Context, int)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static ViewTreeObserver.OnGlobalLayoutListener attach(final Activity activity,
                                                                 IPanelHeightTarget target,
                                                                 OnKeyboardShowingListener lis) {
        final ViewGroup contentView = activity.findViewById(android.R.id.content);
        final boolean isFullScreen = ViewUtil.isFullScreen(activity);
        final boolean isTranslucentStatus = ViewUtil.isTranslucentStatus(activity);
        final boolean isFitSystemWindows = ViewUtil.isFitsSystemWindows(activity);

        // 获取键盘高度
        final Display display = activity.getWindowManager().getDefaultDisplay();
        final int screenHeight;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point screenSize = new Point();
            display.getSize(screenSize);
            screenHeight = screenSize.y;
        } else {
            //noinspection deprecation
            screenHeight = display.getHeight();
        }

        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new KeyboardStatusListener(
                isFullScreen,
                isTranslucentStatus,
                isFitSystemWindows,
                contentView,
                target,
                lis,
                screenHeight);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        return globalLayoutListener;
    }

    /**
     * @see #attach(Activity, IPanelHeightTarget, OnKeyboardShowingListener)
     */
    public static ViewTreeObserver.OnGlobalLayoutListener attach(final Activity activity,
                                                                 IPanelHeightTarget target) {
        return attach(activity, target, null);
    }

    /**
     * 移除OnGlobalLayoutListener
     *
     * @param activity same activity used in {@link #attach} method
     * @param l        ViewTreeObserver.OnGlobalLayoutListener returned by {@link #attach} method
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void detach(Activity activity, ViewTreeObserver.OnGlobalLayoutListener l) {
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.getViewTreeObserver().removeOnGlobalLayoutListener(l);
        } else {
            //noinspection deprecation
            contentView.getViewTreeObserver().removeGlobalOnLayoutListener(l);
        }
    }

    public static class KeyboardStatusListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private static final String TAG = "KeyboardStatusListener";

        private int previousDisplayHeight = 0;
        private final ViewGroup contentView;
        private final IPanelHeightTarget panelHeightTarget;
        private final boolean isFullScreen;
        private final boolean isTranslucentStatus;
        private final boolean isFitSystemWindows;
        private final int statusBarHeight;
        private boolean lastKeyboardShowing;
        private final OnKeyboardShowingListener keyboardShowingListener;
        private final int screenHeight;

        private boolean isOverlayLayoutDisplayHContainStatusBar = false;

        KeyboardStatusListener(boolean isFullScreen, boolean isTranslucentStatus,
                               boolean isFitSystemWindows,
                               ViewGroup contentView, IPanelHeightTarget panelHeightTarget,
                               OnKeyboardShowingListener listener, int screenHeight) {
            this.contentView = contentView;
            this.panelHeightTarget = panelHeightTarget;
            this.isFullScreen = isFullScreen;
            this.isTranslucentStatus = isTranslucentStatus;
            this.isFitSystemWindows = isFitSystemWindows;
            this.statusBarHeight = StatusBarHeightUtil.getStatusBarHeight(contentView.getContext());
            this.keyboardShowingListener = listener;
            this.screenHeight = screenHeight;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @Override
        public void onGlobalLayout() {
            final View userRootView = contentView.getChildAt(0);
            final View actionBarOverlayLayout = (View) contentView.getParent();

            // Step 1. calculate the current display frame's height.
            Rect r = new Rect();

            final int displayHeight;
            final int notReadyDisplayHeight = -1;
            if (isTranslucentStatus) {
                // status bar translucent.

                // In the case of the Theme is Status-Bar-Translucent, we calculate the keyboard
                // state(showing/hiding) and the keyboard height based on assuming that the
                // displayHeight includes the height of the status bar.

                actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);

                final int overlayLayoutDisplayHeight = (r.bottom - r.top);

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    // in case of the keyboard is hiding, the display height of the
                    // action-bar-overlay-layout would be possible equal to the screen height.

                    // and if isOverlayLayoutDisplayHContainStatusBar has already been true, the
                    // display height of action-bar-overlay-layout must include the height of the
                    // status bar always.
                    isOverlayLayoutDisplayHContainStatusBar =
                            overlayLayoutDisplayHeight == screenHeight;
                }

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    // In normal case, we need to plus the status bar height manually.
                    displayHeight = overlayLayoutDisplayHeight + statusBarHeight;
                } else {
                    // In some case(such as Samsung S7 edge), the height of the
                    // action-bar-overlay-layout display bound already included the height of the
                    // status bar, in this case we doesn't need to plus the status bar height
                    // manually.
                    displayHeight = overlayLayoutDisplayHeight;
                }

            } else {
                if (userRootView != null) {
                    userRootView.getWindowVisibleDisplayFrame(r);
                    displayHeight = (r.bottom - r.top);
                } else {
                    displayHeight = notReadyDisplayHeight;
                }
            }

            if (displayHeight == notReadyDisplayHeight) {
                return;
            }

            calculateKeyboardHeight(displayHeight);
            calculateKeyboardShowing(displayHeight);

            previousDisplayHeight = displayHeight;
        }

        private void calculateKeyboardHeight(final int displayHeight) {
            // first result.
            if (previousDisplayHeight == 0) {
                previousDisplayHeight = displayHeight;

                // init the panel height for target.
                panelHeightTarget.refreshHeight(KeyboardUtil.getValidPanelHeight(getContext()));
                return;
            }

            int keyboardHeight;
            if (KPSwitchConflictUtil.isHandleByPlaceholder(isFullScreen, isTranslucentStatus,
                    isFitSystemWindows)) {
                // the height of content parent = contentView.height + actionBar.height
                final View actionBarOverlayLayout = (View) contentView.getParent();
                keyboardHeight = actionBarOverlayLayout.getHeight() - displayHeight;
            } else {
                keyboardHeight = Math.abs(displayHeight - previousDisplayHeight);
            }
            // no change.
            if (keyboardHeight <= getMinKeyboardHeight(getContext())) {
                return;
            }
            // influence from the layout of the Status-bar.
            if (keyboardHeight == this.statusBarHeight) {
                return;
            }
            // save the keyboardHeight
            boolean changed = KeyboardUtil.saveKeyboardHeight(getContext(), keyboardHeight);
            if (changed) {
                final int validPanelHeight = KeyboardUtil.getValidPanelHeight(getContext());
                if (this.panelHeightTarget.getHeight() != validPanelHeight) {
                    // Step3. refresh the panel's height with valid-panel-height which refer to
                    // the last keyboard height
                    this.panelHeightTarget.refreshHeight(validPanelHeight);
                }
            }
        }

        private int maxOverlayLayoutHeight;

        private void calculateKeyboardShowing(final int displayHeight) {

            boolean isKeyboardShowing;

            // the height of content parent = contentView.height + actionBar.height
            final View actionBarOverlayLayout = (View) contentView.getParent();
            // in the case of FragmentLayout, this is not real ActionBarOverlayLayout, it is
            // LinearLayout, and is a child of DecorView, and in this case, its top-padding would be
            // equal to the height of status bar, and its height would equal to DecorViewHeight -
            // NavigationBarHeight.
            final int actionBarOverlayLayoutHeight = actionBarOverlayLayout.getHeight()
                    - actionBarOverlayLayout.getPaddingTop();

            if (KPSwitchConflictUtil.isHandleByPlaceholder(isFullScreen, isTranslucentStatus,
                    isFitSystemWindows)) {
                if (!isTranslucentStatus
                        && actionBarOverlayLayoutHeight - displayHeight == this.statusBarHeight) {
                    // handle the case of status bar layout, not keyboard active.
                    isKeyboardShowing = lastKeyboardShowing;
                } else {
                    isKeyboardShowing = actionBarOverlayLayoutHeight > displayHeight;
                }

            } else {

                final int phoneDisplayHeight = contentView.getResources()
                        .getDisplayMetrics().heightPixels;
                if (!isTranslucentStatus
                        && phoneDisplayHeight == actionBarOverlayLayoutHeight) {
                    // no space to settle down the status bar, switch to fullscreen,
                    // only in the case of paused and opened the fullscreen page.
                    return;

                }

                if (maxOverlayLayoutHeight == 0) {
                    // non-used.
                    isKeyboardShowing = lastKeyboardShowing;
                } else {
                    isKeyboardShowing = displayHeight < maxOverlayLayoutHeight
                            - getMinKeyboardHeight(getContext());
                }

                maxOverlayLayoutHeight = Math
                        .max(maxOverlayLayoutHeight, actionBarOverlayLayoutHeight);
            }

            if (lastKeyboardShowing != isKeyboardShowing) {
                this.panelHeightTarget.onKeyboardShowing(isKeyboardShowing);
                if (keyboardShowingListener != null) {
                    keyboardShowingListener.onKeyboardShowing(isKeyboardShowing);
                }
            }

            lastKeyboardShowing = isKeyboardShowing;
            KeyboardUtil.isKeyboardShowing = isKeyboardShowing;
        }

        private Context getContext() {
            return contentView.getContext();
        }
    }

    /**
     * The interface is used to listen the keyboard showing state.
     *
     * @see #attach(Activity, IPanelHeightTarget, OnKeyboardShowingListener)
     * @see KeyboardStatusListener#calculateKeyboardShowing(int)
     */
    public interface OnKeyboardShowingListener {

        /**
         * Keyboard showing state callback method.
         * <p>
         * This method is invoked in
         * {@link ViewTreeObserver.OnGlobalLayoutListener#onGlobalLayout()} which is one of the
         * ViewTree lifecycle callback methods. So deprecating those time-consuming operation(I/O,
         * complex calculation, alloc objects, etc.) here from blocking main ui thread is
         * recommended.
         * </p>
         *
         * @param isShowing Indicate whether keyboard is showing or not.
         */
        void onKeyboardShowing(boolean isShowing);

    }
}
