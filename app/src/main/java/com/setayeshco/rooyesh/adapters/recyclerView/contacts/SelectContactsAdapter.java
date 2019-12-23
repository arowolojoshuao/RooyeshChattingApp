package com.setayeshco.rooyesh.adapters.recyclerView.contacts;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.setayeshco.rooyesh.R;
import com.setayeshco.rooyesh.activities.groups.AddMembersToGroupActivity;
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.activities.profile.ProfilePreviewActivity;
import com.setayeshco.rooyesh.animations.AnimationsUtil;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.ui.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.RealmList;


/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SelectContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private final Activity mActivity;
    private RealmList<ContactsModel> mContactsModel;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 2;
    private MemoryCache memoryCache;
    private String SearchQuery;

    public void setContacts(RealmList<ContactsModel> contactsModelList) {
        this.mContactsModel = contactsModelList;
        notifyDataSetChanged();
    }

    public SelectContactsAdapter(@NonNull Activity mActivity, RealmList<ContactsModel> mContactsModel) {
        this.mActivity = mActivity;
        this.mContactsModel = mContactsModel;
        this.memoryCache = new MemoryCache();
    }

    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    public void animateTo(List<ContactsModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ContactsModel> newModels) {
        int arraySize = mContactsModel.size();
        for (int i = arraySize - 1; i >= 0; i--) {
            final ContactsModel model = mContactsModel.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContactsModel> newModels) {
        int arraySize = newModels.size();
        for (int i = 0; i < arraySize; i++) {
            final ContactsModel model = newModels.get(i);
            if (!mContactsModel.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContactsModel> newModels) {
        int arraySize = newModels.size();
        for (int toPosition = arraySize - 1; toPosition >= 0; toPosition--) {
            final ContactsModel model = newModels.get(toPosition);
            final int fromPosition = mContactsModel.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private ContactsModel removeItem(int position) {
        final ContactsModel model = mContactsModel.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, ContactsModel model) {
        mContactsModel.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ContactsModel model = mContactsModel.remove(fromPosition);
        mContactsModel.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    //Methods for search end
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {

            View itemView = LayoutInflater.from(mActivity).inflate(R.layout.header_contacts, parent, false);
            return new ContactsHeaderViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_contacts, parent, false);
            return new ContactsViewHolder(itemView);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContactsViewHolder) {
            final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            final ContactsModel contactsModel = this.mContactsModel.get(position - 1);
            try {

                String Username;
                String name = UtilsPhone.getContactName(mActivity, contactsModel.getPhone());
                if (name != null) {
                    Username = name;
                } else {
                    Username = contactsModel.getPhone();
                }
                SpannableString recipientUsername = SpannableString.valueOf(Username);
                if (SearchQuery == null) {
                    contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.NORMAL);
                } else {
                    int index = TextUtils.indexOf(Username.toLowerCase(), SearchQuery.toLowerCase());
                    if (index >= 0) {
                        recipientUsername.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorSpanSearch)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        recipientUsername.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.SPANNABLE);
                }


                if (contactsModel.getStatus() != null) {
                    contactsViewHolder.setStatus(contactsModel.getStatus());
                } else {
                    contactsViewHolder.setStatus(contactsModel.getPhone());
                }

                if (contactsModel.isLinked()) {
                    contactsViewHolder.hideInviteButton();
                } else {
                    contactsViewHolder.showInviteButton();
                }
                contactsViewHolder.setUserImage(contactsModel.getImage(), contactsModel.getId());

            } catch (Exception e) {
                AppHelper.LogCat("" + e.getMessage());
            }
            contactsViewHolder.setOnClickListener(view -> {
                if (view.getId() == R.id.user_image) {
                    Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                    mIntent.putExtra("userID", contactsModel.getId());
                    mIntent.putExtra("isGroup", false);
                    mActivity.startActivity(mIntent);
                    AnimationsUtil.setSlideInAnimation(mActivity);
                } else {

                    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


                    Bundle bundle = new Bundle();
                    bundle.putInt("conversationID", 0);
                    bundle.putInt("recipientID", contactsModel.getId());
                    bundle.putBoolean("isGroup", false);
                    MessagesFragment messageFragmentOk = new MessagesFragment();
                    messageFragmentOk.setArguments(bundle);


                    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

                    Intent messagingIntent = new Intent(mActivity, MessagesActivity.class);
                    messagingIntent.putExtra("conversationID", 0);
                    messagingIntent.putExtra("recipientID", contactsModel.getId());
                    messagingIntent.putExtra("isGroup", false);
                    mActivity.startActivity(messagingIntent);
                    AnimationsUtil.setSlideInAnimation(mActivity);
                    mActivity.finish();
                }

            });
        }

    }


    @Override
    public int getItemCount() {
        return mContactsModel.size() > 0 ? mContactsModel.size() + 1 : 1;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return mContactsModel.size() > pos ? Character.toString(mContactsModel.get(pos).getUsername().charAt(0)) : null;
        } catch (Exception e) {
            AppHelper.LogCat(e.getMessage());
            return e.getMessage();
        }
    }


    public class ContactsHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageHeader)
        AppCompatImageView imageHeader;

        ContactsHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                mActivity.startActivity(new Intent(mActivity, AddMembersToGroupActivity.class));
                mActivity.finish();
            });
        }
    }


    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image)
        ImageView userImage;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.status)
        EmojiconTextView status;
        @BindView(R.id.invite)
        TextView invite;

        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setTypeFaces();
        }

        private void setTypeFaces() {
            if (AppConstants.ENABLE_FONTS_TYPES) {
                status.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
                username.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
                invite.setTypeface(AppHelper.setTypeFace(mActivity, "IranSans"));
            }
        }


        void setUserImage(String ImageUrl, int recipientId) {
            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, mActivity, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userImage);
            } else {

                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userImage.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ROWS_IMAGE_URL + ImageUrl, ImageUrl, mActivity, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);


                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        userImage.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeHolderDrawable) {
                        super.onLoadStarted(placeHolderDrawable);
                        userImage.setImageDrawable(placeHolderDrawable);
                    }
                };
                RooyeshImageLoader.loadCircleImage(mActivity, EndPoints.ROWS_IMAGE_URL + ImageUrl, target, R.drawable.image_holder_ur_circle, AppConstants.ROWS_IMAGE_SIZE);
            }
        }


        void hideInviteButton() {
            invite.setVisibility(View.GONE);
        }

        void showInviteButton() {
            invite.setVisibility(View.VISIBLE);
        }

        void setUsername(String phone) {
            String name = UtilsPhone.getContactName(mActivity, phone);
            if (name != null) {
                username.setText(name);
            } else {
                username.setText(phone);
            }

        }

        void setStatus(String Status) {
            String user = UtilsString.unescapeJava(Status);
            if (user.length() > 18)
                status.setText(user.substring(0, 18) + "... " + "");
            else
                status.setText(user);
        }


        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            userImage.setOnClickListener(listener);
        }

    }
}
