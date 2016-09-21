package gt.com.metrocasas.inoutcheck;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;


public class GeofenceService extends IntentService {

    public static final String TAG = "GeofenceService";
    NotificationManager notificationManager;
    Notification myNotification;
    private static final int MY_NOTIFICATION_ID = 1;
    String user, proyecto, latitud, longitud;

    public GeofenceService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
            user = settings.getString("id", null);
            latitud = String.valueOf(geofencingEvent.getTriggeringLocation().getLatitude());
            longitud = String.valueOf(geofencingEvent.getTriggeringLocation().getLongitude());
            if(!geofencingEvent.hasError()) {
                int transition = geofencingEvent.getGeofenceTransition();
                List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
                Geofence geofence = geofences.get(0);
                String requestid = geofence.getRequestId();
                proyecto = requestid;
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("proyecto", proyecto);
                editor.apply();

                if(transition == Geofence.GEOFENCE_TRANSITION_ENTER) { if(!settings.getString("estado", "").equals("Ingreso")) {
                    sendNotification("Llegando a " + requestid, "Pulsa para registrar tu entrada", "Ingreso");
                }
                } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) { if(!settings.getString("estado", "").equals("Salida")) {
                    sendNotification("Saliendo de " + requestid, "Pulsa para tu registrar tu salida", "Salida");
                }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "SERVICE: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String titulo, String msg, String estado) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DetalleRevisionActivity.class)
                        .putExtra("id", user)
                        .putExtra("proyecto", proyecto)
                        .putExtra("estado", estado)
                        .putExtra("latitud", latitud)
                        .putExtra("longitud", longitud)
                        .putExtra("init", "notification"),
                PendingIntent.FLAG_UPDATE_CURRENT);

        switch (proyecto) {
            case LocationProviderReceiver.GEOFENCE_VIVENTI_ID:
                myNotification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(titulo)
                        .setContentText(msg)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setLights(0xffffffff, 500, 1500)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.viventi))
                        .setSmallIcon(R.drawable.viventi)
                        .setContentIntent(contentIntent)
                        .build();
                break;
            case LocationProviderReceiver.GEOFENCE_CASA_ID:
                myNotification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(titulo)
                        .setContentText(msg)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setLights(0xffffffff, 500, 1500)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.casa))
                        .setSmallIcon(R.drawable.casa)
                        .setContentIntent(contentIntent)
                        .build();
                break;
            case LocationProviderReceiver.GEOFENCE_METROCASAS_ID:
                myNotification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(titulo)
                        .setContentText(msg)
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setLights(0xffffffff, 500, 1500)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.metrocasas))
                        .setSmallIcon(R.drawable.metrocasas)
                        .setContentIntent(contentIntent)
                        .build();
                break;
        }
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }
}