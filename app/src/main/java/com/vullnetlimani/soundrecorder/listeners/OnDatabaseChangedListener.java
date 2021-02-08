package com.vullnetlimani.soundrecorder.listeners;

public interface OnDatabaseChangedListener {
    void onNewDatabaseEntryAdded();

    void onDatabaseEntryRenamed();
}
