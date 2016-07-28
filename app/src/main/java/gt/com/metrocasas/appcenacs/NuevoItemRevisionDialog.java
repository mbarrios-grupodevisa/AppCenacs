package gt.com.metrocasas.appcenacs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

public class NuevoItemRevisionDialog extends DialogFragment {

    private TextView nombre_proyecto;
    private TextView hora;
    private Spinner lista_estado;


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_ingresar_item_revision, null);
        nombre_proyecto = (TextView) v.findViewById(R.id.proyecto);
        hora = (TextView) v.findViewById(R.id.hora);
        lista_estado = (Spinner) v.findViewById(R.id.clasificacion);
        String proyecto = getArguments().getString("proyecto");
        final double lat = getArguments().getDouble("latitud");
        final double lng = getArguments().getDouble("longitud");
        final String userid = getArguments().getString("id");
        nombre_proyecto.setText("Proyecto: "+proyecto);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        hora.setText("Fecha y enriqueHora: " + today.format("%C %B %l:%M%p"));

        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            //Subir Datos
                            String proyecto = nombre_proyecto.getText().toString();
                            String registro = lista_estado.getSelectedItem().toString();
                            String  latitud = lat+"";
                            String  longitud = lng+"";
                            String fechaRegistro = hora.getText().toString();
                            new IngresoElemento(getActivity()).execute(userid, registro, proyecto, latitud, longitud, fechaRegistro);
                    }
                })
                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NuevoItemRevisionDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();

    }

}
