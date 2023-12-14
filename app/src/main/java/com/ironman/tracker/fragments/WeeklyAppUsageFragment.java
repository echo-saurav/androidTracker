package com.ironman.tracker.fragments;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.valueHolder.AppUsageValueHolder;
import com.ironman.tracker.valueHolder.WeeklyAppValueHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class WeeklyAppUsageFragment extends Fragment {
    private String uid;
    private List<WeeklyAppValueHolder> weeklyAppValueHolders;
    private Adapter adapter;

    public WeeklyAppUsageFragment(String uid) {
        this.uid = uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_usage_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weeklyAppValueHolders =new ArrayList<>();
        adapter=new Adapter(weeklyAppValueHolders);
        RecyclerView recyclerView=view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        syncData();

    }
    void syncData(){
        Firebase.getAppUsageRef(uid).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(),"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
            if (queryDocumentSnapshots != null) {

                ArrayList<AppUsageValueHolder> tmp = new ArrayList<>(queryDocumentSnapshots.toObjects(AppUsageValueHolder.class));
                weeklyAppValueHolders.addAll(getUseParentage(tmp));

                adapter.notifyDataSetChanged();
            }
        });

        Firebase.getAppUsageRef(uid).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               List<AppUsageValueHolder> tmp=task.getResult().toObjects(AppUsageValueHolder.class);
               weeklyAppValueHolders.addAll(getUseParentage(tmp));
           }

        });

    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private List<WeeklyAppValueHolder> documentSnapshots;

        public Adapter(List<WeeklyAppValueHolder> documentSnapshots) {
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

        public void setViews(WeeklyAppValueHolder weeklyAppValueHolders){
            if(weeklyAppValueHolders==null) return;


            ((TextView)itemView.findViewById(R.id.title)).setText(weeklyAppValueHolders.name);
            ((TextView)itemView.findViewById(R.id.subtitle)).setText("Usage this week: "+weeklyAppValueHolders.percent+"%");

            ((ImageView)itemView.findViewById(R.id.image_view)).setImageResource(R.drawable.apps_icon);

//            itemView.setOnClickListener(v -> {
//                PackageManager pm = getContext().getPackageManager();
//                Intent launchIntent = pm.getLaunchIntentForPackage(appUsageValueHolder.packageName);
//
//                if (launchIntent != null) {
//                    getContext().startActivity(launchIntent);
//                } else {
//                    Toast.makeText(getContext(), "Apps not found in your device , please install", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

     List<WeeklyAppValueHolder> getUseParentage(List<AppUsageValueHolder> appUsageValueHolders){
        ArrayList<AppUsageValueHolder> tmpAppUsageValueHolders=new ArrayList<>();

        long totalMin=0;

        for(AppUsageValueHolder appUsageValueHolder:appUsageValueHolders){
            if(appUsageValueHolder.totalTimeVisible!=0){
                tmpAppUsageValueHolders.add(appUsageValueHolder);
                totalMin=totalMin+appUsageValueHolder.totalTimeVisible;
            }
        }

         Log.d("MY_LOG", "getUseParentage: total min:"+totalMin);

        List<WeeklyAppValueHolder> weeklyAppValueHolders=new ArrayList<>();

         for(AppUsageValueHolder appUsageValueHolder:appUsageValueHolders){
             if(appUsageValueHolder.totalTimeVisible!=0){

                 double percent=((double) appUsageValueHolder.totalTimeVisible/(double) totalMin)*100;
                 percent= BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP).doubleValue();

                 Log.d("MY_LOG", "getUseParentage: per percent "+percent);
                 weeklyAppValueHolders.add(new WeeklyAppValueHolder(
                         appUsageValueHolder.appName,
                         percent
                 ));

             }
         }

         return weeklyAppValueHolders;

     }


}
