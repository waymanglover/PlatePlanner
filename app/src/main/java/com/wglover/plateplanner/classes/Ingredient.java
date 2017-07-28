package com.wglover.plateplanner.classes;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.wglover.plateplanner.database.SQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Ingredient implements Parcelable {
    public static final List<String> dummyIngredientNames = Arrays.asList("Pepper", "Onion", "Raddish", "Tomato", "Pickle", "Banana");
    public static final List<String> dummyStores = Arrays.asList("Sams", "H-Mart", "Farmer's Market", "Publix", "Kroger");
    public static final List<String> dummyCategories = Arrays.asList("Produce", "Canned Vegetables", "Asian", "Mexican", "Vegan");
    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.
    public static final Parcelable.Creator<Ingredient> CREATOR
            = new Parcelable.Creator<Ingredient>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    public long id;
    public String name;
    public String store;
    public String category;
    public Unit units;

    // Should match writeToParcel
    public Ingredient(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.store = in.readString();
        this.category = in.readString();
        this.units = Unit.valueOf(in.readString());
    }

    public Ingredient(long id, String name, String store, String category, Unit units) {
        this.id = id;
        this.name = name;
        this.store = store;
        this.category = category;
        this.units = units;
    }

    public Ingredient() {
        // ID is assigned by the database -- we'll leave it null for now
        this.name = "";
        this.store = "";
        this.category = "";
        this.units = Unit.quantity;
    }

    public static Ingredient fromCursor(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.id = cursor.getInt(SQL.COL_INDEX_ID);
        ingredient.name = cursor.getString(SQL.COL_INDEX_NAME);
        ingredient.store = cursor.getString(SQL.COL_INDEX_STORE);
        ingredient.category = cursor.getString(SQL.COL_INDEX_CATEGORY);
        ingredient.units = Unit.valueOf(cursor.getString(SQL.COL_INDEX_UNITS));
        return ingredient;
    }

    public static ArrayList<Ingredient> createTestItems() {
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        Random rand = new Random();
        String name = "";
        String store = "";
        String category = "";
        Unit unit = Unit.cups;
        for (int n = 0; n < 25; n++) {
            name = dummyIngredientNames.get(rand.nextInt(dummyIngredientNames.size()));
            store = dummyStores.get(rand.nextInt(dummyStores.size()));
            category = dummyCategories.get(rand.nextInt(dummyCategories.size()));
            unit = Unit.values()[rand.nextInt(Unit.values().length)];
            Ingredient ingredient = new Ingredient(rand.nextLong(), name, store, category, unit);
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.store);
        dest.writeString(this.category);
        dest.writeString(this.units.name());
    }
}
