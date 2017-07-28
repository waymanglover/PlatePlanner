package com.wglover.plateplanner.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.wglover.plateplanner.classes.Ingredient;

/**
 * TODO: Improve class header comment.
 * Performs asynchronous statements through the DbProvider and provides an
 * interface for receiving callbacks on completion.
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
        startInsert(0, null, DbProvider.URI_INGREDIENTS, values);
    }

    public void update(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(SQL.COLUMN_NAME, ingredient.name);
        values.put(SQL.COLUMN_CATEGORY, ingredient.category);
        values.put(SQL.COLUMN_STORE, ingredient.store);
        values.put(SQL.COLUMN_UNITS, ingredient.units.name());
        String selection = String.format("%s = ?", SQL.COLUMN_ID);
        String selectionArgs[] = {String.valueOf(ingredient.id)};
        startUpdate(0, null, DbProvider.URI_INGREDIENTS, values, selection, selectionArgs);
    }

    public void delete(Ingredient ingredient) {
        String selection = String.format("%s = ?", SQL.COLUMN_ID);
        String selectionArgs[] = {String.valueOf(ingredient.id)};
        startDelete(0, null, DbProvider.URI_INGREDIENTS, selection, selectionArgs);
    }

    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);

        void onInsertComplete(int token, Object cookie, Uri uri);

        void onUpdateComplete(int token, Object cookie, int result);

        void onDeleteComplete(int token, Object cookie, int result);
    }
}
