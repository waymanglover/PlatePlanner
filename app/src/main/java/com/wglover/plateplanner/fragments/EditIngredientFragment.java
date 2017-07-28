package com.wglover.plateplanner.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.wglover.plateplanner.R;
import com.wglover.plateplanner.classes.Ingredient;
import com.wglover.plateplanner.classes.InstantAutoComplete;
import com.wglover.plateplanner.classes.Unit;
import com.wglover.plateplanner.database.DbHandler;
import com.wglover.plateplanner.database.DbProvider;
import com.wglover.plateplanner.database.SQL;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditIngredientFragment.OnEditIngredientInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditIngredientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditIngredientFragment extends Fragment implements DbHandler.AsyncQueryListener {
    public static final String ARG_INGREDIENT = "ingredient";
    public static final String ARG_CREATE = "create";

    private Ingredient mIngredient;
    private boolean mCreate = false;
    private DbHandler mDbHandler;

    private InstantAutoComplete mEditName;
    private InstantAutoComplete mEditStore;
    private InstantAutoComplete mEditCategory;
    private Spinner mEditUnits;

    private OnEditIngredientInteractionListener mListener;
    private ArrayAdapter<String> mStoreAdapter;
    private ArrayAdapter<String> mCategoryAdapter;

    public EditIngredientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ingredient The ingredient to edit. If null, add a new ingredient.
     * @return A new instance of fragment EditIngredientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditIngredientFragment newInstance(@Nullable Ingredient ingredient) {
        EditIngredientFragment fragment = new EditIngredientFragment();
        Bundle args = new Bundle();
        if (ingredient != null) {
            args.putParcelable(ARG_INGREDIENT, ingredient);
            fragment.setArguments(args);
            // Ingredient already exists, so we need to update the existing ingredient
            // instead of creating a new one.
            args.putBoolean(ARG_CREATE, false);
        } else {
            args.putParcelable(ARG_INGREDIENT, null);
            args.putBoolean(ARG_CREATE, true);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mIngredient = args.getParcelable(ARG_INGREDIENT);
            mCreate = args.getBoolean(ARG_CREATE);
        }
        if (mIngredient == null) {
            mIngredient = new Ingredient();
        }
        mDbHandler = new DbHandler(getContext(), this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This may need to be changed if other options are
        // added to the details menu, but currently it's only
        // used for deleting items that have already been created.
        if (!mCreate) {
            inflater.inflate(R.menu.menu_details, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteIngredient();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Log.d("EditIngredientsFragment", "Unexpected option item selected " + item.toString());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_ingredient, container, false);
        initializeViews(view);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.edit_ingredient_save_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIngredient();
            }
        });

        return view;
    }

    // After this is called, AsyncQueryListener callbacks are called when operation is complete
    private void saveIngredient() {
        this.mIngredient.name = mEditName.getText().toString();
        this.mIngredient.store = mEditStore.getText().toString();
        this.mIngredient.category = mEditCategory.getText().toString();
        this.mIngredient.units = Unit.valueOf(mEditUnits.getSelectedItem().toString());
        if (mCreate) {
            mDbHandler.create(mIngredient);
        } else {
            mDbHandler.update(mIngredient);
        }
    }

    // After this is called, AsyncQueryListener callbacks are called when operation is complete
    private void deleteIngredient() {
        mDbHandler.delete(mIngredient);
    }

    private void initializeViews(View view) {
        mEditName = (InstantAutoComplete) view.findViewById(R.id.edit_ingredient_name);
        mEditStore = (InstantAutoComplete) view.findViewById(R.id.edit_ingredient_store);
        mEditCategory = (InstantAutoComplete) view.findViewById(R.id.edit_ingredient_category);
        mEditUnits = (Spinner) view.findViewById(R.id.edit_ingredient_units);

        Unit[] units = Unit.values();
        String[] unitArray = new String[units.length];

        for (int i = 0; i < units.length; i++) {
            unitArray[i] = units[i].name();
        }

        mStoreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        mCategoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        // Query previously set values to display in the AutoComplete view.
        // Setting adapters from the queries is done in the onQueryComplete callback.
        queryAutoCompleteValues();

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, unitArray);

        mEditStore.setAdapter(mStoreAdapter);
        mEditCategory.setAdapter(mCategoryAdapter);
        mEditUnits.setAdapter(unitAdapter);

        if (mIngredient != null) {
            mEditName.setText(mIngredient.name);
            mEditStore.setText(mIngredient.store);
            mEditCategory.setText(mIngredient.category);
            mEditUnits.setSelection(unitAdapter.getPosition(mIngredient.units.name()));
        }
    }

    private void queryAutoCompleteValues() {
        mDbHandler.startQuery(SQL.COL_INDEX_STORE,
                null,
                DbProvider.URI_INGREDIENTS_DISTINCT,
                new String[]{SQL.COLUMN_STORE},
                null,
                null,
                String.format(SQL.SORT_UPPER_ASCENDING, SQL.COLUMN_STORE));
        mDbHandler.startQuery(SQL.COL_INDEX_CATEGORY,
                null,
                DbProvider.URI_INGREDIENTS_DISTINCT,
                new String[]{SQL.COLUMN_CATEGORY},
                null,
                null,
                String.format(SQL.SORT_UPPER_ASCENDING,
                        SQL.COLUMN_CATEGORY));
    }

    // TODO: Rename method, update argument and hook method into UI event
    // TODO: Is this even needed?
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onEditIngredientInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditIngredientInteractionListener) {
            mListener = (OnEditIngredientInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        Log.v("EditIngredientsFragment", "onQueryComplete Cursor: " + DatabaseUtils.dumpCursorToString(cursor));
        String column = "";
        ArrayAdapter<String> adapter;
        if (token == SQL.COL_INDEX_STORE) {
            column = SQL.COLUMN_STORE;
            adapter = mStoreAdapter;
        } else if (token == SQL.COL_INDEX_CATEGORY) {
            column = SQL.COLUMN_CATEGORY;
            adapter = mCategoryAdapter;
        } else {
            throw new UnsupportedOperationException("Unexpected token " + token);
        }
        cursor.moveToFirst();
        do {
            adapter.add(cursor.getString(cursor.getColumnIndexOrThrow(column)));
        }
        while (cursor.moveToNext());
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        // Go back to list
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onUpdateComplete(int token, Object cookie, int result) {
        // Go back to list
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
        // Go back to list
        getActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEditIngredientInteractionListener {
        // TODO: Update argument type and name
        void onEditIngredientInteraction(Uri uri);
    }
}
