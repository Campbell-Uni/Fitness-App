package grp2.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import grp2.fitness.Helpers.AccountHandler;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private AccountHandler accountHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        accountHandler = new AccountHandler();
        checkLogin();

        NavigationView navigationView   = findViewById(R.id.navMenu);
        mDrawerLayout                   = findViewById(R.id.drawerLayout);
        drawerToggle                    = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        switch(item.getItemId()){
                            case R.id.logout :
                                accountHandler.logout();
                                checkLogin();
                            default:
                                break;
                        }

                        return true;
                    }
                }
        );
    }

    private void checkLogin(){
        if(!accountHandler.isLoggedIn()){
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
