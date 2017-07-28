package com.wglover.plateplanner.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wglover.plateplanner.MainActivity;
import com.wglover.plateplanner.R;
import com.wglover.plateplanner.adapters.RecipesAdapter;
import com.wglover.plateplanner.classes.Ingredient;
import com.wglover.plateplanner.database.DbProvider;
import com.wglover.plateplanner.database.SQL;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link RecipeListener}
 * interface.
 */
public class RecipesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;

    private RecipesFragment.RecipeListener mListener;
    private RecipesAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipesFragment newInstance(int columnCount) {
        RecipesFragment fragment = new RecipesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        this.mAdapter = new RecipesAdapter(getContext(), null, (MainActivity) getActivity());

        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = recyclerView.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(mAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.ingredient_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addEditRecipe(null);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipesFragment.RecipeListener) {
            mListener = (RecipesFragment.RecipeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IngredientListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        String sortOrder = SQL.COLUMN_NAME + " ASC";
        return new CursorLoader(getContext(),
                DbProvider.URI_INGREDIENTS,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface RecipeListener {
        // TODO: Update argument type and name
        void addEditRecipe(@Nullable Ingredient ingredient);
    }
}
