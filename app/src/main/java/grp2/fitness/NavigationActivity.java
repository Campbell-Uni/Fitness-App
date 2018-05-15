package grp2.fitness;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.IdentityManager;

import grp2.fitness.Fragments.CalculatorFragment;
import grp2.fitness.Fragments.DiaryFragment;
import grp2.fitness.Fragments.HeartRateFragment;
import grp2.fitness.Fragments.HomeFragment;
import grp2.fitness.Fragments.LeaderboardFragment;
import grp2.fitness.Fragments.PedometerFragment;
import grp2.fitness.Fragments.RecipeFragment;
import grp2.fitness.Fragments.SettingsFragment;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.navigation_toolbar);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        drawerLayout    = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        setSupportActionBar(toolbar);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        updateView(HomeFragment.class);
    }

    public void updateView(Class fragmentClass) {
        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_frame, fragment).commit();

        drawerLayout.closeDrawers();
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
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class fragmentClass;

        switch (item.getItemId()) {
            case R.id.home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.recipies:
                fragmentClass = RecipeFragment.class;
                break;
            case R.id.diary:
                fragmentClass = DiaryFragment.class;
                break;
            case R.id.pedometer:
                fragmentClass = PedometerFragment.class;
                break;
            case R.id.heartrate:
                fragmentClass = HeartRateFragment.class;
                break;
            case R.id.calculator:
                fragmentClass = CalculatorFragment.class;
                break;
            case R.id.leaderboard:
                fragmentClass = LeaderboardFragment.class;
                break;
            case R.id.settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.logout:
//                IdentityManager.getDefaultIdentityManager().signOut();
                startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
                finish();
                return true;
            default:
                return false;
        }

        setTitle(item.getTitle());
        item.setChecked(true);

        updateView(fragmentClass);
        return true;
    }

    //Pass listener to relevant fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
