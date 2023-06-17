package com.cs4530.a4lyfe.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cs4530.a4lyfe.models.database.AppDao;
import com.cs4530.a4lyfe.models.database.tables.HikeTable;
import com.cs4530.a4lyfe.models.database.tables.UserTable;
import com.cs4530.a4lyfe.models.database.tables.WeatherTable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {WeatherTable.class, UserTable.class, HikeTable.class}, version = 1, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);
    private static final androidx.room.RoomDatabase.Callback sRoomDatabaseCallback = new androidx.room.RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
    public static volatile RoomDatabase mInstance;

    static synchronized RoomDatabase getDatabase(final Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDatabase.class, "user.db").addCallback(sRoomDatabaseCallback).build();
        }
        return mInstance;
    }

    public abstract AppDao appDao();
}
