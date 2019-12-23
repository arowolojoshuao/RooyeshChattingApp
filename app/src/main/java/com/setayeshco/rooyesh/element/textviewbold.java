package com.setayeshco.rooyesh.element;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class textviewbold extends TextView {

    public textviewbold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public textviewbold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public textviewbold(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "font/IRANSans(FaNum)_Bold.ttf");
        setTypeface(tf);
    }

}