package com.wglover.plateplanner.classes;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wglover.plateplanner.database.SQL;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class Recipe implements Parcelable {
    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.
    public static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    public long id;
    public String name;
    public ArrayList<RecipeIngredient> ingredients;
    public String directions;

    // Should match writeToParcel
    public Recipe(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        in.readTypedList(this.ingredients, RecipeIngredient.CREATOR);
        this.directions = in.readString();
    }

    public Recipe(long id, String name, ArrayList<RecipeIngredient> ingredients, String directions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.directions = directions;
    }

    public Recipe() {
        // ID is assigned by the database -- we'll leave it null for now
        this.name = "";
        this.ingredients = new ArrayList<RecipeIngredient>();
        this.directions = "";
    }

    public static Recipe fromCursor(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.id = cursor.getInt(SQL.COL_INDEX_ID);
        recipe.name = cursor.getString(SQL.COL_INDEX_NAME);
        recipe.directions = cursor.getString(SQL.COL_INDEX_DIRECTIONS);
        return recipe;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredients);
        dest.writeString(this.directions);
    }
}
