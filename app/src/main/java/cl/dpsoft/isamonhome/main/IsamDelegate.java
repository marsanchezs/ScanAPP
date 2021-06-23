package cl.dpsoft.isamonhome.main;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.dpsoft.isamonhome.MaestraSQLiteOpenHelper;
import cl.dpsoft.isamonhome.vo.Producto;
import cl.dpsoft.isamonhome.vo.Usuario;

public class IsamDelegate {

    private static final String NOMBRE_BBDD = IsamConstantes.NOMBRE_BBDD;
    private static final int VERSION_BBDD = IsamConstantes.VERSION_BBDD;
    //private static String NOMBRETABLA = "Maestra";
    //private static final String NOMBRE_BBDD = "Maestra";
    //private static final int VERSION_BBDD = 1;

    public IsamDelegate(){ }

    //ADMINISTRADOR
    public ArrayList<String> traerUsuarios(Context contexto) {
        ArrayList<String> listaUsuarios = new ArrayList<String>();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta = "SELECT RUT, USUARIO FROM USUARIO";
        Log.e("CONSULTA: ", consulta);
        @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
        String valor;
        if (fila.getCount() > 0) {
            while (fila.moveToNext()) {
                valor = fila.getString(0) + " | " + fila.getString(1);
                listaUsuarios.add(valor);
            }
        }else{
            valor = "SIN USUARIOS";
            listaUsuarios.add(valor);
        }
        return listaUsuarios;
    }

    public Usuario validarUsuario(Context contexto, String rut){
        Usuario usuario = new Usuario();
        boolean conocido;
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta = "SELECT 1, USUARIO AS OTRO FROM USUARIO WHERE RUT ='" + rut.trim() + "'";
        Log.e("CONSULTA: ", consulta);
        try{
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                fila.moveToFirst();
                conocido = true;
                usuario.setbExiste(conocido);
                usuario.setsNombre(fila.getString(fila.getColumnIndex("OTRO")));
            } else {
                conocido = false;
                usuario.setbExiste(conocido);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR: ", e.getMessage().toUpperCase());
        }finally {
            bd.close();
        }
        return usuario;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String agregarUsuario(Context contexto, String rut, String nombre){
        String respuesta = "OK";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            ContentValues cvUsuario = new ContentValues();
            cvUsuario.put("rut", rut);
            cvUsuario.put("usuario", nombre);
            bd.insert("usuario", null, cvUsuario);
            System.out.println("DATOS USUARIO: " + cvUsuario);

        } catch (Exception e) {
            e.printStackTrace();
            String mensaje = "NO SE GRABÓ EL USUARIO EN LA BASE DE DATOS";
            Log.e("USUARIO", mensaje, e);
            respuesta = mensaje;
        }
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String traerArea(Context contexto, String area){
        String respuesta = "";

        String texto = "ÁREA:";
        boolean resultado = area.contains(texto);
        String areaEliminar = "";
        if(resultado){
            String[] partesArea = area.split(" ");
            areaEliminar = partesArea[1];
        }else{
            areaEliminar = area;
        }

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT PATENTE FROM CAPTURA WHERE PATENTE ='" + areaEliminar + "'";
            Cursor fila = bd.rawQuery(consulta, null);
            Log.i("CONSULTA: ", consulta);
            if (fila.getCount() > 0) {
                //fila.move(1);
                fila.moveToFirst();
                respuesta = "EXISTE";
            }else{
                respuesta = "NO EXISTE";
            }
            fila.close();

        } catch (Exception e) {
            respuesta = "ERROR: "+e.getMessage();
            Log.e("ERROR", respuesta);
        }
        return  respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String eliminarArea(Context contexto, String area){
        String respuesta;
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String condicion = "patente ='" + area + "'";
            Log.e("CONDICIÓN: ", condicion);
            bd.delete("CAPTURA", condicion, null);
            respuesta = "OK";
        } catch (Exception e) {
            respuesta = "NOK";
            String mensaje = "NO SE ELIMINÓ EL ÁREA: "+area;
            Log.e("ERROR", mensaje, e);
        }
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String eliminarCapturaYUsuario(Context contexto){
        String respuesta;
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            bd.delete("CAPTURA", null, null);
            bd.delete("USUARIO", null, null);
            respuesta = "OK";
        } catch (Exception e) {
            respuesta = "NOK";
            String mensaje = "NO SE ELIMINÓ LA CAPTURA Y/O USUARIO";
            Log.e("ERROR", mensaje, e);
        }
        return respuesta;
    }

    public StringBuilder escribirContenidoArchivoTXTArea(Context contexto, String area){
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        StringBuilder contenido = new StringBuilder();
        String consulta = "select PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION " +
                "from CAPTURA where PATENTE = '" + area + "'";

        try{
            Log.e("CONSULTA: ", consulta);
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                while (fila.moveToNext()){
                    contenido.append(fila.getString(fila.getColumnIndex("PATENTE")));
                    contenido.append("|");
                    contenido.append(fila.getString(fila.getColumnIndex("EAN")));
                    contenido.append("|");
                    contenido.append(fila.getString(fila.getColumnIndex("CANTIDAD")));
                    contenido.append("|");
                    String sfecha = fila.getString(fila.getColumnIndex("FECHA"));
                    String[] fecha = sfecha.split(" ");
                    contenido.append(fecha[1]);
                    contenido.append("|");
                    contenido.append(fila.getString(fila.getColumnIndex("RUT")));
                    contenido.append("|");
                    //contenido.append(fila.getString(fila.getColumnIndex("UBICACION")));
                    String ubicacion = fila.getString(fila.getColumnIndex("UBICACION"));
                    if(ubicacion.equals("SIN UBICACIÓN")){
                        ubicacion = "";
                    }
                    contenido.append(ubicacion);
                    contenido.append("\r\n");
                }
            }
            ContentValues cvCaptura = new ContentValues();
            cvCaptura.put("ESTADO", 2);
            String condicion = "PATENTE ='" + area + "'";
            System.out.println("CONTENT VALUES CAPTURA: "+cvCaptura);
            System.out.println("CONDICIÓN WHERE: "+condicion);
            bd.update("CAPTURA", cvCaptura, condicion, null);

        }catch (Exception e){
            e.printStackTrace();
            //resultado = "problemas al completar el contenido";
        }finally {
            bd.close();
        }
        return contenido;
    }

    public int traerUnidades(Context contexto, String area){
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        StringBuilder contenido = new StringBuilder();
        String consulta = "select PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION from CAPTURA where PATENTE = '" + area + "'";
        int cantidad = 0;
        try{
            Log.e("CONSULTA: ", consulta);
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                while (fila.moveToNext()){

                    int unidades = Integer.parseInt(fila.getString(2));
                    cantidad = cantidad + unidades;

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
        }
        return cantidad;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String guardarCaptura(Context contexto, ContentValues cvCaptura){
        String respuesta = "";
        MaestraSQLiteOpenHelper adm = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);

        try (SQLiteDatabase bd = adm.getWritableDatabase()) {
            bd.insert("CAPTURA", null, cvCaptura);
            System.out.println("DATOSCAPTURA: " + cvCaptura);
            respuesta = "OK";
        } catch (Exception e) {
            String mensaje = "NO SE GRABÓ LA CAPTURA EN LA BASE DE DATOS";
            Log.e("ERROR CAPTURA", mensaje, e);
            respuesta = mensaje;
        }
        return respuesta;
    }

    public Producto getProductoScan(Context contexto, String idCaptura){
        Producto prod = new Producto();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        int encontrado = 0;
        try{
            Cursor fila = bd.rawQuery("select EAN, DESCRIPCION, SKU from MAESTRA where EAN ='" + idCaptura + "' ", null);
            //Cursor fila = bd.rawQuery("select EAN, DESCRIPCION, SKU from MAESTRA where EAN ='" + idCaptura + "' ", null);
            //Cursor fila = bd.rawQuery("select idprod, patente, ean, cantidad, descripcion, ubicacion from CAPTURA where _id = 12", null);

            if (fila.getCount()>0) {
                //fila.move(1);
                fila.moveToFirst();
                prod.setCodigo(fila.getString(fila.getColumnIndex("EAN")));
                prod.setIdprod(fila.getString(fila.getColumnIndex("EAN")));
                prod.setPatente("");
                prod.setSku(fila.getString(fila.getColumnIndex("SKU")));
                prod.setCantidad("")  ;
                prod.setDescripcion(fila.getString(fila.getColumnIndex("DESCRIPCION")));
                prod.setUbicacion("");
                prod.setEncontrado(1);
                encontrado = 1;
            }else{
                Cursor fila2 = bd.rawQuery("select EAN, DESCRIPCION, SKU from MAESTRA where SKU ='" + idCaptura + "' ", null);
                if (fila2.getCount()>0) {
                    fila2.moveToFirst();
                    prod.setCodigo(fila2.getString(fila2.getColumnIndex("EAN")));
                    prod.setIdprod(fila2.getString(fila2.getColumnIndex("EAN")));
                    prod.setPatente("");
                    prod.setSku(fila2.getString(fila2.getColumnIndex("SKU")));
                    prod.setCantidad("")  ;
                    prod.setDescripcion(fila2.getString(fila2.getColumnIndex("DESCRIPCION")));
                    prod.setUbicacion("");
                    prod.setEncontrado(1);
                    encontrado = 1;
                }
                fila2.close();
            }
            fila.close();

            if (encontrado == 0){
                prod.setEncontrado(0);
            }

        }catch (Exception e){
            Log.e("","Problemas en la", e);
        }finally {
            bd.close();
        }
        return  prod;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Producto traerProducto(Context contexto, String codigo){
        Producto prod = new Producto();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            int encontrado = 0;
            String consultaEan = "SELECT EAN, DESCRIPCION, SKU FROM MAESTRA WHERE EAN ='" + codigo + "'";
            String consultaSku = "SELECT EAN, DESCRIPCION, SKU FROM MAESTRA WHERE SKU ='" + codigo + "'";
            Cursor fila = bd.rawQuery(consultaEan, null);
            Log.i("CONSULTA EAN: ", consultaEan);
            if (fila.getCount() > 0) {
                //fila.move(1);
                fila.moveToFirst();
                prod.setCodigo(fila.getString(fila.getColumnIndex("EAN")));
                prod.setIdprod(fila.getString(fila.getColumnIndex("EAN")));
                prod.setPatente("");
                prod.setSku(fila.getString(fila.getColumnIndex("SKU")));
                prod.setCantidad("");
                prod.setDescripcion(fila.getString(fila.getColumnIndex("DESCRIPCION")));
                prod.setUbicacion("");
                prod.setEncontrado(1);
                encontrado = 1;
            } else {
                Cursor fila2 = bd.rawQuery(consultaSku, null);
                Log.i("CONSULTA SKU: ", consultaSku);
                if (fila2.getCount() > 0) {
                    fila2.moveToFirst();
                    prod.setCodigo(fila2.getString(fila2.getColumnIndex("EAN")));
                    prod.setIdprod(fila2.getString(fila2.getColumnIndex("EAN")));
                    prod.setPatente("");
                    prod.setSku(fila2.getString(fila2.getColumnIndex("SKU")));
                    prod.setCantidad("");
                    prod.setDescripcion(fila2.getString(fila2.getColumnIndex("DESCRIPCION")));
                    prod.setUbicacion("");
                    prod.setEncontrado(1);
                    encontrado = 1;
                }
                fila2.close();
            }
            fila.close();

            if (encontrado == 0) {
                prod.setEncontrado(0);
            }

        } catch (Exception e) {
            Log.e("", "Problemas en la", e);
        }
        return  prod;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Producto traerCaptura(Context contexto, String id){
        Producto prod = new Producto();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT IDPROD, PATENTE, EAN, CANTIDAD, DESCRIPCION, UBICACION " +
                    "FROM CAPTURA WHERE _ID = " + id;
            Log.i("ID CAPTURA: ", id);
            Cursor fila = bd.rawQuery(consulta, null);
            Log.i("CONSULTA: ", consulta);
            if (fila.getCount() > 0) {
                fila.moveToFirst();
                prod.setCodigo(fila.getString(fila.getColumnIndex("IDPROD")));
                prod.setIdprod(fila.getString(fila.getColumnIndex("IDPROD")));
                prod.setPatente(fila.getString(fila.getColumnIndex("PATENTE")));
                prod.setSku(fila.getString(fila.getColumnIndex("EAN")));
                prod.setCantidad(fila.getString(fila.getColumnIndex("CANTIDAD")));
                prod.setDescripcion(fila.getString(fila.getColumnIndex("DESCRIPCION")));
                prod.setUbicacion(fila.getString(fila.getColumnIndex("UBICACION")));
            }
            fila.close();

        } catch (Exception e) {
            String mensaje = "EL PROBLEMA ES: " + e.getMessage().toUpperCase();
            Log.i("ERROR", mensaje);
        }
        return  prod;
    }

    public Producto getProductoAEditar(Context contexto, String idCaptura){
        Producto prod = new Producto();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Log.i("Valor a consultar", idCaptura);
        try{
            Cursor fila = bd.rawQuery("select idprod, patente, ean, cantidad, descripcion, ubicacion from CAPTURA where _id = " + idCaptura, null);
            //Cursor fila = bd.rawQuery("select idprod, patente, ean, cantidad, descripcion, ubicacion from CAPTURA where _id = 12", null);

            if (fila.getCount()>0) {
                //fila.move(1);
                fila.moveToFirst();
                prod.setCodigo(fila.getString(fila.getColumnIndex("IDPROD")));
                prod.setIdprod(fila.getString(fila.getColumnIndex("IDPROD")));
                prod.setPatente(fila.getString(fila.getColumnIndex("PATENTE")));
                prod.setSku(fila.getString(fila.getColumnIndex("EAN")));
                prod.setCantidad(fila.getString(fila.getColumnIndex("CANTIDAD")))  ;
                prod.setDescripcion(fila.getString(fila.getColumnIndex("DESCRIPCION")));
                prod.setUbicacion(fila.getString(fila.getColumnIndex("UBICACION")));
            }
            fila.close();

        }catch (Exception e){
            Log.e("","Problemas en la", e);
        }finally {
            bd.close();
        }
        return  prod;
    }

    public String getUbicacionesXAreas (Context contexto, String sArea){
        String strAreasCant = "1";

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select count(1) as cant from maestra where sku ='"  + sArea + "'", null);
            //strAreasReg = new String[fila.getCount()];

            if (fila.getCount()>0) {
                int i =0;
                fila.moveToFirst();

                strAreasCant = fila.getString(fila.getColumnIndex("cant"))  ;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
        }

        return strAreasCant;
    }

    public int actualizaCantidadProd(Context contexto, String idCaptura, String newCant){
        int cuantosAfecte = 0;

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd2 = admin.getWritableDatabase();

        try{

            ContentValues registro2 = new ContentValues();
            registro2.put("CANTIDAD", newCant);
            cuantosAfecte = bd2.update("CAPTURA", registro2, "_ID =" + idCaptura, null);

        }catch (Exception e){
            //e.printStackTrace();
            Log.e("","Actualizar Cantidad", e);
        }finally {
            bd2.close();
        }
        return  cuantosAfecte;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String modificarCantidad(Context contexto, String idCaptura, String cantidad){
        String respuesta = "";
        String condicion = "_ID = " + idCaptura;
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            Log.i("CONDICIÓN", condicion);
            ContentValues cvCantidad = new ContentValues();
            cvCantidad.put("CANTIDAD", cantidad);
            System.out.println("CONTENT VALUES CANTIDAD: " + cvCantidad);
            bd.update("CAPTURA", cvCantidad, condicion, null);
            respuesta = "OK";
        } catch (Exception e) {
            String mensaje = e.getMessage().toUpperCase();
            Log.i("ERROR", mensaje);
            respuesta = mensaje;
        }
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String modificarUbicacion(Context contexto, String idCaptura, String ubicacion) {
        String respuesta = "";
        String condicion = "_ID = " + idCaptura;
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            Log.i("CONDICIÓN", condicion);
            ContentValues cvUbicacion = new ContentValues();
            cvUbicacion.put("UBICACION", ubicacion);
            System.out.println("CONTENT VALUES UBICACIÓN: " + cvUbicacion);
            bd.update("CAPTURA", cvUbicacion, condicion, null);
            respuesta = "OK";
        } catch (Exception e) {
            String mensaje = e.getMessage().toUpperCase();
            Log.i("ERROR", mensaje);
            respuesta = mensaje;
        }
        return respuesta;
    }

    public int eliminarProducto(Context contexto, String idCaptura){
        int cuantosAfecte = 0;

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd2 = admin.getWritableDatabase();

        try{

            cuantosAfecte = bd2.delete("CAPTURA", "_ID =" + idCaptura,null);

        }catch (Exception e){
            //e.printStackTrace();
            Log.e("","Eliminar Producto", e);
        }finally {
            bd2.close();
        }
        return  cuantosAfecte;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String eliminarCaptura(Context contexto, String idCaptura, String area, String estado){
        String respuesta = "";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String condicion = "_ID = " + idCaptura + " AND PATENTE = " + area + " AND ESTADO = " + estado;
            bd.delete("CAPTURA", condicion, null);
            respuesta = "OK";
        } catch (Exception e) {
            respuesta = e.getMessage().toUpperCase();
            Log.i("ERROR", respuesta);
        }
        return respuesta;
    }

    public int actualizarUbicacion(Context contexto, String sArea, String sNewUbic, String sOldUbic){
        int cuantosAfecte = 0;

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd2 = admin.getWritableDatabase();

        try{

            ContentValues registro2 = new ContentValues();
            registro2.put("UBICACION", sNewUbic);
            cuantosAfecte = bd2.update("CAPTURA", registro2, "PATENTE ='" + sArea + "' AND UBICACION = '" + sOldUbic + "'", null);

        }catch (Exception e){
            //e.printStackTrace();
            Log.e("","Actualizar Cantidad", e);
        }finally {
            bd2.close();
        }

        return cuantosAfecte;
    }

    public int actualizarUbicacionUnica(Context contexto, String sArea, String sNewUbic, String sEan){
        int cuantosAfecte = 0;

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd2 = admin.getWritableDatabase();

        try{

            Log.e("query: ", "UPDATE CAPTURA SET UBICACION = '"+sNewUbic+"' WHERE PATENTE ='" + sArea + "' AND EAN = '" + sEan + "'");

            ContentValues registro2 = new ContentValues();
            registro2.put("UBICACION", sNewUbic);
            cuantosAfecte = bd2.update("CAPTURA", registro2, "PATENTE ='" + sArea + "' AND IDPROD = '" + sEan + "'", null);
            //bd2.execSQL("UPDATE CAPTURA SET UBICACION = '"+sNewUbic+"' WHERE PATENTE ='" + sArea + "' AND EAN = '" + sEan + "'");
            System.out.println(sArea+" QQ "+sEan+" QQ "+sNewUbic);
            //cuantosAfecte = bd2.update("CAPTURA", registro2, "PATENTE ='" + sArea + "' AND UBICACION = '" + sOldUbic + "'", null);

        }catch (Exception e){
            //e.printStackTrace();
            Log.e("","Actualizar Cantidad", e);
        }finally {
            bd2.close();
        }

        return cuantosAfecte;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String traerServidor(Context contexto){
        String servidor = "";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT SERVER FROM SERVER1";
            Log.e("CONSULTA: ", consulta);
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                fila.moveToFirst();
                servidor = fila.getString(fila.getColumnIndex("SERVER"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return servidor;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String actualizarServidor(Context contexto, String servidor){
        String respuesta = "";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            ContentValues cvServidor = new ContentValues();
            cvServidor.put("SERVER", servidor);
            bd.update("SERVER1", cvServidor, null, null);
            respuesta = "OK";
        } catch (Exception e) {
            respuesta = "NO SE ACTUALIZÓ EL SERVIDOR";
            e.printStackTrace();
            Log.e("ERROR", respuesta, e);
        }
        return respuesta;
    }

    public ArrayList<String> getAreasToRevision(Context contexto, String sArea){
        ArrayList<String> respuesta = new ArrayList<>();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String consulta = "select _id as id from CAPTURA where patente = " + sArea.trim() + " order by _id";
        try{
            Cursor fila = bd.rawQuery(consulta, null);

            respuesta = new ArrayList<String>(fila.getCount());

            if (fila.getCount()>0) {
                int i = 0;
                //fila.moveToFirst();
                while (fila.moveToNext()){

                    String gg = fila.getString(fila.getColumnIndex("id") );
                    gg = gg +";";
                    respuesta.add(gg);
                    i++;
                    Log.i("valores: " , gg);
                }
            }
            fila.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
        }
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> traerCapturas(Context contexto, String area){
        ArrayList<String> listaCapturas = new ArrayList<>();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT _id AS id FROM CAPTURA " +
                    "WHERE PATENTE = " + area.trim() + " ORDER BY _id";
            Log.e("CONSULTA: ", consulta);
            Cursor fila = bd.rawQuery(consulta, null);
            listaCapturas = new ArrayList<>(fila.getCount());

            if (fila.getCount() > 0) {
                int i = 0;
                while (fila.moveToNext()) {
                    String capturas = fila.getString(fila.getColumnIndex("id"));
                    //gg = gg +";";
                    listaCapturas.add(capturas);
                    i++;
                    Log.i("CAPTURAS: ", capturas);
                }
            } else {
                listaCapturas.add("SIN CAPTURAS");
            }
            fila.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaCapturas;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Producto traerCaptura(Context contexto, String id, String area, String estado){
        Producto prod = new Producto();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT _ID, IDPROD, PATENTE, EAN, CANTIDAD, DESCRIPCION, UBICACION " +
                    "FROM CAPTURA WHERE _ID = " + id+" AND PATENTE = "+area+" AND ESTADO = "+estado+
                    " ORDER BY _ID DESC";
            Log.i("ID CAPTURA: ", id);
            Cursor fila = bd.rawQuery(consulta, null);
            Log.i("CONSULTA: ", consulta);
            if (fila.getCount() > 0) {
                fila.moveToFirst();
                prod.setIdCaptura(fila.getString(fila.getColumnIndex("_ID")));
                prod.setCodigo(fila.getString(fila.getColumnIndex("IDPROD")));
                prod.setIdprod(fila.getString(fila.getColumnIndex("IDPROD")));
                prod.setPatente(fila.getString(fila.getColumnIndex("PATENTE")));
                prod.setSku(fila.getString(fila.getColumnIndex("EAN")));
                prod.setCantidad(fila.getString(fila.getColumnIndex("CANTIDAD")));
                prod.setDescripcion(fila.getString(fila.getColumnIndex("DESCRIPCION")));
                prod.setUbicacion(fila.getString(fila.getColumnIndex("UBICACION")));
            }
            fila.close();

        } catch (Exception e) {
            String mensaje = "EL PROBLEMA ES: " + e.getMessage().toUpperCase();
            Log.i("ERROR", mensaje);
        }
        return  prod;





        /*String respuesta = "";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT _id AS id FROM CAPTURA " +
                    "WHERE IDPROD = "+idProd+"+ AND PATENTE = " + area.trim() + " AND EAN = "+ean+" " +
                    "AND CANTIDAD = "+cantidad+" AND RUT = "+rut+" AND ESTADO = "+estado+" " +
                    "AND DESCRIPCION = "+descripcion+" AND UBICACION = "+ubicacion+" ORDER BY _id";
            Log.e("CONSULTA: ", consulta);
            Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                respuesta = fila.getString(fila.getColumnIndex("id"));
            } else {
                respuesta = "CAPTURA SIN ID";
            }
            fila.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respuesta;*/
    }

    public String marcarAreaEnviada(Context contexto, String sArea){
        String respuesta = "OK";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();

        try{
            ContentValues registro3 = new ContentValues();
            registro3.put("ESTADO", 3);
            int res = bd.update("CAPTURA", registro3, "PATENTE ='" + sArea + "'", null);
            System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWW "+res);

            respuesta = "OK";

        }catch (Exception e){
            respuesta = "NOK";
            Log.e("", "", e);
        }finally {
            bd.close();
        }
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String cambiarEstadoArea(Context contexto, String area){
        String respuesta = "OK";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String condicion = "PATENTE ='" + area + "'";
            ContentValues cvEstado = new ContentValues();
            cvEstado.put("ESTADO", 3);
            bd.update("CAPTURA", cvEstado, condicion, null);
            System.out.println("CONTENT VALUES ESTADO: " + cvEstado);
            System.out.println("CONDICIÓN: " + condicion);
            respuesta = "OK";
        } catch (Exception e) {
            respuesta = e.getMessage().toUpperCase();
            Log.i("ERROR", respuesta);
        }
        return respuesta;
    }

    public ArrayList<String> getAreaEnviarList(Context contexto, String sEstado){
        ArrayList<String> respuesta = new ArrayList<String>(1);

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            /*Cursor fila = bd.rawQuery("select patente, count(distinct ubicacion) as totUbicacion, count(1) as totProd" +
                    " from CAPTURA where estado = 2  group by patente", null);*/

            Cursor fila = bd.rawQuery("select patente, count(distinct ubicacion) as totUbicacion, count(1) as totProd" +
                    " from CAPTURA where estado = " + sEstado + " group by patente order by PATENTE desc", null);

            respuesta = new ArrayList<String>(fila.getCount());

            if (fila.getCount()>0) {
                int i = 0;
                //fila.moveToFirst(); order by FECHA desc
                while (fila.moveToNext()){

                    String gg = "Área: " + fila.getString( fila.getColumnIndex("PATENTE") );
                    gg = gg + "; T. Ubicación: " + fila.getString( fila.getColumnIndex("totUbicacion") );
                    gg = gg + "; T. Productos: " + fila.getString( fila.getColumnIndex("totProd") );
                    respuesta.add(gg );
                    i++;
                    Log.i("valores: " , gg);
                }
            }else{
                respuesta.add("No data found") ;
            }
            fila.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
        }
        return  respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> traerAreas(Context contexto, String estado){
        ArrayList<String> respuesta = new ArrayList<>(1);
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT PATENTE, COUNT(DISTINCT UBICACION) AS totUbicacion, COUNT(1) AS totProd" +
                    " FROM CAPTURA WHERE ESTADO = " + estado + " GROUP BY PATENTE ORDER BY PATENTE DESC";
            Log.i("CONSULTA", consulta);
            Cursor fila = bd.rawQuery(consulta, null);
            respuesta = new ArrayList<>(fila.getCount());
            if (fila.getCount() > 0) {
                //int i = 0;
                while (fila.moveToNext()) {
                    //String gg = "Área: " + fila.getString( fila.getColumnIndex("PATENTE") );
                    //gg = gg + "; T. Ubicación: " + fila.getString( fila.getColumnIndex("totUbicacion") );
                    //gg = gg + "; T. Productos: " + fila.getString( fila.getColumnIndex("totProd"));
                    String area = "ÁREA " + fila.getString(fila.getColumnIndex("PATENTE"));
                    respuesta.add(area);
                    //i++;
                    Log.i("ÁREAS: ", area);
                }
            } else {
                respuesta.add("SIN ÁREAS");
            }
            fila.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> obtenerAreasEliminar(Context contexto){
        ArrayList<String> respuesta = new ArrayList<>(1);
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "select patente, count(distinct ubicacion) as totUbicacion, count(1) as totProd" +
                    " from CAPTURA group by patente order by PATENTE desc";
            Log.e("CONSULTA: ", consulta);
            Cursor fila = bd.rawQuery(consulta, null);
            respuesta = new ArrayList<>(fila.getCount());

            if (fila.getCount() > 0) {
                int i = 0;
                while (fila.moveToNext()) {
                    String area = "Area: " + fila.getString(fila.getColumnIndex("PATENTE"));
                    area = area + "; T. Ubicacion: " + fila.getString(fila.getColumnIndex("totUbicacion"));
                    area = area + "; T. Productos: " + fila.getString(fila.getColumnIndex("totProd"));
                    respuesta.add(area);
                    i++;
                    Log.i("ÁREAS: ", area);
                }
            } else {
                respuesta.add("SIN ÁREAS");
            }
            fila.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  respuesta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> traerAreas(Context contexto){
        ArrayList<String> respuesta = new ArrayList<>();
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "select patente, count(distinct ubicacion) as totUbicacion, count(1) as totProd" +
                    " from CAPTURA group by patente order by PATENTE desc";
            Log.e("CONSULTA: ", consulta);
            Cursor fila = bd.rawQuery(consulta, null);
            respuesta = new ArrayList<>(fila.getCount());

            if (fila.getCount() > 0) {
                int i = 0;
                //fila.moveToFirst(); order by FECHA desc
                while (fila.moveToNext()) {
                    String filas = "ÁREA: " + fila.getString(fila.getColumnIndex("PATENTE"));
                    respuesta.add(filas);
                    i++;
                    Log.i("ÁREAS: ", filas);
                }
            } else {
                respuesta.add("SIN ÁREAS");
            }
            fila.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  respuesta;
    }

    public StringBuilder escribirRespaldoTXT(Context contexto){
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        StringBuilder contenido = new StringBuilder();
        String consulta = "SELECT PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION " +
                "FROM CAPTURA ORDER BY PATENTE DESC";

        try{
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            Log.e("CONSULTA: ", consulta);
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
                    contenido.append(fecha[1]);
                    contenido.append("|");
                    contenido.append(fila.getString(fila.getColumnIndex("RUT")));
                    contenido.append("|");
                    String ubicacion = fila.getString(fila.getColumnIndex("UBICACION"));
                    if(ubicacion.equals("SIN UBICACIÓN")){
                        ubicacion = "";
                    }
                    contenido.append(ubicacion);
                    contenido.append("\r\n");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
        }
        return contenido;
    }

    public ArrayList<Map<String, String>> getDataEnviar(Context contexto, String sarea){

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Map<String, String> itemselected = new HashMap<>();
        ArrayList<Map<String, String>> itemData = new ArrayList<>();

        try{
            Cursor fila = bd.rawQuery("select PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION  from CAPTURA where PATENTE = '" + sarea + "'", null);

            if (fila.getCount()>0) {
                while (fila.moveToNext()){
                    itemselected = new HashMap<>();
                    itemselected.put("patente"  , fila.getString(fila.getColumnIndex("PATENTE")));
                    itemselected.put("ean"      , fila.getString(fila.getColumnIndex("EAN")));
                    itemselected.put("cantidad" , fila.getString(fila.getColumnIndex("CANTIDAD")));
                    String sfecha = fila.getString(fila.getColumnIndex("FECHA"));
                    String[] fecha = sfecha.split(" ");
                    itemselected.put("fecha"    , fecha[1]);
                    itemselected.put("rut"      , fila.getString(fila.getColumnIndex("RUT")));
                    String ubicacion = fila.getString(fila.getColumnIndex("UBICACION"));
                    if(ubicacion.equals("SIN UBICACIÓN")){
                        ubicacion = "";
                    }
                    itemselected.put("ubicacion", ubicacion);
                    itemData.add(itemselected);
                }
            }

            /*
            ContentValues registro3 = new ContentValues();
            registro3.put("ESTADO", 3);
            int res = bd.update("CAPTURA", registro3, "PATENTE ='" + sarea + "'", null);
            */

        }catch (Exception ff){
            Log.e("","", ff);
        }finally {
            bd.close();
        }
        return itemData;
    }

    public ArrayList<Map<String, String>> traerDatosArea(Context contexto, String area){
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Map<String, String> areaSeleccionada;
        ArrayList<Map<String, String>> datosArea = new ArrayList<>();
        String consulta = "select PATENTE, EAN, CANTIDAD, FECHA, RUT, UBICACION " +
                "FROM CAPTURA WHERE PATENTE = '" + area + "'";
        try{
            Log.i("CONSULTA", consulta);
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                while (fila.moveToNext()){
                    areaSeleccionada = new HashMap<>();
                    areaSeleccionada.put("patente", fila.getString(fila.getColumnIndex("PATENTE")));
                    areaSeleccionada.put("ean", fila.getString(fila.getColumnIndex("EAN")));
                    areaSeleccionada.put("cantidad", fila.getString(fila.getColumnIndex("CANTIDAD")));
                    String sfecha = fila.getString(fila.getColumnIndex("FECHA"));
                    String[] fecha = sfecha.split(" ");
                    areaSeleccionada.put("fecha", fecha[1]);
                    areaSeleccionada.put("rut", fila.getString(fila.getColumnIndex("RUT")));
                    String ubicacion = fila.getString(fila.getColumnIndex("UBICACION"));
                    if(ubicacion.equals("SIN UBICACIÓN")){
                        ubicacion = "";
                    }
                    areaSeleccionada.put("ubicacion", ubicacion);
                    datosArea.add(areaSeleccionada);
                }
            }
        }catch (Exception e){
            String mensaje = e.getMessage().toUpperCase();
            Log.i("ERROR", mensaje);
        }finally {
            bd.close();
        }
        return datosArea;
    }



    //para eliminar
    public String[] getAreaEnviar(Context contexto){
        String[] respuesta = new String[]{};

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select patente, count(distinct ubicacion) as totUbicacion, count(1) as totProd" +
                    " from CAPTURA  group by patente", null);

            respuesta = new String[fila.getCount()];

            if (fila.getCount()>0) {
                int i =0;
                //fila.moveToFirst();   //esto causa que el primero no se vea en la lista
                while (fila.moveToNext()){

                    String gg =      "Area: " + fila.getString( fila.getColumnIndex("PATENTE") );
                    gg = gg + "; T. Ubicacion: " + fila.getString( fila.getColumnIndex("totUbicacion") );
                    gg = gg + "; T. Productos: " + fila.getString( fila.getColumnIndex("totProd") );
                    respuesta[i] = gg  ;
                    i++;
                    Log.i("valores: " , gg);
                }
            }else{
                respuesta = new String[]{"0"};
            }
            fila.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bd.close();
        }

        /*select patente, count(distinct ubicacion), count(1)
          from CAPTURA
          group by patente
        */

        return  respuesta;
    }

    public String areaabieta(Context contexto){
        String areaOpen = "";

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();

        try {
            Cursor fila = bd.rawQuery("select PATENTE, UBICACION from captura where _ID in (select max(_ID) from captura where estado = 1) ", null);

            if (fila.getCount()>0) {
                fila.moveToFirst();
                areaOpen = fila.getString(fila.getColumnIndex("PATENTE"))
                        + ";" + fila.getString(fila.getColumnIndex("UBICACION"));
            }else{
                areaOpen = "DECERO;DECERO";
            }

        }catch (SQLException ee){
            areaOpen = "sqlexception" + ee.getMessage();
        }catch (Exception ff){
            areaOpen = "nose" + ff.getMessage();
        }finally {
            bd.close();
        }

        return areaOpen;
    }


    public String[] getAreasRegistradas (Context contexto){
        String[] strAreasReg = new String[1];

        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = admin.getWritableDatabase();
        try{
            Cursor fila = bd.rawQuery("select distinct PATENTE AS PATENTE from CAPTURA ", null);

            strAreasReg = new String[fila.getCount()];

            if (fila.getCount()>0) {
                int i =0;
                fila.moveToFirst();
                while (fila.moveToNext()){
                    String gg = fila.getString(fila.getColumnIndex("PATENTE"))  ;
                    strAreasReg[i] = fila.getString(fila.getColumnIndex("PATENTE"))  ;
                    i++;
                    Log.i("valores: " , gg);
                }
            }else{
                strAreasReg = new String[]{"0"};
            }
            fila.close();

        }catch (Exception e){
            Log.e("","", e);
        }finally {

            bd.close();
        }

        return strAreasReg;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean areaExistio(Context contexto, String area){
        boolean existe;
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT PATENTE, UBICACION FROM CAPTURA WHERE PATENTE = '" + area + "' AND ESTADO > 1";
            Log.e("CONSULTA: ", consulta);
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            existe = fila.getCount() > 0;
        } catch (Exception e) {
            existe = false;
        }
        return existe;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String areaAbierta(Context contexto){
        String areaAbierta = "";
        MaestraSQLiteOpenHelper admin = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        try (SQLiteDatabase bd = admin.getWritableDatabase()) {
            String consulta = "SELECT PATENTE, UBICACION FROM CAPTURA " +
                    "WHERE _ID IN (SELECT MAX(_ID) FROM CAPTURA WHERE ESTADO = 1)";
            Log.e("CONSULTA: ", consulta);
            @SuppressLint("Recycle") Cursor fila = bd.rawQuery(consulta, null);
            if (fila.getCount() > 0) {
                fila.moveToFirst();
                String ubicacion = fila.getString(1);
                if (ubicacion.isEmpty()) {
                    areaAbierta = fila.getString(fila.getColumnIndex("PATENTE"))
                            + ";SIN UBICACIÓN";
                } else {
                    areaAbierta = fila.getString(fila.getColumnIndex("PATENTE"))
                            + ";" + fila.getString(fila.getColumnIndex("UBICACION"));
                }

            } else {
                areaAbierta = "SINAREAS;SINUBICACION";
            }

        } catch (Exception e) {
            areaAbierta = "ERROR;" + e.getMessage();
        }
        return areaAbierta;
    }

    //REPORTES
    public ArrayList<Usuario> reporteUsuarios(Context contexto){
        MaestraSQLiteOpenHelper adm = new MaestraSQLiteOpenHelper(contexto, NOMBRE_BBDD, null, VERSION_BBDD);
        SQLiteDatabase bd = adm.getWritableDatabase();
        //TABLA LIBROS
        //db.execSQL("CREATE TABLE LIBROS (Id INTEGER PRIMARY KEY AUTOINCREMENT, TITULO TEXT, AUTOR TEXT, GENERO TEXT) ");
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        String consulta = "SELECT RUT, USUARIO FROM USUARIO ORDER BY RUT";
        Cursor fila = null;
        try{
            Log.e("CONSULTA: ", consulta);
            fila = bd.rawQuery(consulta, null);

            if (fila.moveToFirst()) {
                do {
                    Usuario usuario = new Usuario();
                    usuario.setRut(fila.getString(0));
                    usuario.setsNombre(fila.getString(1));
                    usuario.setbExiste(true);
                    listaUsuarios.add(usuario);
                } while (fila.moveToNext());
            }

        }catch (Exception e){
            String mensaje = "EL PROBLEMA ES EL SIGUIENTE: "+e.getMessage().toUpperCase();
            Log.e("ERROR USUARIOS", mensaje);
        }finally {
            assert fila != null;
            fila.close();
            bd.close();
        }
        return listaUsuarios;
    }

}
