package com.k00140908.darren.the88days.RecycledListViewAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.k00140908.darren.the88days.Animations.AnimationUtils;
import com.k00140908.darren.the88days.Model.Car;
import com.k00140908.darren.the88days.Model.Farm;
import com.k00140908.darren.the88days.PagerAdapters.CustomItemClickListener;
import com.k00140908.darren.the88days.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by darre on 15/02/2016.
 */
public class AdapterCars extends RecyclerView.Adapter<AdapterCars.ViewHolderCar>{

    private ArrayList<Car> mListCars = new ArrayList<>();
    private LayoutInflater mInflater;
    private final Context mContext;

    CustomItemClickListener mlistener;

    //keep track of the previous position for animations where scrolling down requires a different animation compared to scrolling up
    private int mPreviousPosition = 0;


    public AdapterCars(Context context, CustomItemClickListener customItemClickListener) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mlistener = customItemClickListener;
    }
    public void setCarsList(ArrayList<Car> listCars) {
        this.mListCars = listCars;
        //update the adapter to reflect the new set of cars
        //notifyItemRangeChanged(0,listCars.size());
        notifyDataSetChanged();
        //RecyclerView and java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
        //There was actually a bug in RecyclerView and the support 23.1.1 still not fixed.
    }
    @Override
    public ViewHolderCar onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.custom_car_item, parent, false);
        final ViewHolderCar viewHolder = new ViewHolderCar(view);
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
    public void onBindViewHolder(ViewHolderCar holder, int position) {
        Car currentCar = mListCars.get(position);

       holder.carName.setText(currentCar.getTitle());

        //holder.carWorkStatus.setText(currentBackpacker.);// add rating!!!! future work

        holder.carDistance.setText(currentCar.getDistance().toString()+" Km");
        holder.carWorkStatus.setText("$"+currentCar.getPrice().toString());


        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
        }



        mPreviousPosition = position;

        String urlThumnail = currentCar.getCarPhoto();
        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext)
                .load(urlThumnail).error(R.drawable.ic_contact_picture)
                .into(holder.carThumbnail);
    }

    @Override
    public int getItemCount() {
        return mListCars.size();
    }

    static class ViewHolderCar extends RecyclerView.ViewHolder{

        ImageView carThumbnail;
        TextView carName;
        TextView carWorkStatus;
        TextView carDistance;

        public ViewHolderCar(View itemView){
            super(itemView);
            carThumbnail = (ImageView) itemView.findViewById(R.id.carThumbnail);
            carName = (TextView) itemView.findViewById(R.id.carName);
            carWorkStatus = (TextView) itemView.findViewById(R.id.carWorkStatus);
            carDistance = (TextView) itemView.findViewById(R.id.carDistance);
        }

    }
}
