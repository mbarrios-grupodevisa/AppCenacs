package gt.com.metrocasas.inoutcheck;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.gesture.GestureOverlayView;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationProviderReceiver extends BroadcastReceiver {

    public static final String GEOFENCE_VIVENTI_ID = "Viventi";
    public static final String GEOFENCE_CASA_ID = "Casa Asuncion";
    public static final String GEOFENCE_METROCASAS_ID = "Metrocasas";
    public static final String GEOFENCE_SANISIDRO2021_ID = "San Isidro 2021";
    public static final String GEOFENCE_MONTEFIORE_ID = "Montefiore";
    public static final String GEOFENCE_CENTENARIO_ID = "Centenario Tres 18";
    public static GoogleApiClient googleApiClient;

    public LocationProviderReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final SharedPreferences settings = context.getSharedPreferences("User",0);
        if (intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (settings.getString("servicio", "").equals("Detenido")) {
                    googleApiClient = new GoogleApiClient.Builder(context)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(@Nullable Bundle bundle) {
                                    //Toast.makeText(context, "CONECTADA", Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("servicio", "Encendido");
                                    editor.apply();
                                    startGeofenceMonitoring(context);
                                }

                                @Override
                                public void onConnectionSuspended(int i) {

                                }
                            })
                            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                    Toast.makeText(context, "FAIL", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .build();
                    googleApiClient.connect();
                }
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (settings.getString("servicio", null).equals("Encendido")) {
                    stopGeofenceMonitoring(context);
                }
            }
        }
    }

    public static void startGeofenceMonitoring(final Context context) {
        try {
            Geofence geoviventi = new Geofence.Builder()
                    .setRequestId(GEOFENCE_VIVENTI_ID)
                    .setCircularRegion(14.630865, -90.568863, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            Geofence geocasa = new Geofence.Builder()
                    .setRequestId(GEOFENCE_CASA_ID)
                    .setCircularRegion(14.626525, -90.497272, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            Geofence geometro = new Geofence.Builder()
                    .setRequestId(GEOFENCE_METROCASAS_ID)
                    .setCircularRegion(14.605549, -90.516273, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            Geofence geoisidrio2021 = new Geofence.Builder()
                    .setRequestId(GEOFENCE_SANISIDRO2021_ID)
                    .setCircularRegion(14.618552, -90.470821, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            Geofence geocentenario = new Geofence.Builder()
                    .setRequestId(GEOFENCE_CENTENARIO_ID)
                    .setCircularRegion(14.641453, -90.516437, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(0)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geoviventi)
                    .addGeofence(geocasa)
                    .addGeofence(geometro)
                    .addGeofence(geoisidrio2021)
                    .addGeofence(geocentenario)
                    .build();

            Intent intent = new Intent(context, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (!googleApiClient.isConnected()) {
                Toast.makeText(context, "GOOGLE API NO CONECTADA", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Toast.makeText(context, "Localizando Proyectos", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "ACTIVE GPS", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (Exception e) {
            //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stopGeofenceMonitoring (Context context) {
        final SharedPreferences settings = context.getSharedPreferences("User",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("servicio","Detenido");
        editor.apply();
        try {
            ArrayList<String> geofenceids = new ArrayList<>();
            geofenceids.add(GEOFENCE_VIVENTI_ID);
            geofenceids.add(GEOFENCE_CASA_ID);
            geofenceids.add(GEOFENCE_METROCASAS_ID);
            geofenceids.add(GEOFENCE_SANISIDRO2021_ID);
            geofenceids.add(GEOFENCE_CENTENARIO_ID);
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceids);
            Toast.makeText(context, "Detenida Localización de Proyectos", Toast.LENGTH_LONG).show();
            googleApiClient.disconnect();
        } catch (Exception e) {
            Toast.makeText(context, "Detenida Localización de Proyectos", Toast.LENGTH_LONG).show();
        }
    }
}
