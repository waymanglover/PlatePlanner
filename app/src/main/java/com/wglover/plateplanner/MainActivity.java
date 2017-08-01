package com.wglover.plateplanner;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.wglover.plateplanner.classes.Ingredient;
import com.wglover.plateplanner.classes.Recipe;
import com.wglover.plateplanner.fragments.EditIngredientFragment;
import com.wglover.plateplanner.fragments.EditRecipeFragment;
import com.wglover.plateplanner.fragments.ListFragment;

import java.security.InvalidParameterException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ListFragment.ItemListener,
        EditIngredientFragment.OnEditIngredientInteractionListener,
        EditRecipeFragment.OnEditRecipeInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Only execute the code past this point if we're not being resumed.
        if (savedInstanceState != null) return;

        // Create a new Fragment to be placed in the activity layout
        ListFragment initialFragment = ListFragment.newInstance(ListFragment.Type.Ingredient.id);
        // Add the fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_layout, initialFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // At the moment, no menu items are needed on the home screen.
        //getMenuInflater().inflate(R.menu.menu_details, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment newFragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Create a new Fragment to be placed in the activity layout
        if (id == R.id.nav_planner) {
            // TODO: Switch to planner fragment
        } else if (id == R.id.nav_shopping) {
            // TODO: Switch to shopping list fragment
        } else if (id == R.id.nav_recipes) {
            newFragment = ListFragment.newInstance(ListFragment.Type.Recipe.id);
        } else if (id == R.id.nav_ingredients) {
            newFragment = ListFragment.newInstance(ListFragment.Type.Ingredient.id);
        } else if (id == R.id.nav_settings) {
            // TODO: Switch to settings
        }

        if (newFragment != null) {
            // Add the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_layout, newFragment)
                    .addToBackStack(null)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void addEdit(Object item, int type) {
        // Create a new Fragment to be placed in the activity layout
        Fragment editFragment = null;

        if (type == ListFragment.Type.Ingredient.id) {
            editFragment = EditIngredientFragment.newInstance((Ingredient) item);
        } else if (type == ListFragment.Type.Recipe.id) {
            editFragment = EditRecipeFragment.newInstance((Recipe) item);
        }

        if (editFragment != null) {
            // Add the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_layout, editFragment)
                    .addToBackStack(null)
                    .commit();
        } else throw new InvalidParameterException("item was not an expected type");
    }

    @Override
    public void onEditRecipeInteraction(Uri uri) {
        // TODO: Remove this? Doesn't seem needed.
    }

    @Override
    public void onEditIngredientInteraction(Uri uri) {
        // TODO: Remove this? Doesn't seem needed.
    }
}

