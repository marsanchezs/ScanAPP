package cl.dpsoft.isamonhome;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import cl.dpsoft.isamonhome.vo.Producto;

public class UserIngresaCapturaActivity extends AppCompatActivity {

    private String txtArea;
    private String txtUbicacion;
    private String forzarIngreso = "0";
    private String codname;

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private String codUsuario, area, ubicacion, fecha;
    private int codArea;
    private TextView txtTitulo, txtDescripcion, txtSku;
    private EditText edtCodigo, edtCantidad, edtCantidadUnoxUno;
    private RadioButton rbCantidad, rbUnoAUno;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ingresa_captura);

        /*codUsuario = (String) Objects.requireNonNull(getIntent().getExtras()).get ("codUsuario");
        area = (String) getIntent().getExtras().get ("area");
        assert area != null;
        codArea = Integer.parseInt(area);
        ubicacion = (String) getIntent().getExtras().get ("ubicacion");
        String datosRecibidos = "USUARIO: "+codUsuario+" ÁREA: "+area+" UBICACIÓN: "+ubicacion;
        System.out.println(datosRecibidos);
        fecha = utilidades.traerFecha();

        contexto = getApplicationContext();
        barraSuperior = (RelativeLayout) findViewById(R.id.barraSuperiorIC);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);
        rbCantidad = (RadioButton) findViewById(R.id.rbCantidadUIC);
        rbUnoAUno = (RadioButton) findViewById(R.id.rbUnoxUnoUIC);
        edtCodigo = (EditText) findViewById(R.id.edtCodigoUIC);
        txtDescripcion = (TextView) findViewById(R.id.txtDetalleDescripcionUIC);
        txtSku = (TextView) findViewById(R.id.txtDetalleSkuUIC);
        edtCantidad = (EditText) findViewById(R.id.edtCantidadUIC);
        edtCantidadUnoxUno = (EditText) findViewById(R.id.edtUnoxUnoUIC);

        edtCodigo.requestFocus();
        edtCantidadUnoxUno.setVisibility(View.GONE);
        ocultarTeclado(edtCodigo);
        cambiarEditText();*/
        /*scanCodigoxCantidad();
        scanCodigoUnoxUno();
        traerProducto();*/
        //cargarBarraSuperior();
        //volver();
    }

    //MÉTODOS
    private void cambiarEditText(){
        edtCodigo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_NEXT
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    String tipoScan = "";
                    if(rbCantidad.isChecked()){
                        tipoScan = "CANTIDAD";
                    }else if(rbUnoAUno.isChecked()){
                        tipoScan = "1 A 1";
                    }
                    traerProducto(tipoScan);
                    return true;
                }
                return false;
            }
        });
    }

    private void traerProducto(final String tipoScan){

        txtSku.setText("AAAAAAAAAAAAAAA");
        txtDescripcion.setText(getString(R.string.descripcion));

        if(tipoScan.equals("CANTIDAD")){
            edtCantidad.setVisibility(View.VISIBLE);
            edtCantidadUnoxUno.setVisibility(View.GONE);
        }else{
            edtCantidad.setVisibility(View.GONE);
            edtCantidadUnoxUno.setVisibility(View.VISIBLE);
        }

        /*final Producto producto = new Producto();
        Thread hiloTraerProducto = new Thread() {
            @Override
            public void run() {

                if(tipoScan.equals("CANTIDAD")){
                    edtCantidad.setVisibility(View.VISIBLE);
                    edtCantidadUnoxUno.setVisibility(View.GONE);
                }else{
                    edtCantidad.setVisibility(View.GONE);
                    edtCantidadUnoxUno.setVisibility(View.VISIBLE);
                }

                String respuesta = "";
                try{
                    Producto prod = delegar.getProductoScan(contexto, codigo);
                    System.out.println("CÓDIGO: "+prod.getCodigo()+" SKU: "+prod.getSku()+
                            " PATENTE: "+prod.getPatente()+" ID: "+prod.getIdprod());
                    if (prod.getEncontrado() == 1){
                        producto.setCodigo(prod.getIdprod());
                        producto.setDescripcion(prod.getDescripcion());
                        producto.setSku(prod.getSku());
                        producto.setCantidad(prod.getCantidad());  //valor en null o blanco
                        producto.setPatente(prod.getPatente());    //valor en null o blanco
                        respuesta = "EXISTE";
                    }else{
                        respuesta = "NO EXISTE";
                    }
                }catch (Exception e){
                    String mensaje = "EL ERROR ES EL SIGUIENTE: "+e.getMessage();
                    Log.e("EXCEPCIÓN", mensaje, e);
                }
                final String resp = respuesta;
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                cargarProducto(producto, codigo);
                                if(resp.equals("NO EXISTE")){
                                    String mensaje = "PRODUCTO NO ENCONTRADO";
                                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                                }
                            }
                        });
            }
        };
        hiloTraerProducto.start();*/
    }

    /*private void cargarProducto(Producto producto, String codigo){
        edtCodigo.setText(codigo.toUpperCase());
        txtDescripcion.setText(producto.getDescripcion());
        txtSku.setText(producto.getSku());
    }*/

    public void guardarCaptura(View vista){
        String datos = "";
        if(rbCantidad.isChecked()){
            datos = "RB CANTIDAD";
            utilidades.mostrarMensajePersonalizado(contexto, datos, "NOK");
            //validarCapturaxCantidad();
        }else if(rbUnoAUno.isChecked()){
            datos = "RB UNO xUNO";
            utilidades.mostrarMensajePersonalizado(contexto, datos, "NOK");
            //validarCapturaUnoxUno();
        }
        limpiarPantalla();
    }

    private void ocultarTeclado(EditText edt){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    public void mostrarOcultar(View vista){
        if(rbCantidad.isChecked()){
            edtCantidad.setVisibility(View.VISIBLE);
            edtCantidadUnoxUno.setVisibility(View.GONE);
        }else if(rbUnoAUno.isChecked()){
            edtCantidad.setVisibility(View.GONE);
            edtCantidadUnoxUno.setVisibility(View.VISIBLE);
        }
    }

    /*@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void guardarCaptura(View vista){
        if(rbCantidad.isChecked()){
            //utilidades.mostrarMensajePersonalizado(contexto, "RB CANTIDAD", "NOK");
            validarCapturaxCantidad();
            limpiarPantalla();
        }else if(rbUnoAUno.isChecked()){
            //utilidades.mostrarMensajePersonalizado(contexto, "RB UNO x UNO", "NOK");
            validarCapturaUnoxUno();
            limpiarPantalla();
        }
        limpiarPantalla();
    }

    private void scanCodigoxCantidad(){
        edtCantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    guardarCaptura(null);
                    //validarCapturaxCantidad();
                    //limpiarPantalla();
                    //edtCodigo.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private String existeProducto(String codigo){
        String respuesta = "";
        Producto producto = delegar.getProductoScan(contexto, codigo);
        if(producto.getEncontrado() == 1){
            respuesta = "EXISTE";
        }else{
            respuesta = "NO EXISTE";
        }
        System.out.println(codigo.toUpperCase()+" "+respuesta);
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void validarCapturaxCantidad(){
        String codigo = edtCodigo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String sku = txtSku.getText().toString();
        String cantidad = edtCantidad.getText().toString();
        String mensaje = "";

        if(codigo.isEmpty() && cantidad.isEmpty()){
            mensaje = "INGRESAR CÓDIGO Y CANTIDAD";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }else if(codigo.isEmpty()) {
            mensaje = "INGRESAR CÓDIGO";
            edtCodigo.setError(mensaje);
        }else if(!cantidad.isEmpty()){

            String respuestaExisteProducto = existeProducto(codigo);
            if(respuestaExisteProducto.equals("NO EXISTE")){
                grabarCodigoDesconocido(codigo, cantidad);
                //mensaje = "ALERT DIALOG";
                //utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
            }else if(respuestaExisteProducto.equals("EXISTE")){
                grabarxCantidad(codigo, descripcion, sku, cantidad);
                limpiarPantalla();
            }
        }else{
            mensaje = "INGRESAR CANTIDAD";
            edtCantidad.setError(mensaje);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void grabarxCantidad(String codigo, String descripcion, String sku, String cantidad){
        //String datos = "CÓDIGO: "+codigo+" DESCRIPCIÓN: "+descripcion+"\nSKU: "+sku+" CANTIDAD: "+cantidad;
        //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        ContentValues cvCaptura = new ContentValues();
        cvCaptura.put("IDPROD", codigo);//CODIGO PRODUCTO
        cvCaptura.put("PATENTE", codArea);//AREA
        cvCaptura.put("EAN", (txtSku.getText().length()<2?edtCodigo.getText().toString():txtSku.getText().toString()));
        cvCaptura.put("CANTIDAD", cantidad);
        cvCaptura.put("FECHA", fecha);
        cvCaptura.put("RUT", codUsuario);
        cvCaptura.put("ESTADO", "1");
        cvCaptura.put("DESCRIPCION", descripcion);
        cvCaptura.put("UBICACION", ubicacion);
        cvCaptura.put("ESTADO1", "1");
        String respuestaGrabarCaptura = delegar.guardarCaptura(contexto, cvCaptura);
        String mensaje = "";
        if(respuestaGrabarCaptura.equals("OK")){
            mensaje = "CAPTURA GUARDADA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
        }else{
            mensaje = "ERROR. NO SE HA GUARDADO LA CAPTURA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }
    }

    private void scanCodigoUnoxUno(){
        edtCantidadUnoxUno.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    guardarCaptura(null);
                    //validarCapturaUnoxUno();
                    //limpiarPantalla();
                    return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void validarCapturaUnoxUno(){
        String codigo = edtCodigo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String sku = txtSku.getText().toString();
        String cantidad = edtCantidadUnoxUno.getText().toString();
        String mensaje = "";

        if(codigo.isEmpty()){
            mensaje = "INGRESAR CÓDIGO";
            edtCodigo.setError(mensaje);
        }else{

            String respuestaExisteProducto = existeProducto(codigo);
            if(respuestaExisteProducto.equals("NO EXISTE")){
                //mensaje = "ALERT DIALOG";
                //utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                grabarCodigoDesconocido(codigo, "1");
            }else if(respuestaExisteProducto.equals("EXISTE")){
                grabarUnoxUno(codigo, descripcion, sku);
                limpiarPantalla();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void grabarUnoxUno(String codigo, String descripcion, String sku){
        //String datos = "CÓDIGO: "+codigo+" DESCRIPCIÓN: "+descripcion+"\nSKU: "+sku+" CANTIDAD: 1";
        //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        ContentValues cvCaptura = new ContentValues();
        cvCaptura.put("IDPROD", codigo);//CODIGO PRODUCTO
        cvCaptura.put("PATENTE", codArea);//AREA
        cvCaptura.put("EAN", (txtSku.getText().length()<2?edtCodigo.getText().toString():txtSku.getText().toString()));
        cvCaptura.put("CANTIDAD", "1");
        cvCaptura.put("FECHA", fecha);
        cvCaptura.put("RUT", codUsuario);
        cvCaptura.put("ESTADO", "1");
        cvCaptura.put("DESCRIPCION", descripcion);
        cvCaptura.put("UBICACION", ubicacion);
        cvCaptura.put("ESTADO1", "1");
        String respuestaGrabarCaptura = delegar.guardarCaptura(contexto, cvCaptura);
        String mensaje = "";
        if(respuestaGrabarCaptura.equals("OK")){
            mensaje = "CAPTURA GUARDADA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
        }else{
            mensaje = "ERROR. NO SE HA GUARDADO LA CAPTURA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }
    }

    public void grabarCodigoDesconocido(final String codigo, final String cantidad){
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserIngresaCapturaActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        final String mensaje = "EL CÓDIGO "+codigo.toUpperCase()+" NO EXISTE.\n¿REALIZAR LA CAPTURA?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                grabarxCantidad(codigo, "", "", cantidad);
                limpiarPantalla();
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
    }*/

    private void limpiarPantalla(){
        edtCodigo.setText("");
        txtDescripcion.setText("");
        txtSku.setText("");
        edtCantidad.setText("");
        edtCodigo.requestFocus();
    }

    private void cargarBarraSuperior(){
        String titulo = getString(R.string.ingresarCaptura);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                irIngresarArea(null);
            }
        });
    }

    public void irIngresarArea(View vista){
        Intent ingresarArea = new Intent(contexto, UserIngresaAreaActivity.class);
        ingresarArea.putExtra("codUsuario", codUsuario);
        startActivity(ingresarArea);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void irUserRevisar(View vista){
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        String primerElemento = listaCapturas.get(0);
        int largoLista = listaCapturas.size();
        System.out.println("LISTA CAPTURAS: "+listaCapturas);
        System.out.println("PRIMER ELEMENTO LISTA CAPTURAS: "+primerElemento);
        System.out.println("LARGO LISTA CAPTURAS: "+largoLista);
        if(primerElemento.equals("SIN CAPTURAS")){
            String mensaje = "SIN CAPTURAS PARA REVISAR";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }else{
            Intent userRevisar = new Intent(contexto, UserRevisarActivity.class);
            userRevisar.putExtra("codUsuario", codUsuario);
            userRevisar.putExtra("area", area);
            userRevisar.putExtra("ubicacion", ubicacion);
            startActivity(userRevisar);
        }
    }

    public void cerrarArea(final View vista){
        Thread hiloCerrarArea = new Thread() {
            @Override
            public void run() {
                String resultado = "";
                try{
                    File archivo = generarArchivoTXTArea(area);
                    StringBuilder contenido = delegar.escribirContenidoArchivoTXTArea(contexto, area);
                    int unidades = delegar.traerUnidades(contexto, area);
                    String respuestaEscribirArchivoTXT = escribirArchivoTXTArea(archivo, contenido);
                    if(!respuestaEscribirArchivoTXT.equals("OK")){
                        resultado = "ERROR AL CREAR Y/O ESCRIBIR\nEL ARCHIVO: "+area+".txt";
                    }
                    resultado = "CIERRE EXITOSO\nTOTAL UNIDADES: "+unidades;
                }catch (Exception e){
                    String mensaje = "EL ERROR ES EL SIGUIENTE: "+e.getMessage();
                    Log.e("EXCEPCIÓN", mensaje, e);
                }
                final String respuesta = resultado;
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                if(respuesta.equals("ERROR AL CREAR Y/O ESCRIBIR EL ARCHIVO: "+area+".txt")){
                                    utilidades.mostrarMensajePersonalizado(contexto, respuesta, "NOK");
                                }else{
                                    utilidades.mostrarMensajePersonalizado(contexto, respuesta, "OK");
                                    irIngresarArea(vista);
                                }
                            }
                        });
            }
        };
        hiloCerrarArea.start();
    }

    private File generarArchivoTXTArea(String area){
        String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/captura";
        File ruta = new File(directorio);
        String nombreArchivo = area + ".txt";
        return new File(ruta, nombreArchivo);
    }

    private String escribirArchivoTXTArea(File archivo, StringBuilder contenido){
        String respuesta = "";
        if (!archivo.exists()) {
            try{
                FileOutputStream out = new FileOutputStream(archivo);
                out.write(contenido.toString().getBytes());
                out.close();
                respuesta = "OK";
            }catch (IOException e){
                respuesta = "REVISAR PERMISOS";
            }
        }
        return respuesta;
    }

    /*private void traerProducto(){
        try{
            edtCodigo.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return i == KeyEvent.KEYCODE_ENTER;
                }
            });
            edtCodigo.setOnFocusChangeListener(this);
        }catch(Exception e){
            String mensaje = "ERROR: "+e.getMessage();
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // todo your code here...
        if (!hasFocus){
            String codigo = edtCodigo.getText().toString();
            buscarProducto2(codigo.toUpperCase());
        }
    }

    private void buscarProducto2(final String codigo){
        final Producto producto = new Producto();
        Thread hiloTraerProducto = new Thread() {
            @Override
            public void run() {
                String respuesta = "";
                try{
                    Producto prod = delegar.getProductoScan(contexto, codigo);
                    System.out.println("CÓDIGO: "+prod.getCodigo()+" SKU: "+prod.getSku()+
                            " PATENTE: "+prod.getPatente()+" ID: "+prod.getIdprod());
                    if (prod.getEncontrado() == 1){
                        producto.setCodigo(prod.getIdprod());
                        producto.setDescripcion(prod.getDescripcion());
                        producto.setSku(prod.getSku());
                        producto.setCantidad(prod.getCantidad());  //valor en null o blanco
                        producto.setPatente(prod.getPatente());    //valor en null o blanco
                        respuesta = "EXISTE";
                    }else{
                        respuesta = "NO EXISTE";
                    }
                }catch (Exception e){
                    String mensaje = "EL ERROR ES EL SIGUIENTE: "+e.getMessage();
                    Log.e("EXCEPCIÓN", mensaje, e);
                }
                final String resp = respuesta;
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                cargarProducto(producto, codigo);
                                if(resp.equals("NO EXISTE")){
                                    String mensaje = "PRODUCTO NO ENCONTRADO";
                                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                                }
                            }
                        });
            }
        };
        hiloTraerProducto.start();
    }*/

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    public Producto buscarProducto(final String codigo){
        final Producto producto = new Producto();

        Thread tr = new Thread() {
            @Override
            public void run() {
                IsamDelegate delegate = new IsamDelegate();
                Producto prod = delegar.getProductoScan(contexto, codigo);
                System.out.println("CÓDIGO: "+prod.getCodigo()+" SKU: "+prod.getSku()+
                        " PATENTE: "+prod.getPatente()+" ID: "+prod.getIdprod());
                if (prod.getEncontrado() == 1){
                    producto.setCodigo(prod.getIdprod());
                    producto.setDescripcion(prod.getDescripcion());
                    producto.setSku(prod.getSku());
                    producto.setCantidad(prod.getCantidad());  //valor en null o blanco
                    producto.setPatente(prod.getPatente());    //valor en null o blanco

                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    cargarProducto(producto, codigo);
                                }
                            });
                }else{
                    producto.setCodigo("");
                    producto.setDescripcion("");
                    producto.setSku("");

                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    cargarProducto(producto, "");
                                    edtCodigo.requestFocus();
                                    String mensaje = "PRODUCTO NO ENCONTRADO";
                                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                                    //Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        };
        tr.start();
        return producto;
    }

    private void traerProducto(){
        try{
            edtCodigo.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return i == KeyEvent.KEYCODE_ENTER;
                }
            });
            //String codigo = edtCodigo.getText().toString();
            //buscarProducto(codigo.toUpperCase());
            //scanCodigo(null);
            edtCodigo.setOnFocusChangeListener(this);
        }catch(Exception e){
            String mensaje = "NO SE LOGRÓ LEER EL ARCHIVO "+e.getMessage();
            Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarPantalla(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // todo your code here...
        if (!hasFocus){
            String codigo = edtCodigo.getText().toString();
            buscarProducto2(codigo.toUpperCase());
        }
    }

    public void grabarInventario01(){
        EditText txtCodigo        = (EditText) findViewById(R.id.txtUserCodigo);
        EditText txtCantidad      = (EditText) findViewById(R.id.txtUserCantidad);
        TextView txtlbDetSku      = (TextView) findViewById(R.id.lbUserDetSku);
        TextView txtlbDescripcion = (TextView) findViewById(R.id.lbUserDetDescripcion);

        if(txtCantidad.length() == 0){
            Toast.makeText(contexto,"Debe ingresar una cantidad mayor a 0",Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(getApplicationContext(),"PASA",Toast.LENGTH_SHORT).show();
            StringBuffer msgError = new StringBuffer("");
            int intError = 0;

            if (txtCodigo.getText().length()==0 ) {
                msgError.append("Codigo sin valor ; ");
                intError = 2;
            }
            long valCant = 0;
            if (txtCantidad.getText().length()==0 ) {
                msgError.append("Cantidad sin valor ;");
                intError = 3;
            }else{
                try{
                    valCant = Long.parseLong(txtCantidad.getText().toString());
                    if (valCant>99999){
                        msgError.append("Cantidad mayor a la definida; ");
                        intError = 31;
                    }
                }catch (Exception ee){
                    ee.printStackTrace();
                    msgError.append("Valor ingresado en Cantidad no es numerico; ");
                    intError = 32;
                    valCant = 0;
                }
            }

            if (txtlbDetSku.getText().length() < 2) {
                //  msgError.append("Producto No encontrado;");
                final long finValCant = valCant;
                AlertDialog.Builder builder = new AlertDialog.Builder(UserIngresaCapturaActivity.this);
                builder.setTitle("Por favor indicar:");
                builder.setMessage("Codigo No Encontrado, grabar de todas maneras??");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when user clicked the Yes button
                        // Set the TextView visibility GONE
                        if (finValCant  > 0){
                            UserIngresaCapturaActivity.this.setForzarIngreso("0");
                        }else{
                            Toast.makeText(contexto,"La cantidad debe ser mayor a 0",Toast.LENGTH_SHORT).show();
                            UserIngresaCapturaActivity.this.setForzarIngreso("1");
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when No button clicked
                        Toast.makeText(contexto,"ok, no entonces",Toast.LENGTH_SHORT).show();
                        UserIngresaCapturaActivity.this.setForzarIngreso("1");
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();

                intError = Integer.parseInt(getForzarIngreso());
            }

            String sfecha = getFechaActual();

            if(intError==0){
                //grabamos
                ContentValues registro = new ContentValues();
                registro.put("IDPROD", txtCodigo.getText().toString());
                registro.put("PATENTE", codArea);
                registro.put("EAN", (txtlbDetSku.getText().length()<2?txtCodigo.getText().toString():txtlbDetSku.getText().toString()));
                registro.put("CANTIDAD", txtCantidad.getText().toString());
                registro.put("FECHA", sfecha);
                registro.put("RUT", codUsuario);
                registro.put("ESTADO", "1");
                registro.put("DESCRIPCION", txtlbDescripcion.getText().toString());
                registro.put("UBICACION", ubicacion);
                registro.put("ESTADO1", "1");

                MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(UserIngresaCapturaActivity.this, "Maestra", null, 1);
                //new MaestraSQLiteOpenHelp(this, "Maestra", null, 1);
                SQLiteDatabase bd2 = admin.getWritableDatabase();
                try{
                    bd2.insert("CAPTURA", null, registro);
                }catch (Exception ff){
                    intError = 6;
                }finally {
                    bd2.close();
                }
                txtCodigo.setText("");
                txtlbDetSku.setText("");
                txtlbDescripcion.setText("");
                //onYesNoClicked(v);

                if (txtCantidad.isEnabled()){
                    txtCantidad.setText("");
                }else{
                    txtCantidad.setText("1");
                }
                txtCodigo.requestFocus();
            }
            if(intError>0){
                //despliegue mensaje
                Toast.makeText(contexto, "No paso la siguiente validacion: " + msgError.toString(), Toast.LENGTH_SHORT).show();
                txtCodigo.setText("");

                if (txtCantidad.isEnabled()){
                    txtCantidad.setText("");
                }else{
                    txtCantidad.setText("1");
                }

                txtCodigo.requestFocus();
            }
        }
    }

    public void grabarInventario02(){
        //grabar si es 1 a 1
        final EditText txtCodigo        = (EditText) findViewById(R.id.txtUserCodigo);
        final EditText txtCantidad      = (EditText) findViewById(R.id.txtUserCantidad);
        final TextView txtlbDetSku      = (TextView) findViewById(R.id.lbUserDetSku);
        final TextView txtlbDescripcion = (TextView) findViewById(R.id.lbUserDetDescripcion);

        String sfecha = getFechaActual();

        if((txtlbDescripcion.getText().length() == 0) && (txtlbDetSku.getText().length() == 0)){
            Toast.makeText(contexto, "SKU NO Encontrado", Toast.LENGTH_SHORT).show();
            txtCodigo.setText("");
            txtCodigo.requestFocus();
            txtCantidad.setText("1");
            txtCantidad.setEnabled(false);
        }else{
            //grabamos
            final ContentValues registro = new ContentValues();
            registro.put("IDPROD", txtCodigo.getText().toString());
            registro.put("PATENTE", codArea);
            registro.put("EAN", (txtlbDetSku.getText().length()<2?txtCodigo.getText().toString():txtlbDetSku.getText().toString()));
            registro.put("CANTIDAD", "1");
            registro.put("FECHA", sfecha);
            registro.put("RUT", codUsuario);
            registro.put("ESTADO", "1");
            registro.put("DESCRIPCION", txtlbDescripcion.getText().toString());
            registro.put("UBICACION", ubicacion);
            registro.put("ESTADO1", "1");
            System.out.println("CONTENT VALUES REGISTRO: "+registro);

            Thread tr = new Thread() {
                @Override
                public void run() {
                    MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(UserIngresaCapturaActivity.this, "Maestra", null, 1);
                    //new MaestraSQLiteOpenHelp(this, "Maestra", null, 1);
                    SQLiteDatabase bd2 = admin.getWritableDatabase();
                    try{
                        bd2.insert("CAPTURA", null, registro);
                    }catch (Exception ff){
                        //intError = 6;
                        //msgError.append(ff.getMessage());
                    }finally {
                        bd2.close();
                    }
                }
            };
            tr.start();

            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            txtCodigo.setText("");
                            txtCodigo.requestFocus();
                            txtlbDescripcion.setText("");
                            txtlbDetSku.setText("");
                            txtCantidad.setText("1");
                            txtCantidad.setEnabled(false);
                        }
                    });
        }
    }

    public void openVentanaUserIngresaArea01(View v){
        // Método del handler de button1
        String strUser = (String) getIntent().getExtras().get ("codname");
        Intent venMenu = new Intent(getApplicationContext(), UserIngresaAreaActivity.class);
        venMenu.putExtra("codname",strUser);
        startActivity(venMenu);
    }

    public void getCerrarArea01(final View v){
        Thread tr = new Thread() {
            @Override
            public void run() {

                //String response  = "Sin Conexion";
                String resultado = "Cierre Exitoso";

                try{

                    String sarea = (String) getIntent().getExtras().get ("area");
                    if (sarea==null){
                        sarea = getTxtArea();
                    }

                    String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/captura";
                    File path = new File(directorio);
                    //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(path, sarea + ".txt");

                    String directorio2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/respaldo";
                    File path2 = new File(directorio2);
                    File file2 = new File(path2, sarea + ".txt");

                    MaestraSQLiteOpenHelp admin = new MaestraSQLiteOpenHelp(UserIngresaCapturaActivity.this,
                            "Maestra", null, 1);
                    SQLiteDatabase bd = admin.getWritableDatabase();

                    StringBuffer contenido = new StringBuffer();
                    Map<String, String> itemselected = new HashMap<>();
                    ArrayList<Map<String, String>> itemData = new ArrayList<>();

                    try{
                        Cursor fila = bd.rawQuery("select PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION  from CAPTURA where PATENTE = '" + sarea + "'", null);

                        if (fila.getCount()>0) {
                            while (fila.moveToNext()){
                                //fila.getString(fila.getColumnIndex("IDPROD"))
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

                        ContentValues registro3 = new ContentValues();
                        registro3.put("ESTADO", 2);
                        int res = bd.update("CAPTURA", registro3, "PATENTE ='" + sarea + "'", null);

                    }catch (Exception ff){
                        resultado = "problemas al completar el contenido";
                        //response = "";
                    }finally {
                        bd.close();
                    }

                    //escribir archivo en DPA
                    if (!file.exists()) {
                        try{
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(contenido.toString().getBytes());
                            out.close();

                        }catch (IOException e){
                            resultado = "problemas al crear y escribir el archivo, revisar permisos";
                            //response = "";
                        }
                    }
                }catch (Exception e){
                    Log.e("", "", e);
                    //response = "problemas varios: " + e.getMessage();
                }

                //final String resultad2 = response + " " + resultado;
                final String resultad2 = resultado;

                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserIngresaCapturaActivity.this, resultad2, Toast.LENGTH_SHORT).show();
                                //openVentanaUserIngresaArea();
                                openVentanaUserIngresaArea01(v);
                            }
                        });
            }
        };
        tr.start();
    }

    public String getFechaActual(){
        String strFecha = "";
        Date d=new Date();
        try{
            SimpleDateFormat fecc=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss" , Locale.GERMAN);
            strFecha = fecc.format(d);
        }catch(Exception ee){
            strFecha = "01-01-2018";
        }
        return strFecha;
    }

    public String getTxtArea() {
        return txtArea;
    }

    public void setTxtArea(String txtArea) {
        this.txtArea = txtArea;
    }

    public String getTxtUbicacion() {
        return txtUbicacion;
    }

    public void setTxtUbicacion(String txtUbicacion) {
        this.txtUbicacion = txtUbicacion;
    }

    public String getForzarIngreso() {
        return forzarIngreso;
    }

    public void setForzarIngreso(String forzarIngreso) {
        this.forzarIngreso = forzarIngreso;
    }

    public String getCodname() {
        return codname;
    }

    public void setCodname(String codname) {
        this.codname = codname;
    }*/
}



