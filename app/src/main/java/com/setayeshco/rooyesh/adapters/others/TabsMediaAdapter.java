package com.setayeshco.rooyesh.adapters.others;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.setayeshco.rooyesh.fragments.media.DocumentsFragment;
import com.setayeshco.rooyesh.fragments.media.MediaFragment;

/**
 * Created by Abderrahim El imame on 27/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class TabsMediaAdapter extends FragmentPagerAdapter {


    public TabsMediaAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MediaFragment.newInstance("MEDIA");
            case 1:
                return DocumentsFragment.newInstance("DOCUMENTS");
           /* case 2:
                return LinksFragment.newInstance("LINKS");*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "MEDIA";
            case 1:
                default:
                return "DOCUMENTS";
            /*case 2:
            default:
                return "LINKS";*/
        }
    }
}