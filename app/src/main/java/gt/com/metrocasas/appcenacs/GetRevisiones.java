package gt.com.metrocasas.appcenacs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.ArrayList;
import java.util.List;

public class GetRevisiones extends AsyncTask<String, Integer, String> {

    Context context;
    private SwipeRefreshLayout swipeContainer;
    public static String ERROR = "No se encontraron revisiones para este proyecto";
    private RevisionAdapter rAdapter;
    private List<ItemRevision> listRevision = new ArrayList<>();
    LinearLayout p, q;
    ProgressBar progreso;

    public GetRevisiones(Context context, RevisionAdapter rAdapter, List<ItemRevision> listRevision, SwipeRefreshLayout swipeContainer, LinearLayout p, LinearLayout q, ProgressBar progreso) {
        this.context = context;
        this.rAdapter = rAdapter;
        this.listRevision = listRevision;
        this.swipeContainer = swipeContainer;
        this.p = p;
        this.q = q;
        this.progreso = progreso;
    }
    @Override
    protected String doInBackground(String... params) {
        try {

            String proyecto = params[0];
            String userid = params[1];
            String link = "http://atreveteacrecer.metrocasas.com.gt/readRevisiones2.php";
            String data = URLEncoder.encode("proyecto", "UTF-8") + "=" + URLEncoder.encode(proyecto, "UTF-8")
                    + "&" + URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8");

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
            JSONArray jsonArray = jsonObject.getJSONArray("revisiones");
            if(jsonArray.length() > 0)
            {
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject objecto = jsonArray.getJSONObject(i);
                    String fecha = objecto.getString("fechaRevision");
                    ItemRevision item = new ItemRevision(fecha);
                    listRevision.add(item);
                }

            }
        } catch (Exception e) {
            Toast.makeText(this.context, ERROR, Toast.LENGTH_SHORT).show();
        }
        p.setVisibility(View.GONE);
        q.setVisibility(View.VISIBLE);
        swipeContainer.setRefreshing(false);
        rAdapter.notifyDataSetChanged();
    }
}