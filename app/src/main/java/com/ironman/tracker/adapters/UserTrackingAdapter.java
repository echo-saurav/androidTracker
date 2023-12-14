package com.ironman.tracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.UserValueHolder;
import com.ironman.tracker.viewHolder.UserViewHolder;

import java.util.ArrayList;

public class UserTrackingAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private ArrayList<UserValueHolder> userTrackingValueHolders;
    private OnFragmentChangeListener onFragmentChangeListener;

    public UserTrackingAdapter(ArrayList<UserValueHolder> userTrackingValueHolders, OnFragmentChangeListener onFragmentChangeListener) {
        this.userTrackingValueHolders = userTrackingValueHolders;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view_holder, parent, false),onFragmentChangeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setView(userTrackingValueHolders.get(position));
    }

    @Override
    public int getItemCount() {
        return userTrackingValueHolders.size();
    }

}
