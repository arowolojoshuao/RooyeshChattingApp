package com.setayeshco.rooyesh.element;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class mybutton extends Button {

    public mybutton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public mybutton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public mybutton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "font/IRANSans(FaNum)_Bold.ttf");
        setTypeface(tf);
    }

}


