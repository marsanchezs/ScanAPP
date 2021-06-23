package cl.dpsoft.isamonhome;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import cl.dpsoft.isamonhome.Reportes.ReporteUsuarios;
import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamConstantes;
import cl.dpsoft.isamonhome.main.IsamDelegate;


public class AdminMenuActivity extends AppCompatActivity {

    private static final int REQUEST_DISPOSITIVO = 425;
    private static final String TAG_DEBUG = "tag_debug";
    private static final int MODE_PRINT_IMG = 0;

    // volatile: no guarda una copia en caché para cada hilo, si no que los sincroniza cuando cambie la variable
    // de esa manera todos manejarán el mismo valor de la variable y no una copia que puede estar con valor anterior
    private volatile boolean pararLectura;

    // Para la operaciones con dispositivos bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice dispositivoBluetooth;
    private BluetoothSocket bluetoothSocket;

    // identificador unico default
    private UUID aplicacionUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Para el flujo de datos de entrada y salida del socket bluetooth
    private OutputStream outputStream;
    private InputStream inputStream;

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private ConstraintLayout layout;
    private TextView txtTitulo, txtDispositivos;
    private String dirDisp = "", nombreDisp = "";
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();
    private static final String NOMBRE_BBDD = IsamConstantes.NOMBRE_BBDD;
    private static final int VERSION_BBDD = IsamConstantes.VERSION_BBDD;
    private ReporteUsuarios reporteUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        contexto = getApplicationContext();
        layout = (ConstraintLayout) findViewById(R.id.constraintLayoutAM);
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorAM);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);
        txtDispositivos = (TextView) findViewById(R.id.txtDispositivosAM);

        //openVentanaAdminLogin();
        //openVentanaAdminUsuarios();
        //realizaReinicioAPP();

        cargarBarraSuperior();
        volver();
    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.admMenu);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAdminLogin(null);
            }
        });
    }

    public void irAdminLogin(View vista) {
        Intent adminLogin = new Intent(contexto, AdminLoginActivity.class);
        startActivity(adminLogin);
    }

    public void irAdminUsuarios(View vista) {
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        boolean existeBBDD = admin.chequearBBDD();
        System.out.println("EXISTE BBDD MAESTRA CU: "+existeBBDD);
        if(existeBBDD){
            ArrayList<String> listaUsuarios = delegar.traerUsuarios(contexto);
            String primerElemento = listaUsuarios.get(0);
            int largoLista = listaUsuarios.size();
            System.out.println("LISTA USUARIOS: "+listaUsuarios);
            System.out.println("PRIMER ELEMENTO LISTA USUARIOS: "+primerElemento);
            System.out.println("LARGO LISTA USUARIOS: "+largoLista);
            if(primerElemento.equals("SIN USUARIOS")){
                utilidades.mostrarMensajePersonalizado(contexto, "SIN USUARIOS", "NOK");
                //Toast.makeText(contexto, "SIN USUARIOS", Toast.LENGTH_LONG).show();
            }else{
                Intent adminUsuarios = new Intent(contexto, AdminUsuariosActivity.class);
                startActivity(adminUsuarios);
            }
        }else{
            String mensaje = "NO EXISTE BASE DE DATOS\nDEBE ACTIVARLA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }
    }

    public void irAdminServerWS(View vista) {
        Intent adminServerWS = new Intent(contexto, AdminServerWSActivity.class);
        startActivity(adminServerWS);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void irAdminEliminarArea(View vista) {
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        boolean existeBBDD = admin.chequearBBDD();
        System.out.println("EXISTE BBDD MAESTRA EA: "+existeBBDD);
        if(existeBBDD){
            ArrayList<String> listaAreas = delegar.traerAreas(contexto);
            String primerElemento = listaAreas.get(0);
            System.out.println("LISTA ÁREAS: "+listaAreas);
            System.out.println("PRIMER ELEMENTO LISTA ÁREAS: "+primerElemento);
            if(primerElemento.equals("SIN ÁREAS")){
                String mensaje = "SIN ÁREAS PARA ELIMINAR";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            }else{
                Intent adminEliminarArea = new Intent(contexto, AdminEliminarAreaActivity.class);
                startActivity(adminEliminarArea);
            }
        }else{
            String mensaje = "NO EXISTE BASE DE DATOS\nDEBE ACTIVARLA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }

    }

    public void reiniciarAplicacion(View vista) {
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        final String mensaje = "¿ESTÁ SEGURO DE REINICIAR LA APP? SE BORRARÁ TODO.";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                String respustaEliminarCapturaYUsuario = delegar.eliminarCapturaYUsuario(contexto);
                eliminaArchivos();
                String mensaje = "";
                if(respustaEliminarCapturaYUsuario.equals("OK")){
                    mensaje = "APLICACIÓN REINICIADA";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                }else{
                    mensaje = "NO SE HA REINICIADO LA APLICACIÓN";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                }
                dialogoPrecaucion.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPrecaucion.dismiss();
            }
        });
        dialogoPrecaucion.show();

        /*final AlertDialog alertDialog;
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_confirmar,null);
        dialogo1.setView(dialogView);
        alertDialog = dialogo1.create();
        TextView txvMensaje = (TextView) dialogView.findViewById(R.id.mensaje);
        Button btnConfirmar = (Button) dialogView.findViewById(R.id.btnConfirmar);
        String mensaje = "¿ESTÁ SEGURO DE REINICIAR LA APP? SE BORRARÁ TODO.";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(getApplicationContext(), "Maestra", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();

                try {
                    int i;
                    i = bd.delete("usuario", null, null);
                    //i = bd.delete("captura", null, null);

                } catch (SQLException e) {
                    Log.e("", "", e);
                } finally {
                    bd.close();
                }

                try {
                    bd = admin.getWritableDatabase();

                    int i;
                    //i = bd.delete("usuario", null,null);
                    i = bd.delete("captura", null, null);

                } catch (SQLException e) {
                    Log.e("", "", e);
                } finally {
                    bd.close();
                }
                eliminaArchivos();
                Toast.makeText(AdminMenuActivity.this, "Aplicacion reiniciada con Exito!!", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();*/
    }

    private void eliminaArchivos() {
        try {
            String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/captura";
            File dirCaptura = new File(directorio);

            String maestra = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/maestra";
            File dirMaestra = new File(maestra);

            String respaldo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/respaldo";
            File dirRespaldo = new File(respaldo);

            if (!dirCaptura.mkdirs()) {
                for (String aChildren : dirCaptura.list()) {
                    new File(dirCaptura, aChildren).delete();
                }
            }
            if (!dirMaestra.mkdirs()) {
                for (String aChildren : dirMaestra.list()) {
                    new File(dirMaestra, aChildren).delete();
                }
            }
            if (!dirRespaldo.mkdirs()) {
                for (String aChildren : dirRespaldo.list()) {
                    new File(dirRespaldo, aChildren).delete();
                }
            }
        } catch (Exception e) {
            Log.e("", "Eliminar Archivos de carpeta captura", e);
        }

    }

    public void solicitarPermisos(View vista) {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!");
            String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/maestra";
            String nombreArchivo = "Maestra";
            String rutaCompleta = ruta + File.separator + nombreArchivo;
            System.out.println("RUTA COMPLETA MAESTRA: "+rutaCompleta);
            File archivo = new File(rutaCompleta);
            if(!archivo.exists()){
                String mensaje = "DEBE AGREGAR EL ARCHIVO Maestra";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                //Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
            }else{
                copiarBBDD();
            }
        }
    }

    private void copiarBBDD() {
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        final String mensaje = "¿ESTÁ SEGURO DE ACTIVAR LA BASE DE DATOS?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                Thread hiloActivarBBDD = new Thread() {
                    @Override
                    public void run() {
                        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
                        String respuesta = "";
                        try {
                            admin.crearBBDD();
                            final boolean resp = admin.chequearBBDD();
                            System.out.println("BBDD CHECK: "+resp);
                            admin.abrirBBDD();
                            respuesta = "OK";

                            /*runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(contexto, "Base Activada", Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                        } catch (Exception e) {
                            e.printStackTrace();
                            respuesta = "ERROR "+e.getMessage().toUpperCase();
                            final String msg = e.getMessage();
                            /*runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(contexto, "problem " + msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                        }
                        final String msj = respuesta;
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        String mensaje = "";
                                        if(msj.equals("OK")){
                                            mensaje = "BASE ACTIVADA";
                                            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                                        }else{
                                            utilidades.mostrarMensajePersonalizado(contexto, msj, "NOK");
                                        }
                                        //Toast.makeText(contexto, "problem " + msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                };
                hiloActivarBBDD.start();
                dialogoPrecaucion.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPrecaucion.dismiss();
            }
        });
        dialogoPrecaucion.show();

        /*final AlertDialog alertDialog;
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_confirmar,null);
        dialogo1.setView(dialogView);
        alertDialog = dialogo1.create();
        TextView txvMensaje = (TextView) dialogView.findViewById(R.id.mensaje);
        Button btnConfirmar = (Button) dialogView.findViewById(R.id.btnConfirmar);
        String mensaje = "¿ESTÁ SEGURO DE ACTIVAR LA BASE DE DATOS?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread tr = new Thread() {
                    @Override
                    public void run() {
                        //MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(getApplicationContext(), "Maestra", null, 1);
                        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, "Maestra", null, 1);
                        try {
                            admin.crearBBDD();
                            final boolean resp = admin.chequearBBDD();
                            System.out.println("BBDD CHECK: "+resp);
                            admin.abrirBBDD();

                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(contexto, "Base Activada", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                            final String msg = e.getMessage();
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(contexto, "problem " + msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                };
                tr.start();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();*/
    }

    /*public void realizaReinicioAPP_old() {
        Button button2 = (Button) findViewById(R.id.btnCrearUsuario);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método del handler de button2
                MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(getApplicationContext(), "Maestra", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();

                try {
                    int i;
                    i = bd.delete("usuario", null, null);
                    i = bd.delete("captura", null, null);

                } catch (SQLException e) {
                    Log.e("", "", e);
                } finally {
                    bd.close();
                }
                //usuario
                //CAPTURA
            }
        });

    }*/

    ///data/data/cl.dpsoft.pruebainventario03/databases/Maestra

    public void backupdDatabase(View vista) throws IOException {
        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();
            String packageName = "cl.dpsoft.isamonhome";
            String sourceDBName = "Maestra";
            String targetDBName = "mmaestra";
            if (sd.canWrite()) {
                Date now = new Date();
                String currentDBPath = "data/" + packageName + "/databases/" + sourceDBName;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String backupDBPath = targetDBName + dateFormat.format(now) + ".db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.i("backup", "backupDB=" + backupDB.getAbsolutePath());
                Log.i("backup", "sourceDB=" + currentDB.getAbsolutePath());

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                String mensaje = "BASE DE DATOS DE RESPALDO CREADA";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                //Toast.makeText(getApplicationContext(), "Base respaldo creada: " + backupDBPath, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i("Backup", e.toString());
        }
        String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/captura";
        File carpetaRespaldo = new File(directorio);
        //generarTxtRespaldo(carpetaRespaldo);
        generarRespaldo();
    }

    public void generarRespaldo(){
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        final String mensaje = "¿ESTÁ SEGURO DE GENERAR EL ARCHIVO Respaldo.txt?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                File archivo = crearRespaldoTXT();
                StringBuilder contenido = delegar.escribirRespaldoTXT(contexto);
                String respuestaValidarRespaldoTXT = validarRespaldoTXT(archivo, contenido);
                String mensaje = "";
                if(respuestaValidarRespaldoTXT.equals("OK")){
                    mensaje = "EL ARCHIVO Respaldo.txt HA SIDO CREADO";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                }else{
                    mensaje = "ERROR: "+respuestaValidarRespaldoTXT;
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                }
                dialogoPrecaucion.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPrecaucion.dismiss();
            }
        });
        dialogoPrecaucion.show();

        /*final AlertDialog alertDialog;
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_confirmar,null);
        dialogo1.setView(dialogView);
        alertDialog = dialogo1.create();
        TextView txvMensaje = (TextView) dialogView.findViewById(R.id.mensaje);
        Button btnConfirmar = (Button) dialogView.findViewById(R.id.btnConfirmar);
        String mensaje = "¿ESTÁ SEGURO DE GENERAR EL ARCHIVO Respaldo.txt?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/respaldo";
                File path = new File(directorio);
                File file = new File(path, "Respaldo.txt");
                String resultado;

                MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(AdminMenuActivity.this, "Maestra", null, 1);
                SQLiteDatabase bd = admin.getWritableDatabase();

                StringBuffer contenido = new StringBuffer();

                try{
                    Cursor fila = bd.rawQuery("SELECT PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION FROM CAPTURA ORDER BY PATENTE DESC", null);

                    if (fila.getCount()>0) {
                        while (fila.moveToNext()){
                            contenido.append(fila.getString(fila.getColumnIndex("PATENTE")));
                            contenido.append("|");
                            contenido.append(fila.getString(fila.getColumnIndex("EAN")));
                            contenido.append("|");
                            contenido.append(fila.getString(fila.getColumnIndex("CANTIDAD")));
                            contenido.append("|");
                            String sfecha = fila.getString(fila.getColumnIndex("FECHA"));
                            String[] fecha = sfecha.split(" ");
                            //itemselected.put("fecha"    , fecha[1]);
                            //contenido.append(fila.getString(fila.getColumnIndex("FECHA")));
                            contenido.append(fecha[1]);
                            contenido.append("|");
                            contenido.append(fila.getString(fila.getColumnIndex("RUT")));
                            contenido.append("|");
                            contenido.append(fila.getString(fila.getColumnIndex("UBICACION")));
                            contenido.append("\r\n");
                        }
                    }

                }catch (Exception ff){
                    resultado = "problemas al completar el contenido";
                    //response = "";
                }finally {
                    bd.close();
                }

                if (file.exists()) {
                    try{
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(contenido.toString().getBytes());
                        out.close();
                        Toast.makeText(getBaseContext(), "Respaldo Creado", Toast.LENGTH_SHORT).show();

                    }catch (IOException e){
                        resultado = "problemas al crear y escribir el archivo, revisar permisos";
                        //response = "";
                    }
                }else{
                    try{
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(contenido.toString().getBytes());
                        out.close();
                        Toast.makeText(getBaseContext(), "Respaldo Creado", Toast.LENGTH_SHORT).show();

                    }catch (IOException e){
                        resultado = "problemas al crear y escribir el archivo, revisar permisos";
                        //response = "";
                    }
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();*/
    }

    private File crearRespaldoTXT(){
        String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/respaldo";
        File ruta = new File(directorio);
        return new File(ruta, "Respaldo.txt");
    }

    private String validarRespaldoTXT(File archivo, StringBuilder contenido){
        String respuesta = "";
        if (archivo.exists()) {
            try{
                FileOutputStream out = new FileOutputStream(archivo);
                out.write(contenido.toString().getBytes());
                out.close();
                respuesta = "OK";
                //Toast.makeText(getBaseContext(), "Respaldo Creado", Toast.LENGTH_SHORT).show();

            }catch (IOException e){
                respuesta = e.getMessage().toUpperCase();
                //resultado = "problemas al crear y escribir el archivo, revisar permisos";
                //response = "";
            }
        }else{
            try{
                FileOutputStream out = new FileOutputStream(archivo);
                out.write(contenido.toString().getBytes());
                out.close();
                respuesta = "OK";
                //Toast.makeText(getBaseContext(), "Respaldo Creado", Toast.LENGTH_SHORT).show();

            }catch (IOException e){
                respuesta = e.getMessage().toUpperCase();
                //resultado = "problemas al crear y escribir el archivo, revisar permisos";
                //response = "";
            }
        }
        return respuesta;
    }

    public void generarTxtRespaldo(File dir) throws IOException {
        String direct = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/captura";
        File capturadora = new File(direct);
        String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/respaldo";
        File carpeta = new File(directorio);
        File file = new File(carpeta, "Respaldo.txt");
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
        for (final File ficheroEntrada : capturadora.listFiles()) {
            if (ficheroEntrada.isDirectory()) {
                generarTxtRespaldo(ficheroEntrada);
            } else {
                System.out.println("PPPPPPPPPPPPPPP " + ficheroEntrada.getName());
                try {
                    String cadena;
                    FileReader f = new FileReader(ficheroEntrada);
                    BufferedReader b = new BufferedReader(f);
                    while ((cadena = b.readLine()) != null) {
                        System.out.println("WWWWWWWWWWWW " + cadena);
                        // Escribimos el String en el archivo
                        osw.write(cadena);
                        osw.flush();
                        osw.append("\r\n");

                        // Mostramos que se ha guardado
                        Toast.makeText(getBaseContext(), "Guardado123", Toast.LENGTH_SHORT).show();
                    }
                    b.close();
                } catch (Exception ex) {
                    Log.e("Ficheros", "Error al leer fichero desde memoria interna");
                }
            }
        }
        osw.close();
    }

    //IMPRESIÓN BLUETOOTH
    public void buscarDispositivos(View vista) {
        // Cerrar conexión antes de establecer otra
        cerrarConexion();

        Intent intentLista = new Intent(this, ListaDispositivosBluetooth.class);
        startActivityForResult(intentLista, REQUEST_DISPOSITIVO);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_DISPOSITIVO) {
                String mensaje = "CARGANDO... ";
                txtDispositivos.setText(mensaje);

                final String direccionDispositivo = data.getExtras().getString("DireccionDispositivo");
                final String nombreDispositivo = data.getExtras().getString("NombreDispositivo");
                System.out.println("DIRECCIÓN DISPOSITIVO: "+direccionDispositivo);
                System.out.println("NOMBRE DISPOSITIVO: "+nombreDispositivo);

                dirDisp = direccionDispositivo;
                nombreDisp = nombreDispositivo;

                txtDispositivos.setText(nombreDispositivo);

                // Obtenemos el dispositivo con la direccion seleccionada en la lista
                dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(direccionDispositivo);
                System.out.println("DISPOSITIVO BLUETOOTH: "+dispositivoBluetooth.getAddress());

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Conectamos los dispositivos

                            // Creamos un socket
                            bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(aplicacionUUID);
                            //bluetoothSocket = dispositivoBluetooth.createInsecureRfcommSocketToServiceRecord(aplicacionUUID);
                            bluetoothSocket.connect();// conectamos el socket
                            outputStream = bluetoothSocket.getOutputStream();
                            inputStream = bluetoothSocket.getInputStream();

                            //empezarEscucharDatos();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String msg = nombreDispositivo+" CONECTADO";
                                    txtDispositivos.setText(msg);
                                    utilidades.mostrarMensajePersonalizado(contexto, msg, "OK");
                                    //Toast.makeText(AdminMenuActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String msg2 = "NO SE PUDO CONECTAR EL DISPOSITIVO";
                                    txtDispositivos.setText(msg2);
                                    utilidades.mostrarMensajePersonalizado(contexto, msg2, "NOK");
                                    //Toast.makeText(AdminMenuActivity.this, msg2, Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth");
                            e.printStackTrace();
                        }
                    }
                }).start();*/
            }
        }
    }

    private void cerrarConexion() {
        try {
            if (bluetoothSocket != null) {
                if (outputStream != null) outputStream.close();
                pararLectura = true;
                if (inputStream != null) inputStream.close();
                bluetoothSocket.close();
                String mensaje = "CONEXIÓN FINALIZADA";
                txtDispositivos.setText(mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generarReportes(View vista) {
        final AlertDialog dialogoReportes;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(AdminMenuActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_reportes,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoReportes = constructorDialogo.create();
        final Spinner spReportes = (Spinner) vistaDialogo.findViewById(R.id.spReportesDR);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDR);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDR);
        cargarSpinner(spReportes);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                String tipoReporte = spReportes.getSelectedItem().toString();
                //utilidades.mostrarMensajePersonalizado(contexto, "TIPO REPORTE: "+tipoReporte, "OK");
                validarReporte(tipoReporte);

                //dialogoReportes.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                dialogoReportes.dismiss();
            }
        });
        dialogoReportes.show();
    }

    private void validarReporte(String tipoReporte){
        String mensaje = "";
        switch(tipoReporte){
            case "SELECCIONAR...":
                mensaje = "DEBE SELECCIONAR UN REPORTE";
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                break;
            case "REPORTE ÁREAS":
                //utilidades.mostrarMensajePersonalizado(contexto, "REPORTE ÁREAS", "OK");
                generarReporte("REPORTE ÁREAS");
                break;
            case "REPORTE CAPTURAS":
                //utilidades.mostrarMensajePersonalizado(contexto, "REPORTE CAPTURAS", "OK");
                generarReporte("REPORTE CAPTURAS");
                break;
            case "REPORTE USUARIOS":
                //utilidades.mostrarMensajePersonalizado(contexto, "REPORTE USUARIOS", "OK");
                generarReporte("REPORTE USUARIOS");
                break;
        }
    }

    private void generarReporte(String tipoReporte) {

        switch (tipoReporte) {
            case "REPORTE ÁREAS":
                utilidades.mostrarMensajePersonalizado(contexto, "REPORTE ÁREAS GR", "OK");
                //cargarReporteGeneral();
                //reporteGeneral.verPDF();
                break;
            case "REPORTE CAPTURAS":
                utilidades.mostrarMensajePersonalizado(contexto, "REPORTE CAPTURAS GR", "OK");
                //cargarReporteAutor();
                //reporteAutor.verPDF();
                break;
            case "REPORTE USUARIOS":
                cargarReporteUsuarios();
                //reporteUsuarios.verPDF();
                break;

        }
    }

    private void cargarReporteUsuarios(){
        reporteUsuarios = new ReporteUsuarios(contexto);
        reporteUsuarios.abrirDocumentoPDF();
        reporteUsuarios.cerrarDocumento();
        Snackbar mensaje = Snackbar.make(layout, "REPORTE USUARIOS GUARDADO", Snackbar.LENGTH_LONG);
        mensaje.show();
    }

    private void cargarSpinner(Spinner spReportes){
        String[] tipoReporte = new String[]{"SELECCIONAR...", "REPORTE ÁREAS", "REPORTE CAPTURAS", "REPORTE USUARIOS"};
        ArrayAdapter<String> adaptador;
        adaptador = new ArrayAdapter<String>(contexto, R.layout.lista_desplegable, R.id.txt, tipoReporte);
        spReportes.setAdapter(adaptador);
    }
}
