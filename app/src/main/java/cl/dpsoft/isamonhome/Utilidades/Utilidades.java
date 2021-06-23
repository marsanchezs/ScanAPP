package cl.dpsoft.isamonhome.Utilidades;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
//import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cl.dpsoft.isamonhome.R;

public class Utilidades {

    private Toast mToastToShow;

    public void ocultarTeclado(Context contexto, EditText edt){
        InputMethodManager adminMetodoEntrada = (InputMethodManager)contexto.getSystemService(Context.INPUT_METHOD_SERVICE);
        adminMetodoEntrada.hideSoftInputFromWindow(edt.getWindowToken(), 0);
    }

    public Image traerLogo(Context contexto) throws IOException, BadElementException {
        Drawable dibujo = ContextCompat.getDrawable(contexto, R.drawable.logo2);
        BitmapDrawable bitmapDibujo = ((BitmapDrawable) dibujo);
        assert bitmapDibujo != null;
        Bitmap bmp = bitmapDibujo.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Image.getInstance(stream.toByteArray());
    }

    public String traerFecha(){
        String fecha = "SIN FECHA";
        Date dFecha = new Date();
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss" , Locale.GERMAN);
        fecha = sdfFecha.format(dFecha);
        return fecha;
    }

    public String traerHora() {
        String sHora = "SIN HORA";
        Date date = new Date();
        SimpleDateFormat horaF = new SimpleDateFormat("H:mm a", java.util.Locale.getDefault());
        sHora =  horaF.format(date);
        return sHora;
    }

    public void mostrarMensajePersonalizado(Context contexto, String msj, String tipoIcono) {
        Toast mensaje = new Toast(contexto);
        LayoutInflater inflador = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vistaMensaje = inflador.inflate( R.layout.mensaje_personalizado, null );
        mensaje.setView(vistaMensaje);
        ImageView imgIcono = (ImageView) vistaMensaje.findViewById(R.id.imgMP);
        if(tipoIcono.equals("OK")){
            imgIcono.setImageResource(R.drawable.icono_aceptar);
        }else{
            imgIcono.setImageResource(R.drawable.icono_cancelar2);
        }
        TextView txtMensaje = (TextView) vistaMensaje.findViewById(R.id.txtM1MP);
        txtMensaje.setText(msj);
        mensaje.setDuration(Toast.LENGTH_LONG);
        mensaje.show();
    }

    public boolean esNumero(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
