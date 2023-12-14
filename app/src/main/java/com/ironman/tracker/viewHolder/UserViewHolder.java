package com.ironman.tracker.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ironman.tracker.R;
import com.ironman.tracker.fragments.OtherProfileFragment;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.UserValueHolder;
import com.squareup.picasso.Picasso;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private OnFragmentChangeListener onFragmentChangeListener;
    public UserViewHolder(@NonNull View itemView,OnFragmentChangeListener onFragmentChangeListener) {
        super(itemView);
        this.onFragmentChangeListener=onFragmentChangeListener;
    }

    public void setView(UserValueHolder userValueHolder){
        ((TextView)itemView.findViewById(R.id.title)).setText(userValueHolder.userName);
        ((TextView)itemView.findViewById(R.id.subtitle)).setText(userValueHolder.description);


        if(userValueHolder.imageUrl!=null){
            if(!userValueHolder.imageUrl.isEmpty()){
                Picasso.get().load(userValueHolder.imageUrl)
                        .into(((ImageView)itemView.findViewById(R.id.image_view)));
            }
        }

        itemView.setOnClickListener(v -> {
            onFragmentChangeListener.onFragmentChange(new OtherProfileFragment(userValueHolder.uid,onFragmentChangeListener));
        });

    }
}
