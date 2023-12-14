package com.ironman.tracker.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.bottomSheets.AlertDialogBottomSheet;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnAlertDialogListener;
import com.ironman.tracker.valueHolder.CallLogValueHolder;
import com.ironman.tracker.valueHolder.WeeklyCallValueHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WeeklyCallLogFragment extends Fragment {
    private List<WeeklyCallValueHolder> documentSnapshots;
    private WeeklyCallLogFragment.Adapter adapter;
    private String uid;
    private int CALL_PHONE_REQUEST_CODE=309;

    public WeeklyCallLogFragment(String uid) {
        this.uid = uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_log_fragment,container,false);
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
    }
    void syncData(){
        Firebase.getCallLogRef(uid).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(),"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
            if (queryDocumentSnapshots != null) {
//                documentSnapshots.addAll(queryDocumentSnapshots.getDocuments());
                adapter.notifyDataSetChanged();
            }
        });
    }
    class Adapter extends RecyclerView.Adapter<WeeklyCallLogFragment.ViewHolder>{
        private List<WeeklyCallValueHolder> documentSnapshots;

        public Adapter(List<WeeklyCallValueHolder> documentSnapshots) {
            this.documentSnapshots = documentSnapshots;
        }
        @NonNull
        @Override
        public WeeklyCallLogFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WeeklyCallLogFragment.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_view_holder,parent,false));
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

        public void setViews(WeeklyCallValueHolder weeklyCallValueHolder){


//            ((TextView)itemView.findViewById(R.id.title)).setText(weeklyCallValueHolder.callLogValueHolder.name);
//            ((TextView)itemView.findViewById(R.id.subtitle)).setText(weeklyCallValueHolder.callLogValueHolder.number);
//            ((TextView)itemView.findViewById(R.id.time)).setText(new Date(weeklyCallValueHolder.callLogValueHolder.time).toString());

            ((ImageView)itemView.findViewById(R.id.image_view)).setImageResource(R.drawable.call_icon);

            itemView.setOnClickListener(v -> {
                if(isCallLogsPermissionGranted(getContext())){
//                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + weeklyCallValueHolder.callLogValueHolder.number));
//                    startActivity(intent);
                }else {

                    new AlertDialogBottomSheet("Call", "To Call this number you need to give call phone permission",
                            new OnAlertDialogListener() {
                                @Override
                                public void yes() {
                                    if(getActivity()==null) return;
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
                                }

                                @Override
                                public void no() {
                                    Toast.makeText(getContext(),"foreground service permission getting failed",Toast.LENGTH_LONG).show();
                                }
                            }).show(getFragmentManager(),"background_tracking_alert");

                }
            });
        }
    }


    public boolean isCallLogsPermissionGranted(Context context) {
        if (context == null) return false;
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

//    List<WeeklyCallValueHolder> getWeeklyCall(List<CallTmp> callLogValueHolders){
//
//        HashMap<String,WeeklyCallValueHolder> callTmpHashMap=new HashMap<>();
//
//        for(CallTmp callTmp: callLogValueHolders){
//            int fr= Collections.frequency(callLogValueHolders,callTmp);
//            callTmpHashMap.put(callTmp.number,new WeeklyCallValueHolder(
//                    callTmp.name,
//                    callTmp.name,
//                    0,
//                    fr
//            ));
//        }
//
//
//
//
//
//
//    }

    class CallTmp{
        public String name;
        public String number;

        public CallTmp(String name, String number) {
            this.name = name;
            this.number = number;
        }
    }
}
