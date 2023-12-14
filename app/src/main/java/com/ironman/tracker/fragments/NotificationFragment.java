package com.ironman.tracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.adapters.NotificationAdapter;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.NotificationValueHolder;
import java.util.ArrayList;

import static com.ironman.tracker.database.Firebase.getNotificationRef;

public class NotificationFragment extends Fragment {
    private ArrayList<NotificationValueHolder> notificationValueHolders;
    private NotificationAdapter notificationAdapter;
    private OnFragmentChangeListener onFragmentChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView notificationRecyclerView;
        notificationRecyclerView = view.findViewById(R.id.notification_recycle_view);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationValueHolders = new ArrayList<>();
        notificationAdapter=new NotificationAdapter(notificationValueHolders,onFragmentChangeListener);
        notificationRecyclerView.setAdapter(notificationAdapter);

        syncNotification();
    }

    void syncNotification(){
        Firebase.getNotificationRef().addSnapshotListener((value, e) -> {
            if (e != null) return;
            if(value==null)return;

            notificationValueHolders.clear();
            for (QueryDocumentSnapshot doc : value) {
                notificationValueHolders.add(doc.toObject(NotificationValueHolder.class));
            }
            notificationAdapter.notifyDataSetChanged();
        });
    }

    public NotificationFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }
}
