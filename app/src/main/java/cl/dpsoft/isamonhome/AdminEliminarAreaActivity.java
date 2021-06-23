package cl.dpsoft.isamonhome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AdminEliminarAreaActivity extends AppCompatActivity {

    private Context contexto;
    private AutoCompleteTextView edtAreas;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private final IsamDelegate delegar = new IsamDelegate();
    private final Utilidades utilidades = new Utilidades();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_eliminar_area);

        contexto = getApplicationContext();
        barraSuperior = (ConstraintLayout) findViewById(R.id.barraSuperiorEA);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);
        edtAreas = (AutoCompleteTextView) findViewById(R.id.edtAreasAEA);
        edtAreas.requestFocus();

        cargarBarraSuperior();
        //cargar combo
        //actualizarComboAreas();
        cargarAreas();
        volver();
        utilidades.ocultarTeclado(contexto, edtAreas);
        //elimArea();
    }

    //MËTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.eliminarArea);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void cargarAreas(){
        ArrayList<String> listaAreas = delegar.traerAreas(contexto);
        //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaAreas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, R.layout.lista_desplegable,
                R.id.txt, listaAreas);
        edtAreas.setAdapter(adaptador);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void actualizarComboAreas(){
        ArrayList<String> list = delegar.obtenerAreasEliminar(getApplicationContext());
        ///////////////
        //ArrayList<String> list = delegate.getAreaEnviarList(getApplicationContext(), "2");
        //delegate.getAreasRegistradas(getApplicationContext());

        String[] lisAreas = new String[list.size()];
        for (int i = 0; i < list.size(); i++){
            String paso = list.get(i);
            lisAreas[i] = paso;
        }
        if (lisAreas.length == 0){
            lisAreas = new String[]{"Sin Areas creadas aun ; 0"};
        }

        Spinner spAreas = (Spinner) findViewById(R.id.spAreas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lisAreas);
        spAreas.setAdapter(adaptador);
        edtAreas.setAdapter(adaptador);
    }

    private void elimArea(){
        try{
            edtAreas.setOnKeyListener(new View.OnKeyListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public boolean onKey(View view, int i, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                        eliminarArea(null);
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
    public void eliminarArea(View vista){
        String area = edtAreas.getText().toString();
        if(area.isEmpty()){
            String mensaje = "DEBE INGRESAR UN ÁREA";
            utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
            edtAreas.requestFocus();
        }else{

            String respuestaTraerArea = delegar.traerArea(contexto, area);
            String mensaje = "";
            if(respuestaTraerArea.equals("EXISTE")){

                String texto = "ÁREA:";
                boolean resultado = area.contains(texto);
                String respuesta = String.valueOf(resultado);
                //utilidades.mostrarMensajePersonalizado(contexto, respuesta, "OK");
                if(resultado){
                    String[] partesArea = area.split(" ");
                    String areaEliminar = partesArea[1];
                    eliminar(areaEliminar);
                }else{
                    eliminar(area);
                }

            }else if(respuestaTraerArea.equals("NO EXISTE")){

                String texto = "ÁREA:";
                boolean resultado = area.contains(texto);
                if(resultado){
                    String[] partesArea = area.split(" ");
                    mensaje = "EL ÁREA "+partesArea[1]+" NO EXISTE";
                    //String areaEliminar = partesArea[1];
                    //eliminar(areaEliminar);
                }else{
                    mensaje = "EL ÁREA "+area+" NO EXISTE";
                    //eliminar(area);
                }
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            }else{
                mensaje = respuestaTraerArea;
                utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
            }
        }
    }

    private void eliminar(final String area){
        final AlertDialog dialogoPrecaucion;
        final AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(AdminEliminarAreaActivity.this);
        LayoutInflater inflador = getLayoutInflater();
        View vistaDialogo = inflador.inflate(R.layout.dialogo_precaucion,null);
        constructorDialogo.setView(vistaDialogo);
        dialogoPrecaucion = constructorDialogo.create();
        TextView txvMensaje = (TextView) vistaDialogo.findViewById(R.id.txtMensajeDP);
        ImageButton btnConfirmar = (ImageButton) vistaDialogo.findViewById(R.id.btnConfirmarDP);
        ImageButton btnCancelar = (ImageButton) vistaDialogo.findViewById(R.id.btnCancelarDP);
        final String mensaje = "¿ESTÁ SEGURO DE ELIMINAR EL ÁREA "+area.toUpperCase()+"?";
        txvMensaje.setText(mensaje);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String respustaEliminarAreas = delegar.eliminarArea(contexto, area);
                String mensaje = "";
                if (respustaEliminarAreas.equals("OK")) {
                    mensaje = "ÁREA "+area+" ELIMINADA";
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "OK");
                    //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                    edtAreas.setText("");
                    cargarAreas();
                    edtAreas.requestFocus();
                } else {
                    mensaje = "NO SE HA ELIMINADO EL ÁREA "+area;
                    utilidades.mostrarMensajePersonalizado(contexto, mensaje, "NOK");
                    //Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                    edtAreas.requestFocus();
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
}
