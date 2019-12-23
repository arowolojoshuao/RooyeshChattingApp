package com.setayeshco.rooyesh.element;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class textviewlight extends TextView {

    public textviewlight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public textviewlight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public textviewlight(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "font/IRANSans(FaNum)_UltraLight.ttf");
        setTypeface(tf);
    }

}