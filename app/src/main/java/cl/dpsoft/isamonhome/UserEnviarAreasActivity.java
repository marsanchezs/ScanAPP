package cl.dpsoft.isamonhome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import us.monoid.web.AbstractContent;
import us.monoid.web.Resty;
import us.monoid.web.TextResource;

public class UserEnviarAreasActivity extends AppCompatActivity {

    IsamDelegate delegate = new IsamDelegate();
    ArrayList<String> lista;

    private Context contexto;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private String codUsuario;
    private Spinner spAreasCerradas, spAreasEnviadas;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_enviar_areas);

        codUsuario = (String) Objects.requireNonNull(getIntent().getExtras()).get ("codUsuario");

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorEA);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);

        spAreasCerradas = (Spinner) findViewById(R.id.listUserAreaEnviar);
        spAreasEnviadas = (Spinner) findViewById(R.id.listUserAreaEnviadas);

        //getActualizarLista();
        cargarAreas();
        cargarBarraSuperior();
        volver();
    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.enviarAreas);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                irIngresarArea(vista);
            }
        });
    }

    public void irIngresarArea(View vista){
        Intent ingresarArea = new Intent(contexto, UserIngresaAreaActivity.class);
        ingresarArea.putExtra("codUsuario", codUsuario);
        startActivity(ingresarArea);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void cargarAreas(){
        ArrayList<String> listaAreasCerradas = delegar.traerAreas(contexto, "2");
        ArrayAdapter<String> adaptadorLAC = new ArrayAdapter<>(this, R.layout.lista_desplegable,
                R.id.txt, listaAreasCerradas);
        spAreasCerradas.setAdapter(adaptadorLAC);

        ArrayList<String> listaAreasEnviadas = delegar.traerAreas(contexto, "3");
        ArrayAdapter<String> adaptadorLAE = new ArrayAdapter<>(this, R.layout.lista_desplegable,
                R.id.txt, listaAreasEnviadas);
        spAreasEnviadas.setAdapter(adaptadorLAE);
    }

    public void enviarAreasCerradas(View vista){
        String areasEnviar = spAreasCerradas.getSelectedItem().toString();
        System.out.println("ÁREAS ENVIAR: "+areasEnviar);
        if(areasEnviar.equals("SIN ÁREAS")){
            utilidades.mostrarMensajePersonalizado(contexto, "SIN ÁREAS PARA ENVIAR", "NOK");
        }else {
            //utilidades.mostrarMensajePersonalizado(contexto, "ALERT DIALOG", "OK");
            final AlertDialog dialogoPrecaucion;
            final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserEnviarAreasActivity.this);
            LayoutInflater inflador = getLayoutInflater();
            View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
            constructorDialogo.setView(vistaDialogo);
            dialogoPrecaucion = constructorDialogo.create();
            TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
            ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
            ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
            String mensaje = "¿ENVIAR LAS ÁREAS CERRADAS?";
            txvMensaje.setText(mensaje);

            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View vista) {
                    enviarAreas("ENVIAR");
                    dialogoPrecaucion.dismiss();
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vista) {
                    dialogoPrecaucion.dismiss();
                }
            });
            dialogoPrecaucion.show();
        }
    }

    public void reEnviarAreasCerradas(View vista){
        final String areaReEnviar = spAreasEnviadas.getSelectedItem().toString();
        if(areaReEnviar.equals("SIN ÁREAS")){
            utilidades.mostrarMensajePersonalizado(contexto, "SIN ÁREAS PARA RE-ENVIAR", "NOK");
        }else{
            String[] partesArea = areaReEnviar.split(" ");
            final String area = partesArea[1];
            final AlertDialog dialogoPrecaucion;
            final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserEnviarAreasActivity.this);
            LayoutInflater inflador = getLayoutInflater();
            View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
            constructorDialogo.setView(vistaDialogo);
            dialogoPrecaucion = constructorDialogo.create();
            TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
            ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
            ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
            String mensaje = "¿ENVIAR EL ÁREA "+area+"?";
            txvMensaje.setText(mensaje);

            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View vista) {
                    enviarAreas(area);
                    //utilidades.mostrarMensajePersonalizado(contexto, area, "OK");
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

    private void enviarAreas(final String tipoEnvio){
        Thread hiloEnviarAreas = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String respuesta = "";
                ArrayList<Map<String, String>> datosArea;

                try {
                    String urlIP = delegar.traerServidor(contexto);
                    String url = "http://" + urlIP + "/isam/IsamWS/recibe_area.php";
                    if(urlIP.length() > 8){

                        if(tipoEnvio.equals("ENVIAR")){
                            JSONArray arrayArea = new JSONArray();
                            ArrayList<String> listaAreasCerradas = delegar.traerAreas(contexto, "2");
                            System.out.println("LISTA ÁREAS CERRADAS: "+listaAreasCerradas);
                            for (int i = 0; i < listaAreasCerradas.size(); i++) {
                                String area = listaAreasCerradas.get(i);
                                String[] partesArea = area.split(" ");
                                String areaEnviar = partesArea[1];
                                datosArea = delegar.traerDatosArea(contexto, areaEnviar);
                                System.out.println("DATOS ÁREA: " + datosArea);
                                arrayArea.put(datosArea);
                                JSONObject jsonArea = new JSONObject();
                                jsonArea.put("squadName", areaEnviar + ".txt");
                                jsonArea.put("detalle", datosArea);
                                AbstractContent contenido = Resty.content(String.valueOf(jsonArea));
                                Resty resty = new Resty();
                                resty = new Resty().json(url, contenido);
                                TextResource resultado = resty.text(url, contenido);
                                System.out.println("RESULTADO: " + resultado);
                                JSONObject jsonObject = new JSONObject(resultado.toString());
                                respuesta = jsonObject.getString("resulted");
                                if (respuesta.equals("OK")) {
                                    String respuestaCambiarEstadoArea = delegar.cambiarEstadoArea(contexto, areaEnviar);
                                    System.out.println("RESPUESTA CAMBIAR ESTADO ÁREA: " + respuestaCambiarEstadoArea);
                                }
                            }
                        }else{

                            JSONArray arrayArea2 = new JSONArray();  //getDataEnviar
                            datosArea = delegar.traerDatosArea(contexto, tipoEnvio);
                            arrayArea2.put(datosArea);
                            JSONObject jsonArea2 = new JSONObject();
                            jsonArea2.put("squadName", tipoEnvio + ".txt");
                            jsonArea2.put("detalle", datosArea);
                            AbstractContent contenido2 = Resty.content(String.valueOf(jsonArea2));
                            Resty resty2 = new Resty();
                            resty2 = new Resty().json(url, contenido2);
                            TextResource resultado2 = resty2.text(url, contenido2);
                            JSONObject jsonObject2 = new JSONObject(resultado2.toString());
                            respuesta = jsonObject2.getString("resulted");
                            respuesta = respuesta+"2";

                        }

                    }else{
                          respuesta = "FALTA SERVIDOR";
                        }
                } catch (IOException e) {
                    respuesta = e.getMessage().toUpperCase();
                    Log.i("ERROR", respuesta);
                } catch (JSONException e) {
                    respuesta = e.getMessage().toUpperCase();
                    Log.i("ERROR", respuesta);
                }
                final String finalRespuesta = respuesta;
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                if(finalRespuesta.equals("OK")) {
                                    utilidades.mostrarMensajePersonalizado(contexto, "ÁREAS ENVIADAS", "OK");
                                    eliminaArchivosCaptura();
                                    cargarAreas();
                                }else if(finalRespuesta.equals("OK2")){
                                    utilidades.mostrarMensajePersonalizado(contexto, "ÁREAS RE-ENVIADAS", "OK");
                                }else{
                                    utilidades.mostrarMensajePersonalizado(contexto, finalRespuesta,"NOK");
                                }
                            }
                        });
            }
        };
        hiloEnviarAreas.start();
    }

    public void getActualizarLista(){
        //Toast.makeText(getApplicationContext(), "FUNCIÓN", Toast.LENGTH_SHORT).show();
        ArrayList<String> list = delegate.getAreaEnviarList(getApplicationContext(), "2");
        String[] listVentas = new String[list.size()];
        for (int i = 0; i < list.size(); i++){
            String paso = list.get(i);
            listVentas[i] = paso;
        }
        Spinner listV = (Spinner) findViewById(R.id.listUserAreaEnviar);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listVentas);
        listV.setAdapter(adapter5);

        list = delegate.getAreaEnviarList(getApplicationContext(), "3");
        listVentas = new String[list.size()];
        for (int i = 0; i < list.size(); i++){
            String paso = list.get(i);
            listVentas[i] = paso;
        }
        Spinner listV2 = (Spinner) findViewById(R.id.listUserAreaEnviadas);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listVentas);
        listV2.setAdapter(adapter6);
    }

    public void openVentanaUserIngresaArea(View vista){
        Intent ingresarArea = new Intent(contexto, UserIngresaAreaActivity.class);
        ingresarArea.putExtra("codUsuario", codUsuario);
        startActivity(ingresarArea);
    }

    /*public void sendAreaToWS(View v){
        final AlertDialog alertDialog;
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(UserEnviarAreasActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_atencion,null);
        dialogo1.setView(dialogView);
        alertDialog = dialogo1.create();
        TextView txvMensaje = (TextView) dialogView.findViewById(R.id.mensajeAtencion);
        Button btnEnviar = (Button) dialogView.findViewById(R.id.btnEnviar);
        String mensaje = "¿ENVIAR LAS ÁREAS CERRADAS?";
        txvMensaje.setText(mensaje);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread tr = new Thread() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        String response;
                        //String sarea = "";
                        //Map<String, String> itemselected = new HashMap<>();
                        ArrayList<Map<String, String>> itemData = new ArrayList<>();

                        try {
                            String urlIP = delegate.traerServidor(getApplicationContext());
                            String url = "http://" + urlIP + "/isam/IsamWS/recibe_area.php";
                            //String url = "http://dpsoft.cl/prueba_isam/recibe_area.php";
                            //http://192.168.1.105/isam/IsamWS/index.php

                            if (urlIP.length() > 8) {

                                //enviar archivo via WS
                                JSONArray newArray = new JSONArray();  //getDataEnviar

                                ArrayList<String> list = delegate.getAreaEnviarList(getApplicationContext(), "2");
                                //String[] listVentas = new String[list.size()];
                                System.out.println("SSSSSSSSSSSSSSS "+list);
                                for (int i = 0; i < list.size(); i++){
                                    String paso = list.get(i);
                                    //listVentas[i] = paso;
                                    String[] paso2 = paso.split(";");
                                    String[] paso3 = paso2[0].split(":");
                                    final String sAreaElim = paso3[1].trim();

                                    itemData = delegate.getDataEnviar(getApplicationContext(), sAreaElim);
                                    System.out.println("XXXXXXXXXXXXXXXXXXX "+itemData);
                                    newArray.put(itemData);

                                    JSONObject jsonO = new JSONObject();
                                    jsonO.put("squadName", sAreaElim + ".txt");
                                    jsonO.put("detalle", itemData);

                                    AbstractContent ac = Resty.content(jsonO);

                                    Resty r = new Resty();
                                    //r =  new Resty().json(url, mpc);
                                    r = new Resty().json(url, ac);

                                    //TextResource result = r.text(url, mpc);
                                    TextResource result = r.text(url, ac);
                                    //System.out.println("valores: " + result.toString());
                                    System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRR "+result);

                                    JSONObject jsonObject = new JSONObject(result.toString());
                                    response = jsonObject.getString("resulted");


                                    //if marca = ok entonces marcamos los estados
                                    if (response.compareTo("OK")==0){
                                        String paso4 = delegate.marcarAreaEnviada(getApplicationContext(), sAreaElim);
                                    }
                                }

                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(contexto, "ÁREAS ENVIADAS", Toast.LENGTH_LONG).show();
                                                eliminaArchivosCaptura();
                                                getActualizarLista();
                                            }
                                        });
                            }else{
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(contexto, "Falta el server donde enviar!!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } catch (IOException e) {
                            response = "Sin Conexión WS";
                            Log.e("", response, e);
                            //
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(contexto, "Server de WS NO está disponible!!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } catch (JSONException e) {
                            response = "Problemas en Json part";
                            Log.e("", response, e);

                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(contexto, "No logramos adjuntar bien los datos!!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                };
                tr.start();
                getActualizarLista();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }*/

    public void eliminaArchivosCaptura() {
        try {
            String directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/capturadora/captura";
            File dirCaptura = new File(directorio);
            if (!dirCaptura.mkdirs()) {
                for (String aChildren : dirCaptura.list()) {
                    new File(dirCaptura, aChildren).delete();
                }
            }
        } catch (Exception e) {
            String mensaje = e.getMessage().toUpperCase();
            Log.i("ERROR", mensaje);
        }
    }

    public void sendAreaEnvToWS(View v){
        Spinner spdir = (Spinner) findViewById(R.id.listUserAreaEnviadas);

        String[] paso = spdir.getSelectedItem().toString().split(";");
        String[] paso2 = paso[0].split(":");
        final String sAreaElim = paso2[1].trim();

        if (sAreaElim == null){
            Toast.makeText(UserEnviarAreasActivity.this, "Debes seleccionar un area enviada...", Toast.LENGTH_LONG).show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(UserEnviarAreasActivity.this);
            builder.setTitle("Por favor confirmar.");
            builder.setMessage("Enviaremos Area: " + sAreaElim);

            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Thread tr = new Thread() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run() {
                            String response;
                            //String sarea = "";
                            Map<String, String> itemselected = new HashMap<>();
                            ArrayList<Map<String, String>> itemData = new ArrayList<>();

                            try {
                                String urlIP = delegate.traerServidor(getApplicationContext());
                                //String url = "http://" + urlIP + "/IsamWS/recibe_area.php";
                                String url = "http://" + urlIP + "/isam/IsamWS/recibe_area.php";

                                if (urlIP.length() > 8) {
                                    //enviar archivo via WS
                                    JSONArray newArray = new JSONArray();  //getDataEnviar
                                    itemData = delegate.getDataEnviar(getApplicationContext(), sAreaElim);

                                    newArray.put(itemData);

                                    JSONObject jsonO = new JSONObject();
                                    jsonO.put("squadName", sAreaElim + ".txt");
                                    jsonO.put("detalle", itemData);

                                    AbstractContent ac = Resty.content(String.valueOf(jsonO));

                                    Resty r = new Resty();
                                    //r =  new Resty().json(url, mpc);
                                    r = new Resty().json(url, ac);

                                    //TextResource result = r.text(url, mpc);
                                    TextResource result = r.text(url, ac);
                                    //System.out.println("valores: " + result.toString());

                                    JSONObject jsonObject = new JSONObject(result.toString());
                                    response = jsonObject.getString("resulted");

                                    runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(UserEnviarAreasActivity.this, "Area re-enviada con Exito!!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }else{
                                    runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(UserEnviarAreasActivity.this, "Falta el server donde enviar!!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            } catch (IOException e) {
                                Log.e("", "", e);
                                response = "Sin Conexion WS";
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserEnviarAreasActivity.this, "Server de WS no esta disponible!!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } catch (JSONException e) {
                                Log.e("", "", e);
                                response = "Problemas en Json part";
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserEnviarAreasActivity.this, "No logramos adjuntar bien los datos!!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    };
                    tr.start();
                }
            });
            // Set the alert dialog no button click listener
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(UserEnviarAreasActivity.this, "No gracias...", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            // Display the alert dialog on interface
            dialog.show();
            getActualizarLista();
        }
    }

    private void eliminar(final String mensaje){
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(UserEnviarAreasActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                /*String respustaEliminarAreas = delegar.eliminarArea(contexto, area);
                String mensaje = "";
                if (respustaEliminarAreas.equals("OK")) {
                    mensaje = "ÁREA "+area+" ELIMINADA";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                    edtAreas.setText("");
                    cargarAreas();
                } else {
                    mensaje = "NO SE HA ELIMINADO EL ÁREA "+area;
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                }*/
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
