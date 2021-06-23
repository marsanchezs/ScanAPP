package cl.dpsoft.isamonhome;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;

public class MainActivity extends AppCompatActivity {

    private Context contexto;
    Utilidades utilidades = new Utilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contexto = getApplicationContext();

    }

    public void irAdminLogin(View vista){
        Intent adminLogin = new Intent(contexto, AdminLoginActivity.class);
        startActivity(adminLogin);
    }

    public void irUserLogin(View vista){
        Intent userLogin = new Intent(contexto, UserLoginActivity.class);
        startActivity(userLogin);
    }

}
