package gt.com.metrocasas.inoutcheck;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
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
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int FRAGMENT_VIVENTI = 1;
    public static int FRAGMENT_CASA_ASUNCION = 2;
    public static int FRAGMENT_METROCASAS = 3;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

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
        if (settings.getString("proyecto", "").equals("Viventi")) {
            setFragment(FRAGMENT_VIVENTI);
        } else if (settings.getString("proyecto", "").equals("Casa Asuncion")){
            setFragment(FRAGMENT_CASA_ASUNCION);
        } else{
            setFragment(FRAGMENT_METROCASAS);
        }
        hello();
        requestLocationPermission();
        SharedPreferences settings = getSharedPreferences("User",0);
        String dato = settings.getString("alarm",null);
        if(dato == null) {
            Toast.makeText(this, "Creando recordatorios...", Toast.LENGTH_SHORT).show();
            setAutomaticCheckOut();
            setInAlarm();
            setOutAlarm();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (settings.getString("proyecto", "").equals("Viventi")) {
            setFragment(FRAGMENT_VIVENTI);
        } else if (settings.getString("proyecto", "").equals("Casa Asuncion")){
            setFragment(FRAGMENT_CASA_ASUNCION);
        } else{
            setFragment(FRAGMENT_METROCASAS);
        }
        toolbar.setTitle(settings.getString("proyecto", null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settings.getString("proyecto", "").equals("Viventi")) {
            setFragment(FRAGMENT_VIVENTI);
        } else if (settings.getString("proyecto", "").equals("Casa Asuncion")){
            setFragment(FRAGMENT_CASA_ASUNCION);
        } else{
            setFragment(FRAGMENT_METROCASAS);
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

    public void cancelInAlarm() {
        Intent intent = new Intent(this, AlarmIngressReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 123, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void cancelOutAlarm() {
        Intent intent = new Intent(this, AlarmExitReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 321, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void cancelAutomaticAlarm() {

    }

    public void setAutomaticCheckOut() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        Intent intentAlarm = new Intent(this, CheckTimeReceiver.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this.getApplicationContext(), 111, intentAlarm, 0));
    }

    public void setInAlarm() {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, 10);
        calendar2.set(Calendar.MINUTE, 30);
        Intent intentAlarm2 = new Intent(this, AlarmIngressReceiver.class);
        AlarmManager alarmManager2 = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this.getApplicationContext(), 123, intentAlarm2, 0));
    }

    public void setOutAlarm() {
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTimeInMillis(System.currentTimeMillis());
        calendar3.set(Calendar.HOUR_OF_DAY, 19);
        calendar3.set(Calendar.MINUTE, 0);
        Intent intentAlarm3 = new Intent(this, AlarmExitReceiver.class);
        AlarmManager alarmManager3 = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager3.setRepeating(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this.getApplicationContext(), 321, intentAlarm3, 0));
        SharedPreferences settings = getSharedPreferences("User",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("alarm","on");
        editor.apply();
    }

    private void requestLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
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
        } else if (id == R.id.metrocasas) {
            setFragment(FRAGMENT_METROCASAS);
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
                                cancelInAlarm();
                                cancelOutAlarm();
                                cancelAutomaticAlarm();
                                SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("id",null);
                                editor.putString("firstname",null);
                                editor.putString("lastname",null);
                                editor.putString("alarm", null);
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
        } else if (id == FRAGMENT_METROCASAS) {
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Metrocasas");
            args.putString("id", userid);
            args.putString("init", init);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Metrocasas");
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
