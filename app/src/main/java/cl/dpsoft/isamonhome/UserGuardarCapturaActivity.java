package cl.dpsoft.isamonhome;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import cl.dpsoft.isamonhome.vo.Producto;

public class UserGuardarCapturaActivity extends AppCompatActivity {

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private String codUsuario, area, ubicacion, fecha;
    private long codArea;
    //private TextView txtTitulo, txtDescripcionC, txtSkuC, txtDescripcionU, txtSkuU, txtCantidadUnoxUno;
    //private EditText edtCodigoC, edtCantidadC, edtCodigoU;
    private TextView txtTitulo, txtDescripcion, txtSku;
    private EditText edtCodigo, edtCantidad, edtCantidadUnoxUno;;
    private RadioButton rbCantidad, rbUnoAUno, rbAutomatico, rbManual;
    private FrameLayout frCantidad, frUnoAUno;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();
    private Button btnScan;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ingresa_captura);

        codUsuario = (String) Objects.requireNonNull(getIntent().getExtras()).get ("codUsuario");
        area = (String) getIntent().getExtras().get ("area");
        assert area != null;
        codArea = Long.parseLong(area);
        ubicacion = (String) getIntent().getExtras().get ("ubicacion");
        String datosRecibidos = "USUARIO: "+codUsuario+" ÁREA: "+area+" UBICACIÓN: "+ubicacion;
        System.out.println(datosRecibidos);
        fecha = utilidades.traerFecha();

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorIC);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);
        rbCantidad = (RadioButton) findViewById(R.id.rbCantidadUIC);
        rbUnoAUno = (RadioButton) findViewById(R.id.rbUnoxUnoUIC);
        rbAutomatico = (RadioButton) findViewById(R.id.rbAutomaticoUIC);
        rbManual = (RadioButton) findViewById(R.id.rbManualUIC);
        btnScan = (Button) findViewById(R.id.btnUsrScan);
        edtCodigo = (EditText) findViewById(R.id.edtCodigoUIC);
        txtDescripcion = (TextView) findViewById(R.id.txtDetalleDescripcionUIC);
        txtSku = (TextView) findViewById(R.id.txtDetalleSkuUIC);
        edtCantidad = (EditText) findViewById(R.id.edtCantidadUIC);
        //txtCantidadUnoxUno = (TextView) findViewById(R.id.txtCantidadUnoxUnoUIC);
        edtCantidadUnoxUno = (EditText) findViewById(R.id.edtUnoxUnoUIC);

        cargarBarraSuperior();
        volver();
        //edtCodigo.requestFocus();
        edtCantidadUnoxUno.setVisibility(View.GONE);
        //ocultarTeclado(edtCodigo);
        cambiarEditText();
        guardarCapturaxCantidad();
        //guardarCapturaUnoxUno();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    //MÉTODOS
    public void scan() {
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("ESCANEAR CÓDIGO");
        intent.setCameraId(0);
        intent.setOrientationLocked(false);
        intent.setBeepEnabled(false);
        intent.setCaptureActivity(CaptureActivityPortrait.class);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                utilidades.mostrarMensajePersonalizado(contexto, "SCANEO CANCELADO", "NOK");
                //Toast.makeText(MainActivity.this, "Cancelaste el escaneo", Toast.LENGTH_SHORT).show();
                if(rbCantidad.isChecked()){
                    limpiar("CANTIDAD");
                }else if(rbUnoAUno.isChecked()){
                    limpiar("1 A 1");
                }
            } else {
                String tipoScan = "";
                if(rbCantidad.isChecked()){
                    tipoScan = "CANTIDAD";
                }else if(rbUnoAUno.isChecked()){
                    tipoScan = "1 A 1";
                }
                String codigo = result.getContents();
                //edtCodigo.setText(codigo);
                //edtCantidad.requestFocus();
                cambiarEditTextxScan(codigo, tipoScan);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void cambiarEditTextxScan(String codigo, String tipoScan){
        edtCodigo.setText(codigo);
        String respuestaTraerProducto = traerProducto(codigo, tipoScan);
        if(respuestaTraerProducto.equals("NO EXISTE")){
            validarCodigoDesconocido(codigo, tipoScan);
        }else if(respuestaTraerProducto.equals("EXISTE")){
            //guardarCaptura(tipoScan);
            if(tipoScan.equals("CANTIDAD")){
                edtCantidad.requestFocus();
            }else if(tipoScan.equals("1 A 1")){
                //validarCapturaUnoxUno(tipoScan);
                String descripcion = txtDescripcion.getText().toString();
                String sku = txtSku.getText().toString();
                String datos = "CÓDIGO: "+codigo+" DESCRIPCIÓN: "+descripcion+" SKU: "+sku+" TIPO SCAN: "+tipoScan;
                utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
                grabarUnoxUno(codigo, descripcion, sku);
                limpiar(tipoScan);
                edtCodigo.requestFocus();
                //edtCantidadUnoxUno.requestFocus();
            }
        }

    }

    private void cambiarEditText(){
        //utilidades.mostrarMensajePersonalizado(contexto, tipoCodigo, "OK");
        edtCodigo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    String codigo = edtCodigo.getText().toString();
                    String tipoScan = "";
                    if(rbCantidad.isChecked()){
                        tipoScan = "CANTIDAD";
                    }else if(rbUnoAUno.isChecked()){
                        tipoScan = "1 A 1";
                    }
                    String respuestaTraerProducto = traerProducto(codigo, tipoScan);
                    if(respuestaTraerProducto.equals("NO EXISTE")){
                        validarCodigoDesconocido(codigo, tipoScan);
                    }else if(respuestaTraerProducto.equals("EXISTE")){
                        guardarCaptura(tipoScan);
                    }
                    if(tipoScan.equals("CANTIDAD")){
                        edtCantidad.requestFocus();
                    }else if(tipoScan.equals("1 A 1")){
                        edtCantidadUnoxUno.requestFocus();
                    }

                }
                return false;
            }
        });

        /*edtCodigo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    String codigo = edtCodigo.getText().toString();
                    String tipoScan = "";
                    if(rbCantidad.isChecked()){
                        tipoScan = "CANTIDAD";
                    }else if(rbUnoAUno.isChecked()){
                        tipoScan = "1 A 1";
                    }
                    String respuestaTraerProducto = traerProducto(codigo, tipoScan);
                    if(respuestaTraerProducto.equals("NO EXISTE")){
                        validarCodigoDesconocido(codigo, tipoScan);
                    }else if(respuestaTraerProducto.equals("EXISTE")){
                        guardarCaptura(tipoScan);
                    }
                    return true;
                }
                return false;
            }
        });*/
    }

    private void guardarCapturaxCantidad(){
        edtCantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    validarCapturaxCantidad("CANTIDAD");
                    return true;
                }
                return false;
            }
        });
    }

    private void guardarCapturaUnoxUno(final String codigo, final String descripcion, final String sku){
        edtCantidadUnoxUno.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //validarCapturaUnoxUno("1 A 1");
                    grabarUnoxUno(codigo, descripcion, sku);
                    //edtCodigo.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String traerProducto(final String codigo, String tipoScan) {
        final Producto producto = new Producto();
        String respuesta = "";
        Producto prod = delegar.traerProducto(contexto, codigo.toUpperCase());
        System.out.println("CÓDIGO: "+prod.getCodigo()+" SKU: "+prod.getSku()+
                " PATENTE: "+prod.getPatente()+" ID: "+prod.getIdprod());
        if (prod.getEncontrado() == 1){
            producto.setCodigo(prod.getIdprod());
            producto.setDescripcion(prod.getDescripcion());
            producto.setSku(prod.getSku());
            producto.setCantidad(prod.getCantidad());
            producto.setPatente(prod.getPatente());
            respuesta = "EXISTE";
            cargarProducto(producto, codigo, tipoScan);
        }else{
            respuesta = "NO EXISTE";
        }
        return respuesta;
    }

    private void cargarProducto(Producto producto, String codigo, String tipoScan){
        edtCodigo.setText(codigo.toUpperCase());
        txtDescripcion.setText(producto.getDescripcion());
        txtSku.setText(producto.getSku());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void guardarCaptura(String tipoScan){
        String codigo = edtCodigo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String sku = txtSku.getText().toString();
        String cantidad = edtCantidad.getText().toString();
        String datos = "";
        if(tipoScan.equals("CANTIDAD")){
            //datos = "CÓDIGO: "+codigo+" DESCRIPCIÓN: "+descripcion+" SKU: "+sku+" CANTIDAD: "+cantidad;
            //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
            validarCapturaxCantidad(tipoScan);
        }else if(tipoScan.equals("1 A 1")){
            //datos = "CÓDIGO: "+codigo+" DESCRIPCIÓN: "+descripcion+" SKU: "+sku+" CANTIDAD: 1";
            //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
            validarCapturaUnoxUno(tipoScan);
        }
        //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        //limpiar();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void validarCapturaxCantidad(String tipoScan){
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
        }else if(cantidad.isEmpty()){
            mensaje = "INGRESAR CANTIDAD";
            edtCantidad.setError(mensaje);
        }else{

            boolean esNumero = utilidades.esNumero(cantidad);
            if(!esNumero){
                mensaje = "INGRESAR SÓLO NÚMEROS";
                edtCantidad.setError(mensaje);
            }else{
                //String respuestaExisteProducto = existeProducto(codigo);
                grabarxCantidad(codigo, descripcion, sku, cantidad);
                limpiar(tipoScan);
            }

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void validarCapturaUnoxUno(String tipoScan){
        String codigo = edtCodigo.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String sku = txtSku.getText().toString();
        //String cantidad = edtCantidadUnoxUno.getText().toString();
        String mensaje = "";

        if(codigo.isEmpty()){
            mensaje = "INGRESAR CÓDIGO";
            edtCodigo.setError(mensaje);
        }else{

            //String respuestaExisteProducto = existeProducto(codigo);
            //utilidades.mostrarMensajePersonalizado(contexto, "VALIDAR 1 A 1", "OK");
            //grabarUnoxUno(codigo, descripcion, sku);
            guardarCapturaUnoxUno(codigo, descripcion, sku);
            //limpiar(tipoScan);
            //edtCodigoU.requestFocus();
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
            //edtCodigoU.requestFocus();
            limpiar("1 A 1");
        }else{
            mensaje = "ERROR. NO SE HA GUARDADO LA CAPTURA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }
    }

    public void validarCodigoDesconocido(final String codigo, final String tipoScan){
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserGuardarCapturaActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        final String mensaje = "EL CÓDIGO "+codigo.toUpperCase()+" NO EXISTE.\n¿DESEA CONTINUAR?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                if(tipoScan.equals("CANTIDAD")){
                    edtCantidad.requestFocus();
                }else if(tipoScan.equals("1 A 1")){
                    grabarUnoxUno(codigo, "", "");
                    edtCodigo.setText("");
                    edtCodigo.requestFocus();
                }
                dialogoPrecaucion.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                edtCodigo.setText("");
                edtCodigo.requestFocus();
                dialogoPrecaucion.dismiss();
            }
        });
        dialogoPrecaucion.show();
    }

    private void limpiar(String tipoScan){
        //utilidades.mostrarMensajePersonalizado(contexto, "LIMPIAR "+tipoScan, "OK");
        edtCodigo.setText("");
        txtDescripcion.setText("");
        txtSku.setText("");
        if(tipoScan.equals("CANTIDAD")){
            edtCantidad.setText("");
        }
        edtCodigo.requestFocus();
    }

    public void mostrarOcultar(View vista){
        if(rbCantidad.isChecked()){
            edtCantidad.setVisibility(View.VISIBLE);
            edtCantidadUnoxUno.setVisibility(View.GONE);
        }else if(rbUnoAUno.isChecked()){
            edtCantidad.setVisibility(View.GONE);
            edtCantidadUnoxUno.setVisibility(View.VISIBLE);
        }

        /*if(rbCantidad.isChecked()){
            frCantidad.setVisibility(View.VISIBLE);
            frUnoAUno.setVisibility(View.GONE);
            edtCodigoC.requestFocus();
            //edtCantidad.setVisibility(View.VISIBLE);
            //txtCantidadUnoxUno.setVisibility(View.GONE);
        }else if(rbUnoAUno.isChecked()){
            frCantidad.setVisibility(View.GONE);
            frUnoAUno.setVisibility(View.VISIBLE);
            edtCodigoU.requestFocus();
            //edtCantidad.setVisibility(View.GONE);
            //txtCantidadUnoxUno.setVisibility(View.VISIBLE);
        }*/
        //edtCodigo.requestFocus();
    }

    private void ocultarTeclado(EditText edt){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
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
                    resultado = "TOTAL UNIDADES: "+unidades;
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
                                    //utilidades.mostrarMensajePersonalizado(contexto, respuesta, "OK");
                                    //irIngresarArea(vista);
                                    dialogoCerrarArea(respuesta);
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

    public void dialogoCerrarArea(final String mensaje){
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserGuardarCapturaActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txtTitulo = (TextView) vistaDialogo.findViewById(R.id.txtTituloDP);
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        btnCancelar.setVisibility(View.GONE);

        String titulo = "CIERRE EXITOSO";
        txtTitulo.setText(titulo);
        txtTitulo.setTextSize(20);
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                irIngresarArea(null);
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
    }

}
