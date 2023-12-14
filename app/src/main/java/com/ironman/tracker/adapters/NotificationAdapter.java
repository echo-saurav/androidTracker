package com.ironman.tracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.NotificationValueHolder;
import com.ironman.tracker.viewHolder.NotificationViewHolder;

import java.util.ArrayList;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationViewHolder> {
    private ArrayList<NotificationValueHolder> notificationValueHolders;
    private OnFragmentChangeListener onFragmentChangeListener;

    public NotificationAdapter(ArrayList<NotificationValueHolder> notificationValueHolders, OnFragmentChangeListener onFragmentChangeListener) {
        this.notificationValueHolders = notificationValueHolders;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setViews(notificationValueHolders.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationValueHolders.size();
    }
}
