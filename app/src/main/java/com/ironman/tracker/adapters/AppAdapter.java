package com.ironman.tracker.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.viewHolder.TrackingViewHolder;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<TrackingViewHolder> {
    private ArrayList<DocumentSnapshot> documentSnapshots;
    private OnFragmentChangeListener onFragmentChangeListener;

    public AppAdapter(ArrayList<DocumentSnapshot> documentSnapshots, OnFragmentChangeListener onFragmentChangeListener) {
        this.documentSnapshots = documentSnapshots;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    @NonNull
    @Override
    public TrackingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_view_holder,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingViewHolder holder, int position) {
        holder.setViews(documentSnapshots.get(position));
    }

    @Override
    public int getItemCount() {
        return documentSnapshots.size();
    }
}
