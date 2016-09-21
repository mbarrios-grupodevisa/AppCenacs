package gt.com.metrocasas.inoutcheck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetalleRevisionActivity extends AppCompatActivity {


    private List<Elemento> listItemCI = new ArrayList<>();
    private RecyclerView recyclerViewCenacInterno;

    private String proyecto;
    private String user;
    private String fechaRevision, estado, latitud, longitud;
    private View v;
    TextView info;
    LinearLayout p, q;
    ProgressBar progreso;
    MenuItem btn ;

    private static final int VIVENTI = 3;
    private static final int CASA_ASUNCION = 4;
    public final static String BUCKET_NAME = "projectsgtimages";

    ElementoAdapter aAdapterCI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detalle_revision);
        proyecto = getIntent().getExtras().getString("proyecto");
        user = getIntent().getExtras().getString("id");
        estado = getIntent().getExtras().getString("estado");
        latitud = getIntent().getExtras().getString("latitud");
        longitud = getIntent().getExtras().getString("longitud");
        fechaRevision = new SimpleDateFormat("dd MMMM yyyy hh:mm a").format(new Date());
        this.setTitle(proyecto);

        recyclerViewCenacInterno = (RecyclerView) findViewById(R.id.recycler_view_ci);

        Button enviar_datos = (Button) findViewById(R.id.btn_enviar_datos);
        enviar_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    subirInformacion();
                } else {
                    Toast.makeText(getApplication(), "Conéctate a la red y presiona recargar", Toast.LENGTH_LONG).show();
                }
            }
        });
        initCardViews();
        initAdapters();

        if(isNetworkAvailable()) {
            hilosSecundarios();
        } else {
            Toast.makeText(getApplication(), "Conéctate a la red y presiona recargar", Toast.LENGTH_LONG).show();
        }
    }

    private void initCardViews() {
        final TextView titulo_ci = (TextView) findViewById(R.id.text_view_title_cenac_interno);
        titulo_ci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerViewCenacInterno.getVisibility() == View.VISIBLE) {
                    recyclerViewCenacInterno.setVisibility(View.GONE);
                    titulo_ci.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow_down,0);
                }else {
                    titulo_ci.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow_up,0);
                    recyclerViewCenacInterno.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initAdapters() {
        aAdapterCI = new ElementoAdapter(listItemCI, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewCenacInterno.setLayoutManager(mLayoutManager);
        recyclerViewCenacInterno.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCenacInterno.setAdapter(aAdapterCI);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.isActiveNetworkMetered();
    }

    public void hilosSecundarios() {
        p = (LinearLayout)findViewById(R.id.layoutprogress2);
        q = (LinearLayout)findViewById(R.id.layoutlist2);

        progreso = (ProgressBar)findViewById(R.id.progressBar2);
        progreso.setProgress(0);
        info = (TextView)findViewById(R.id.tvUpload2);
        v = findViewById(R.id.detalle);
        new IngresoElemento(this, v).execute(user, estado, proyecto, latitud, longitud, fechaRevision);
        new GetElementos(this, aAdapterCI, p, q, progreso).execute(proyecto);
    }

    public void subirInformacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación de envío");
        builder.setMessage("Confirmo que deseo enviar y cerrar esta revisión.");
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                p.setVisibility(View.VISIBLE);
                q.setVisibility(View.GONE);
                info.setText(R.string.subiendofotos);
                progreso.setProgress(0);
                upLoadPictures();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public CognitoCachingCredentialsProvider amazonCognito() {
        return new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:66fdcec3-f2c0-4015-80ef-f9efcad31fb3", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
    }

    private boolean bandera = true;
    public void upLoadPictures() {
        bandera = true;
        List<Elemento> elements = getListElements();
        ArrayList<File> files = new ArrayList<>();
        for (Elemento e: elements) {
            if(!e.getImagePath().equals(""))
            files.add(new File(e.getImagePath()));
        }
        if (!files.isEmpty()) {
            for (File i : files) {
                AmazonS3 s3Client = new AmazonS3Client(amazonCognito());
                TransferUtility transferUtility = new TransferUtility(s3Client, this);
                TransferObserver transferObserver = transferUtility.upload(BUCKET_NAME, "revisionescenac/" + i.getName(), i);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            if (bandera) {
                                ingresarInformacion();
                                limpiarCampos();
                                bandera = false;
                            }
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        progreso.setProgress((int) bytesCurrent);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        //Toast.makeText(DetalleRevisionActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            ingresarInformacion();
            limpiarCampos();
        }
    }

    public void ingresarInformacion() {
        String json = getJSON(getListElements());
        Intent databack = new Intent();
        databack.putExtra("fecha",fechaRevision);
        if(proyecto.equals("Viventi")) setResult(VIVENTI,databack);
        if(proyecto.equals("Casa Asunción")) setResult(CASA_ASUNCION,databack);
        if(proyecto.equals("Metrocasas")) setResult(CASA_ASUNCION,databack);
        new IngresoRevision(getApplicationContext(), v, p, q, progreso, this, info).execute(user, proyecto, fechaRevision, json);
    }

    public void limpiarCampos() {
        listItemCI = new ArrayList<>();
    }

    public List<Elemento> getListElements() {
        List<Elemento> list = new ArrayList<>();
        list.addAll(listItemCI);
        return list;
    }

    public String getJSON(List<Elemento> lista) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (Elemento i:lista) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("elemento_id", i.getId());
                jsonObject.put("estado", i.isEstado());
                jsonObject.put("imagen", i.getImagen());
                jsonObject.put("comentario", i.getComentario());
                jsonArray.put(jsonObject);
            }
            JSONObject studentsObj = new JSONObject();
            studentsObj.put("detalles", jsonArray);
            return studentsObj.toString();
        } catch (Exception e) {
            return "null";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        btn = menu.findItem(R.id.action_settings);
        return !isNetworkAvailable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if(isNetworkAvailable()) {
                limpiarCampos();
                initAdapters();
                hilosSecundarios();
                btn.setVisible(false);
                return super.onOptionsItemSelected(item);
            } else {
                Toast.makeText(getApplication(), "Conéctate a la red y presiona recargar", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }


}
