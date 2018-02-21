package com.davidju.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.davidju.popularmovies.database.FavoritesContract.*;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoritesEntry.COLUMN_ID + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now, just drop and recreate the table
        final String SQL_DELETE_FAVORITES_TABLE = "DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_FAVORITES_TABLE);
        onCreate(sqLiteDatabase);
    }
}
