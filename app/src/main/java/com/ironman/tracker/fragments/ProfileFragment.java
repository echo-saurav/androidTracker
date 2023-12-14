package com.ironman.tracker.fragments;

import android.app.usage.UsageStats;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ironman.tracker.activities.EditUserActivity;
import com.ironman.tracker.R;
import com.ironman.tracker.adapters.AppAdapter;
import com.ironman.tracker.adapters.TabViewPagerAdapter;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.getInfo.AppUsages;
import com.ironman.tracker.utills.OnFragmentChangeListener;
import com.ironman.tracker.valueHolder.UserValueHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.ironman.tracker.database.Firebase.getUserInfo;


public class ProfileFragment extends Fragment {
    private OnFragmentChangeListener onFragmentChangeListener;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabViewPagerAdapter viewPagerAdapter;
    private ArrayList<Fragment> fragments;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProfileInfo(view);
        view.findViewById(R.id.edit_user_button).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditUserActivity.class));
        });

        view.findViewById(R.id.copy_button).setOnClickListener(v -> {
            copyId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        });

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
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
//        });
//

        viewPagerAdapter.notifyDataSetChanged();
    }

    void copyId(String text){
        ClipboardManager clipboard = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("profileId", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(),"Profile id copid",Toast.LENGTH_LONG).show();
    }


    void setProfileInfo(View view){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            ((TextView)view.findViewById(R.id.user_email)).setText(firebaseUser.getEmail());
            ((TextView)view.findViewById(R.id.user_id)).setText(firebaseUser.getUid());

            Firebase.getUserInfo(firebaseUser.getUid(), (documentSnapshot, e) -> {
                if (e != null) {
                    Toast.makeText(getContext(),"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    UserValueHolder userValueHolder=documentSnapshot.toObject(UserValueHolder.class);
                    if(userValueHolder!=null){

                        ((TextView)view.findViewById(R.id.user_name)).setText(userValueHolder.userName);
                        ((TextView)view.findViewById(R.id.user_description)).setText(userValueHolder.description);
                        if(!userValueHolder.imageUrl.isEmpty())
                            Picasso.get().load(userValueHolder.imageUrl).error(R.mipmap.ic_launcher_round).into((ImageView)view.findViewById(R.id.image_view));

                    }
                }
            });
        }
    }

    public ProfileFragment(OnFragmentChangeListener onFragmentChangeListener) {
        this.onFragmentChangeListener = onFragmentChangeListener;
    }
}
