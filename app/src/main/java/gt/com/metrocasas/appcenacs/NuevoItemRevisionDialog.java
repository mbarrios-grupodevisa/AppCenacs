package gt.com.metrocasas.appcenacs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NuevoItemRevisionDialog extends DialogFragment{

    private Spinner lista_proyectos;
    private Spinner lista_clasificacion;
    private EditText revision;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_ingresar_item_revision, null);
        lista_proyectos = (Spinner) v.findViewById(R.id.proyecto);
        lista_clasificacion = (Spinner) v.findViewById(R.id.clasificacion);
        revision = (EditText) v.findViewById(R.id.editText_revision);
        String proyecto = getArguments().getString("proyecto");
        assert proyecto != null;
        if(proyecto.equals("Viventi")) lista_proyectos.setSelection(0);
        if(proyecto.equals("Casa Asunción")) lista_proyectos.setSelection(1);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if(true)
                        {
                            //Subir Datos
                            String proyecto = lista_proyectos.getSelectedItem().toString();
                            String clasificación = lista_clasificacion.getSelectedItem().toString();
                            String rev = revision.getText().toString();
                            new IngresoElemento(getActivity()).execute(proyecto,clasificación,rev);
                        }
                        else
                        {
                            Toast.makeText(v.getContext(),"Hay campos vacios",Toast.LENGTH_LONG).show();
                            NuevoItemRevisionDialog.this.getDialog().dismiss();
                        }
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
