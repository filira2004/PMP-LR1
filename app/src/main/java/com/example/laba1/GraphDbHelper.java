package com.example.laba1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GraphDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "graph_lab4.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_POINTS = "graph_points";
    public static final String COL_ID = "_id";
    public static final String COL_X = "x";
    public static final String COL_Y = "y";

    public GraphDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_POINTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_X + " REAL NOT NULL, "
                + COL_Y + " REAL NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
        onCreate(db);
    }
}
