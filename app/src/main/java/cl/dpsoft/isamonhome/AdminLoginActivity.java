package cl.dpsoft.isamonhome;

import android.content.Context;

import android.os.Bundle;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;

public class AdminLoginActivity extends AppCompatActivity {

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private EditText edtClave;
    private final Utilidades utilidades = new Utilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorAL);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);

        edtClave = (EditText) findViewById(R.id.edtClaveAL);
        edtClave.requestFocus();

        utilidades.ocultarTeclado(contexto, edtClave);
        irAdmMenu();
        cargarBarraSuperior();
        volver();
    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.admLogin);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                irMainActivity(null);
            }
        });
    }

    public void irMainActivity(View vista){
        Intent mainActivity = new Intent(contexto, MainActivity.class);
        startActivity(mainActivity);
    }

    private void irAdmMenu(){
        edtClave.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    irAdminMenu(null);
                    return true;
                }
                return false;
            }
        });

        /*try{
            edtClave.setOnKeyListener(new View.OnKeyListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onKey(View view, int i, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                        irAdminMenu(null);
                        return false;
                    }
                    return false;
                }
            });
        }catch(Exception e){
            String mensaje = "ERROR "+e.getMessage().toUpperCase();
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            //Toast.makeText(contexto, "ERROR", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void irAdminMenu(View vista){
        String clave = edtClave.getText().toString();
        String mensaje = "";
        if(validarUsuario(clave)) {
            mensaje = "BIENVENIDO ADMINISTRADOR";
            //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
            Intent adminMenu = new Intent(contexto, AdminMenuActivity.class);
            startActivity(adminMenu);
        }else if(clave.isEmpty()){
            mensaje = "DEBE INGRESAR CLAVE";
            edtClave.setError(mensaje);
            edtClave.requestFocus();
        }else{
            mensaje = "CLAVE INCORRECTA";
            //edtClave.setError(mensaje);
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            edtClave.requestFocus();
        }
    }

    private boolean validarUsuario(String clave){
        boolean conocido;
        conocido = clave.equals("1");
        return conocido;
    }
}
