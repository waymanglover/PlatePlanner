package com.wglover.plateplanner.database;

/**
 * TODO: Add a class header comment!
 */

public final class SQL {
    public static final String SORT_UPPER_ASCENDING = "UPPER(%s) ASC";

    //region Ingredients

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STORE = "store";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_UNITS = "units";

    public static final int COL_INDEX_ID = 0;
    public static final int COL_INDEX_NAME = 1;
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

    public static final String INSERT_INGREDIENT =
            "INSERT INTO " + TABLE_INGREDIENTS + " " +
                    "(" + COLUMN_NAME + ", " + COLUMN_STORE + ", " + COLUMN_CATEGORY + ", " + COLUMN_UNITS + ") " +
                    "VALUES (?, ?, ?, ?)";

    public static final String READ_INGREDIENT =
            "SELECT * FROM " + TABLE_INGREDIENTS;

    public static final String UPDATE_INGREDIENT =
            "UPDATE " + TABLE_INGREDIENTS + " SET " +
                    COLUMN_NAME + " = ?, " +
                    COLUMN_STORE + " = ?, " +
                    COLUMN_CATEGORY + " = ?, " +
                    COLUMN_UNITS + " = ? " +
                    "WHERE " + COLUMN_ID + " = ? ";

    public static final String DELETE_INGREDIENT =
            "DELETE FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_ID + " = ?";

    public static final String READ_STORES =
            "SELECT DISTINCT " + COLUMN_STORE + " FROM " + TABLE_INGREDIENTS;
    //endregion
}
