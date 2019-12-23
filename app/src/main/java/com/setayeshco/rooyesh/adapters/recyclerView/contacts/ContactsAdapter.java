package com.setayeshco.rooyesh.adapters.recyclerView.contacts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.setayeshco.rooyesh.activities.messages.MessagesActivity;
import com.setayeshco.rooyesh.activities.profile.ProfilePreviewActivity;
import com.setayeshco.rooyesh.app.AppConstants;
import com.setayeshco.rooyesh.app.EndPoints;
import com.setayeshco.rooyesh.fragments.home.MessagesFragment;
import com.setayeshco.rooyesh.helpers.AppHelper;
import com.setayeshco.rooyesh.helpers.Files.cache.ImageLoader;
import com.setayeshco.rooyesh.helpers.Files.cache.MemoryCache;
import com.setayeshco.rooyesh.helpers.RateHelper;
import com.setayeshco.rooyesh.helpers.UtilsPhone;
import com.setayeshco.rooyesh.helpers.UtilsString;
import com.setayeshco.rooyesh.helpers.images.RooyeshImageLoader;
import com.setayeshco.rooyesh.models.users.contacts.ContactsModel;
import com.setayeshco.rooyesh.ui.RecyclerViewFastScroller;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.RealmList;


/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private RealmList<ContactsModel> mContactsModel;
    private String SearchQuery;
    private MemoryCache memoryCache;

    public ContactsAdapter(RealmList<ContactsModel> mContactsModel) {
        this.mContactsModel = mContactsModel;
        this.memoryCache = new MemoryCache();

    }

    public ContactsAdapter() {
        this.memoryCache = new MemoryCache();
        this.mContactsModel = new RealmList<>();
    }


    public void setContacts(RealmList<ContactsModel> contactsModelList) {
        this.mContactsModel = contactsModelList;
        notifyDataSetChanged();
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contacts, parent, false);
        return new ContactsViewHolder(itemView);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ContactsViewHolder) {
            final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            Context context = holder.itemView.getContext();

            final ContactsModel contactsModel = this.mContactsModel.get(position);
            try {

                contactsViewHolder.setUsername(contactsModel.getUsername());
                contactsViewHolder.setstd_number(contactsModel.getStd_number());


                String Username;
                String name = UtilsPhone.getContactName(context, contactsModel.getPhone());
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
                        recipientUsername.setSpan(new ForegroundColorSpan(AppHelper.getColor(context, R.color.colorSpanSearch)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        recipientUsername.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.SPANNABLE);
                }
                if (contactsModel.getStatus() != null) {
                    String status = UtilsString.unescapeJava(contactsModel.getStatus());
                    if (status.length() > 33)
                        contactsViewHolder.setStatus(status.substring(0, 33) + "... " + "");

                    else
                        contactsViewHolder.setStatus(status);

                } else {
                    contactsViewHolder.setStatus(contactsModel.getPhone());
                }

                if (contactsModel.isLinked() && contactsModel.isActivate()) {
                    contactsViewHolder.hideInviteButton();
                } else {
                    contactsViewHolder.showInviteButton();
                }
                contactsViewHolder.setUserImage(contactsModel.getImage(), contactsModel.getId());

            } catch (Exception e) {
                AppHelper.LogCat("Contacts adapters Exception " + e.getMessage());
            }
            contactsViewHolder.setOnClickListener(view -> {
                if (view.getId() == R.id.user_image) {
                    RateHelper.significantEvent(context);
                    if (AppHelper.isAndroid5()) {
                        if (contactsModel.isLinked()) {
                            Intent mIntent = new Intent(context, ProfilePreviewActivity.class);
                            mIntent.putExtra("userID", contactsModel.getId());
                            mIntent.putExtra("isGroup", false);
                            context.startActivity(mIntent);
                        }
                    } else {
                        if (contactsModel.isLinked()) {
                            Intent mIntent = new Intent(context, ProfilePreviewActivity.class);
                            mIntent.putExtra("userID", contactsModel.getId());
                            mIntent.putExtra("isGroup", false);
                            context.startActivity(mIntent);
                            // context.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                        }
                    }
                } else {


                    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


                    Bundle bundle = new Bundle();
                    bundle.putInt("conversationID", 0);
                    bundle.putBoolean("isGroup", false);
                    bundle.putInt("recipientID", contactsModel.getId());
                    MessagesFragment messageFragmentOk = new MessagesFragment();
                    messageFragmentOk.setArguments(bundle);




                    //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


                    RateHelper.significantEvent(context);
                    if (contactsModel.isLinked() && contactsModel.isActivate()) {
                        Intent messagingIntent = new Intent(context, MessagesActivity.class);
                        messagingIntent.putExtra("conversationID", 0);
                        messagingIntent.putExtra("recipientID", contactsModel.getId());
                        messagingIntent.putExtra("isGroup", false);
                        context.startActivity(messagingIntent);
                        //  AnimationsUtil.setSlideInAnimation(context);
                    } else {
                        String number = contactsModel.getPhone();
                        contactsViewHolder.setShareApp(context.getString(R.string.invitation_from) + " " + number);
                    }
                }

            });


        }


    }


    @Override
    public int getItemCount() {
        return mContactsModel.size() > 0 ? mContactsModel.size() : 0;
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

    public ContactsModel getItem(int position) {
        return mContactsModel.get(position);
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        Context context;
        @BindView(R.id.user_image)
        ImageView userImage;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.status)
        EmojiconTextView status;
        @BindView(R.id.invite)
        TextView invite;
        @BindView(R.id.std_number)
        TextView std_number;

        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            setTypeFaces();
        }


        private void setTypeFaces() {
            if (AppConstants.ENABLE_FONTS_TYPES) {
                status.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                invite.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                username.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
                std_number.setTypeface(AppHelper.setTypeFace(context, "IranSans"));
            }
        }

        void setShareApp(String subject) {

            //   Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + "ic_launcher");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, AppConstants.INVITE_MESSAGE_SMS + String.format(context.getString(R.string.rate_helper_google_play_url), context.getPackageName()));
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("text/*");
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.shareItem)));
        }

        void setUserImage(String ImageUrl, int recipientId) {
            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, context, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userImage);
            } else {

                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userImage.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ROWS_IMAGE_URL + ImageUrl, ImageUrl, context, recipientId, AppConstants.USER, AppConstants.ROW_PROFILE);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (ImageUrl != null) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(ImageUrl));
                            } catch (IOException ex) {
                                // AppHelper.LogCat(e.getMessage());
                            }
                            if (bitmap != null) {
                                ImageLoader.SetBitmapImage(bitmap, userImage);
                            } else {
                                userImage.setImageDrawable(errorDrawable);
                            }
                        } else {
                            userImage.setImageDrawable(errorDrawable);
                        }
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        userImage.setImageDrawable(placeholder);
                    }
                };

                RooyeshImageLoader.loadCircleImage(context, EndPoints.ROWS_IMAGE_URL + ImageUrl, target, R.drawable.image_holder_ur_circle, AppConstants.ROWS_IMAGE_SIZE);
            }
        }


        void hideInviteButton() {
            invite.setVisibility(View.GONE);
        }

        void showInviteButton() {
            invite.setVisibility(View.VISIBLE);
        }

        void setUsername(String phone) {
            String name = UtilsPhone.getContactName(context, phone);
            if (name != null) {
                username.setText(name);
            } else {
                username.setText(phone);
            }


        }
        void setstd_number(String std) {
//            String name = UtilsPhone.getContactName(context, phone);
//            if (name != null) {
//                username.setText(name);
//            } else {
//                username.setText(phone);
//            }
            std_number.setText(std);


        }

        void setStatus(String Status) {
            status.setText(Status);
        }


        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            userImage.setOnClickListener(listener);
        }

    }


}
