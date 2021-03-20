package cn.jj.base.common.anim;

import android.animation.Animator;
import android.view.View;

/**
 * Created by yangxl on 2018/1/25.
 */

public class PressedStateAnimator extends StateAnimator {
    private static final float DEFALUT_SCALE = 1.4f;
    private static final int DURATION = 150;
    Animator animator;
    private float scale = DEFALUT_SCALE;

    public PressedStateAnimator(View target) {
        super(target);
    }

    public PressedStateAnimator(View target, float scale) {
        super(target);
        this.scale = scale;
    }

    @Override
    protected void onSelectedChanged() {

    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected void onPressedChanged() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        int duration;
        if (selected) {
            duration = (int) ((scale - target.getScaleX()) / (scale - 1) * DURATION);
            animator = AnimUtils.ofPropertyValuesHolder(
                    target, new PropertyListBuilder().scale(scale).build());
        } else {
            duration = (int) ((target.getScaleX() - 1) / (scale - 1) * DURATION);
            animator = AnimUtils.ofPropertyValuesHolder(
                    target, new PropertyListBuilder().scale(1f).build());
        }
        duration = Math.max(duration, 0);
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    protected void onActivatedChanged() {

    }

    @Override
    protected void onCheckedChanged() {

    }

    @Override
    protected void onEnabledChanged() {

    }


    @Override
    public void cancel() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }
}
