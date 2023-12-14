package com.ironman.tracker.viewHolder;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.ironman.tracker.R;
import com.ironman.tracker.valueHolder.AppUsageValueHolder;

import java.util.Date;

public class TrackingViewHolder extends RecyclerView.ViewHolder {

    public TrackingViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setViews(DocumentSnapshot documentSnapshot){
        Context context=itemView.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppUsageValueHolder appUsageValueHolder =documentSnapshot.toObject(AppUsageValueHolder.class);

//            ((TextView)itemView.findViewById(R.id.title)).setText(getAppNameFromPkgName(context,usageStats.getPackageName()));
//            ((TextView)itemView.findViewById(R.id.subtitle)).setText(usageStats.getTotalTimeInForeground()/(60*1000)+" minute");
//            ((TextView)itemView.findViewById(R.id.time)).setText(new Date(usageStats.getLastTimeUsed()).toString());
//
            ((TextView)itemView.findViewById(R.id.title)).setText(appUsageValueHolder.packageName);
            ((TextView)itemView.findViewById(R.id.subtitle)).setText(appUsageValueHolder.totalTimeVisible+"");
            ((TextView)itemView.findViewById(R.id.time)).setText(appUsageValueHolder.time+"");


        }
    }

//    String getAppNameFromPkgName(Context context, String Packagename) {
//        try {
//            PackageManager packageManager = context.getPackageManager();
//            ApplicationInfo info = packageManager.getApplicationInfo(Packagename, PackageManager.GET_META_DATA);
//            return packageManager.getApplicationLabel(info).toString();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    Drawable getDrawableFromPkgName(Context context, String Packagename) {
//        try {
//            PackageManager packageManager = context.getPackageManager();
//            ApplicationInfo info = packageManager.getApplicationInfo(Packagename, PackageManager.GET_META_DATA);
//            return packageManager.getApplicationIcon(info);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
