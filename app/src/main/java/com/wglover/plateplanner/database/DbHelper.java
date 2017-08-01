package com.wglover.plateplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * TODO: Improve class header comment.
 * Performs onCreate/onUpdate functions for the database,
 * and provides methods for performing synchronous queries.
 * Synchronous queries shouldn't be used if possible.
 */

public class DbHelper extends SQLiteOpenHelper {
    //region Constants
    public static final int DB_VERSION = 1;
    public static final String DB_FILENAME = "PlatePlanner";
    //endregion

    public DbHelper(Context context) {
        super(context, DB_FILENAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteStatement createIngredient = db.compileStatement(SQL.CREATE_INGREDIENT);
        SQLiteStatement createRecipe = db.compileStatement(SQL.CREATE_RECIPE);

        createIngredient.execute();
        createRecipe.execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Do something.
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    }
}
