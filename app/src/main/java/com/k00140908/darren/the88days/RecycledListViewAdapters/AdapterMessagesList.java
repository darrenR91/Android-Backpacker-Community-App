package com.k00140908.darren.the88days.RecycledListViewAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.k00140908.darren.the88days.Animations.AnimationUtils;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.PagerAdapters.CustomItemClickListener;
import com.k00140908.darren.the88days.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by darre on 15/02/2016.
 */
public class AdapterMessagesList extends RecyclerView.Adapter<AdapterMessagesList.ViewHolderMessageList>{

    private ArrayList<User> mListMessagess = new ArrayList<>();
    private LayoutInflater mInflater;
    private final Context mContext;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm a");


    CustomItemClickListener mlistener;

    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up
    private int mPreviousPosition = 0;


    public AdapterMessagesList(Context context, CustomItemClickListener customItemClickListener) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mlistener = customItemClickListener;
    }
    public void setFriendsList(ArrayList<User> listBackpackers) {
        this.mListMessagess = listBackpackers;
        //update the adapter to reflect the new set of backpackers
        //notifyItemRangeChanged(0, mListMessagess.size());
        notifyDataSetChanged();
        //RecyclerView and java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
        //There was actually a bug in RecyclerView and the support 23.1.1 still not fixed.
    }
    @Override
    public ViewHolderMessageList onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.custom_message_item, parent, false);
        final ViewHolderMessageList viewHolder = new ViewHolderMessageList(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onItemClick(v, viewHolder.getAdapterPosition());
               int i =  viewHolder.getAdapterPosition();
                int u = 0;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderMessageList holder, int position) {
        User currentBackpacker = mListMessagess.get(position);

        holder.backpackerName.setText(currentBackpacker.getUserName());

        if (currentBackpacker.getCount() > 0)
        {
        holder.backpackerNewMessage.setText(currentBackpacker.getCount()+ " new message(s)");
        }

        Date lastMessage = new Date(currentBackpacker.getLastMsg());

        holder.backpackerLastMessage.setText(sdf.format(lastMessage));

        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
        }



        mPreviousPosition = position;

        String urlThumnail = "https://the88daysblob.blob.core.windows.net/profilephotos/"+currentBackpacker.getUserId();
        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext)
                .load(urlThumnail).error(R.drawable.ic_contact_picture)
                .into(holder.backpackerThumbnail);
    }

    @Override
    public int getItemCount() {
        return mListMessagess.size();
    }

    static class ViewHolderMessageList extends RecyclerView.ViewHolder{

        ImageView backpackerThumbnail;
        TextView backpackerName;
        TextView backpackerNewMessage;
        TextView backpackerLastMessage;

        public ViewHolderMessageList(View itemView){
            super(itemView);
            backpackerThumbnail = (ImageView) itemView.findViewById(R.id.backpackerThumbnail);
            backpackerName = (TextView) itemView.findViewById(R.id.backpackerName);
            backpackerNewMessage = (TextView) itemView.findViewById(R.id.backpackerNewMessage);
            backpackerLastMessage = (TextView) itemView.findViewById(R.id.backpackerLastMessage);
        }

    }
}
