package com.wglover.plateplanner.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO: Extend a class header comment!
 */

// Simple class that allows for tracking a quantity with an ingredient
public class RecipeIngredient extends Ingredient implements Parcelable {
    public static final Parcelable.Creator<RecipeIngredient> CREATOR
            = new Parcelable.Creator<RecipeIngredient>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public RecipeIngredient createFromParcel(Parcel in) {
            return new RecipeIngredient(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }
    };
    public int quantity;

    // Should match writeToParcel
    public RecipeIngredient(Parcel in) {
        super(in);
        this.quantity = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.quantity);
    }
}
