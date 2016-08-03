package gt.com.metrocasas.appcenacs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginConnection extends AsyncTask<String, Integer, String> {

    Context context;
    ProgressBar progressBar;
    EditText user, pass;
    Button login;
    public static String ERROR = "Error en los datos, revise";

    public LoginConnection(Context context, ProgressBar progressBar, EditText user, EditText pass, Button login) {
        this.context = context;
        this.progressBar = progressBar;
        this.user = user;
        this.pass = pass;
        this.login = login;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            String user = params[0];
            String pass = params[1];

            String link = "http://atreveteacrecer.metrocasas.com.gt/revisionlogin2.php";
            String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                    + "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String line;
            if ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            return ERROR;
        }
    }

    @Override
    protected void onPreExecute() {
        user.setEnabled(false);
        pass.setEnabled(false);
        login.setEnabled(false);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        this.progressBar.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        user.setEnabled(true);
        pass.setEnabled(true);
        login.setEnabled(true);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            if(jsonArray.length() > 0)
            {
                JSONObject objecto = jsonArray.getJSONObject(0);
                String id = objecto.getString("id");
                String name = objecto.getString("firstname");
                String last = objecto.getString("lastname");

                SharedPreferences settings = context.getSharedPreferences("User",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("id",id);
                editor.putString("firstname",name);
                editor.putString("lastname", last);
                editor.putString("estado", "Salida");
                editor.apply();

                Intent i = new Intent(this.context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(i);
                progressBar.setVisibility(View.INVISIBLE);
                user.setText("");
                pass.setText("");
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this.context, "Datos Inválidos", Toast.LENGTH_SHORT).show();
        }
    }
}
