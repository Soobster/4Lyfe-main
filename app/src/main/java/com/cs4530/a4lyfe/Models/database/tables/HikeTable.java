package com.cs4530.a4lyfe.models.database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "hike_table")
public class HikeTable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "hikedata")
    private String hikeJson;

    public HikeTable(@NonNull String hikeJson) {
        this.hikeJson = hikeJson;
    }

    @NonNull
    public String getHikeJson() {
        return hikeJson;
    }

    public void setHikeJson(@NonNull String hiked) {
        this.hikeJson = hiked;
    }
}