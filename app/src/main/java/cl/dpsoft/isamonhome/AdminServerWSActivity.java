package cl.dpsoft.isamonhome;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;

public class AdminServerWSActivity extends AppCompatActivity {

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private EditText edtServidor;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_server_ws);

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorASWS);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);

        edtServidor = (EditText) findViewById(R.id.edtServidorASWS);
        edtServidor.requestFocus();

        /*EditText wsServer = (EditText) findViewById(R.id.txtAdminServer);

        String servi = delegate.getServer(getApplicationContext());
        if (servi.length()<2){
            wsServer.setText("192.168.2.0:8001");
        }else{
            wsServer.setText(servi);
        }*/

        cargarServidor();
        cargarBarraSuperior();
        volver();
        utilidades.ocultarTeclado(contexto, edtServidor);
        actServidor();
    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.confServidor);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                irAdminMenu(null);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void cargarServidor(){
        String servidor = delegar.traerServidor(contexto);
        if (servidor.length() < 2){
            String ipxDefecto = "192.168.2.0:8001";
            edtServidor.setText(ipxDefecto);
        }else{
            edtServidor.setText(servidor);
        }
    }

    private void actServidor(){
        try{
            edtServidor.setOnKeyListener(new View.OnKeyListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onKey(View view, int i, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                        actualizarServidor(null);
                        return false;
                    }
                    return false;
                }
            });
        }catch(Exception e){
            String mensaje = "ERROR "+e.getMessage().toUpperCase();
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            //Toast.makeText(contexto, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void actualizarServidor(View vista){
        String servidor = edtServidor.getText().toString();
        String mensaje = "";
        if(servidor.isEmpty()){
            mensaje = "INGRESAR SERVIDOR";
            edtServidor.setError(mensaje);
        }else{
            String respuestaActualizarServidor = delegar.actualizarServidor(contexto, servidor);
            if(respuestaActualizarServidor.equals("OK")){
                mensaje = "SERVIDOR ACTUALIZADO";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                //Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                irAdminMenu(null);
            }else{
                mensaje = "NO SE HA ACTUALIZADO EL SERVIDOR";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                //Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                edtServidor.requestFocus();
            }
        }
    }

    public void irAdminMenu(View vista){
        Intent adminMenu = new Intent(contexto, AdminMenuActivity.class);
        startActivity(adminMenu);
    }

    /*IsamDelegate delegate = new IsamDelegate();

    public void setServerInBase(View v){
        EditText serv = (EditText) findViewById(R.id.txtAdminServer);
        String servi = serv.getText().toString();
        String result = delegate.actualizarServidor(getApplicationContext(), servi);

        Toast.makeText(getApplicationContext(), "Hemos actualizado el server: " + result, Toast.LENGTH_SHORT).show();

    }

    public void irAdminMenu(View vista){
        Intent adminMenu = new Intent(contexto, AdminMenuActivity.class);
        startActivity(adminMenu);
    }*/
}
