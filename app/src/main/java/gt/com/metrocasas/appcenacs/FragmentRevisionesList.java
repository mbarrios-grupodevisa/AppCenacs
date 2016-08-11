package gt.com.metrocasas.appcenacs;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class FragmentRevisionesList extends Fragment {

    public String proyecto, userid, init;
    String name, last, estado;
    public static final String GEOFENCE_VIVENTI_ID = "Viventi";
    public static final String GEOFENCE_CASA_ID = "Casa Asuncion";
    private GoogleApiClient googleApiClient;
    public ImageView registro;
    String longitudeNetwork, latitudeNetwork, service;
    boolean bandera  = true;
    TextView estad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View partenView = inflater.inflate(R.layout.lista_revisiones, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences("User",0);
        name = settings.getString("firstname", null);
        last = settings.getString("lastname", null);
        estado = settings.getString("estado", null);
        service = settings.getString("service",null);
        userid = getArguments().getString("id");
        init = getArguments().getString("init");
        proyecto = getArguments().getString("proyecto");
        estad = (TextView)partenView.findViewById(R.id.txtIngreso);
        if (estado.equals("Salida")) {
            estad.setText("Pulse para registrar su ingreso");
        } else {
            estad.setText("Pulse para registrar su salida");
        }

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
                    solicitarGPS();
            }
        });

        return partenView;
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
        if (estado.equals("Salida")) {
            estad.setText("Pulse para registrar su ingreso");
        } else {
            estad.setText("Pulse para registrar su salida");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public void solicitarGPS() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Activar GPS");
            builder.setMessage("Se necesita tener activo el GPS para usar esta función.");
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent gpsOptionsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
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
        else {
            if(service.equals("Stop")) {
                startLocationRequest();
                startGeofenceMonitoring();
                SharedPreferences settings = getActivity().getSharedPreferences("User",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("service","Start");
                service = "Start";
                editor.apply();
            }
            else {
                startLocationRequest();
            }
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
                            //Log.i("NETWORK", latitudeNetwork + " , " + longitudeNetwork);

                            if(init.equals("normal") && bandera) {
                                SharedPreferences settings = getActivity().getSharedPreferences("User",0);
                                if(!settings.getString("estado", null).equals("Ingreso")) {
                                    estado = "Ingreso";
                                } else {
                                    estado = "Salida";
                                }
                                bandera = false;
                                Intent detalleRevision = new Intent(getActivity(), DetalleRevisionActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", userid);
                                bundle.putString("proyecto", proyecto);
                                bundle.putString("estado", estado);
                                bundle.putString("latitud", latitudeNetwork);
                                bundle.putString("longitud", longitudeNetwork);
                                detalleRevision.putExtras(bundle);
                                startActivity(detalleRevision);
                            }
                        }
                    });
        } catch (SecurityException se) {
            //Log.i("ERROR", se.toString());
        }
    }

    public void startGeofenceMonitoring() {
        try {
            Geofence geofence1 = new Geofence.Builder()
                    .setRequestId(GEOFENCE_VIVENTI_ID)
                    .setCircularRegion(14.630865,-90.568863, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setLoiteringDelay(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .build();

            Geofence geofence2 = new Geofence.Builder()
                    .setRequestId(GEOFENCE_CASA_ID)
                    .setCircularRegion(14.626525,-90.497272, 100)
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
            PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (!googleApiClient.isConnected()) {
                Toast.makeText(getActivity(), "GOOGLE API NO CONECTADA", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
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
            //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bandera = true;
        if (estado.equals("Salida")) {
            estad.setText("Pulse para registrar su ingreso");
        } else {
            estad.setText("Pulse para registrar su salida");
        }
    }
}
