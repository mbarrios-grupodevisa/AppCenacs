package gt.com.metrocasas.appcenacs;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private GoogleApiClient googleApiClient;
    public static final String GEOFENCE_VIVENTI_ID = "Viventi";
    public static final String GEOFENCE_CASA_ID = "Casa Asuncion";
    private Toolbar toolbar;
    public String userid, proyecto;
    double longitudeNetwork, latitudeNetwork;
    MenuItem btn ;
    String name, last;


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
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        //Toast.makeText(getApplicationContext(), "CONECTADA", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        //Toast.makeText(getApplicationContext(), "SUSPENDIDO", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show();
                    }
                })
                .build();
        setFragment(1);
        hello();

    }

    private void hello() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
        name = settings.getString("firstname", null);
        last = settings.getString("lastname", null);
        Snackbar.make(toolbar,"Bienvenido(a) " + name + " " + last, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        btn = (MenuItem) menu.findItem(R.id.action_settings);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setEnabled(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            try {
                solicitarGPS();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDialog() {
        NuevoItemRevisionDialog nird = new NuevoItemRevisionDialog();
        Bundle bundle = new Bundle();
        bundle.putString("proyecto", proyecto);
        bundle.putDouble("latitud",latitudeNetwork);
        bundle.putDouble("longitud",longitudeNetwork);
        bundle.putString("id", userid);
        nird.setArguments(bundle);
        nird.show(this.getFragmentManager(),"");
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
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
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

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(final Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();
            //Menu menu = (Menu) findViewById(R.menu.main);
            //menu.getItem(0).setEnabled(true);
            btn.setEnabled(true);
            Toast.makeText(getApplicationContext(),latitudeNetwork+","+longitudeNetwork,Toast.LENGTH_LONG).show();
            Log.i("LocationListener: ", location.toString());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void solicitarGPS() throws Settings.SettingNotFoundException {

        if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE) == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Activar GPS");
            builder.setMessage("Se necesita tener activo el GPS para usar esta función.");
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent gpsOptionsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(gpsOptionsIntent, 1);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            startLocationRequest();
            startGeofenceMonitoring();
        }
    }

    public void startLocationRequest() {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i("LOCATION", "UBICACION: " + location.getLatitude() + ", " + location.getLongitude());
                        }
                    });
        } catch (SecurityException se) {
        }
    }

    public void startGeofenceMonitoring() {
        try {
            Geofence geofence1 = new Geofence.Builder()
                    .setRequestId(GEOFENCE_VIVENTI_ID)
                    .setCircularRegion(14.6050705,-90.5164723, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setLoiteringDelay(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .build();

            Geofence geofence2 = new Geofence.Builder()
                    .setRequestId(GEOFENCE_CASA_ID)
                    .setCircularRegion(14.592738,-90.513156, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setLoiteringDelay(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence1)
                    .addGeofence(geofence2)
                    .build();

            Intent intent = new Intent(this, GeofenceService.class);
            intent.putExtra("userid", userid);
            intent.putExtra("nombre", name);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (!googleApiClient.isConnected()) {
                Toast.makeText(this, "GOOGLE API NO CONECTADA", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "NO HAY PERMISOS", Toast.LENGTH_SHORT).show();
                }
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "GEOFENCE AÑADIDO", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "ACTIVE GPS", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stopGeofenceMonitoring () {
        Toast.makeText(this, "ELIMINANDO GEOFENCE", Toast.LENGTH_SHORT).show();
        ArrayList<String> geofenceids = new ArrayList<>();
        geofenceids.add(GEOFENCE_VIVENTI_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceids);
    }
}
