package babarshamsi92.drawerapp.BaseActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


;import babarshamsi92.drawerapp.Fragments.MyFirstFragment;
import babarshamsi92.drawerapp.Fragments.MySecondFragment;
import babarshamsi92.drawerapp.R;


/**
 * Created by Lenovo on 11/17/2016.
 */
//
public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Context context;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    LinearLayout content;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_base_drawer);

        InitViews();


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    public void InitViews() {

        content = (LinearLayout) findViewById(R.id.activity_blank);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        frameLayout = (FrameLayout) findViewById(R.id.fl_content_frame);

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(DrawerBaseActivity.this);
        navigationView.setItemIconTintList(null);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            finish();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //if you want to use fragment..
        Fragment fragment = null;


        if (id == R.id.nav_first_item) {

            fragment = new MyFirstFragment();

        } else if (id == R.id.nav_second_item) {

            fragment = new MySecondFragment();
        }


        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fl_content_frame, fragment).commit();

        } else {

            Log.e("TabScreenActivity", "Error in creating fragment");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
