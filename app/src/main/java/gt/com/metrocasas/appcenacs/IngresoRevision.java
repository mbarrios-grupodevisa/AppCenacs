package gt.com.metrocasas.appcenacs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class IngresoRevision extends AsyncTask<String, Integer, String> {

    View v;
    Context context;
    String revisionid;
    LinearLayout p, q;
    ProgressBar progreso;
    AppCompatActivity activity;
    TextView info;

    public IngresoRevision(Context context, View v, LinearLayout p, LinearLayout q, ProgressBar progreso, AppCompatActivity activity, TextView info) {
        this.context = context;
        this.v = v;
        this.p = p;
        this.q = q;
        this.progreso = progreso;
        this.activity = activity;
        this.info = info;
    }

    @Override
    protected String doInBackground(String... params) {
        try
        {
            String user = params[0];
            String proyecto = params[1];
            String fechaRevision = params[2];
            String json = params[3];

            String link = "http://atreveteacrecer.metrocasas.com.gt/insertRevision.php";
            String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                    + "&" + URLEncoder.encode("proyecto", "UTF-8") + "=" + URLEncoder.encode(proyecto, "UTF-8")
                    + "&" + URLEncoder.encode("fechaRevision", "UTF-8") + "=" + URLEncoder.encode(fechaRevision, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String line;
            if((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("revisiones");
            revisionid  = jsonArray.getJSONObject(0).getString("id");

                link = "http://atreveteacrecer.metrocasas.com.gt/insertDetalleRevision.php";
                data = URLEncoder.encode("revision_id", "UTF-8") + "=" + URLEncoder.encode(revisionid, "UTF-8")
                        + "&" + URLEncoder.encode("json", "UTF-8") + "=" + URLEncoder.encode(json, "UTF-8");

                url = new URL(link);
                conn = url.openConnection();
                conn.setDoOutput(true);

                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                sb = new StringBuilder();
                if((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }

            return "success";
        }
        catch(Exception e)  {
            return e.toString();
        }
    }

    @Override
    protected void onPreExecute() {
        info.setText(R.string.guardando);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progreso.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        p.setVisibility(View.GONE);
        q.setVisibility(View.VISIBLE);
        activity.finish();
    }

}
