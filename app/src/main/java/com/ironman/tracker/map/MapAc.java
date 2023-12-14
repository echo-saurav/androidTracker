package com.ironman.tracker.map;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.valueHolder.LocationValueHolder;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MapAc extends Fragment {
    private String uid;
    private ArrayList<DocumentSnapshot> documentSnapshots;
    private MapView map;

    public MapAc(String uid) {
        this.uid = uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //handle permissions first, before map is created. not depicted here
        documentSnapshots = new ArrayList<>();
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string


        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Enable multi-touch controls
        map.setMultiTouchControls(true);

        // Set the initial zoom level and center point for Bangladesh
        MapController mapController = (MapController) map.getController();
        GeoPoint bangladeshCenter = new GeoPoint(23.8103, 90.4125); // Use the coordinates for Bangladesh
        mapController.setZoom(9); // Adjust the zoom level as needed
        mapController.setCenter(bangladeshCenter);


        syncData();


        view.findViewById(R.id.show_more).setOnClickListener(v->{
            new MapBottom(uid).show(getFragmentManager(),"map");
        });
    }


    void syncData() {
        if(map == null){
            return;
        }

        Firebase.getLocationsRef(uid).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            if (queryDocumentSnapshots != null) {
                documentSnapshots.addAll(queryDocumentSnapshots.getDocuments());
                if(map != null && isVisible()){

                    Polyline line = new Polyline(map);
                    line.getOutlinePaint().setColor(Color.BLUE);

                    for (int i = 0; i < documentSnapshots.size(); i++) {
                        DocumentSnapshot documentSnapshot = documentSnapshots.get(i);
                        LocationValueHolder locationValueHolder = documentSnapshot.toObject(LocationValueHolder.class);
                        addMarker(locationValueHolder.lat, locationValueHolder.lon,
                                locationValueHolder.address, locationValueHolder.time);

                        line.addPoint(new GeoPoint(locationValueHolder.lat, locationValueHolder.lon));
                    }
                    map.getOverlays().add(line);

                    // Refresh the map to display the marker
                    map.invalidate();
                }
            }
        });

    }

    private void showRoad(){
        GeoPoint gPt0 = new GeoPoint(52.507621d, 13.407334d);
        GeoPoint gPt1 = new GeoPoint(52.527621d, 13.427334d);
        Polyline line = new Polyline(map);
        line.getOutlinePaint().setColor(Color.BLUE);

        addMarker(52.507621d, 13.407334d, "fkj",0);
        addMarker(52.527621d, 13.427334d, "fkj",0);
        line .addPoint(gPt0);
        line .addPoint(gPt1);
        map.getOverlays().add(line);
    }

    private void addMarker(double lat, double lon, String address, long time) {
        // Create a marker and add it to the map
        Marker marker = new Marker(map);
        GeoPoint markerPosition = new GeoPoint(lat, lon); // Set the marker's position (you can adjust this)
        marker.setPosition(markerPosition);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(getFormattedTime(time)); // Set a title for the marker (optional)
        marker.setSnippet(address); // Set a description for the marker (optional)


        // Add the marker to the map's overlay
        map.getOverlays().add(marker);


    }

    private String getFormattedTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the date as a string
        String formattedDate = sdf.format(time);
        return  formattedDate;
    }


    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
