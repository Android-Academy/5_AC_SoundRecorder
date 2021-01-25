package com.vullnetlimani.soundrecorder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vullnetlimani.soundrecorder.adapters.MyViewPagerAdapter;
import com.vullnetlimani.soundrecorder.fragments.FileViewerFragment;
import com.vullnetlimani.soundrecorder.fragments.RecordFragment;

public class MainActivity extends AppCompatActivity {

    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);

        ViewPager2 mViewPager2 = findViewById(R.id.mViewPager2);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        titles = new String[]{getString(R.string.tab_title_record), getString(R.string.tab_title_saved_recordings)};

        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(this);

        viewPagerAdapter.addFragment(new RecordFragment(MainActivity.this));
        viewPagerAdapter.addFragment(new FileViewerFragment(MainActivity.this));

        mViewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();
    }
}