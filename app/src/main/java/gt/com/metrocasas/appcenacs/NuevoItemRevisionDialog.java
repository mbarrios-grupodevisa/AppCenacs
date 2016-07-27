package gt.com.metrocasas.appcenacs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

public class NuevoItemRevisionDialog extends DialogFragment implements LocationListener {

    private TextView nombre_proyecto;
    private Spinner lista_estado;
    private LocationManager locationManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_ingresar_item_revision, null);
        nombre_proyecto = (TextView) v.findViewById(R.id.proyecto);
        lista_estado = (Spinner) v.findViewById(R.id.clasificacion);
        String proyecto = getArguments().getString("proyecto");
        nombre_proyecto.setText(proyecto);

        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            //Subir Datos
                            String proyecto = nombre_proyecto.getText().toString();
                            String clasificación = lista_estado.getSelectedItem().toString();
                            //new IngresoElemento(getActivity()).execute(proyecto,clasificación,rev);
                    }
                })
                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NuevoItemRevisionDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
