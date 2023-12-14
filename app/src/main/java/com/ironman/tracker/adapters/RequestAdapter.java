package com.ironman.tracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.RequestValueHolder;
import com.ironman.tracker.viewHolder.RequestViewHolder;
import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestViewHolder> {
    private ArrayList<RequestValueHolder> requestViewHolders;
    private OnFragmentChangeListener onFragmentChangeListener;

    public RequestAdapter(ArrayList<RequestValueHolder> requestViewHolders, OnFragmentChangeListener onFragmentChangeListener) {
        this.requestViewHolders = requestViewHolders;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.request_view_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.setView(requestViewHolders.get(position));
    }

    @Override
    public int getItemCount() {
        return requestViewHolders.size();
    }
}
