package com.ironman.tracker.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ironman.tracker.R;
import com.ironman.tracker.adapters.TabViewPagerAdapter;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.UserValueHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.ironman.tracker.database.Firebase.getUserInfo;

public class OtherProfileFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabViewPagerAdapter viewPagerAdapter;
    private ArrayList<Fragment> fragments;
    private String uid;
    private OnFragmentChangeListener onFragmentChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.others_profile_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager=view.findViewById(R.id.view_pager);
        tabLayout=view.findViewById(R.id.tab_layout);
        fragments=new ArrayList<>();
        fragments.add(new AppUsageListFragment(uid,onFragmentChangeListener));
        fragments.add(new CallLogListFragment(uid,onFragmentChangeListener));
        fragments.add(new LocationListFragment(uid,onFragmentChangeListener));
        viewPagerAdapter=new TabViewPagerAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        view.findViewById(R.id.hide_heading_button).setOnClickListener(v -> {

            if(view.findViewById(R.id.heading).getVisibility()==View.VISIBLE){
                ((FloatingActionButton)view.findViewById(R.id.hide_heading_button)).setImageResource(R.drawable.down_icon);
                view.findViewById(R.id.heading).setVisibility(View.GONE);
            }else {
                ((FloatingActionButton)view.findViewById(R.id.hide_heading_button)).setImageResource(R.drawable.up_arrow_icon);
                view.findViewById(R.id.heading).setVisibility(View.VISIBLE);
            }

        });

//        view.findViewById(R.id.weekly_report_button).setOnClickListener(v -> {
//            onFragmentChangeListener.onFragmentChange(new WeeklyReportFragment(uid,onFragmentChangeListener));
////            onFragmentChangeListener.onFragmentChange(new AppStatsFragment(uid));
//        });


        viewPagerAdapter.notifyDataSetChanged();
        setProfileInfo(view);
    }

    void copyId(String text){
        ClipboardManager clipboard = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("profileId", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(),"Profile id copid",Toast.LENGTH_LONG).show();
    }

    void setProfileInfo(View view){
        getUserInfo(uid, (documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(getContext(),"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                UserValueHolder userValueHolder=documentSnapshot.toObject(UserValueHolder.class);
                if(userValueHolder!=null){
                    ((TextView)view.findViewById(R.id.user_id)).setText(documentSnapshot.getId());
                    ((TextView)view.findViewById(R.id.user_name)).setText(userValueHolder.userName);
                    ((TextView)view.findViewById(R.id.user_description)).setText(userValueHolder.description);
                    ((TextView)view.findViewById(R.id.user_email)).setText(userValueHolder.email);

                    view.findViewById(R.id.copy_button).setOnClickListener(v -> {
                        copyId(documentSnapshot.getId());
                    });

                    if(userValueHolder.imageUrl!=null){

                        if(!userValueHolder.imageUrl.isEmpty()){

                            Picasso.get().load(userValueHolder.imageUrl).error(R.mipmap.ic_launcher_round).into((ImageView)view.findViewById(R.id.image_view));
                        }
                    }
                }
            }
        });
    }

    public OtherProfileFragment(String uid,OnFragmentChangeListener onFragmentChangeListener) {
        this.uid = uid;
        this.onFragmentChangeListener=onFragmentChangeListener;
    }
}
