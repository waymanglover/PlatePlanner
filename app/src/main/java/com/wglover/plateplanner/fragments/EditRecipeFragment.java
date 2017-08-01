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
import android.widget.EditText;

import com.wglover.plateplanner.R;
import com.wglover.plateplanner.classes.InstantAutoComplete;
import com.wglover.plateplanner.classes.Recipe;
import com.wglover.plateplanner.database.DbHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditRecipeFragment.OnEditRecipeInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditRecipeFragment extends Fragment implements DbHandler.AsyncQueryListener {
    public static final String ARG_RECIPE = "ingredient";
    public static final String ARG_CREATE = "create";

    private InstantAutoComplete mEditName;
    private EditText mEditDirections;

    private Recipe mRecipe;
    private boolean mCreate = false;
    private DbHandler mDbHandler;

    private EditRecipeFragment.OnEditRecipeInteractionListener mListener;

    public EditRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe The recipe to edit. If null, add a new recipe.
     * @return A new instance of fragment EditRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditRecipeFragment newInstance(@Nullable Recipe recipe) {
        EditRecipeFragment fragment = new EditRecipeFragment();
        Bundle args = new Bundle();
        if (recipe != null) {
            args.putParcelable(ARG_RECIPE, recipe);
            // Ingredient already exists, so we need to update the existing ingredient
            // instead of creating a new one.
            args.putBoolean(ARG_CREATE, false);
        } else {
            args.putParcelable(ARG_RECIPE, null);
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
            mRecipe = args.getParcelable(ARG_RECIPE);
            mCreate = args.getBoolean(ARG_CREATE);
        }
        if (mRecipe == null) {
            mRecipe = new Recipe();
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
                deleteRecipe();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Log.d("EditRecipeFragment", "Unexpected option item selected " + item.toString());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
        initializeViews(view);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.edit_recipe_save_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecipe();
            }
        });

        return view;
    }

    // After this is called, AsyncQueryListener callbacks are called when operation is complete
    private void saveRecipe() {
        this.mRecipe.name = mEditName.getText().toString();
        this.mRecipe.directions = mEditDirections.getText().toString();
        if (mCreate) {
            mDbHandler.create(mRecipe);
        } else {
            mDbHandler.update(mRecipe);
        }
    }

    // After this is called, AsyncQueryListener callbacks are called when operation is complete
    private void deleteRecipe() {
        mDbHandler.delete(mRecipe);
    }

    private void initializeViews(View view) {
        mEditName = (InstantAutoComplete) view.findViewById(R.id.edit_recipe_name);
        mEditDirections = (EditText) view.findViewById(R.id.edit_recipe_directions);

        if (mRecipe != null) {
            mEditName.setText(mRecipe.name);
            mEditDirections.setText(mRecipe.directions);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    // TODO: Is this even needed?
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onEditRecipeInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditRecipeFragment.OnEditRecipeInteractionListener) {
            mListener = (EditRecipeFragment.OnEditRecipeInteractionListener) context;
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
        Log.v("EditRecipeFragment", "onQueryComplete Cursor: " + DatabaseUtils.dumpCursorToString(cursor));
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
    public interface OnEditRecipeInteractionListener {
        // TODO: Update argument type and name
        void onEditRecipeInteraction(Uri uri);
    }
}
