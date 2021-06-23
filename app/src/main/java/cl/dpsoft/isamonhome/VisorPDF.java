package cl.dpsoft.isamonhome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.Objects;

public class VisorPDF extends AppCompatActivity {

    private PDFView visorPDF;
    private File archivo;
    private ImageButton btnVolver;
    private ConstraintLayout barraSuperior;
    private TextView txtTitulo;
    private Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_pdf);

        contexto = getApplicationContext();
        visorPDF = (PDFView) findViewById(R.id.visorPDF);
        barraSuperior = (ConstraintLayout) findViewById(R.id.miBarraSuperiorVPDF);
        btnVolver = (ImageButton) findViewById(R.id.btnVolverBS);
        txtTitulo = (TextView) findViewById(R.id.txtTituloBS);
        cargarBarraSuperior();
        volver();

        Bundle datos = getIntent().getExtras();
        if(datos != null){
            archivo = new File(Objects.requireNonNull(datos.getString("Ruta")));
        }
        String dato = datos.getString("Ruta");
        System.out.println("RUTA ARCHIVO: "+dato);

        visorPDF.fromFile(archivo)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .load();

    }

    //MÉTODOS
    private void cargarBarraSuperior(){
        String titulo = getString(R.string.reportes);
        txtTitulo.setText(titulo);
    }

    private void volver(){
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                Intent adminMenu = new Intent(contexto, AdminMenuActivity.class);
                startActivity(adminMenu);
            }
        });
    }

}