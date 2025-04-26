package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.RestrictTo;

/**
 * A FrameLayout with an aspect ration of 16:9, when the height is set to wrap_content.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SixteenByNineFrameLayout extends FrameLayout {
    public SixteenByNineFrameLayout(Context context) {
        this(context, null);
    }

    public SixteenByNineFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SixteenByNineFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int sixteenNineHeight = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(widthMeasureSpec) * 9 / 16,
                    MeasureSpec.EXACTLY
            );
            super.onMeasure(widthMeasureSpec, sixteenNineHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
