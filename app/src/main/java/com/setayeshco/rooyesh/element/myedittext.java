package com.setayeshco.rooyesh.element;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;


public class myedittext extends EditText {

    public myedittext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public myedittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public myedittext(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "font/IRANSans(FaNum)_UltraLight.ttf");
        setTypeface(tf);
    }

}