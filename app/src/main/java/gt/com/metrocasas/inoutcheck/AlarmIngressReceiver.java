package gt.com.metrocasas.inoutcheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

public class AlarmIngressReceiver extends BroadcastReceiver {
    NotificationManager notificationManager;
    Notification myNotification;
    private static final int MY_NOTIFICATION_ID_ENTER = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences("User",0);
        String dato = settings.getString("estado","");
        if(dato.equals("Salida")) {
            String user = settings.getString("id",null);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class)
                            .putExtra("id", user)
                            .putExtra("init", "normal"),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            myNotification = new Notification.Builder(context)
                    .setContentTitle("In-Out Check")
                    .setContentText("No has realizado tu registro de entrada.")
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setLights(0xffffffff, 500, 1500)
                    .setContentIntent(contentIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.metrocasas))
                    .setSmallIcon(R.drawable.metrocasas)
                    .build();
            notificationManager.notify(MY_NOTIFICATION_ID_ENTER, myNotification);
        }
    }
}
