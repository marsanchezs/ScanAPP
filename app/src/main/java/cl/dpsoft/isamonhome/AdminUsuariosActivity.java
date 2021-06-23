package cl.dpsoft.isamonhome;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;

public class AdminUsuariosActivity extends AppCompatActivity {

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private EditText edtRut, edtNombre;
    private ListView lvUsuarios;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_usuarios);

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorCU);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);
        edtRut = (EditText) findViewById(R.id.edtRutAU);
        edtNombre = (EditText) findViewById(R.id.edtNombreAU);
        lvUsuarios = (ListView) findViewById(R.id.lvUsuariosAU);

        edtRut.requestFocus();
        cargarUsuarios();
        cargarBarraSuperior();
        volver();
        utilidades.ocultarTeclado(contexto, edtRut);
        cambiarEditText();
    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.crearUsuario);
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

    public void irAdminMenu(View vista){
        Intent adminMenu = new Intent(contexto, AdminMenuActivity.class);
        startActivity(adminMenu);
    }

    public void cargarUsuarios() {
        ArrayList<String> listaUsuarios = delegar.traerUsuarios(contexto);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                R.layout.lista_desplegable, R.id.txt, listaUsuarios);
        lvUsuarios.setAdapter(adaptador);
    }

    private void cambiarEditText(){
        edtRut.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtNombre.requestFocus();
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void validarUsuario(View vista){
        String rut = edtRut.getText().toString();
        String nombre = edtNombre.getText().toString();
        String mensaje = "";
        if(rut.isEmpty() && nombre.isEmpty()){
            mensaje = "INGRESAR RUT Y NOMBRE";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
            edtRut.requestFocus();
        }else if(rut.isEmpty()){
            mensaje = "INGRESAR RUT";
            edtRut.setError(mensaje);
            edtRut.requestFocus();
        }else if(nombre.isEmpty()){
            mensaje = "INGRESAR NOMBRE";
            edtNombre.setError(mensaje);
            edtNombre.requestFocus();
        }else{
            agregarUsuario(rut, nombre);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void agregarUsuario(String rut, String nombre){
        String respuestaAgregarUsuario = delegar.agregarUsuario(contexto, rut, nombre);
        String mensaje = "";
        if(respuestaAgregarUsuario.equals("OK")){
            mensaje = nombre.toUpperCase()+" HA SIDO AGREGADO";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
            //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
            limpiarPantalla();
            cargarUsuarios();
        }else{
            utilidades.mostrarMensajePersonalizado(contexto, respuestaAgregarUsuario, "NOK");
            //Toast.makeText(contexto, respuestaAgregarUsuario, Toast.LENGTH_LONG).show();
            edtRut.requestFocus();
        }
    }

    private void limpiarPantalla(){
        edtRut.setText("");
        edtNombre.setText("");
        edtRut.requestFocus();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*public void grabarUsuario(View view){
        String respuesta = "";
        int intError=0;

        EditText txtRut = (EditText) findViewById(R.id.txtUserRut);
        EditText txtName = (EditText) findViewById(R.id.txtUserNombres);

        if (txtRut.getText().length()<6){
            intError = 1;
        }

        if (txtName.getText().length()<2){
            intError = 2;
        }

        if(intError==0){

            ContentValues registro = new ContentValues();
            registro.put("rut", txtRut.getText().toString());
            registro.put("usuario", txtName.getText().toString());

            MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(AdminUsuariosActivity.this, "Maestra", null, 1);
            //new MaestraSQLiteOpenHelp(this, "Maestra", null, 1);
            SQLiteDatabase bd2 = admin.getWritableDatabase();
            try{
                bd2.insert("usuario", null, registro);
            }catch (Exception ff){
                intError = 6;
            }finally {
                bd2.close();
            }

            respuesta="USUARIO AGREGADO";
            txtRut.setText("");
            txtName.setText("");
            txtRut.requestFocus();
        }else{
            respuesta="Usuario no grabado";
        }

        Toast.makeText(AdminUsuariosActivity.this, respuesta, Toast.LENGTH_SHORT).show();
        resfrescarLista();

        return;
    }

    public void resfrescarLista() {
        //list_usuarios
        MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(AdminUsuariosActivity.this, "Maestra", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery("select RUT, USUARIO from USUARIO ", null);

        ArrayList<String> listvalor= new ArrayList<String>();

        if (fila.getCount() > 0) {
            //int i =0;
            while (fila.moveToNext()) {
                String  valor = new String ( fila.getString(fila.getColumnIndex("RUT")) + " | " + fila.getString(fila.getColumnIndex("USUARIO")));
                listvalor.add(valor);
            }
        }else{
            String  valor2 = "Sin Usuarios";
            listvalor.add(valor2);
        }
        String [] listVentas = new String[listvalor.size()];
        for (int j=0;j<listvalor.size();j++){
            listVentas[j]=listvalor.get(j);
        }

        ListView listarVentas = (ListView) findViewById(R.id.lvUsuariosAU);
        //String[] listProd = getListaVentas();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listVentas);
        listarVentas.setAdapter(adapter);

    }

    public void cargaMasiva(View view){
        MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(AdminUsuariosActivity.this, "Maestra", null, 1);
        SQLiteDatabase bd2 = admin.getWritableDatabase();

        try {
            // Se obtiene el archivo
            bd2.delete("usuario", null, null);

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "usuarios.csv");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader in = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(in);
            String linea = "";
            String rut = "";
            String usuario = "";

            ContentValues registro = new ContentValues();

            while ((linea = br.readLine()) != null ) {
                String[] data = linea.split(";");
                rut = data[0];
                //usuario = data[2];
                usuario = data[1];

                usuario = usuario.replace("á", "a");
                usuario = usuario.replace("é", "e");
                usuario = usuario.replace("í", "i");
                usuario = usuario.replace("ó", "o");
                usuario = usuario.replace("ú", "u");
                usuario = usuario.replace("ñ", "n");

                usuario = usuario.replace("  ", " ");
                String remove = "\\t";
                usuario = usuario.replaceAll(remove, " ");

                registro.put("rut", rut);
                registro.put("usuario", usuario);

                bd2.insert("usuario", null, registro);

            }

        }catch (IOException io){
            Toast.makeText(AdminUsuariosActivity.this, "No logramos leer el archivo: " + io.getMessage(), Toast.LENGTH_SHORT).show();
            // io.printStackTrace();
        }catch (Exception ww){
            Toast.makeText(AdminUsuariosActivity.this, "No logramos del archivo: " + ww.getLocalizedMessage() + ww.getMessage() , Toast.LENGTH_SHORT).show();
        }finally {
            bd2.close();
        }
        resfrescarLista();
    }*/
}


