package cl.dpsoft.isamonhome;

import android.content.Context;

import android.os.Bundle;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import cl.dpsoft.isamonhome.vo.Usuario;

public class UserLoginActivity extends AppCompatActivity {

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private EditText edtClave;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorUL);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);

        edtClave = (EditText) findViewById(R.id.edtClaveUL);
        edtClave.requestFocus();

        //openVentanaUserIngresaArea();

        cargarBarraSuperior();
        volver();
        utilidades.ocultarTeclado(contexto, edtClave);
        irIngresarArea();
    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.usrLogin);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(contexto, MainActivity.class);
                startActivity(mainActivity);
            }
        });
    }

    private void irIngresarArea(){
        edtClave.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    validar(null);
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
                        validar(null);
                        return false;
                    }
                    return false;
                }
            });
        }catch(Exception e){
            String mensaje = "ERROR "+e.getMessage().toUpperCase();
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }*/
    }

    public void validar(View vista){
        String clave = edtClave.getText().toString();
        String mensaje = "";
        if(clave.isEmpty()){
            mensaje = "INGRESAR CLAVE";
            edtClave.setError(mensaje);
            edtClave.requestFocus();
        }else{
            Usuario usuario = delegar.validarUsuario(contexto, clave);
            if (usuario.isbExiste()){
                mensaje = "BIENVENIDO "+usuario.getsNombre().toUpperCase();
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                Intent ingresarArea = new Intent(contexto, UserIngresaAreaActivity.class);
                ingresarArea.putExtra("codUsuario", clave);
                startActivity(ingresarArea);
            }else{
                mensaje = "CLAVE INCORRECTA";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                edtClave.requestFocus();
            }
        }
    }

    public void irMainActivity(View vista){
        Intent mainActivity = new Intent(contexto, MainActivity.class);
        startActivity(mainActivity);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*public void openVentanaUserIngresaArea(){
        Button button1 = (Button) findViewById(R.id.btnUsrLog);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método del handler de button1
                EditText et1=(EditText)findViewById(R.id.txtUserClave);

                Usuario uUser = validarUsuario(et1.getText().toString());
                if (uUser.isbExiste()){
                    Toast.makeText(UserLoginActivity.this, "Bienvenido " + uUser.getsNombre(), Toast.LENGTH_LONG).show();
                    Intent venMenu = new Intent(getApplicationContext(), UserIngresaAreaActivity.class);
                    venMenu.putExtra("codname",et1.getText().toString());
                    startActivity(venMenu);
                }else{
                    Toast.makeText(UserLoginActivity.this, "Usuario No Encontrado", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Usuario validarUsuario(String nRut){
        Usuario uUsuario = new Usuario();

        boolean conocido = false;

        MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(contexto,
                "Maestra", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{
            Cursor fila = bd.rawQuery("select 1, USUARIO as otro from usuario where rut='" + nRut.trim() + "'", null);

            if (fila.getCount()>0) {
                fila.moveToFirst();
                conocido = true;
                uUsuario.setbExiste(conocido);
                uUsuario.setsNombre(fila.getString(fila.getColumnIndex("otro")));
            } else {
                conocido = false;
                uUsuario.setbExiste(conocido);
                //uUsuario.setsNombre(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
            //conocido=false;
        }
        return uUsuario;
    }*/
}
