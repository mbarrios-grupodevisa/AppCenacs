package gt.com.metrocasas.appcenacs;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener
{

    private GoogleApiClient client;
    private Toolbar toolbar;
    public String userid, proyecto;
    double longitudeNetwork, latitudeNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Viventi");
        proyecto = "Viventi";
        setSupportActionBar(toolbar);
        userid = getIntent().getExtras().getString("id");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setFragment(1);

        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000, 10, locationListenerNetwork);
        hello();
    }

    private void hello() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
        String name = settings.getString("firstname",null);
        Snackbar.make(toolbar,"Bienvenido(a) " + name, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            NuevoItemRevisionDialog nird = new NuevoItemRevisionDialog();
            Bundle bundle = new Bundle();
            bundle.putString("proyecto", proyecto);
            bundle.putDouble("latitud",latitudeNetwork);
            bundle.putDouble("longitud",longitudeNetwork);
            bundle.putString("id", userid);
            nird.setArguments(bundle);
            nird.show(this.getFragmentManager(),"");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.viventi) {
            proyecto = "Viventi";
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Viventi");
            args.putString("id", userid);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Viventi");
        } else if (id == R.id.casa_asuncion) {
            proyecto = "Casa Asunción";
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Casa Asuncion");
            args.putString("id", userid);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Casa Asunción");
        }
        else if (id == R.id.logout)
        {
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

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://gt.com.metrocasas.appcenacs/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://gt.com.metrocasas.appcenacs/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private void setFragment(int id) {
        if (id == 1) {
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Viventi");
            args.putString("id", userid);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Viventi");
        } else if (id == 2) {
            Fragment fragment = new FragmentRevisionesList();
            Bundle args = new Bundle();
            args.putString("proyecto", "Casa Asuncion");
            args.putString("id", userid);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            toolbar.setTitle("Casa Asunción");
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        //int lat = (int) (location.getLatitude());
        ///int lng = (int) (location.getLongitude());
        //Log.i("COORDENADAS:",""+lat+"-"+lng);
        //String msg = "New Latitude: " + location.getLatitude()
        //        + "New Longitude: " + location.getLongitude();
        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        //Toast.makeText(getBaseContext(), "Gps is turned on!! ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(intent);
        //Toast.makeText(getBaseContext(), "Gps is turned off!! ",Toast.LENGTH_SHORT).show();
    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(final Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
