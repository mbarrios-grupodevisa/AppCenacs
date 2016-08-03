package gt.com.metrocasas.appcenacs;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;


public class GeofenceService extends IntentService {

    public static final String TAG = "GeofenceService";
    NotificationManager notificationManager;
    Notification myNotification;
    private static final int MY_NOTIFICATION_ID = 1;
    String user, proyecto, name;

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
        proyecto = intent.getStringExtra("proyecto");
        name = intent.getStringExtra("nombre");
        if(!geofencingEvent.hasError()) {
            int transition = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestid = geofence.getRequestId();

            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                sendNotification("Bienvenido a " + requestid, "No olvide hacer su registro de entrada");
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                sendNotification("Salida de " + requestid, "Vuelva pronto");
            } else if (transition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            }
        }
    }

    private void sendNotification(String titulo, String msg) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DetalleRevisionActivity.class)
                        .putExtra("id", user)
                        .putExtra("proyecto", proyecto),
                PendingIntent.FLAG_UPDATE_CURRENT);

        myNotification = new Notification.Builder(getApplicationContext())
                .setContentTitle(titulo)
                .setContentText(msg)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.arrow_down_float)
                .setContentIntent(contentIntent)
                .build();
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }
}