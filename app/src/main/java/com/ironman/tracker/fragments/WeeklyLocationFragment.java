package com.ironman.tracker.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.ironman.tracker.valueHolder.LocationValueHolder;

import java.util.ArrayList;
import java.util.Date;

public class WeeklyLocationFragment extends Fragment {
    private ArrayList<DocumentSnapshot> documentSnapshots;
    private WeeklyLocationFragment.Adapter adapter;
    private String TAG="MY_LOG";
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_tarcking_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        documentSnapshots =new ArrayList<>();
        adapter=new WeeklyLocationFragment.Adapter(documentSnapshots);
        RecyclerView recyclerView=view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        syncData();
    }

    public WeeklyLocationFragment(String uid) {
        this.uid = uid;
    }

    void syncData(){
        Firebase.getLocationsRef(uid).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(),"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
            if (queryDocumentSnapshots != null) {
                Log.d(TAG, "syncData: "+queryDocumentSnapshots.size());
                documentSnapshots.addAll(queryDocumentSnapshots.getDocuments());
                adapter.notifyDataSetChanged();
            }
        });
    }
    class Adapter extends RecyclerView.Adapter<WeeklyLocationFragment.ViewHolder>{
        private ArrayList<DocumentSnapshot> documentSnapshots;

        public Adapter(ArrayList<DocumentSnapshot> documentSnapshots) {
            this.documentSnapshots = documentSnapshots;
        }
        @NonNull
        @Override
        public WeeklyLocationFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WeeklyLocationFragment.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_view_holder,parent,false));
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
            LocationValueHolder locationValueHolder=documentSnapshot.toObject(LocationValueHolder.class);

            ((TextView)itemView.findViewById(R.id.title)).setText(locationValueHolder.address);
            ((TextView)itemView.findViewById(R.id.subtitle)).setText("click to show on map");
            ((TextView)itemView.findViewById(R.id.time)).setText(new Date(locationValueHolder.time).toString());

            ((ImageView)itemView.findViewById(R.id.image_view)).setImageResource(R.drawable.location_icon);
            String address="";
            itemView.setOnClickListener(v -> {
                double latitude = locationValueHolder.lat;
                double longitude = locationValueHolder.lon;
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + address + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                PackageManager packageManager = getActivity().getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),"No app found to show location on maps",Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
