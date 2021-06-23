package cl.dpsoft.isamonhome;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRouter;
import android.os.Build;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Objects;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import cl.dpsoft.isamonhome.vo.Producto;

public class UserRevisarActivity extends AppCompatActivity {

    private static String NOMBRETABLA = "Maestra";
    public int intPosition;
    Button btnGoPrev, btnGoNext, btnGoFirst, btnGoLast;
    EditText edtCantidad;

    private Context contexto;
    private String codUsuario, area, ubicacion;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo, txtNumeroCaptura, txtArea, txtUbicacion, txtEan,
            txtCantidad, txtDescripcion, txtCapturaSel;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_revisar);

        codUsuario = (String) Objects.requireNonNull(getIntent().getExtras()).get ("codUsuario");
        area = (String) getIntent().getExtras().get ("area");
        ubicacion = (String) getIntent().getExtras().get ("ubicacion");
        String datosRecibidos = "USUARIO: "+codUsuario+" ÁREA: "+area+" UBICACIÓN: "+ubicacion;
        System.out.println(datosRecibidos);

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorIC);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);

        txtNumeroCaptura = (TextView) findViewById(R.id.txtNumerocapturaUR);
        txtArea = (TextView) findViewById(R.id.lbUserDescArea);
        txtUbicacion = (TextView) findViewById(R.id.listUserDescUbicacionOld);
        txtEan = (TextView) findViewById(R.id.lbUserDetEan);
        txtCantidad = (TextView) findViewById(R.id.txtCantidad);
        txtDescripcion = (TextView) findViewById(R.id.lbDetDescripcion);
        txtCapturaSel = (TextView) findViewById(R.id.lbUserAreaSel);

        //busco la lista
        /*recuperoLista(area);

        TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);
        String[] paso = lbListAreas.getText().toString().split(";");
        Log.i("", "" + paso[0]);
        //elijo el primero
        Producto prod = delegar.getProductoAEditar(getApplicationContext(), paso[0]);

        //lo pinto
        pintaProducto(prod);*/

        /*btnGoPrev = (Button) findViewById(R.id.btnUsrGoPrev);
        btnGoNext = (Button) findViewById(R.id.btnUsrGoNext);
        btnGoFirst = (Button) findViewById(R.id.btnUsrGoFirst);
        btnGoLast = (Button) findViewById(R.id.btnUsrGoLast);*/
        edtCantidad = (EditText) findViewById(R.id.lbUserDetCantidad);
        edtCantidad.requestFocus();

        cargarCapturas();
        cargarBarraSuperior();
        volver();
    }

    //MÉTODOS
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void cargarCapturas(){
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        String primerElemento = listaCapturas.get(0);
        int largoLista = listaCapturas.size();
        System.out.println("LISTA CAPTURAS: "+listaCapturas); //[18, 20, 21, 22]
        System.out.println("PRIMER ELEMENTO LISTA CAPTURAS: "+primerElemento); //18
        System.out.println("LARGO LISTA CAPTURAS: "+largoLista); //4
        int numeroCaptura = 0;
        for (int i = 0; i < listaCapturas.size(); i++){
            //linea = linea + lAreas.get(i).toString() + ";";
            String captura = listaCapturas.get(i);
            //linea = linea + lAreas.get(i).toString();
            numeroCaptura = numeroCaptura + 1;
            System.out.println("DATOS CAPTURA: "+captura);
            System.out.println("ÍNDICE: "+i);
            System.out.println("N° CAPTURA: "+numeroCaptura);
        }
        Producto producto = delegar.traerCaptura(contexto, primerElemento);
        cargarCaptura(producto, "0");
    }

    /*I/System.out: LISTA CAPTURAS: [18, 20, 21, 22]
    PRIMER ELEMENTO LISTA CAPTURAS: 18
    LARGO LISTA CAPTURAS: 4
    DATOS CAPTURA: 18
    ÍNDICE: 0
    N° CAPTURA: 1
    DATOS CAPTURA: 20
    ÍNDICE: 1
    N° CAPTURA: 2
    DATOS CAPTURA: 21
    ÍNDICE: 2
    N° CAPTURA: 3
    DATOS CAPTURA: 22
    ÍNDICE: 3
    N° CAPTURA: 4*/

    private void cargarBarraSuperior(){
        String titulo = getString(R.string.revisarCapturas);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                irIngresarCaptura(null);
            }
        });
    }

    public void irIngresarCaptura(View vista){
        //Intent ingresarCaptura = new Intent(contexto, UserIngresaCapturaActivity.class);
        Intent ingresarCaptura = new Intent(contexto, UserGuardarCapturaActivity.class);
        ingresarCaptura.putExtra("codUsuario", codUsuario);
        ingresarCaptura.putExtra("area", area);
        ingresarCaptura.putExtra("ubicacion", ubicacion);
        startActivity(ingresarCaptura);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void avanzarUnPuesto(View vista){
        String indiceAnterior = txtNumeroCaptura.getText().toString();
        String[] partesIndice = indiceAnterior.split(" ");
        String indiceAnt = partesIndice[2].trim();
        int indiceAnter = Integer.parseInt(indiceAnt) - 1;
        int indice = indiceAnter + 1;
        System.out.println("ÍNDICE: "+indice);
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        int largoLista = listaCapturas.size();
        if(indice >= largoLista){
            String mensaje = "NO HAY MAS CAPTURAS";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }else{
            String elemento = listaCapturas.get(indice);
            Producto producto = delegar.traerCaptura(contexto, elemento);
            cargarCaptura(producto, String.valueOf(indice));
        }

        /*TextView lbAreaSel = (TextView) findViewById(R.id.lbUserAreaSel);
        TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);
        long pos = Long.valueOf(lbAreaSel.getText().toString());
        String[] paso = lbListAreas.getText().toString().split(";");
        int cant = paso.length;
        if (pos + 1 < cant){
            pos = pos + 1;
            int i = (int) pos;
            Producto prod = delegar.getProductoAEditar(getApplicationContext(), paso[i]);
            pintaProducto(prod);
            lbAreaSel.setText(String.valueOf(pos));
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void retrocederUnPuesto(View vista){
        String indicePosterior = txtNumeroCaptura.getText().toString();
        String[] partesIndice = indicePosterior.split(" ");
        String indicePost = partesIndice[2].trim();
        int indicePos = Integer.parseInt(indicePost) - 1;
        int indice = indicePos - 1;
        System.out.println("ÍNDICE: "+indice);
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        int largoLista = listaCapturas.size();
        if(indice < 0){
            String mensaje = "NO HAY MAS CAPTURAS";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
        }else{
            String elemento = listaCapturas.get(indice);
            Producto producto = delegar.traerCaptura(contexto, elemento);
            cargarCaptura(producto, String.valueOf(indice));
        }

        /*TextView lbAreaSel = (TextView) findViewById(R.id.lbUserAreaSel);
        TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);
        long pos = Long.valueOf(lbAreaSel.getText().toString());
        String[] paso = lbListAreas.getText().toString().split(";");
        int cant = paso.length;
        if (pos - 1 >= 0){
            pos = pos - 1;
            int i = (int) pos;
            Producto prod = delegar.getProductoAEditar(getApplicationContext(), paso[i]);
            pintaProducto(prod);
            lbAreaSel.setText(String.valueOf(pos));
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void retrocederTodos(View vista){
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        String primerElemento = listaCapturas.get(0);
        Producto producto = delegar.traerCaptura(contexto, primerElemento);
        cargarCaptura(producto, "0");

        /*TextView lbAreaSel = (TextView) findViewById(R.id.lbUserAreaSel);
        TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);
        String[] paso = lbListAreas.getText().toString().split(";");
        Producto prod = delegar.getProductoAEditar(getApplicationContext(), paso[0]);
        pintaProducto(prod);
        lbAreaSel.setText("0");*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void avanzarTodos(View vista){
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        int largoLista = listaCapturas.size() - 1;
        String ultimoElemento = listaCapturas.get(largoLista);
        System.out.println("LISTA CAPTURAS: "+listaCapturas); //[18, 20, 21, 22]
        System.out.println("ÚLTIMO ELEMENTO LISTA CAPTURAS: "+ultimoElemento); //18
        System.out.println("LARGO LISTA CAPTURAS: "+largoLista); //4
        Producto producto = delegar.traerCaptura(contexto, ultimoElemento);
        cargarCaptura(producto, String.valueOf(largoLista));

        /*TextView lbAreaSel = (TextView) findViewById(R.id.lbUserAreaSel);
        TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);
        String[] paso = lbListAreas.getText().toString().split(";");
        int cant = paso.length - 1;
        Producto prod = delegar.getProductoAEditar(getApplicationContext(), paso[cant]);
        pintaProducto(prod);
        lbAreaSel.setText(String.valueOf(cant));*/
    }

    public void recuperoLista(String sArea) {
        ArrayList<String> lAreas = delegar.getAreasToRevision(contexto, sArea);
        String linea = "";
        int indice = 0;
        for (int i = 0; i < lAreas.size(); i++){
            //linea = linea + lAreas.get(i).toString() + ";";
            linea = linea + lAreas.get(i).toString();
            indice = indice + 1;
            System.out.println("LÍNEA LISTA CAPTURA: "+linea);
            System.out.println("ÍNDICE: "+indice);
        }
        System.out.println("LÍNEA LISTA CAPTURA2: "+linea);
        //I/System.out: LÍNEA LISTA CAPTURA: 32;33;34;
        TextView lbAreaSel = (TextView) findViewById(R.id.lbUserAreaSel);
        TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);

        lbListAreas.setText(linea);
        lbAreaSel.setText("0");
    }

    public void cargarCaptura(Producto prod, String indice){
        int numeroCaptura = Integer.parseInt(indice) + 1;
        String mensajeNumeroCaptura = "CAPTURA N° "+numeroCaptura;
        txtNumeroCaptura.setText(mensajeNumeroCaptura);
        String mensajeArea = "ÁREA "+prod.getPatente().toUpperCase();
        txtArea.setText(mensajeArea);
        String mensajeEan = "EAN "+prod.getCodigo().toUpperCase();
        txtEan.setText(mensajeEan);
        String mensajeDescripcion = "DESCRIPCIÓN "+prod.getDescripcion().toUpperCase();
        txtDescripcion.setText(mensajeDescripcion);
        String mensajeUbicacion = "";
        if(prod.getUbicacion().toUpperCase().equals("SIN UBICACIÓN")){
            mensajeUbicacion = "UBICACIÓN";
        }else{
            mensajeUbicacion = "UBICACIÓN "+prod.getUbicacion().toUpperCase();
        }
        txtUbicacion.setText(mensajeUbicacion);
        String mensajeCantidad = "CANTIDAD "+prod.getCantidad();
        txtCantidad.setText(mensajeCantidad);

    }

    public void pintaProducto(Producto prod){
        TextView descArea = (TextView) findViewById(R.id.lbUserDescArea);
        TextView descEan  = (TextView) findViewById(R.id.lbUserDetEan);
        TextView deDetalle  = (TextView) findViewById(R.id.lbDetDescripcion);
        TextView lbUbicacionOld = (TextView) findViewById(R.id.listUserDescUbicacionOld);
        TextView cantidad = (TextView) findViewById(R.id.txtCantidad);

        EditText lbUbicacion = (EditText) findViewById(R.id.listUserDescUbicacion);
        EditText lbCantidad = (EditText) findViewById(R.id.lbUserDetCantidad);

        String mensajeArea = "ÁREA "+prod.getPatente().toUpperCase();
        descArea.setText(mensajeArea);
        String mensajeEan = "EAN "+prod.getCodigo().toUpperCase();
        descEan.setText(mensajeEan);
        String mensajeDescripcion = "DESCRIPCIÓN "+prod.getDescripcion().toUpperCase();
        deDetalle.setText(mensajeDescripcion);
        String mensajeUbicacion = "UBICACIÓN "+prod.getUbicacion().toUpperCase();
        System.out.println("MENSAJE UBICACIÓN: "+mensajeUbicacion);
        lbUbicacionOld.setText(mensajeUbicacion);
        lbCantidad.setText(prod.getCantidad());
        String mensajeCantidad = "CANTIDAD "+prod.getCantidad();
        cantidad.setText(mensajeCantidad);

        lbUbicacion.setText("");
    }

    public void openVentanaUserIngresaArea(View view){
        String strUser = (String) getIntent().getExtras().get ("codname");
        Intent venMenu = new Intent(getApplicationContext(), UserIngresaAreaActivity.class);
        venMenu.putExtra("codname",strUser);
        startActivity(venMenu);
    }

    public void openVentanaUserIngresaCaptura(View view){
        String strUser = (String) getIntent().getExtras().get ("codname");

        String areaUbiOpen = delegar.areaabieta(getApplicationContext());
        String[] sArea = areaUbiOpen.split(";");
        String areaOpen = sArea[0];
        String areaUbica = sArea[1];

        if ("DECERO".compareTo(areaOpen)==0){
            Intent venMenu = new Intent(getApplicationContext(), UserIngresaAreaActivity.class);
            venMenu.putExtra("codname",strUser);
            startActivity(venMenu);
        }else{
            Intent venMenu = new Intent(getApplicationContext(), UserIngresaCapturaActivity.class);
            venMenu.putExtra("codname",strUser);
            venMenu.putExtra("txtArea",areaOpen);
            venMenu.putExtra("txtUbicacion",areaUbica);
            startActivity(venMenu);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Producto traerDatosCaptura(){
        ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        String captura = txtNumeroCaptura.getText().toString();
        String[] partesCaptura = captura.split(" ");
        final String idCaptura = partesCaptura[2];
        int indice = Integer.parseInt(idCaptura) - 1;
        final String idCapt = listaCapturas.get(indice);
        System.out.println("ID CAPTURA2: "+idCapt);
        String area = txtArea.getText().toString();
        String[] partesArea = area.split(" ");
        final String numArea = partesArea[1];
        return delegar.traerCaptura(contexto, idCapt, numArea, "1");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void modificarCantidad(View vista){
        final Producto datosCaptura = traerDatosCaptura();
        final String id = datosCaptura.getIdCaptura();
        final String area = datosCaptura.getPatente();
        String descripcion = datosCaptura.getDescripcion();
        String datos = "ID: "+id+" ÁREA: "+area+" DESCRIPCIÓN: "+descripcion;
        //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserRevisarActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_modificar,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txtTitulo = (TextView) vistaDialogo.findViewById(R.id.txtTituloDM);
        final EditText edtModificar = (EditText) vistaDialogo.findViewById(R.id.edtModificarDM);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDM);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDM);
        String titulo = getString(R.string.modCantidad);
        txtTitulo.setText(titulo);
        String mensaje = getString(R.string.ingresarCantidad);
        edtModificar.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtModificar.setHint(mensaje);
        edtModificar.requestFocus();

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                String cantidad = edtModificar.getText().toString();
                String mensaje = "";
                if(cantidad.isEmpty()){
                    mensaje = "DEBE AGREGAR CANTIDAD";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                }else{
                    String respuestaModificarCantidad = delegar.modificarCantidad(contexto, datosCaptura.getIdCaptura(), cantidad);
                    if(respuestaModificarCantidad.equals("OK")){
                        mensaje = "CANTIDAD MODIFICADA";
                        utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                        String nuevaCantidad = "CANTIDAD "+cantidad;
                        txtCantidad.setText(nuevaCantidad);
                    }else{
                        utilidades.mostrarMensajePersonalizado(contexto, respuestaModificarCantidad, "NOK");
                    }
                    dialogoPrecaucion.dismiss();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPrecaucion.dismiss();
            }
        });
        dialogoPrecaucion.show();

        /*final TextView lbAreaSel = (TextView) findViewById(R.id.lbUserAreaSel);
        final TextView lbListAreas = (TextView) findViewById(R.id.lbUserListAreas);
        final TextView cantidad = (TextView) findViewById(R.id.txtCantidad);
        final EditText txtCant = (EditText) findViewById(R.id.lbUserDetCantidad);
        final AlertDialog alertDialog;
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(UserRevisarActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_editar_cantidad,null);
        dialogo1.setView(dialogView);
        alertDialog = dialogo1.create();
        alertDialog.setCancelable(true);
        final EditText edtCant = (EditText) dialogView.findViewById(R.id.edtCantidad);
        edtCant.setText(txtCant.getText().toString());
        edtCant.requestFocus();
        Button btnModif = (Button) dialogView.findViewById(R.id.btPositivo);
        btnModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long valCAnt = Long.valueOf(edtCant.getText().toString());
                if (valCAnt >=0 ) {
                    long pos = Long.valueOf(lbAreaSel.getText().toString());
                    String[] paso = lbListAreas.getText().toString().split(";");
                    int i = (int) pos;
                    int cantprodafec = delegar.actualizaCantidadProd(getApplicationContext(), paso[i], String.valueOf(valCAnt));
                    txtCant.setText(String.valueOf(valCAnt));
                    cantidad.setText("CANTIDAD: "+String.valueOf(valCAnt));
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void modificaarUbicacion(View vista){
        final Producto datosCaptura = traerDatosCaptura();
        final String id = datosCaptura.getIdCaptura();
        final String area = datosCaptura.getPatente();
        String descripcion = datosCaptura.getDescripcion();
        String datos = "ID: "+id+" ÁREA: "+area+" DESCRIPCIÓN: "+descripcion;
        //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserRevisarActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_modificar,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txtTitulo = (TextView) vistaDialogo.findViewById(R.id.txtTituloDM);
        final EditText edtModificar = (EditText) vistaDialogo.findViewById(R.id.edtModificarDM);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDM);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDM);
        String titulo = getString(R.string.modUbicacion);
        txtTitulo.setText(titulo);
        String mensaje = getString(R.string.ingresarUbicacion);
        edtModificar.setHint(mensaje);
        edtModificar.requestFocus();

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {
                String ubicacion = edtModificar.getText().toString();
                String mensaje = "";
                if(ubicacion.isEmpty()){
                    mensaje = "DEBE AGREGAR UBICACIÓN";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                }else{
                    String respuestaModificarUbicacion = delegar.modificarUbicacion(contexto, datosCaptura.getIdCaptura(), ubicacion);
                    if(respuestaModificarUbicacion.equals("OK")){
                        mensaje = "UBICACIÓN MODIFICADA";
                        utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                        String nuevaUbicacion = "UBICACIÓN "+ubicacion.toUpperCase();
                        txtUbicacion.setText(nuevaUbicacion);
                    }else{
                        utilidades.mostrarMensajePersonalizado(contexto, respuestaModificarUbicacion, "NOK");
                    }
                    dialogoPrecaucion.dismiss();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoPrecaucion.dismiss();
            }
        });
        dialogoPrecaucion.show();

        /*final TextView txtArea = (TextView) findViewById(R.id.lbUserDescArea);
        String area = txtArea.getText().toString();
        String[] partesArea = area.split(" ");
        final String areaFinal = partesArea[1];
        final TextView txtUbicacion = (TextView) findViewById(R.id.listUserDescUbicacionOld);
        final String ubicacion = txtUbicacion.getText().toString();
        System.out.println("UBICACIÓN: "+ubicacion);
        String ubicacionFinal = "";
        if(ubicacion.trim().equals("UBICACIÓN")){
            ubicacionFinal = "";
        }else{
            String[] partesUbicacion = ubicacion.split(" ");
            ubicacionFinal = partesUbicacion[1];
        }

        final TextView txtEan = (TextView) findViewById(R.id.lbUserDetEan);
        final String ean = txtEan.getText().toString();
        String[] partesEan = ean.split(" ");
        final String eanFinal = partesEan[1];
        //Toast.makeText(UserRevisarActivity.this, "Ubicación " + ean, Toast.LENGTH_SHORT).show();
        final AlertDialog alertDialog;
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(UserRevisarActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_editar_ubicacion,null);
        dialogo1.setView(dialogView);
        alertDialog = dialogo1.create();
        alertDialog.setCancelable(true);
        final EditText edtUbic = (EditText) dialogView.findViewById(R.id.edtUbicacion);
        edtUbic.setText(ubicacionFinal);
        edtUbic.requestFocus();
        Button btnModif = (Button) dialogView.findViewById(R.id.btPositivo);
        //E/query:: UPDATE CAPTURA SET UBICACION = 'vvv' WHERE PATENTE ='ÁREA 8585'
        // AND EAN = 'EAN 3841391'
        btnModif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nuevaubicacion = edtUbic.getText().toString();
                int cuantos = delegar.actualizarUbicacionUnica(contexto, areaFinal, nuevaubicacion, eanFinal);
                //avanzarUnPuesto(vista);
                actualizar(areaFinal, eanFinal);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();*/
    }

    public void actualizar(String area, String idProd){
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(this, "Maestra", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        final TextView txtUbicacion = (TextView) findViewById(R.id.listUserDescUbicacionOld);

        Cursor fila = bd.rawQuery("select * from CAPTURA WHERE PATENTE = '" + area + "' AND IDPROD = '" + idProd + "'", null);
        if(fila.moveToFirst()){
            do{
                //lista.add(fila.getString(5)+ ";" + fila.getString(6)+ ";" + fila.getString(7));
                String mensajeUbicacion = "UBICACIÓN "+fila.getString(9).toUpperCase();
                txtUbicacion.setText(mensajeUbicacion);
            }while(fila.moveToNext());
        }
        bd.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void eliminarCaptura(View vista){
        final Producto datosCaptura = traerDatosCaptura();
        final String id = datosCaptura.getIdCaptura();
        final String area = datosCaptura.getPatente();
        String descripcion = datosCaptura.getDescripcion();
        String datos = "ID: "+id+" ÁREA: "+area+" DESCRIPCIÓN: "+descripcion;
        //utilidades.mostrarMensajePersonalizado(contexto, datos, "OK");
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserRevisarActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        String mensaje = "";
        if(descripcion.isEmpty()){
            mensaje = "¿ESTÁ SEGURO DE ELIMINAR LA CAPTURA?";
        }else{
            mensaje = "¿ESTÁ SEGURO DE ELIMINAR LA CAPTURA DEL PRODUCTO "+descripcion.toUpperCase()+"?";
        }
        txvMensaje.setText(mensaje);

        final ArrayList<String> listaCapturas = delegar.traerCapturas(contexto, area);
        final String primerElemento = listaCapturas.get(0);
        System.out.println("LISTA CAPTURAS: "+listaCapturas); //[32, 33, 34]
        System.out.println("PRIMER ELEMENTO: "+primerElemento); //32
        System.out.println("ID CAPTURA: "+id); //32

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View vista) {

                String respuestaEliminarCaptura = delegar.eliminarCaptura(contexto, id, area, "1");
                if(respuestaEliminarCaptura.equals("OK")){

                    if((primerElemento.equals("SIN CAPTURAS")) || (listaCapturas.size() == 1)) {
                        irIngresarCaptura(null);
                    }else if(primerElemento.equals(id)){
                        //int idCapt = Integer.parseInt(id) + 1;
                        String elemento = listaCapturas.get(1);
                        Producto producto = delegar.traerCaptura(contexto, elemento);
                        cargarCaptura(producto, "1");
                    }else{
                        retrocederUnPuesto(null);
                    }
                    String mensaje = "CAPTURA ELIMINADA";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                }else{
                    utilidades.mostrarMensajePersonalizado(contexto, respuestaEliminarCaptura, "NOK");
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
    }

    public static String  palabraEliminar(String cadena) {
        if(cadena.contains("DESCRIPCIÓN"))
            return cadena.replaceAll("DESCRIPCIÓN", "");
        return cadena;
    }

    private void actualizar(){
        ArrayList<String> lista = delegar.getAreasToRevision(contexto, area);
        if(lista.size() == 0) {
            actualizarPantalla();
        }else if(lista.size() == 1){
            recuperoLista(area);
        }else {
            lista.size();
            avanzarUnPuesto(null);
        }
    }

    public void actualizarPantalla(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() { }
    public int getIntPosition() {
        return intPosition;
    }
    public void setIntPosition(int intPosition) {
        this.intPosition = intPosition;
    }
}
