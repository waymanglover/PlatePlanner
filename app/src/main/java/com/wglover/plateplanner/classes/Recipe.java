package com.wglover.plateplanner.classes;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

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
    public ArrayList<Ingredient> ingredients;
    public String instructions;

    // Should match writeToParcel
    public Recipe(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        in.readTypedList(this.ingredients, Ingredient.CREATOR);
        this.instructions = in.readString();
    }

    public Recipe(long id, String name, ArrayList<Ingredient> ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Recipe() {
        // ID is assigned by the database -- we'll leave it null for now
        this.name = "";
        this.ingredients = new ArrayList<Ingredient>();
        this.instructions = "";
    }

    public static Recipe fromCursor(Cursor cursor) {
        /*
        Recipe ingredient = new Recipe();
        ingredient.id = cursor.getInt(SQL.COL_INDEX_ID);
        ingredient.name = cursor.getString(SQL.COL_INDEX_NAME);
        ingredient.store = cursor.getString(SQL.COL_INDEX_STORE);
        ingredient.category = cursor.getString(SQL.COL_INDEX_CATEGORY);
        ingredient.units = Unit.valueOf(cursor.getString(SQL.COL_INDEX_UNITS));
        return ingredient; */
        // TODO: Fix this.
        throw new UnsupportedOperationException();
    }

    public static ArrayList<Recipe> createTestItems() {
        /*
        ArrayList<Recipe> ingredients = new ArrayList<>();
        Random rand = new Random();
        String name = "";
        String store = "";
        String category = "";
        Unit unit = Unit.cups;
        for(int n = 0; n < 25; n++)
        {
            name = dummyIngredientNames.get(rand.nextInt(dummyIngredientNames.size()));
            store = dummyStores.get(rand.nextInt(dummyStores.size()));
            category = dummyCategories.get(rand.nextInt(dummyCategories.size()));
            unit = Unit.values()[rand.nextInt(Unit.values().length)];
            Recipe ingredient = new Recipe(rand.nextLong(), name, store, category, unit);
            ingredients.add(ingredient);
        }
        return ingredients;
        */
        throw new UnsupportedOperationException();
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
        dest.writeString(this.instructions);
    }
}
