package com.ironman.tracker.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.auth.FirebaseAuth;
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.valueHolder.AppUsageValueHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AppStatsFragment extends Fragment {
    private List<DataEntry> data = new ArrayList<>();
    private AnyChartView anyChartView;
    private String uid;

    public AppStatsFragment(String uid) {
        this.uid = uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_stats_fragment,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = (AnyChartView)view.findViewById(R.id.any_chart_view);

        syncData();
    }

    void syncData(){
        Firebase.getAppUsageRef(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<AppUsageValueHolder> tmp=task.getResult().toObjects(AppUsageValueHolder.class);
                data.addAll(getDataEntry(tmp));
                Pie pie = AnyChart.pie();
                pie.data(data);
                Toast.makeText(getContext(),"Processing data please wait...",Toast.LENGTH_LONG).show();
                anyChartView.setChart(pie);

            }

        });

    }

    List<DataEntry> getDataEntry(List<AppUsageValueHolder> appUsageValueHolders){
        List<DataEntry> tmpData = new ArrayList<>();
        for(AppUsageValueHolder appUsageValueHolder:appUsageValueHolders){

            if(!appUsageValueHolder.packageName.equals("com.ironman.tracker")){
                    if(!appUsageValueHolder.appName.trim().isEmpty() && appUsageValueHolder.totalTimeVisible>0){
                        Log.d("APP_DATA_TAG", "app name: "+appUsageValueHolder.appName+" visible:"+appUsageValueHolder.totalTimeVisible);
                        tmpData.add(new ValueDataEntry(appUsageValueHolder.appName.trim().replace("\n"," "),appUsageValueHolder.totalTimeVisible));
                    }
            }
        }
        return tmpData;
    }

}
