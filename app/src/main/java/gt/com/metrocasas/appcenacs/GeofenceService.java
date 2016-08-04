package gt.com.metrocasas.appcenacs;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;


public class GeofenceService extends IntentService {

    public static final String TAG = "GeofenceService";
    NotificationManager notificationManager;
    Notification myNotification;
    private static final int MY_NOTIFICATION_ID = 1;
    String user, proyecto, name, latitud, longitud;

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
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        user = intent.getStringExtra("userid");
        name = intent.getStringExtra("nombre");
        latitud = String.valueOf(geofencingEvent.getTriggeringLocation().getLatitude());
        longitud = String.valueOf(geofencingEvent.getTriggeringLocation().getLongitude());
        if(!geofencingEvent.hasError()) {
            int transition = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestid = geofence.getRequestId();
            proyecto = requestid;
            SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);

            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER) { if(!settings.getString("estado", null).equals("Ingreso")) {
                    sendNotification("Bienvenido a " + requestid, "No olvides hacer tu registro de entrada", "Ingreso");
                }
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) { if(!settings.getString("estado", null).equals("Salida")) {
                    sendNotification("Salida de " + requestid, "No olvides hacer tu registro de salida", "Salida");
                }
            }
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

        if (proyecto.equals("Viventi")) {
            myNotification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(titulo)
                    .setContentText(msg)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.viventi))
                    .setSmallIcon(R.drawable.viventi)
                    .setContentIntent(contentIntent)
                    .build();
        } else {
            myNotification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(titulo)
                    .setContentText(msg)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.casa))
                    .setSmallIcon(R.drawable.casa)
                    .setContentIntent(contentIntent)
                    .build();
        }
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }
}