package com.example.rlybr.bluetoothtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter = null;
    private ListView deviceListView;
    private Button discoveryButton;
    private Button discoverableButton;
    private ToggleButton searchTogButton;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceListView = (ListView)findViewById(R.id.list);
        discoveryButton = (Button)findViewById(R.id.button);
        discoverableButton = (Button)findViewById(R.id.button2);
        searchTogButton = (ToggleButton)findViewById(R.id.toggleButton);

        final ArrayList<String> listArray = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listArray);
        deviceListView.setAdapter(listAdapter);

        discoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.clear();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
                boolean started = mBluetoothAdapter.startDiscovery();
                listAdapter.add("Device name: " + mBluetoothAdapter.getName());
                listAdapter.add("Device address " + mBluetoothAdapter.getAddress());
                listAdapter.add("Discovery started Successfully: " + Boolean.toString(started));
            }
        });

        discoverableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });

        searchTogButton.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if(isChecked) {
                    listAdapter.clear();
                    registerReceiver(mReceiver, filter);
                    boolean started = mBluetoothAdapter.startDiscovery();
                    listAdapter.add("Device name: " + mBluetoothAdapter.getName());
                    listAdapter.add("Device address " + mBluetoothAdapter.getAddress());
                    listAdapter.add("Discovery started Successfully: " + Boolean.toString(started));
                }
                else {
                    unregisterReceiver(mReceiver);
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        }));

        // Get BluetoothAdapter and check if device is compatible with bluetooth, if not close activity
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available on this device", Toast.LENGTH_LONG).show();
            this.finish();
        }

        // Check if bluetooth is enabled, if not request to enable it
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        // Register for broadcasts when a device is discovered.
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                listAdapter.add(deviceName + ", " + deviceHardwareAddress);
                listAdapter.add("test2");
                Toast.makeText(getApplicationContext(), "something received", Toast.LENGTH_LONG).show();
                System.out.println("test");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
}
