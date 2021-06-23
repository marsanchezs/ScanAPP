package cl.dpsoft.isamonhome;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import cl.dpsoft.isamonhome.main.IsamConstantes;

import static android.os.Environment.getExternalStorageDirectory;

public class MaestraSQLiteOpenHelper extends SQLiteOpenHelper {

    private final Context miContexto;
    private static final String RUTA_BBDD = IsamConstantes.RUTA_BBDD;
    private static final String NOMBRE_BBDD = IsamConstantes.NOMBRE_BBDD;
    //private static final String RUTA_BBDD = "data/cl.dpsoft.isamonhome/databases/";
    //private static final String NOMBRE_BBDD = "Maestra";
    private SQLiteDatabase miBase;

    public MaestraSQLiteOpenHelper(Context contexto, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, name, factory, version);
        this.miContexto = contexto;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TABLA CAPTURA
        /*db.execSQL("CREATE TABLE CAPTURA (_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IDPROD TEXT, PATENTE TEXT, EAN TEXT, CANTIDAD TEXT, FECHA TEXT, " +
                "RUT TEXT, ESTADO TEXT, DESCRIPCION TEXT, UBICACION TEXT, ESTADO1 TEXT) ");
        //TABLA MAESTRA
        db.execSQL("CREATE TABLE MAESTRA (_ID INTEGER PRIMARY KEY AUTOINCREMENT, EAN TEXT, DESCRIPCION TEXT, SKU TEXT) ");
        //TABLA SERVER1
        db.execSQL("CREATE TABLE SERVER1 (_ID INTEGER PRIMARY KEY AUTOINCREMENT, SERVER TEXT) ");
        //TABLA USUARIO
        db.execSQL("CREATE TABLE USUARIO (_Id INTEGER PRIMARY KEY AUTOINCREMENT, RUT TEXT, USUARIO TEXT) ");

        cargarMaestra(db);
        cargarServer1(db);
        cargarUsuario(db);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }

    private void cargarMaestra(SQLiteDatabase db){
        db.execSQL("INSERT INTO MAESTRA (EAN, DESCRIPCION, SKU) VALUES ('1234', 'MESA', '1111')");
        db.execSQL("INSERT INTO MAESTRA (EAN, DESCRIPCION, SKU) VALUES ('5678', 'SILLA', '2222')");
        db.execSQL("INSERT INTO MAESTRA (EAN, DESCRIPCION, SKU) VALUES ('8888', 'ALFOMBRA', '3333')");
    }

    private void cargarServer1(SQLiteDatabase db){
        db.execSQL("INSERT INTO SERVER1 (SERVER) VALUES ('00001111')");
    }

    private void cargarUsuario(SQLiteDatabase db){
        db.execSQL("INSERT INTO USUARIO (RUT, USUARIO) VALUES ('12345678', 'PEDRO')");
        db.execSQL("INSERT INTO USUARIO (RUT, USUARIO) VALUES ('123454321', 'JUAN')");
        db.execSQL("INSERT INTO USUARIO (RUT, USUARIO) VALUES ('87654321', 'DIEGO')");
    }

    public void crearBBDD() {
        this.getReadableDatabase();
        try {
            copiarBBDD();
        } catch (Exception e) {
            String mensaje = "ERROR AL COPIAR BBDD";
            Log.e("ERROR crearBBDD", mensaje, e);
        }
    }

    public boolean chequearBBDD() {
        SQLiteDatabase chequearBBDD = null;
        try {
            String ruta = "data/" +RUTA_BBDD + NOMBRE_BBDD;
            chequearBBDD = SQLiteDatabase.openDatabase(ruta, null, SQLiteDatabase.OPEN_READONLY);
            Log.e("RUTA BBDD MAESTRA", ruta);
        } catch (SQLiteException e) {
            String mensaje = "ERROR CHEQUEAR BBDD";
            Log.e("ERROR chequearBBDD", mensaje);
            // Base de datos no creada todavia
        }

        if (chequearBBDD != null) {
            chequearBBDD.close();
        }
        return chequearBBDD != null;
    }

    public void abrirBBDD() throws SQLException {
        String ruta = "data/" +RUTA_BBDD + NOMBRE_BBDD; //Máquina.
        //String myPath = "data/" + DB_PATH + DB_NAME; //Emulador.
        miBase = SQLiteDatabase.openDatabase(ruta, null, SQLiteDatabase.OPEN_READONLY);
        System.out.println("RUTA: " +  ruta);
    }

    @Override
    public synchronized void close() {
        if (miBase != null)
            miBase.close();
        super.close();
    }

    private void copiarBBDD() throws Exception {
        /*String ruta = "/capturadora/maestra/"+NOMBRE_BBDD;
        File archivo = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), ruta);
        Log.i("RUTA ARCHIVO: ", archivo.toString());
        if(!archivo.exists()){ // Si no existe, crea el archivo.
            Log.i("ARCHIVO: ", "NO EXISTE");
        }else{
            Log.i("ARCHIVO: ", "EXISTE");
            leerFicheroMemoriaExterna(archivo);
        }*/


        String rutaDestinoBBDD = RUTA_BBDD + NOMBRE_BBDD; //Máquina.
        //String destDBPath = "data/" + DB_PATH + DB_NAME; //Emulador.
        String rutaOrigenBBDD = Environment.DIRECTORY_DOWNLOADS+"/capturadora/maestra/"+NOMBRE_BBDD;
        Log.i("RUTA DESTINO BBDD: ", rutaDestinoBBDD);
        Log.i("RUTA ORIGEN BBDD: ", rutaOrigenBBDD);

        //File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //File sd = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), rutaOrigenBBDD);
        File sd = getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        File destinoBBDD = new File(data, rutaDestinoBBDD);
        File origenBBDD = new File(sd.toString(), rutaOrigenBBDD);
        Log.i("DESTINO BBDD: ", destinoBBDD.toString());
        Log.i("ORIGEN BBDD: ", origenBBDD.toString());

        if(!origenBBDD.exists()){ // Si no existe, crea el archivo.
            Log.i("BASE DE DATOS: ", "NO EXISTE");
        }else{
            Log.i("BASE DE DATOS: ", "EXISTE");
        }

        FileChannel origen = new FileInputStream(origenBBDD).getChannel();
        FileChannel destino = new FileOutputStream(destinoBBDD).getChannel();

        destino.transferFrom(origen, 0, origen.size());
        origen.close();
        destino.close();
    }

    private void leerFicheroMemoriaExterna(File archivo){
        try
        {
            System.out.println("RUTA ABSOLUTA: "+archivo.getAbsolutePath());
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(archivo)));

            String texto = fin.readLine();
            System.out.println("CONTENIDO FICHERO MEMORIA EXTERNA: "+texto);
            fin.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde tarjeta SD");
        }
    }
}
