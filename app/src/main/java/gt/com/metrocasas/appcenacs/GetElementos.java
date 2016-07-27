package gt.com.metrocasas.appcenacs;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class GetElementos extends AsyncTask<String, Integer, String> {

    Context context;
    List<Elemento> listInternos;
    List<Elemento> listExternos;
    List<Elemento> listDespensa;
    List<Elemento> listLimpieza;
    List<Elemento> listConstruccion;
    ElementoAdapter iAdapter;
    ElementoAdapter eAdapter;
    ElementoAdapter dAdapter;
    ElementoAdapter lAdapter;
    ElementoAdapter cAdapter;
    LinearLayout p, q;
    ProgressBar progreso;
    public static String ERROR = "No se encontraron elementos de este proyecto";

    public GetElementos(Context context, ElementoAdapter iAdapter, ElementoAdapter eAdapter, ElementoAdapter dAdapter, ElementoAdapter lAdapter, ElementoAdapter cAdapter,  LinearLayout p, LinearLayout q, ProgressBar progreso) {
        this.context = context;
        this.iAdapter = iAdapter;
        this.eAdapter = eAdapter;
        this.dAdapter = dAdapter;
        this.lAdapter = lAdapter;
        this.cAdapter = cAdapter;
        this.listInternos = iAdapter.getListElemento();
        this.listExternos = eAdapter.getListElemento();
        this.listDespensa = dAdapter.getListElemento();
        this.listLimpieza = lAdapter.getListElemento();
        this.listConstruccion = cAdapter.getListElemento();
        this.p = p;
        this.q = q;
        this.progreso = progreso;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String proyecto = params[0];
            String link = "http://atreveteacrecer.metrocasas.com.gt/getProjectElements2.php";
            String data = URLEncoder.encode("proyecto", "UTF-8") + "=" + URLEncoder.encode(proyecto, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            if ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progreso.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("elementos");
            if(jsonArray.length() > 0)
            {
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject objecto = jsonArray.getJSONObject(i);
                    if(objecto.getString("clasificacion").equals("Interno")) {
                        Elemento item = new Elemento(objecto.getString("id"), objecto.getString("proyecto"), objecto.getString("clasificacion"), objecto.getString("elemento"));
                        listInternos.add(item);
                    } else if(objecto.getString("clasificacion").equals("Externo")) {
                        Elemento item = new Elemento(objecto.getString("id"), objecto.getString("proyecto"), objecto.getString("clasificacion"), objecto.getString("elemento"));
                        listExternos.add(item);
                    } else if(objecto.getString("clasificacion").equals("Despensa")) {
                        Elemento item = new Elemento(objecto.getString("id"), objecto.getString("proyecto"), objecto.getString("clasificacion"), objecto.getString("elemento"));
                        listDespensa.add(item);
                    } else if(objecto.getString("clasificacion").equals("Limpieza")) {
                        Elemento item = new Elemento(objecto.getString("id"), objecto.getString("proyecto"), objecto.getString("clasificacion"), objecto.getString("elemento"));
                        listLimpieza.add(item);
                    } else if(objecto.getString("clasificacion").equals("Construcción")) {
                        Elemento item = new Elemento(objecto.getString("id"), objecto.getString("proyecto"), objecto.getString("clasificacion"), objecto.getString("elemento"));
                        listConstruccion.add(item);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this.context, ERROR, Toast.LENGTH_SHORT).show();
        }
        p.setVisibility(View.GONE);
        q.setVisibility(View.VISIBLE);
        iAdapter.notifyDataSetChanged();
        eAdapter.notifyDataSetChanged();
        dAdapter.notifyDataSetChanged();
        lAdapter.notifyDataSetChanged();
        cAdapter.notifyDataSetChanged();

        if(listInternos.isEmpty())
        {
            cAdapter.hidenCardViewCenacInterno();
        }
        if(listExternos.isEmpty())
        {
            cAdapter.hidenCardViewCenacExterno();
        }
        if(listLimpieza.isEmpty())
        {
            cAdapter.hidenCardViewLimpieza();
        }
        if(listDespensa.isEmpty())
        {
            cAdapter.hidenCardViewDespensa();
        }
        if(listConstruccion.isEmpty())
        {
           cAdapter.hidenCardViewConstruccion();
        }
    }
}