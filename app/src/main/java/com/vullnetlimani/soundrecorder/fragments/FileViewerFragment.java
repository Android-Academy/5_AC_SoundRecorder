package com.vullnetlimani.soundrecorder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vullnetlimani.soundrecorder.R;
import com.vullnetlimani.soundrecorder.adapters.FileViewerAdapter;

public class FileViewerFragment extends Fragment {

    public static final String SOUND_RECORDER_WITH_SEP = "/SoundRecorder";

    private AppCompatActivity appCompatActivity;
    private ConstraintLayout mainLayout;
    private RecyclerView mRecyclerView;
    private LinearLayout noDataLinearLayout;
    private FileViewerAdapter fileViewerAdapter;


    public FileViewerFragment(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View recordView = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        mainLayout = recordView.findViewById(R.id.fragment_file_viewer);
        mRecyclerView = recordView.findViewById(R.id.recyclerView);

        noDataLinearLayout = recordView.findViewById(R.id.noDataLinearLayout);


        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(appCompatActivity);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        fileViewerAdapter = new FileViewerAdapter(appCompatActivity, this, layoutManager);
        mRecyclerView.setAdapter(fileViewerAdapter);

        return recordView;
    }
}
