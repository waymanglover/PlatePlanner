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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * TODO: Add a class header comment!
 */

public class DbProvider extends ContentProvider {
    public static final String AUTHORITY = "com.wglover.plateplanner.database.DbProvider";

    // ------- define some Uris
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_RECIPES = "recipes";
    public static final String DISTINCT = "/D";
    public static final String ID = "/#";
    public static final Uri URI_INGREDIENTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_INGREDIENTS);
    public static final Uri URI_INGREDIENTS_DISTINCT = Uri.parse("content://" + AUTHORITY + "/" + PATH_INGREDIENTS + DISTINCT);
    public static final Uri URI_RECIPES = Uri.parse("content://" + AUTHORITY + "/" + PATH_RECIPES);
    // ------- setup UriMatcher
    private static final int INGREDIENTS = 010;
    private static final int INGREDIENTS_DISTINCT = 020;
    private static final int INGREDIENT_ID = 030;
    private static final int RECIPES = 110;
    private static final int RECIPE_ID = 130;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, PATH_INGREDIENTS, INGREDIENTS);
        sURIMatcher.addURI(AUTHORITY, PATH_INGREDIENTS + DISTINCT, INGREDIENTS_DISTINCT);
        sURIMatcher.addURI(AUTHORITY, PATH_INGREDIENTS + ID, INGREDIENT_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_RECIPES, RECIPES);
        sURIMatcher.addURI(AUTHORITY, PATH_RECIPES + ID, RECIPE_ID);
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

        // Set the table
        int uriType = sURIMatcher.match(uri);

        // check if the caller has requested a column which does not exists
        checkColumns(projection, uriType);

        switch (uriType) {
            case INGREDIENTS_DISTINCT:
                queryBuilder.setDistinct(true);
            case INGREDIENTS:
                queryBuilder.setTables(SQL.TABLE_INGREDIENTS);
                break;
            case RECIPES:
                queryBuilder.setTables(SQL.TABLE_RECIPES);
                break;
            case RECIPE_ID:
                queryBuilder.setTables(SQL.TABLE_RECIPES);
                queryBuilder.appendWhere(SQL.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case INGREDIENT_ID:
                queryBuilder.setTables(SQL.TABLE_INGREDIENTS);
                // adding the ID to the original query
                queryBuilder.appendWhere(SQL.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown or Unhandled URI: " + uri);
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
        String table;
        long id = 0;
        switch (uriType) {
            case INGREDIENTS:
                table = SQL.TABLE_INGREDIENTS;
                path = PATH_INGREDIENTS;
                break;
            case RECIPES:
                table = SQL.TABLE_RECIPES;
                path = PATH_RECIPES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        id = sqlDB.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        String table;
        String id = "";
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case INGREDIENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SQL.COLUMN_ID + "=" + id;
                } else {
                    selection = SQL.COLUMN_ID + "=" + id + " and " + selection;
                }
            case INGREDIENTS:
                table = SQL.TABLE_INGREDIENTS;
                break;
            case RECIPE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SQL.COLUMN_ID + "=" + id;
                } else {
                    selection = SQL.COLUMN_ID + "=" + id + " and " + selection;
                }
            case RECIPES:
                table = SQL.TABLE_RECIPES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        rowsDeleted = sqlDB.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        String table = "";
        String id;
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case INGREDIENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SQL.COLUMN_ID + "=" + id;
                } else {
                    selection = SQL.COLUMN_ID + "=" + id + " and " + selection;
                }
                // Fall through
                // TODO: Add other fall through comments
            case INGREDIENTS:
                table = SQL.TABLE_INGREDIENTS;
                break;
            case RECIPE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = SQL.COLUMN_ID + "=" + id;
                } else {
                    selection = SQL.COLUMN_ID + "=" + id + " and " + selection;
                }
                // Fall through
                // TODO: Add other fall through comments
            case RECIPES:
                table = SQL.TABLE_RECIPES;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        rowsUpdated = sqlDB.update(table, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection, int uriType) {
        ArrayList<String> available = new ArrayList();

        switch (uriType) {
            case INGREDIENTS:
            case INGREDIENTS_DISTINCT:
            case INGREDIENT_ID:
                available.addAll(Arrays.asList(SQL.COLUMN_ID, SQL.COLUMN_NAME, SQL.COLUMN_CATEGORY, SQL.COLUMN_STORE, SQL.COLUMN_UNITS));
                break;
            case RECIPES:
            case RECIPE_ID:
                available.addAll(Arrays.asList(SQL.COLUMN_ID, SQL.COLUMN_NAME));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI type: " + uriType);
        }
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    available);
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }
}
