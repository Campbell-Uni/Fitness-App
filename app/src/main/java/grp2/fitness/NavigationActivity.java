package grp2.fitness;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import grp2.fitness.Fragments.CalculatorFragment;
import grp2.fitness.Fragments.DiaryFragment;
import grp2.fitness.Fragments.HeartRateFragment;
import grp2.fitness.Fragments.HomeFragment;
import grp2.fitness.Fragments.LeaderboardFragment;
import grp2.fitness.Fragments.PedometerFragment;
import grp2.fitness.Fragments.RecipeFragment;
import grp2.fitness.Fragments.SettingsFragment;

public class NavigationActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar         = findViewById(R.id.navigation_toolbar);
        drawerLayout    = findViewById(R.id.drawer_layout);
        navigationView  = findViewById(R.id.navigation_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        setSupportActionBar(toolbar);
        drawerLayout.addDrawerListener(drawerToggle);
        initDrawer(navigationView);

        updateView(navigationView.getMenu().findItem(R.id.home));
    }


    private void initDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        updateView(menuItem);
                        return true;
                    }
                });
    }

    public void updateView(MenuItem menuItem) {
        Fragment fragment = null;

        if (menuItem.getItemId() == R.id.logout) {
            startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
            finish();
            return;
        }

        try {
            fragment = (Fragment) getFragment(menuItem.getItemId()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();

        setTitle(menuItem.getTitle());
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private Class getFragment(int menuItemId) {
        switch (menuItemId) {
            case R.id.home:
                return HomeFragment.class;
            case R.id.recipies:
                return RecipeFragment.class;
            case R.id.diary:
                return DiaryFragment.class;
            case R.id.pedometer:
                return PedometerFragment.class;
            case R.id.heartrate:
                return HeartRateFragment.class;
            case R.id.calculator:
                return CalculatorFragment.class;
            case R.id.leaderboard:
                return LeaderboardFragment.class;
            case R.id.settings:
                return SettingsFragment.class;
            default:
                return HomeFragment.class;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        drawerToggle.onConfigurationChanged(configuration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
