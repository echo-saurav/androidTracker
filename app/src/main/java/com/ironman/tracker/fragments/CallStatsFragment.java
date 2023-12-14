package com.ironman.tracker.fragments;

import android.os.Bundle;
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
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.valueHolder.CallLogValueHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CallStatsFragment extends Fragment {
    private List<DataEntry> data = new ArrayList<>();
    private AnyChartView anyChartView;
    private String uid;

    public CallStatsFragment(String uid) {
        this.uid = uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_stats_fragment,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = (AnyChartView)view.findViewById(R.id.any_chart_view);
        syncData();
    }


    void syncData(){
        Firebase.getCallLogRef(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<CallLogValueHolder> tmp=task.getResult().toObjects(CallLogValueHolder.class);
                data.addAll(getDataEntry(tmp));
                Pie pie = AnyChart.pie();
                pie.data(data);
                Toast.makeText(getContext(),"Processing data please wait...",Toast.LENGTH_LONG).show();
                anyChartView.setChart(pie);
            }

        });
    }



    List<DataEntry> getDataEntry(List<CallLogValueHolder> callLogValueHolders){
        List<DataEntry> tmpData = new ArrayList<>();

        HashMap<String,CallLogValueHolder> holderHashMap=new HashMap<>();

        for(CallLogValueHolder callLogValueHolder:callLogValueHolders){
                holderHashMap.put(callLogValueHolder.number,callLogValueHolder);
        }

        Iterator iterator=holderHashMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry)iterator.next();
            CallLogValueHolder valueHolder= (CallLogValueHolder) pair.getValue();

            int occurrences = Collections.frequency(callLogValueHolders, valueHolder);
            tmpData.add(new ValueDataEntry(valueHolder.number.trim().replace("\n",""),occurrences));

            iterator.remove();
        }

        return tmpData;
    }

}
