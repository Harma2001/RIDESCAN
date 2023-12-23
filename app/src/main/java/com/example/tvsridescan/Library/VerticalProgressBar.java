package com.example.tvsridescan.Library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.tvsridescan.R;

import java.math.BigDecimal;

public class VerticalProgressBar extends androidx.appcompat.widget.AppCompatImageView {

    /**
     * @see <a href="http://developer.android.com/reference/android/graphics/drawable/ClipDrawable.html">ClipDrawable</a>
     */
    private static final BigDecimal MAX = BigDecimal.valueOf(10000);

    public VerticalProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setImageResource(R.drawable.progress_bar);
    }

    public void setCurrentValue(Percent percent){
        int cliDrawableImageLevel = percent.asBigDecimal().multiply(MAX).intValue();
        setImageLevel(cliDrawableImageLevel);
    }
}
