package com.example.smartledapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

public class BTPairing extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "BTPairing";

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

    Button button;
    ListView listview;
    // broadcast receiver is used a messaging system within the app to send messages about state changes\
    // this broadcast receiver is getting state changes when bluetooth is turning on and off, as well as updating the output messages to the console output
    private final BroadcastReceiver mBroadCastReciever1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        System.out.println("Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        System.out.println("Bluetooth turning off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        System.out.println("Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        System.out.println("Bluetooth turning on");
                        break;
                }
            }
        }
    };
    // here the broadcast receiver is getting the data of discovered devices name and address
    private BroadcastReceiver mBroadCastReciever3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                System.out.println("Discovered Devices: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };
    //this boradcast receiver is getting updates on state changes of the bluetooth bonding process and then notifies the console output of the changes
    private BroadcastReceiver mBroadCastReciever4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    System.out.println("Successfully finished bonding");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    System.out.println("Bond is still being made");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    System.out.println("Bond could not be done");
                }
            }
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBroadCastReciever1);
        unregisterReceiver(mBroadCastReciever3);
        unregisterReceiver(mBroadCastReciever4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_t_pairing);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        button = (Button) findViewById(R.id.pairedBtn);
        listview = (ListView) findViewById(R.id.lvpairedDevices);

        button.setOnClickListener(new View.OnClickListener() {
            // this method displays all the paired devices on the application. It loops through the paired devices
            // and then it displays the device's name
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bt = mBluetoothAdapter.getBondedDevices();
                String[] string  = new String[bt.size()];
                int index = 0;

                if(bt.size()>0){
                    for(BluetoothDevice device:bt){
                        string[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, string);
                    listview.setAdapter(arrayAdapter);
                }
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadCastReciever4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(BTPairing.this);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Log.d(TAG, "enabling/disabling bluetooth");
                System.out.println("enabling/disabling bluetooth");
                enableDisableBT();
            }
        });
    }
    //method to enable and disbale bluetooth, the method first checks to see if bluetooth adapter is avaliable on device
    public void enableDisableBT(){
        if (mBluetoothAdapter == null){
        }
        //id bluetooth is not enabled the method goes ahead and turns it on and changes the state
        if (!mBluetoothAdapter.isEnabled()){
            System.out.println("enabling bluetooth");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadCastReciever1, BTIntent);
        }
        //if bluetooth is already enabled then the method goes ahead and disables it
        if (mBluetoothAdapter.isEnabled()){
            System.out.println("disabling bluetooth");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadCastReciever1, BTIntent);
        }
    }
    // this method controls what happens when the discover button is pressed
    // the method will check to see if device is discovering and then pass an intent filter to find the device
    public void btnDiscover(View view) {
        System.out.println("Looking for unpaired devices");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();

            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadCastReciever3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            checkBTPermissions();
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadCastReciever3, discoverDevicesIntent);

        }
    }
    //this method overrides the Bluetooth permissions on newer android devices, allowing the current devices to connect with other devices
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
        }
    }
    //when a device is clicked on from the list of discovered unpaired devices, the method will cancel discovery
    //and then get the name and address of the device and will start creating a bond.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mBluetoothAdapter.cancelDiscovery();
        System.out.println("You clicked on a device");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();
        System.out.println("Device Name: " + deviceName);
        System.out.println("Device Address: " + deviceAddress);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            System.out.println("Trying to pair with: " + deviceName);
            mBTDevices.get(position).createBond();
        }
    }
}