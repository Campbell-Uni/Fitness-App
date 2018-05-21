package grp2.fitness;

import android.content.Intent;
import android.content.res.Configuration;
import android.icu.util.Calendar;
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

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.DailyDataDO;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import grp2.fitness.Fragments.CalculatorFragment;
import grp2.fitness.Fragments.DiaryFragment;
import grp2.fitness.Fragments.HeartRateFragment;
import grp2.fitness.Fragments.HomeFragment;
import grp2.fitness.Fragments.LeaderboardFragment;
import grp2.fitness.Fragments.PedometerFragment;
import grp2.fitness.Fragments.RecipeFragment;
import grp2.fitness.Fragments.SettingsFragment;
import grp2.fitness.Handlers.GoogleFitApi;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private GoogleFitApi googleFitApi;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private DynamoDBMapper dynamoDBMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Toolbar toolbar = findViewById(R.id.navigation_toolbar);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        setSupportActionBar(toolbar);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        updateView(HomeFragment.class);

        initialiseCognito();
    }

    private void initialiseCognito() {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-southeast-2:9e086252-ebfb-4c0d-8d66-bdd54c04d6c1", // Identity pool ID
                Regions.AP_SOUTHEAST_2 // Region
        );

        syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.AP_SOUTHEAST_2, // Region
                credentialsProvider);

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();


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
                IdentityManager.getDefaultIdentityManager().signOut();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == GoogleFitApi.REQUEST_KEY) {
            googleFitApi.setAuthState(false);
            if( resultCode == RESULT_OK ) {
                    googleFitApi.getClient().connect();
            }
        }
    }

    public GoogleFitApi getGoogleFitApi(GoogleFitApi.GoogleFitApiCallback callback){
        if(googleFitApi == null){
            googleFitApi = new GoogleFitApi(this, callback);
        }else {
            googleFitApi.setCallback(callback);
        }

        return googleFitApi;
    }

    public CognitoSyncManager getSyncClient(){
        return this.syncClient;
    }

    public void createDailyData() {
        final DailyDataDO dailyData = new DailyDataDO();

        dailyData.setUserId(credentialsProvider.getIdentityId());

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String formattedDate = dateFormat.format(today);

        dailyData.setDate(formattedDate);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(dailyData);
            }
        }).start();
    }
}
