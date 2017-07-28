package com.wglover.plateplanner.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * TODO: Add a class header comment!
 */

public class DbProvider extends ContentProvider {
    public static final String AUTHORITY = "com.wglover.plateplanner.database.DbProvider";

    // ------- define some Uris
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String DISTINCT = "/D";
    public static final String ID = "/#";
    public static final Uri URI_INGREDIENTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_INGREDIENTS);
    public static final Uri URI_INGREDIENTS_DISTINCT = Uri.parse("content://" + AUTHORITY + "/" + PATH_INGREDIENTS + DISTINCT);
    // ------- setup UriMatcher
    private static final int INGREDIENTS = 10;
    private static final int INGREDIENTS_DISTINCT = 20;
    private static final int INGREDIENT_ID = 30;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, PATH_INGREDIENTS, INGREDIENTS);
        sURIMatcher.addURI(AUTHORITY, PATH_INGREDIENTS + DISTINCT, INGREDIENTS_DISTINCT);
        sURIMatcher.addURI(AUTHORITY, PATH_INGREDIENTS + ID, INGREDIENT_ID);
    }

    private DbHelper mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String groupBy = null;

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(SQL.TABLE_INGREDIENTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case INGREDIENTS_DISTINCT:
                queryBuilder.setDistinct(true);
                //groupBy = projection[0];
            case INGREDIENTS:
                break;
            case INGREDIENT_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(SQL.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        String path;
        long id = 0;
        switch (uriType) {
            case INGREDIENTS:
                id = sqlDB.insert(SQL.TABLE_INGREDIENTS, null, values);
                path = PATH_INGREDIENTS;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case INGREDIENTS:
                rowsDeleted = sqlDB.delete(SQL.TABLE_INGREDIENTS, selection,
                        selectionArgs);
                break;
            case INGREDIENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            SQL.TABLE_INGREDIENTS,
                            SQL.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            SQL.TABLE_INGREDIENTS,
                            SQL.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case INGREDIENTS:
                rowsUpdated = sqlDB.update(SQL.TABLE_INGREDIENTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case INGREDIENT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(SQL.TABLE_INGREDIENTS,
                            values,
                            SQL.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(SQL.TABLE_INGREDIENTS,
                            values,
                            SQL.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {SQL.COLUMN_ID, SQL.COLUMN_NAME, SQL.COLUMN_CATEGORY,
                SQL.COLUMN_STORE, SQL.COLUMN_UNITS};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }
}
