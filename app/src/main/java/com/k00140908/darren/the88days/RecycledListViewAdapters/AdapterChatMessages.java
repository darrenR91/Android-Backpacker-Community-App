package com.k00140908.darren.the88days.RecycledListViewAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.k00140908.darren.the88days.Animations.AnimationUtils;
import com.k00140908.darren.the88days.Model.Message;
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
public class AdapterChatMessages extends RecyclerView.Adapter<AdapterChatMessages.ViewHolderMessageList>{

    private ArrayList<Message> mListMessagess = new ArrayList<>();
    private LayoutInflater mInflater;
    private final Context mContext;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm a");
    User user;

    CustomItemClickListener mlistener;

    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up
    private int mPreviousPosition = 0;


    public AdapterChatMessages(Context context, CustomItemClickListener customItemClickListener) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mlistener = customItemClickListener;
    }
    public void setFriendsList(ArrayList<Message> listBackpackers, User user) {
        this.mListMessagess = listBackpackers;
        this.user = user;
        //update the adapter to reflect the new set of backpackers
        notifyItemRangeChanged(0, mListMessagess.size());
        //notifyDataSetChanged();
        //RecyclerView and java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
        //There was actually a bug in RecyclerView and the support 23.1.1 still not fixed.

    }
    @Override
    public ViewHolderMessageList onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.custom_chat_message_item, parent, false);
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
        Message currentMessage = mListMessagess.get(position);

        if(currentMessage.getBackpackerReceiver().equals(user.getUserId()))
        {
            holder.mLeftView.setVisibility(View.VISIBLE);
            holder.mRightView.setVisibility(View.GONE);
            holder.senderMessage.setText(currentMessage.getMsg());
            holder.senderDate.setText(sdf.format(currentMessage.getSentAt()));
        }
        else
        {
            holder.mRightView.setVisibility(View.VISIBLE);
            holder.mLeftView.setVisibility(View.GONE);
            holder.recieverMessage.setText(currentMessage.getMsg());
            holder.recieverDate.setText(sdf.format(currentMessage.getSentAt()));
        }
//
//
//
        mPreviousPosition = position;
//
    }

    @Override
    public int getItemCount() {
        return mListMessagess.size();
    }

    static class ViewHolderMessageList extends RecyclerView.ViewHolder{

        TextView senderMessage;
        TextView senderDate;
        TextView recieverDate;
        TextView recieverMessage;
        View mRightView;
        View mLeftView;

        public ViewHolderMessageList(View itemView){
            super(itemView);
            senderMessage = (TextView) itemView.findViewById(R.id.SenderMsg);
            senderDate = (TextView) itemView.findViewById(R.id.SenderMsgTime);
            recieverMessage = (TextView) itemView.findViewById(R.id.RecieverMsg);
            recieverDate = (TextView) itemView.findViewById(R.id.RecieverMsgTime);

            mLeftView = itemView.findViewById(R.id.SenderMsgBlock);
            mRightView = itemView.findViewById(R.id.RecieverMsgBlock);
        }

    }
}
