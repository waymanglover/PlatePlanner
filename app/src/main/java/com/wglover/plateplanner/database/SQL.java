package com.wglover.plateplanner.database;

/**
 * TODO: Add a class header comment!
 */

public final class SQL {
    public static final String SORT_UPPER_ASCENDING = "UPPER(%s) ASC";

    //region Shared
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";

    public static final int COL_INDEX_ID = 0;
    public static final int COL_INDEX_NAME = 1;
    //endregion

    //region Ingredients
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_STORE = "store";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_UNITS = "units";

    public static final int COL_INDEX_STORE = 2;
    public static final int COL_INDEX_CATEGORY = 3;
    public static final int COL_INDEX_UNITS = 4;

    public static final String CREATE_INGREDIENT =
            "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " VARCHAR," +
                    COLUMN_STORE + " VARCHAR," +
                    COLUMN_CATEGORY + " VARCHAR," +
                    COLUMN_UNITS + " VARCHAR)";

    //region Recipes
    public static final String TABLE_RECIPES = "RECIPEs";

    public static final String COLUMN_DIRECTIONS = "directions";

    public static final int COL_INDEX_DIRECTIONS = 2;

    public static final String CREATE_RECIPE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " VARCHAR," +
                    COLUMN_DIRECTIONS + " VARCHAR)";
    //endregion
}
