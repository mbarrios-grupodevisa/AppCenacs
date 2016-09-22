package gt.com.metrocasas.inoutcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class CheckTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Salida de Proyecto", Toast.LENGTH_LONG).show();
        SharedPreferences settings = context.getSharedPreferences("User",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("estado", "Salida");
        editor.putString("proyecto", "Metrocasas");
        editor.apply();
    }
}