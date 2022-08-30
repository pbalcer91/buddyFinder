package pl.com.wfiis.android.buddyfinder.Animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class SearchMapAnimation extends Animation {

    private int mWidth;
    private int mStartWidth;
    private View mView;

    public SearchMapAnimation(View view, int width) {
        mView = view;
        mWidth = RelativeLayout.LayoutParams.MATCH_PARENT;
        mStartWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

        mView.getLayoutParams().width = newWidth;
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
