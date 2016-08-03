package gt.com.metrocasas.appcenacs;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.List;

public class FragmentRevisionesList extends Fragment {

    public String proyecto, userid;
    String name, last, estado;
    public static final String GEOFENCE_VIVENTI_ID = "Viventi";
    public static final String GEOFENCE_CASA_ID = "Casa Asuncion";
    private GoogleApiClient googleApiClient;
    public ImageView registro;
    String longitudeNetwork, latitudeNetwork;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View partenView = inflater.inflate(R.layout.lista_revisiones, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("User",0);
        name = settings.getString("firstname", null);
        last = settings.getString("lastname", null);
        estado = settings.getString("estado", null);
        userid = getArguments().getString("id");
        proyecto = getArguments().getString("proyecto");
        googleApiClient = new GoogleApiClient.Builder(getActivity())
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
                        Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_LONG).show();
                    }
                })
                .build();

        registro = (ImageView)partenView.findViewById(R.id.registro);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    solicitarGPS();
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return partenView;
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

    public void solicitarGPS() throws Settings.SettingNotFoundException {

        if (Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE) == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latitudeNetwork = String.valueOf(location.getLatitude());
                            longitudeNetwork = String.valueOf(location.getLongitude());
                            Log.i("NETWORK", latitudeNetwork + " , " + longitudeNetwork);
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

            Intent intent = new Intent(getActivity(), GeofenceService.class);
            intent.putExtra("userid", userid);
            intent.putExtra("nombre", name);
            intent.putExtra("latitud", latitudeNetwork);
            intent.putExtra("longitud", longitudeNetwork);
            PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (!googleApiClient.isConnected()) {
                Toast.makeText(getActivity(), "GOOGLE API NO CONECTADA", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "NO HAY PERMISOS", Toast.LENGTH_SHORT).show();
                }
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Toast.makeText(getActivity(), "GEOFENCE AÑADIDO", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "ACTIVE GPS", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stopGeofenceMonitoring () {
        Toast.makeText(getActivity(), "ELIMINANDO GEOFENCE", Toast.LENGTH_SHORT).show();
        ArrayList<String> geofenceids = new ArrayList<>();
        geofenceids.add(GEOFENCE_VIVENTI_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceids);
    }
}
