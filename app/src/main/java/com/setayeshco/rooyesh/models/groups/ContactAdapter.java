package com.setayeshco.rooyesh.models.groups;

import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.interfaces.OnItemClickListener;
import com.setayeshco.rooyesh.interfaces.UpdatePresentClickListener;
import com.setayeshco.rooyesh.models.Roozh;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.hoang8f.widget.FButton;

/**
 * Created by Vahid on 24/02/2018.
 */

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Activity activity;
    private List<GroupPresent> contacts;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private UpdatePresentClickListener mUpdatePresentClickListener;


    public ContactAdapter(RecyclerView recyclerView, List<GroupPresent> contacts, Activity activity,UpdatePresentClickListener updatePresentClickListener) {
        this.contacts = contacts;
        this.activity = activity;
        mUpdatePresentClickListener = updatePresentClickListener;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return contacts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_row, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            GroupPresent contact = contacts.get(position);
            UserViewHolder userViewHolder = (UserViewHolder) holder;


        /*    SimpleDateFormat m_year1 = new SimpleDateFormat("yyyy");
            int m_year = Integer.parseInt(m_year1.format(contact.getDate()));
            SimpleDateFormat m_Month1 = new SimpleDateFormat("MM");
            int m_Month = Integer.parseInt(m_Month1.format(contact.getDate()));
            SimpleDateFormat m_day1 = new SimpleDateFormat("dd");
            int m_day = Integer.parseInt(m_day1.format(contact.getDate()));*/

            String y = contact.getDate().substring(0,4);
            String m= contact.getDate().substring(5,7);
            String d = contact.getDate().substring(8,10);
            Log.d("DATEE", "date : " +contact.getDate());
            Log.d("DATEE", "y : " +y);
            Log.d("DATEE", "m : " +m);
            Log.d("DATEE", "d : " +d);

            Roozh jCal = new Roozh();
            jCal.GregorianToPersian(Integer.parseInt(contact.getDate().substring(0,4)), Integer.parseInt(contact.getDate().substring(5,7)), Integer.parseInt(contact.getDate().substring(8,10)));


            String date = jCal.toString() + "" ;




            userViewHolder.date.setText(date);
            userViewHolder.img_present.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mUpdatePresentClickListener.onItemClick(contact.getpID(),contact.getPresent());
                    if (contact.getPresent().equals("1") || contact.getPresent().equals(1)){
                        userViewHolder.img_present.setBackgroundResource(R.drawable.ic_absent1);
                    }else {
                        userViewHolder.img_present.setBackgroundResource(R.drawable.ic_present);
                    }
                    userViewHolder.present.setText((contact.getPresent().equals("1") || contact.getPresent().equals(1) ) ? "غایب" : "حاضر");


                    return false;
                }
            });

    /*        userViewHolder.img_present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUpdatePresentClickListener.onItemClick(contact.getpID(),contact.getPresent());
                    if (contact.getPresent().equals("1") || contact.getPresent().equals(1)){
                        userViewHolder.img_present.setBackgroundResource(R.drawable.ic_absent1);
                    }else {
                        userViewHolder.img_present.setBackgroundResource(R.drawable.ic_present);
                    }
                    userViewHolder.present.setText((contact.getPresent().equals("1") || contact.getPresent().equals(1) ) ? "غایب" : "حاضر");
                }
            });*/


            userViewHolder.present.setText((contact.getPresent().equals("1") || contact.getPresent().equals(1) ) ? "حاضر" : "غایب");
            Log.d("Present","pppppppppppppp  " + (contact.getPresent().equals("1") || contact.getPresent().equals(1)));
            if (contact.getPresent().equals("1") || contact.getPresent().equals(1)){
                userViewHolder.img_present.setBackgroundResource(R.drawable.ic_present);
            }else {
                userViewHolder.img_present.setBackgroundResource(R.drawable.ic_absent1);
            }



        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView present;
        public ImageView img_present;
        public info.hoang8f.widget.FButton btnUpdate;

        public UserViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.txt_date);
            present = (TextView) view.findViewById(R.id.txt_present);
            img_present = (ImageView) view.findViewById(R.id.img_present);
            img_present = (ImageView) view.findViewById(R.id.img_present);
            btnUpdate = (FButton) view.findViewById(R.id.btnUpdate);
            date.setTypeface(AppHelper.setTypeFace(activity, "IranSans"));
            present.setTypeface(AppHelper.setTypeFace(activity, "IranSans"));

        }
    }
}