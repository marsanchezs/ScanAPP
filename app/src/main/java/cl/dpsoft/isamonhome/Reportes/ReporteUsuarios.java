package cl.dpsoft.isamonhome.Reportes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

//import androidx.core.content.ContextCompat;

import cl.dpsoft.isamonhome.VisorPDF;
import cl.dpsoft.isamonhome.main.IsamDelegate;
import cl.dpsoft.isamonhome.Utilidades.Utilidades;
import cl.dpsoft.isamonhome.vo.Usuario;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class ReporteUsuarios {
    private final Context contexto;
    private File archivoPDF;
    private Document documento;
    private PdfWriter pdfEscritor;
    private Paragraph parrafo;
    private final Utilidades utlilidades = new Utilidades();
    private final IsamDelegate delegar = new IsamDelegate();

    public ReporteUsuarios(Context contexto) {
        this.contexto = contexto;
    }

    private void cargarDatos(){
        String fecha = utlilidades.traerFecha();
        System.out.println("FECHA REPORTE USUARIOS: "+fecha);
        //FECHA REPORTE USUARIOS: 16/06/2021 03:23:07
        String[] partesFecha = fecha.split(" ");
        String hora = utlilidades.traerHora();
        String[] encabezadoReporte = {"RUT", "NOMBRE"};
        agregarMetadatos("REPORTE USUARIOS", "Detalle Usuarios", "DPSoft Spa");
        agregarTitulos("REPORTE USUARIOS", "Detalle Usuarios", partesFecha[0], hora);
        //agregarParrafo(textoCorto);
        //agregarParrafo(textLargo);
        ArrayList<Usuario> listaUsuarios = delegar.reporteUsuarios(contexto);
        System.out.println("LISTA REPORTE USUARIOS: "+listaUsuarios.toString());
        crearTabla(encabezadoReporte, listaUsuarios);
    }

    public void abrirDocumentoPDF(){
        crearArchivo();
        try{
            documento = new Document(PageSize.LETTER);
            pdfEscritor = PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
            documento.open();
            cargarDatos();

        }catch(Exception e){
            Log.e("abrirDocumentoPDF", e.toString());
        }
    }

    private void crearArchivo(){
        File ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File carpeta = new File(ruta.getAbsolutePath(), "ReportesScanAPP");
        if(!carpeta.exists())
            carpeta.mkdirs();
        archivoPDF = new File(carpeta, "ReporteUsuarios.pdf");
    }

    public void cerrarDocumento(){
        documento.close();
    }


    public void agregarMetadatos(String titulo, String tema, String autor){
        documento.addTitle(titulo);
        documento.addSubject(tema);
        documento.addAuthor(autor);
    }

    public void agregarTitulos(String titulo, String subTitulo, String fecha, String hora){
        try{
            parrafo = new Paragraph();
            parrafo.setIndentationLeft(10);
            PdfPTable tablaEncabezado = new PdfPTable(3);
            tablaEncabezado.setWidthPercentage(100);
            tablaEncabezado.setWidths(new int[]{30, 80, 40});
            PdfPCell celda;
            Image imagen = utlilidades.traerLogo(contexto);
            celda = new PdfPCell(imagen, true);
            //celda.setPadding(10);
            celda.setRowspan(2);
            celda.setFixedHeight(70);
            tablaEncabezado.addCell(celda);
            celda = new PdfPCell(new Phrase(titulo, LetrasReportes.letraTituloReporte));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setRowspan(2);
            celda.setBorder(Rectangle.NO_BORDER);
            celda.setPaddingLeft(10f);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablaEncabezado.addCell(celda);
            celda = new PdfPCell(new Phrase(fecha, LetrasReportes.letraReporte));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setBorder(Rectangle.NO_BORDER);
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_BOTTOM);
            celda.setPaddingRight(10f);
            tablaEncabezado.addCell(celda);
            /*celda = new PdfPCell(new Phrase(subTitulo, LetrasReportes.letraReporte));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setBorder(Rectangle.NO_BORDER);
            celda.setPaddingLeft(10f);
            tablaEncabezado.addCell(celda);*/
            celda = new PdfPCell(new Phrase(hora, LetrasReportes.letraReporte));
            celda.setBackgroundColor(BaseColor.DARK_GRAY);
            celda.setBorder(Rectangle.NO_BORDER);
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setPaddingRight(10f);
            tablaEncabezado.addCell(celda);
            parrafo.setAlignment(Element.ALIGN_CENTER);
            parrafo.add(tablaEncabezado);
            parrafo.setSpacingAfter(30);
            documento.add(parrafo);
        }catch(Exception e){
            Log.e("agregarTitulo", e.toString());
        }
    }

    public void crearTabla(String[] encabezado, ArrayList<Usuario> usuarios){

        try{

            parrafo = new Paragraph();
            parrafo.setFont(LetrasReportes.letraNormal);
            parrafo.setIndentationLeft(10);
            PdfPTable tablaPDF = new PdfPTable(encabezado.length);
            tablaPDF.setWidths(new int[]{40, 100});
            tablaPDF.setWidthPercentage(100);
            PdfPCell celda;
            int indiceColumna = 0;
            while(indiceColumna < encabezado.length){
                celda = new PdfPCell(new Phrase(encabezado[indiceColumna++], LetrasReportes.letraCabeceraTabla));
                //celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setBackgroundColor(BaseColor.GRAY);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setPaddingLeft(10f);
                celda.setFixedHeight(40);
                tablaPDF.addCell(celda);
            }

            int total = 0;
            for(int indiceFila = 0; indiceFila < usuarios.size(); indiceFila++){
                String rut = usuarios.get(indiceFila).getRut();
                String nombre = usuarios.get(indiceFila).getsNombre();
                //String genero = usuarios.get(indiceFila).getRut();
                total = total + 1;
                String[] filaUsuario = {rut.toUpperCase(), nombre.toUpperCase()};
                for(int indiceColumnas = 0; indiceColumnas < encabezado.length; indiceColumnas++){
                    celda = new PdfPCell(new Phrase(filaUsuario[indiceColumnas], LetrasReportes.letraDatosTabla));
                    //celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                    //celda.setUseAscender(true);
                    celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda.setPaddingLeft(10f);
                    celda.setFixedHeight(40);
                    tablaPDF.addCell(celda);

                }
            }

            parrafo.add(tablaPDF);
            documento.add(parrafo);

            Paragraph parrafoPie = new Paragraph();
            parrafoPie.setIndentationLeft(10);
            PdfPTable tablaPie = new PdfPTable(2);
            tablaPie.setWidths(new int[]{130, 40});
            tablaPie.setWidthPercentage(100);
            PdfPCell celdaPie;
            celdaPie = new PdfPCell(new Phrase("TOTAL USUARIOS", LetrasReportes.letraCabeceraTabla));
            celdaPie.setFixedHeight(40);
            celdaPie.setPaddingLeft(10f);
            celdaPie.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaPie.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablaPie.addCell(celdaPie);
            celdaPie = new PdfPCell(new Phrase(String.valueOf(total), LetrasReportes.letraCabeceraTabla));
            celdaPie.setFixedHeight(40);
            celdaPie.setPaddingRight(10f);
            celdaPie.setBackgroundColor(BaseColor.GRAY);
            celdaPie.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaPie.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablaPie.addCell(celdaPie);
            parrafoPie.add(tablaPie);
            documento.add(parrafoPie);

        }catch(Exception e){
            Log.e("crearTabla", e.toString());
        }
    }

    public void verPDF(){
        Bundle datos = new Bundle();
        datos.putString("Ruta", archivoPDF.getAbsolutePath());
        Intent intento = new Intent(contexto, VisorPDF.class);
        //intento.putExtra("Ruta", archivoPDF.getAbsolutePath());
        intento.putExtras(datos);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contexto.startActivity(intento);
    }
}
