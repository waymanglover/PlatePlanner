package com.wglover.plateplanner.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wglover.plateplanner.R;
import com.wglover.plateplanner.classes.Ingredient;
import com.wglover.plateplanner.fragments.IngredientsFragment.IngredientListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment!
 */

public class IngredientsAdapter extends CursorRecyclerViewAdapter<IngredientsAdapter.ViewHolder> {
    private final List<Ingredient> mValues;
    private final IngredientListener mListener;

    public IngredientsAdapter(Context context, Cursor cursor, IngredientListener listener) {
        super(context, cursor);
        mListener = listener;
        mValues = new ArrayList<Ingredient>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ingredients, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        viewHolder.mIngredient = Ingredient.fromCursor(cursor);
        viewHolder.mNameView.setText(viewHolder.mIngredient.name);

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.addEditIngredient(viewHolder.mIngredient);
                }
            }
        });
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                mValues.add(Ingredient.fromCursor(cursor));
            }
            while (cursor.moveToNext());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mNameView;
        public Ingredient mIngredient;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.ingredient_name);
        }
    }
}
