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
import com.wglover.plateplanner.classes.Recipe;
import com.wglover.plateplanner.fragments.ListFragment;
import com.wglover.plateplanner.fragments.ListFragment.ItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment!
 */

public class ListAdapter extends CursorRecyclerViewAdapter<ListAdapter.ViewHolder> {
    private final List<Object> mValues;
    private final ItemListener mListener;
    private final int mType;

    public ListAdapter(Context context, Cursor cursor, ItemListener listener, int type) {
        super(context, cursor);
        mListener = listener;
        mValues = new ArrayList<>();
        mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        String name = "";
        if (mType == ListFragment.Type.Ingredient.id) {
            viewHolder.mItem = Ingredient.fromCursor(cursor);
            Ingredient ingredient = (Ingredient) viewHolder.mItem;
            name = ingredient.name;
        } else if (mType == ListFragment.Type.Recipe.id) {
            viewHolder.mItem = Recipe.fromCursor(cursor);
            Recipe recipe = (Recipe) viewHolder.mItem;
            name = recipe.name;
        } else
            throw new IllegalArgumentException("mType was not an expected type. mType = " + mType);

        viewHolder.mNameView.setText(name);

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.addEdit(viewHolder.mItem, mType);
                }
            }
        });
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        if (cursor != null) {
            if (mType == ListFragment.Type.Ingredient.id) {
                while (cursor.moveToNext()) {
                    mValues.add(Ingredient.fromCursor(cursor));
                }
            } else if (mType == ListFragment.Type.Recipe.id) {
                while (cursor.moveToNext()) {
                    mValues.add(Recipe.fromCursor(cursor));
                }
            } else
                throw new IllegalArgumentException("mType was not an expected type. mType = " + mType);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mNameView;
        public Object mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.item_name);
        }
    }
}
