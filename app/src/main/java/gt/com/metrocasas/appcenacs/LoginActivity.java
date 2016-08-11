package gt.com.metrocasas.appcenacs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText user, pass;
    ProgressBar progress;
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getSharedPreferences("User",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("servicio","Detenido");
        editor.apply();
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);


        progress = (ProgressBar)findViewById(R.id.progresslogin);
        user = (EditText)findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.password);

        login = (Button)findViewById(R.id.iniciarsesion);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(user.getText().toString().equals("") || pass.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Campos vacíos", Toast.LENGTH_LONG).show();
                    } else {
                        if (isNetworkAvailable()) {
                            progress.setVisibility(View.VISIBLE);
                            progress.setProgress(0);
                            new LoginConnection(getApplicationContext(), progress, user, pass, login).execute(user.getText().toString(), pass.getText().toString());
                        } else {
                            Toast.makeText(getApplication(), "No tienes conexión a internet", Toast.LENGTH_LONG).show();
                        }
                    }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.isActiveNetworkMetered();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
        //SharedPreferences.Editor editor = editor = settings.edit();
        //editor.putInt("homeScore", YOUR_HOME_SCORE);
        //editor.apply();

        SharedPreferences settings = getApplicationContext().getSharedPreferences("User",0);
        String dato = settings.getString("id",null);
        if(dato!=null)
        {
            Intent i = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", dato);
            bundle.putString("init","normal");
            i.putExtras(bundle);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
            user.setText("");
            pass.setText("");
        }
    }
}
