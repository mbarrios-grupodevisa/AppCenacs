package gt.com.metrocasas.inoutcheck;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class comentarioDialog extends DialogFragment {

    private EditText comentario;
    private ImageView imagen;
    private Button btn_coment;

    Elemento item;
    private String pictureImagePath = "";
    public final static int RESULT_CAMERA = 1;
    File imgFile = null;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_comentario, null);
        comentario = (EditText) v.findViewById(R.id.editText_comentario);
        imagen = (ImageView)v.findViewById(R.id.Foto);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });

        builder.setView(v)
                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            if(!comentario.getText().toString().equals("") || imgFile != null)
                            {
                                if(imgFile != null){
                                    item.setImagen(imgFile.getName());
                                    item.setImagePath(imgFile.getAbsolutePath());
                                }
                                item.setComentario(comentario.getText().toString());
                                item.setEstado(true);
                                btn_coment.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                btn_coment.setTextColor(Color.WHITE);
                            }else{
                                comentarioDialog.this.getDialog().dismiss();
                            }

                    }
                })
                .setNegativeButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        comentarioDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void setElement(Elemento e){
        this.item = e;
    }

    public void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasPermission = getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                takePicture();
            }
        } else {
            takePicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_ASK_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permiso concedido, presion de nuevo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Se requiere este permiso para continuar", Toast.LENGTH_SHORT).show();
        }
    }

    public void takePicture() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Revision" + timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, RESULT_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CAMERA) {
            imgFile = new  File(pictureImagePath);
            if(imgFile.exists()){
                imagen.setBackground(Drawable.createFromPath(imgFile.getAbsolutePath()));
                imagen.setImageResource(0);
            }
        }
    }

    public void setBtn_coment(Button btn_coment) {
        this.btn_coment = btn_coment;
    }
}
