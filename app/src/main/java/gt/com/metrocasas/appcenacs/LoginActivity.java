package gt.com.metrocasas.appcenacs;

import android.content.Context;
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
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
