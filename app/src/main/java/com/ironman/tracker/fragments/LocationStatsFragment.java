package com.ironman.tracker.fragments;

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
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.valueHolder.LocationValueHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocationStatsFragment extends Fragment {
    private List<DataEntry> data = new ArrayList<>();
    private AnyChartView anyChartView;
    private String uid;

    public LocationStatsFragment(String uid) {
        this.uid = uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_stats_fragment,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = (AnyChartView)view.findViewById(R.id.any_chart_view);
        syncData();
    }


    void syncData(){
        Firebase.getLocationsRef(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<LocationValueHolder> tmp=task.getResult().toObjects(LocationValueHolder.class);
                data.addAll(getDataEntry(tmp));
                Pie pie = AnyChart.pie();
                pie.data(data);
                Toast.makeText(getContext(),"Processing data please wait...",Toast.LENGTH_LONG).show();
                anyChartView.setChart(pie);
            }

        });
    }

    List<DataEntry> getDataEntry(List<LocationValueHolder> locationValueHolders){
        List<DataEntry> tmpData = new ArrayList<>();

        HashMap<String,LocationValueHolder> holderHashMap=new HashMap<>();

        double prevLat=0,prevLon=0;

        for(LocationValueHolder locationValueHolder:locationValueHolders){
            if(prevLat==0 && prevLon==0){
                holderHashMap.put(locationValueHolder.address,locationValueHolder);
                prevLat=locationValueHolder.lat;
                prevLon=locationValueHolder.lon;
            }
            else {

                if(getDistance(locationValueHolder.lat,locationValueHolder.lon,prevLat,prevLon)>1.0){
                    holderHashMap.put(locationValueHolder.address,locationValueHolder);
                    prevLat=locationValueHolder.lat;
                    prevLon=locationValueHolder.lon;
                }
            }

        }

        Log.d("MY_LOG", "getDataEntry: hashSize:"+holderHashMap.size());

        Iterator iterator=holderHashMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry)iterator.next();
            LocationValueHolder valueHolder= (LocationValueHolder) pair.getValue();

            if(valueHolder!=null){

                if(valueHolder.address!=null){
                    Log.d("MY_LOG", "getDataEntry: map:"+pair.getKey().toString()+" data: "+pair.getValue());
                    int occurrences = Collections.frequency(locationValueHolders, valueHolder);

                    if(valueHolder.address.length()>30){
                        tmpData.add(new ValueDataEntry(valueHolder.address.substring(0,29).replace("\n"," "),occurrences));
                    }else {
                        tmpData.add(new ValueDataEntry(valueHolder.address.replace("\n"," "),occurrences));
                    }

                }
            }


            iterator.remove();
        }

        return tmpData;
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
