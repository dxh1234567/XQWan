package cn.jj.base.baseclass;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import cn.jj.base.common.helper.FragmentBackHandler;
import cn.jj.base.utils.BitmapUtil;
import cn.jj.base.utils.Utility;
import cn.jj.basiclib.R;


/**
 * Created by Administrator on 2017/7/29 0029.
 */

public class BaseFragment extends Fragment implements OnBackListener {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    private boolean isDestroyed = true;
    private boolean isViewDestroyed = true;
    protected Toolbar toolbar;
    protected ViewGroup toolbarContainer;

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isViewDestroyed = false;
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        isViewDestroyed = true;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitleView();
    }

    @Override
    @CallSuper
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isDestroyed = false;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean isViewDestroyed() {
        return isViewDestroyed;
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    protected String getFragmentTitle() {
        return "";
    }


    protected Drawable getNavigationIcon() {
        Drawable drawable = ContextCompat.getDrawable(Utility.getApplication(), R.drawable.ic_back);
        int tintColor = ContextCompat.getColor(Utility.getApplication(), R.color.basic_black_text);
        drawable = BitmapUtil.getTintDrawable(drawable, tintColor, null);
        return drawable;
    }

    protected View.OnClickListener getNavigationOnClickListener() {
        return v -> onBackPressed();
    }

    private void initTitleView() {
        toolbar = findToolbar();
        TextView titleTx = getView().findViewById(R.id.custom_title);
        if (titleTx != null) {
            toolbarContainer = (ViewGroup) titleTx.getParent();
        }
        if (null == toolbarContainer) {
            toolbarContainer = getView().findViewById(R.id.toolbar_container);
        }
        View view = getView();
        Activity activity = getActivity();
        if (toolbar != null && view != null && activity != null) {
            if (activity instanceof AppCompatActivity) {
                ((AppCompatActivity) activity).setSupportActionBar(toolbar);
                ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            setTitle(getFragmentTitle());
            Drawable icon = getNavigationIcon();
            toolbar.setNavigationIcon(icon);
            toolbar.setNavigationOnClickListener(icon == null ? null : getNavigationOnClickListener());
        }
    }

    protected void setTitle(String title) {
        View view = getView();
        if (toolbar == null || view == null) {
            return;
        }
        TextView titleTx = getView().findViewById(R.id.custom_title);
        if (titleTx != null) {
            titleTx.setText(title);
        } else {
            toolbar.setTitle(title);
        }
    }

    protected void setTitleColor(@ColorInt int color) {
        View view = getView();
        if (toolbar == null || view == null) {
            return;
        }
        TextView titleTx = getView().findViewById(R.id.custom_title);
        if (titleTx != null) {
            titleTx.setTextColor(color);
        } else {
            toolbar.setTitleTextColor(color);
        }
    }

    private Toolbar findToolbar() {
        View view = getView().findViewById(R.id.custom_toolbar);
        if (view instanceof Toolbar) {
            return (Toolbar) view;
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                if (child instanceof Toolbar) {
                    return (Toolbar) child;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return FragmentBackHandler.INSTANCE.handleBackPress(this);
    }
}
