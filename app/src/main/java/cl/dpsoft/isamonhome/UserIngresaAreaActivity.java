package cl.dpsoft.isamonhome;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;

public class UserIngresaAreaActivity extends AppCompatActivity {

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private EditText edtArea, edtUbicacion;
    private String codUsuario, tipo = "";
    private Button btnScanArea, btnScanUbicacion;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ingresa_area);

        codUsuario = (String) getIntent().getExtras().get ("codUsuario");

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorIA);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);

        edtArea = (EditText) findViewById(R.id.edtAreaUIA);
        edtUbicacion = (EditText) findViewById(R.id.edtUbicacionUIA);
        btnScanArea = (Button) findViewById(R.id.btnScanAreaUIA);
        btnScanUbicacion = (Button) findViewById(R.id.btnScanUbicacionUIA);

        edtArea.requestFocus();
        cargarAreaUbicacion();
        cargarBarraSuperior();
        volver();
        utilidades.ocultarTeclado(contexto, edtArea);
        //valArea();
        //cambiarEditText();

        btnScanArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo = "ÁREA";
                scan(tipo);
            }
        });
        btnScanUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipo = "UBICACIÓN";
                scan(tipo);
            }
        });
    }

    //MÉTODOS
    private void scan(String tipo) {
        IntentIntegrator integrador = new IntentIntegrator(this);
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        if(tipo.equals("ÁREA")){
            integrador.setPrompt("ESCANEAR ÁREA");
        }else if(tipo.equals("UBICACIÓN")){
            integrador.setPrompt("ESCANEAR UBICACIÓN");
        }
        integrador.setCameraId(0);
        integrador.setOrientationLocked(false);
        integrador.setBeepEnabled(false);
        integrador.setCaptureActivity(CaptureActivityPortrait.class);
        integrador.setBarcodeImageEnabled(false);
        integrador.initiateScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult resultado = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String datos = "REQUEST CODE: "+requestCode+" RESULT CODE: "+resultCode+" DATA: "+data;
        //tilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        if(resultado != null) {
            if(resultado.getContents() == null) {
                utilidades.mostrarMensajePersonalizado(contexto, "SCANEO CANCELADO", "NOK");
            } else {

                String res = resultado.getContents();
                if(tipo.equals("ÁREA")){
                    edtArea.setText(res);
                    edtUbicacion.requestFocus();
                }else if(tipo.equals("UBICACIÓN")){
                    edtUbicacion.setText(res);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void cargarBarraSuperior(){
        String titulo = getString(R.string.ingresarArea);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                irUserLogin(null);
            }
        });
    }

    public void irUserLogin(View vista){
        Intent userLogin = new Intent(contexto, UserLoginActivity.class);
        startActivity(userLogin);
    }

    public void irEnviarAreas(View vista){
        Intent enviarAreas = new Intent(contexto, UserEnviarAreasActivity.class);
        enviarAreas.putExtra("codUsuario", codUsuario);
        startActivity(enviarAreas);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void cargarAreaUbicacion(){
        String areaUbicacion = delegar.areaAbierta(contexto);
        System.out.println("ÁREA ABIERTA: "+areaUbicacion);
        String[] partesAreaUbicacion = areaUbicacion.split(";");
        String areaAbierta = partesAreaUbicacion[0];
        switch (areaAbierta){
            case "SINAREAS":
                edtArea.setText("");
                edtUbicacion.setText("");
                edtArea.requestFocus();
                break;
            case "ERROR":
                edtArea.setText("E");
                edtUbicacion.setText("E");
                edtArea.requestFocus();
                break;
            default:
                String ubicacionAbierta = partesAreaUbicacion[1];
                //String mensajeArea = "ÁREA: "+areaAbierta;
                edtArea.setText(areaAbierta);
                if(ubicacionAbierta.equals("SIN UBICACIÓN")){
                    ubicacionAbierta = "";
                    edtUbicacion.requestFocus();
                }else{
                    deshabilitarEditText(edtUbicacion);
                    deshabilitarBoton(btnScanUbicacion);
                }
                //String mensajeUbicacion = "UBICACIÓN: "+ubicacionAbierta;
                edtUbicacion.setText(ubicacionAbierta);
                //edtArea.requestFocus();
                deshabilitarEditText(edtArea);
                deshabilitarBoton(btnScanArea);
                break;
        }
    }

    private void deshabilitarEditText(EditText edt){
        edt.setFocusable(false);
        edt.setEnabled(false);
        edt.setCursorVisible(false);
        edt.setKeyListener(null);
        edt.setBackgroundColor(Color.TRANSPARENT);
    }

    private void deshabilitarBoton(Button boton){
        boton.setEnabled(false);
    }

    private void cambiarEditText(){
        edtArea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_NEXT
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    edtUbicacion.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void validar(View vista){
        String area = edtArea.getText().toString();
        String mensaje = "";
        if(area.isEmpty()){
            mensaje = "INGRESAR ÁREA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            edtArea.requestFocus();
        }else{

            String areaUbicacion = delegar.areaAbierta(contexto);
            System.out.println("ÁREA ABIERTA: "+areaUbicacion);
            //ÁREA ABIERTA: 555;gggg
            String[] partesAreaUbicacion = areaUbicacion.split(";");
            String areaAbierta = partesAreaUbicacion[0];
            if ((areaAbierta.equals(area)) || (areaAbierta.equals("SINAREAS"))){
                if (delegar.areaExistio(contexto, area)) {
                    mensaje = "EL ÁREA "+area+" ESTÁ CERRADA";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                    edtArea.requestFocus();
                    //Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                }else{
                    irIngresarCaptura();
                }
            }else{
                edtArea.setText(areaAbierta);
                mensaje = "EL ÁREA "+areaAbierta+" ESTÁ ABIERTA";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                //Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                edtArea.requestFocus();
            }

        }
    }

    private void irIngresarCaptura(){
        //Intent ingresarCaptura = new Intent(contexto, UserIngresaCapturaActivity.class);
        Intent ingresarCaptura = new Intent(contexto, UserGuardarCapturaActivity.class);
        String ubicacion = edtUbicacion.getText().toString();
        if(ubicacion.isEmpty()){
            ubicacion = "SIN UBICACIÓN";
        }
        ingresarCaptura.putExtra("area", edtArea.getText().toString());
        ingresarCaptura.putExtra("ubicacion", ubicacion);
        ingresarCaptura.putExtra("codUsuario", codUsuario);
        startActivity(ingresarCaptura);
    }
}
