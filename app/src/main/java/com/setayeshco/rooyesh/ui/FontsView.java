package com.setayeshco.rooyesh.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Abderrahim on 11/5/2015.
 * This class to create icon by font awesome
 */
public class FontsView extends AppCompatTextView {
    private static final String TAG = FontsView.class.getSimpleName();
    //Cache the font loadCircleImage status to improve performance
    private static Typeface font;

    public FontsView(Context context) {
        super(context);
        if (!isInEditMode())// prevent exception in Android Studio / ADT interface builder
            setFont(context);
    }

    public FontsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())// prevent exception in Android Studio / ADT interface builder
            setFont(context);
    }

    public FontsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())// prevent exception in Android Studio / ADT interface builder
            setFont(context);
    }


    private void setFont(Context context) {


        //Check for font is already loaded
        if (font == null) {
            try {
                font = Typeface.createFromAsset(context.getAssets(), "fonts/Linearicons_free.ttf");
                Log.d(TAG, "Font awesome loaded");
            } catch (RuntimeException e) {
                Log.e(TAG, "Font awesome not loaded");
            }
        }

        //Finally set the font
        setTypeface(font);
    }
}
