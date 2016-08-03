package gt.com.metrocasas.appcenacs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class IngresoElemento extends AsyncTask<String, Integer, String> {

    Context context;
    public static String ERROR = "Error en los datos, revise";
    public String estado = "";

    public IngresoElemento(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try
        {
            String userid = params[0];
            estado = params[1];
            String proyecto = params[2];
            String latitud = params[3];
            String longitud = params[4];
            String fechaRegistro = params[5];

            String link = "http://atreveteacrecer.metrocasas.com.gt/insertRegistro.php";
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
        Toast.makeText(this.context, result, Toast.LENGTH_LONG).show();
        if (result.equals("Registro Correcto")) {
            SharedPreferences settings = context.getSharedPreferences("User", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("estado", estado);
            editor.apply();
        }
    }
}
