package com.wglover.plateplanner.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.wglover.plateplanner.classes.Ingredient;
import com.wglover.plateplanner.classes.Recipe;

/**
 * TODO: Improve class header comment.
 * Performs asynchronous statements through the DbProvider and provides an
 * interface for receiving callbacks on completion.
 * Used mostly by the Edit fragments.
 */

public class DbHandler extends AsyncQueryHandler {

    private AsyncQueryListener mListener;

    public DbHandler(Context context, AsyncQueryListener listener) {
        super(context.getContentResolver());
        mListener = listener;
    }

    public DbHandler(ContentResolver cr) {
        super(cr);
    }

    public DbHandler(Context context) {
        super(context.getContentResolver());
    }

    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(AsyncQueryListener listener) {
        mListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (mListener != null) {
            mListener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        if (mListener != null) {
            mListener.onInsertComplete(token, cookie, uri);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if (mListener != null) {
            mListener.onUpdateComplete(token, cookie, result);
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        if (mListener != null) {
            mListener.onDeleteComplete(token, cookie, result);
        }
    }

    public void create(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(SQL.COLUMN_NAME, ingredient.name);
        values.put(SQL.COLUMN_CATEGORY, ingredient.category);
        values.put(SQL.COLUMN_STORE, ingredient.store);
        values.put(SQL.COLUMN_UNITS, ingredient.units.name());
        create(DbProvider.URI_INGREDIENTS, values);
    }

    public void update(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(SQL.COLUMN_NAME, ingredient.name);
        values.put(SQL.COLUMN_CATEGORY, ingredient.category);
        values.put(SQL.COLUMN_STORE, ingredient.store);
        values.put(SQL.COLUMN_UNITS, ingredient.units.name());
        update(DbProvider.URI_INGREDIENTS, ingredient.id, values);
    }

    public void delete(Ingredient ingredient) {
        delete(DbProvider.URI_INGREDIENTS, ingredient.id);
    }

    public void create(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(SQL.COLUMN_NAME, recipe.name);
        values.put(SQL.COLUMN_DIRECTIONS, recipe.directions);
        create(DbProvider.URI_RECIPES, values);
    }

    public void update(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(SQL.COLUMN_NAME, recipe.name);
        values.put(SQL.COLUMN_DIRECTIONS, recipe.directions);
        update(DbProvider.URI_RECIPES, recipe.id, values);
    }

    public void delete(Recipe recipe) {
        delete(DbProvider.URI_RECIPES, recipe.id);
    }

    public void delete(Uri uri, long id) {
        String selection = String.format("%s = ?", SQL.COLUMN_ID);
        String selectionArgs[] = {String.valueOf(id)};
        startDelete(0, null, uri, selection, selectionArgs);
    }

    public void update(Uri uri, long id, ContentValues values) {
        String selection = String.format("%s = ?", SQL.COLUMN_ID);
        String selectionArgs[] = {String.valueOf(id)};
        startUpdate(0, null, uri, values, selection, selectionArgs);
    }

    public void create(Uri uri, ContentValues values) {
        startInsert(0, null, DbProvider.URI_INGREDIENTS, values);
    }

    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);

        void onInsertComplete(int token, Object cookie, Uri uri);

        void onUpdateComplete(int token, Object cookie, int result);

        void onDeleteComplete(int token, Object cookie, int result);
    }
}
