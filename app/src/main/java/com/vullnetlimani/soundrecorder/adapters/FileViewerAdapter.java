package com.vullnetlimani.soundrecorder.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vullnetlimani.soundrecorder.R;
import com.vullnetlimani.soundrecorder.RecordingItem;
import com.vullnetlimani.soundrecorder.database.DBHelper;
import com.vullnetlimani.soundrecorder.database.OnDatabaseChangedListener;
import com.vullnetlimani.soundrecorder.fragments.FileViewerFragment;
import com.vullnetlimani.soundrecorder.fragments.PlaybackFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.vullnetlimani.soundrecorder.RecordingService.getRealPathFromURI;
import static com.vullnetlimani.soundrecorder.fragments.FileViewerFragment.SOUND_RECORDER_WITH_SEP;

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder> implements OnDatabaseChangedListener {

    public static final String DIALOG_PLAYBACK = "dialog_playback";
    private static final String LOG_TAG = "FileViewerAdapterLog";
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

        Log.d("DateLog", "Milisec - " + recordingItem.getTime());

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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaybackFragment playbackFragment = new PlaybackFragment().newInstance(getItem(holder.getLayoutPosition()), appCompatActivity);
                FragmentTransaction transaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
                playbackFragment.show(transaction, DIALOG_PLAYBACK);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ArrayList<String> entries = new ArrayList<>();
                entries.add(appCompatActivity.getString(R.string.share_file));
                entries.add(appCompatActivity.getString(R.string.rename_file));
                entries.add(appCompatActivity.getString(R.string.delete_file));

                final CharSequence[] items = entries.toArray(new CharSequence[entries.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
                builder.setTitle(R.string.options);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                shareFileDialog(holder.getLayoutPosition());
                                break;
                            case 1:
                                renameFileDialog(holder.getLayoutPosition());
                                break;
                            case 2:
                                deleteFileDialog(holder.getLayoutPosition());
                                break;
                            default:
                                break;
                        }

                    }
                });
                builder.setCancelable(true);

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });

    }

    private void deleteFileDialog(int position) {

        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(appCompatActivity);
        renameFileBuilder.setTitle(R.string.confrim_delete);
        renameFileBuilder.setMessage(R.string.delete_message);
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                remove(position);

                dialog.cancel();
            }
        });

        renameFileBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = renameFileBuilder.create();
        alertDialog.show();

    }

    private void remove(int position) {

        Log.d(LOG_TAG, "Deleted Item - " + position);

    }
    private void renameFileDialog(int position) {


        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(appCompatActivity);
        LayoutInflater inflater = LayoutInflater.from(appCompatActivity);

        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(appCompatActivity.getString(R.string.rename_file));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(appCompatActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (input.getText().toString().trim().isEmpty())
                    return;

                String value = input.getText().toString().trim() + appCompatActivity.getString(R.string.mp3);
                rename(position, value);

                dialog.cancel();
            }
        });

        renameFileBuilder.setNegativeButton(appCompatActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        renameFileBuilder.setView(view);
        AlertDialog alertDialog = renameFileBuilder.create();
        alertDialog.show();

    }

    private void rename(int position, String name) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentResolver mContentResolver = appCompatActivity.getContentResolver();
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, name);

            Uri uri = getRealPathFromURI(appCompatActivity, getItem(position).getName());
            mContentResolver.update(uri, mContentValues, null, null);
            mDatabase.renameItem(getItem(position), name, String.valueOf(uri));
            notifyItemChanged(position);

        } else {
            String mFilePath;
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += SOUND_RECORDER_WITH_SEP + "/" + name;

            Log.d(LOG_TAG, "mFilePath : " + mFilePath);

            File newFilePath = new File(mFilePath);

            if (newFilePath.exists() && !newFilePath.isDirectory()) {
                Toast.makeText(appCompatActivity, String.format(appCompatActivity.getString(R.string.toast_file_exists), name), Toast.LENGTH_SHORT).show();
            } else {
                File oldFilePath = new File(getItem(position).getFilePath());
                oldFilePath.renameTo(newFilePath);
                mDatabase.renameItem(getItem(position), name, mFilePath);
                notifyItemChanged(position);
            }

        }


    }

    private void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, shareFile(position));
        shareIntent.setType("audio/mp4");
        appCompatActivity.startActivity(Intent.createChooser(shareIntent, appCompatActivity.getString(R.string.send_to)));

    }

    private Uri shareFile(int position) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getRealPathFromURI(appCompatActivity, getItem(position).getName());
        } else {
            return Uri.fromFile(new File(getItem(position).getFilePath()));
        }

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
