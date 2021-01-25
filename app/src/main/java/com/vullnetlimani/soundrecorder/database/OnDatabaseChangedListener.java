package com.vullnetlimani.soundrecorder.database;

public interface OnDatabaseChangedListener {
    void onNewDatabaseEntryAdded();

    void onDatabaseEntryRenamed();
}
