package cn.jj.base.common.anim;

import android.animation.Animator;
import android.view.View;

/**
 * Created by yangxl on 2018/1/25.
 */

public class ActivatedStateAnimator extends StateAnimator {
    private static final float MAX_SCALE = 1.15f;
    private static final int DURATION = 150;
    Animator animator;

    public ActivatedStateAnimator(View target) {
        super(target);
    }

    @Override
    protected void onSelectedChanged() {

    }

    @Override
    protected void onActivatedChanged() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        int duration;
        if (activated) {
            duration = (int) ((MAX_SCALE - target.getScaleX()) / (MAX_SCALE - 1) * DURATION);
            animator = AnimUtils.ofPropertyValuesHolder(
                    target, new PropertyListBuilder().scale(1.2f).build());
        } else {
            duration = (int) ((target.getScaleX() - 1) / (MAX_SCALE - 1) * DURATION);
            animator = AnimUtils.ofPropertyValuesHolder(
                    target, new PropertyListBuilder().scale(1f).build());
        }
        duration = Math.max(duration, 0);
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    protected void onCheckedChanged() {

    }

    @Override
    protected void onEnabledChanged() {

    }

    @Override
    protected void onPressedChanged() {

    }

    @Override
    public void cancel() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }
}
