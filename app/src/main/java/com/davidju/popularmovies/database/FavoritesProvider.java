package com.davidju.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/** Content provider that manages CRUD operations to its underlying data repository */
public class FavoritesProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private FavoritesDbHelper helper;
    private SQLiteDatabase database;

    private static final int FAVORITES = 100;
    private static final int FAVORITES_ID = 200;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_NAME, FAVORITES);
        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_NAME + "/#", FAVORITES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        helper = new FavoritesDbHelper(getContext());
        database = helper.getWritableDatabase();
        return database != null;
    }

    @Nullable @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                return FavoritesContract.FavoritesEntry.CONTENT_DIR_TYPE;
            case FAVORITES_ID:
                return FavoritesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
    }

    @Nullable @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                long id = database.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = FavoritesContract.FavoritesEntry.buildFavoritesUri(id);
                } else {
                    throw new SQLException("Failed to insert: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] args) {
        int count;
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                count = database.delete(FavoritesContract.FavoritesEntry.TABLE_NAME, selection, args);
                database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritesContract.FavoritesEntry.TABLE_NAME + "'");
                break;
            case FAVORITES_ID:
                count = database.delete(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        FavoritesContract.FavoritesEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritesContract.FavoritesEntry.TABLE_NAME + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] args) {
        int count;

        if (contentValues == null) {
            throw new IllegalArgumentException("null content values");
        }

        switch(uriMatcher.match(uri)) {
            case FAVORITES:
                count = database.update(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        contentValues, selection, args);
                break;
            case FAVORITES_ID:
                count = database.update(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        contentValues, FavoritesContract.FavoritesEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Nullable @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] args, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                cursor = database.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection, selection, args, null, null, sortOrder);
                return cursor;
            case FAVORITES_ID:
                cursor = database.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection, FavoritesContract.FavoritesEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null, null, sortOrder);
                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
    }
}
