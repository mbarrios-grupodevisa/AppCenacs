package gt.com.metrocasas.inoutcheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

class IngresoElemento extends AsyncTask<String, Integer, String> {

    private View v;
    Context context;
    private String estado = "";
    public String proyecto = "";

    IngresoElemento(Context context, View v) {
        this.context = context;
        this.v = v;
    }

    @Override
    protected String doInBackground(String... params) {
        try
        {
            String userid = params[0];
            estado = params[1];
            proyecto = params[2];
            String latitud = params[3];
            String longitud = params[4];
            String fechaRegistro = params[5];

            String link = "http://monkeepower.com/appsinternas/inoutcheck/insertRegistro.php";
            String data = URLEncoder.encode("userid", "UTF-8") + "=" + URLEncoder.encode(userid, "UTF-8")
                    + "&" + URLEncoder.encode("registro", "UTF-8") + "=" + URLEncoder.encode(estado, "UTF-8")
                    + "&" + URLEncoder.encode("proyecto", "UTF-8") + "=" + URLEncoder.encode(proyecto, "UTF-8")
                    + "&" + URLEncoder.encode("latitud", "UTF-8") + "=" + URLEncoder.encode(latitud, "UTF-8")
                    + "&" + URLEncoder.encode("longitud", "UTF-8") + "=" + URLEncoder.encode(longitud, "UTF-8")
                    + "&" + URLEncoder.encode("fechaRegistro", "UTF-8") + "=" + URLEncoder.encode(fechaRegistro, "UTF-8");

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
            return sb.toString();
        }
        catch(Exception e)  {
            String ERROR;
            ERROR = "Error en los datos, revise";
            return ERROR;
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(String result) {
        Snackbar.make(v, "Registro de " + estado + " Correcto", Snackbar.LENGTH_INDEFINITE)
                .setAction("Aceptar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
        if (result.equals("Registro Correcto")) {
            SharedPreferences settings = context.getSharedPreferences("User", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("estado", estado);
            editor.putString("proyecto", proyecto);
            editor.apply();
        }
    }
}
