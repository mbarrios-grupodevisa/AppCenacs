package gt.com.metrocasas.appcenacs;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int FRAGMENT_VIVENTI = 1;
    public static int FRAGMENT_CASA_ASUNCION = 2;

    private Toolbar toolbar;
    private String userid, init;
    MenuItem btn_update;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getApplicationContext().getSharedPreferences("User",0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(settings.getString("proyecto", null));
        setSupportActionBar(toolbar);
        userid = getIntent().getExtras().getString("id");
        init = getIntent().getExtras().getString("init");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (settings.getString("proyecto", null).equals("Viventi")) {
            setFragment(FRAGMENT_VIVENTI);
        } else {
            setFragment(FRAGMENT_CASA_ASUNCION);
        }
        hello();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (settings.getString("proyecto", null).equals("Viventi")) {
            setFragment(FRAGMENT_VIVENTI);
        } else {
            setFragment(FRAGMENT_CASA_ASUNCION);
        }
        toolbar.setTitle(settings.getString("proyecto", null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settings.getString("proyecto", null).equals("Viventi")) {
            setFragment(FRAGMENT_VIVENTI);
        } else {
            setFragment(FRAGMENT_CASA_ASUNCION);
        }
        toolbar.setTitle(settings.getString("proyecto", null));
    }

    private void hello() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
        String name, last;
        name = settings.getString("firstname", null);
        last = settings.getString("lastname", null);
        Snackbar.make(toolbar,"Bienvenido(a) " + name + " " + last, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        btn_update = menu.findItem(R.id.action_settings);
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id != R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.viventi) {
            setFragment(FRAGMENT_VIVENTI);
        } else if (id == R.id.casa_asuncion) {
            setFragment(FRAGMENT_CASA_ASUNCION);
        }
        else if (id == R.id.logout) {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Está seguro que desea cerrar su sesión y salir?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("id",null);
                                editor.putString("firstname",null);
                                editor.putString("lastname",null);
                                editor.apply();
                            finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(int id) {
        if (id == FRAGMENT_VIVENTI) {
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Viventi");
            args.putString("id", userid);
            args.putString("init", init);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Viventi");
        } else if (id == FRAGMENT_CASA_ASUNCION) {
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Casa Asuncion");
            args.putString("id", userid);
            args.putString("init", init);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Casa Asunción");
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
