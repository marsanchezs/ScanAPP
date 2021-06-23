package cl.dpsoft.isamonhome;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

import cl.dpsoft.isamonhome.ListasDispositivosBluetooth.DispositivosAdapter;
import cl.dpsoft.isamonhome.ListasDispositivosBluetooth.ItemDispositivo;
import cl.dpsoft.isamonhome.Utilidades.Utilidades;

public class ListaDispositivosBluetooth extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<ItemDispositivo> dispositivos;
    private DispositivosAdapter adapterDispositivos;
    private BluetoothAdapter bluetoothAdapter;
    private final Utilidades utilidades = new Utilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_dispositivos_bluetooth);

        recycler = (RecyclerView) findViewById(R.id.recycler_dispositivos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        dispositivos = new ArrayList<ItemDispositivo>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            if(!bluetoothAdapter.isEnabled()){// si no está activado
                // Mandamos a activarlo
                Intent habilitarBluIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(habilitarBluIntent, 243);
            }else {

                // Obtenemos la lista de dispositivos sincronizados
                Set<BluetoothDevice> dispositivosSync = bluetoothAdapter.getBondedDevices();

                // Si hay dispositivos sincronizados
                if(dispositivosSync.size() > 0){
                    // Llenamos el array de dispositivos para pasarlo al adapter
                    for(BluetoothDevice dispositivo : dispositivosSync){
                        dispositivos.add(new ItemDispositivo(dispositivo.getName(),  dispositivo.getAddress()));
                    }
                }
            }
        }else{
            utilidades.mostrarMensajePersonalizado(this, "EL DISPOSITIVO NO SOPORTA BLUETOOTH", "NOK");
            //Toast.makeText(this, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }
        adapterDispositivos = new DispositivosAdapter(new EscuchadorClick(), dispositivos);
        recycler.setAdapter(adapterDispositivos);
    }

    //MÉTODOS
    private class EscuchadorClick implements DispositivosAdapter.MiListenerClick{

        @Override
        public void clickItem(View itemView, int posicion) {
            // Mandamos la direccion al onActivityResult de la actividad que lanzo esta
            Bundle bundle = new Bundle();
            bundle.putString("DireccionDispositivo", adapterDispositivos.getDispositivos().get(posicion).getDireccion());
            bundle.putString("NombreDispositivo", adapterDispositivos.getDispositivos().get(posicion).getNombre());
            System.out.println("DIRECCIÓN DISPOSITIVO LD: "+adapterDispositivos.getDispositivos().get(posicion).getDireccion());
            System.out.println("NOMBRE DISPOSITIVO LD: "+adapterDispositivos.getDispositivos().get(posicion).getNombre());
            Intent intentPaAtras = new Intent();
            intentPaAtras.putExtras(bundle);
            setResult(Activity.RESULT_OK, intentPaAtras);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK ) {
            if ( requestCode == 243 ) {
                if( bluetoothAdapter.isEnabled() ){
                    // Obtenemos la lista de dispositivos sincronizados
                    Set<BluetoothDevice> dispositivosSync = bluetoothAdapter.getBondedDevices();

                    // Si hay dispositivos sincronizados
                    if(dispositivosSync.size() > 0){
                        // Llenamos el array de dispositivos para pasarlo al adapter
                        for(BluetoothDevice dispositivo : dispositivosSync){
                            dispositivos.add(new ItemDispositivo(dispositivo.getName(),  dispositivo.getAddress()));
                            adapterDispositivos.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }
}