package com.ironman.tracker.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.AppUsageValueHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AppUsageListFragment extends Fragment {
    private String uid;
    private ArrayList<DocumentSnapshot> documentSnapshots;
    private Adapter adapter;
    private OnFragmentChangeListener onFragmentChangeListener;

    public AppUsageListFragment(String uid, OnFragmentChangeListener onFragmentChangeListener) {
        this.uid = uid;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_usage_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        documentSnapshots =new ArrayList<>();
        adapter=new Adapter(documentSnapshots);
        RecyclerView recyclerView=view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        syncData();

        view.findViewById(R.id.show_weekly_report).setOnClickListener(v -> {
            onFragmentChangeListener.onFragmentChange(new AppStatsFragment(uid));
        });

    }
    void syncData(){
        Firebase.getAppUsageRef(uid).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(),"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
            if (queryDocumentSnapshots != null) {
//                documentSnapshots.addAll(queryDocumentSnapshots.getDocuments());
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){

                    if(!documentSnapshot.getString("appName").isEmpty()){
                        documentSnapshots.add(documentSnapshot);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private ArrayList<DocumentSnapshot> documentSnapshots;

        public Adapter(ArrayList<DocumentSnapshot> documentSnapshots) {
            this.documentSnapshots = documentSnapshots;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_view_holder,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setViews(documentSnapshots.get(position));
        }

        @Override
        public int getItemCount() {
            return documentSnapshots.size();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setViews(DocumentSnapshot documentSnapshot){
            AppUsageValueHolder appUsageValueHolder=documentSnapshot.toObject(AppUsageValueHolder.class);
            if(appUsageValueHolder==null) return;

            long totalTimeVisibleMinute=appUsageValueHolder.totalTimeVisible/(1000*60);

            ((TextView)itemView.findViewById(R.id.title)).setText(appUsageValueHolder.appName);
            ((TextView)itemView.findViewById(R.id.subtitle)).setText("Total time used :" +totalTimeVisibleMinute+" minute");
            ((TextView)itemView.findViewById(R.id.time)).setText(new Date(appUsageValueHolder.time).toString());

            ((ImageView)itemView.findViewById(R.id.image_view)).setImageResource(R.drawable.apps_icon);

            itemView.setOnClickListener(v -> {
                PackageManager pm = getContext().getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(appUsageValueHolder.packageName);

                if (launchIntent != null) {
                    getContext().startActivity(launchIntent);
                } else {
                    Toast.makeText(getContext(), "Apps not found in your device , please install", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
