package com.vullnetlimani.soundrecorder.adapters;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vullnetlimani.soundrecorder.R;
import com.vullnetlimani.soundrecorder.RecordingItem;
import com.vullnetlimani.soundrecorder.database.DBHelper;
import com.vullnetlimani.soundrecorder.database.OnDatabaseChangedListener;
import com.vullnetlimani.soundrecorder.fragments.FileViewerFragment;

import java.util.concurrent.TimeUnit;

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder> implements OnDatabaseChangedListener {

    AppCompatActivity appCompatActivity;
    LinearLayoutManager linearLayoutManager;
    FileViewerFragment fileViewerFragment;
    private DBHelper mDatabase;
    private RecordingItem recordingItem;

    public FileViewerAdapter(AppCompatActivity appCompatActivity, FileViewerFragment fileViewerFragment, LinearLayoutManager linearLayoutManager) {
        super();

        this.appCompatActivity = appCompatActivity;
        this.fileViewerFragment = fileViewerFragment;
        this.linearLayoutManager = linearLayoutManager;

        mDatabase = new DBHelper(appCompatActivity);
        DBHelper.setOnDatabaseChangedLister(this);

    }

    @NonNull
    @Override
    public RecordingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new RecordingsViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecordingsViewHolder holder, int position) {

        recordingItem = getItem(position);

        long itemDuration = recordingItem.getLength();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);

        holder.file_name_text.setText(recordingItem.getName());
        holder.file_length_text.setText(String.format("%02d:%02d", minutes, seconds));

        holder.file_date_added_text.setText(
                DateUtils.formatDateTime(appCompatActivity,
                        recordingItem.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                ));


        //  mDatabase.addRecording(recordingItem.getName(), recordingItem.getFilePath(), recordingItem.getLength());

    }

    private RecordingItem getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    @Override
    public int getItemCount() {
        return mDatabase.getCount();
    }

    @Override
    public void onNewDatabaseEntryAdded() {
        notifyItemInserted(getItemCount() - 1);
        linearLayoutManager.scrollToPosition(getItemCount() - 1);
    }

    @Override
    public void onDatabaseEntryRenamed() {

    }

    public static class RecordingsViewHolder extends RecyclerView.ViewHolder {

        protected TextView file_name_text;
        protected TextView file_length_text;
        protected TextView file_date_added_text;
        protected ImageView imageView;
        protected View cardView;


        public RecordingsViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name_text = itemView.findViewById(R.id.file_name_text);
            file_length_text = itemView.findViewById(R.id.file_length_text);
            file_date_added_text = itemView.findViewById(R.id.file_date_added_text);
            imageView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}
