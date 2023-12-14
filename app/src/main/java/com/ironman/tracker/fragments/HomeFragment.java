package com.ironman.tracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.bottomSheets.RequestNewUserBottomSheet;
import com.ironman.tracker.adapters.RequestAdapter;
import com.ironman.tracker.adapters.UserTrackingAdapter;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.utills.OnGetTrackingUsers;
import com.ironman.tracker.valueHolder.RequestValueHolder;
import com.ironman.tracker.valueHolder.UserValueHolder;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private OnFragmentChangeListener onFragmentChangeListener;
    private RequestAdapter requestAdapter;
    private UserTrackingAdapter userTrackingAdapter;
    private ArrayList<RequestValueHolder> requestValueHolders;
    private ArrayList<UserValueHolder> userTrackingMap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView requestRecyclerView, userRecyclerView;
        requestRecyclerView = view.findViewById(R.id.request_recycle_view);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecyclerView = view.findViewById(R.id.users_recycle_view);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestValueHolders = new ArrayList<>();
        userTrackingMap = new ArrayList<>();

        requestAdapter = new RequestAdapter(requestValueHolders, onFragmentChangeListener);
        userTrackingAdapter = new UserTrackingAdapter(userTrackingMap, onFragmentChangeListener);

        requestRecyclerView.setAdapter(requestAdapter);
        userRecyclerView.setAdapter(userTrackingAdapter);

        view.findViewById(R.id.add_user).setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                new RequestNewUserBottomSheet().show(getFragmentManager(), "req_bottom");
            }
        });

        syncRequests(view);
        syncTracking(view);
    }

    void syncRequests(View view){
        Firebase.getRequestRef().addSnapshotListener((value, e) -> {
            if (e != null) return;
            if(value==null)return;

            requestValueHolders.clear();
            for (QueryDocumentSnapshot doc : value) {
                requestValueHolders.add(doc.toObject(RequestValueHolder.class));
            }
            refreshMessage(view);
            requestAdapter.notifyDataSetChanged();
        });
    }

    void syncTracking(View view){
        Firebase.getTrackingUsers(new OnGetTrackingUsers() {
            @Override
            public void onUserGetComplete(ArrayList<UserValueHolder> valueHolders) {
                userTrackingMap.clear();
                userTrackingMap.addAll(valueHolders);
                refreshMessage(view);
                userTrackingAdapter.notifyDataSetChanged();
            }

            @Override
            public void error() {

            }
        });
    }

    void refreshMessage(View view){
        if(userTrackingMap.isEmpty()){
            view.findViewById(R.id.user_recycle_title).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.user_recycle_title).setVisibility(View.VISIBLE);
        }

        if(requestValueHolders.isEmpty()){
            view.findViewById(R.id.request_recycle_title).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.request_recycle_title).setVisibility(View.VISIBLE);
        }

        if(requestValueHolders.isEmpty() && userTrackingMap.isEmpty()){
            view.findViewById(R.id.start_message).setVisibility(View.VISIBLE);
        }else {
            view.findViewById(R.id.start_message).setVisibility(View.GONE);
        }
    }

    public HomeFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }


}
