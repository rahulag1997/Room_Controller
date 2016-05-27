package com.example.rahul.roomcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

public class connect extends AppCompatActivity implements AdapterView.OnItemClickListener
{

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bt;
    Button refreshButton;
    ListView list;
    Set<BluetoothDevice> arrayOfDevices;
    ArrayAdapter<String> availableDevices,pairedDevices;
    BroadcastReceiver receiver;
    ArrayList<BluetoothDevice> devices;
    BluetoothDevice selectedDevice;
    IntentFilter filter1=new IntentFilter(BluetoothDevice.ACTION_FOUND);
    IntentFilter filter2=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
    IntentFilter filter3=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    IntentFilter filter4=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        setup();
        if(!bt.isEnabled())
        {
            switchOnBT();
        }
        getPairedDevices();
        Refresh();
    }

    private void setup()
    {
        bt=BluetoothAdapter.getDefaultAdapter();
        if(bt==null)
        {
            Toast.makeText(this,"This application needs Bluetooth. Cannot proceed further.",Toast.LENGTH_SHORT).show();
            finish();
        }
        refreshButton=(Button)findViewById(R.id.refreshButton);
        list=(ListView)findViewById(R.id.availableDevicesListView);
        pairedDevices=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
        availableDevices= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,0);
        list.setAdapter(availableDevices);
        list.setOnItemClickListener(this);
        devices=new ArrayList<BluetoothDevice>();
        receiver=new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                    addExtraDevices(intent);
                else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals((action)))
                    Toast.makeText(getApplicationContext(), "Refreshing Available Devices", Toast.LENGTH_SHORT).show();
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals((action)))
                    Toast.makeText(getApplicationContext(), "Available Devices Updated", Toast.LENGTH_SHORT).show();
                else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals((action)))
                {
                    if (bt.getState() == bt.STATE_OFF)
                    {
                        Toast.makeText(getApplicationContext(), "Please enable Bluetooth to Continue", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        };
        registerReceiver(receiver,filter1);
        registerReceiver(receiver,filter2);
        registerReceiver(receiver,filter3);
        registerReceiver(receiver,filter4);
    }

    private void switchOnBT()
    {
        Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i,REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED && requestCode==REQUEST_ENABLE_BT)
        {
            Toast.makeText(this,"This application needs Bluetooth. Cannot proceed further.",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getPairedDevices()
    {
        arrayOfDevices=bt.getBondedDevices();
        if(arrayOfDevices.size()>0)
        {
            for(BluetoothDevice device:arrayOfDevices)
                pairedDevices.add(device.getName());
        }
    }

    private void Refresh()
    {
        if(bt.isDiscovering())
        {
            Toast.makeText(this, "Updating! Please Wait", Toast.LENGTH_SHORT).show();
            return;
        }
        availableDevices.clear();
        devices.clear();
        pairedDevices.clear();
        getPairedDevices();
        bt.startDiscovery();
    }

    private void addExtraDevices(Intent intent)
    {
        BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String s="";
        for(int looper=0;looper<pairedDevices.getCount();looper++)
        {
            if(device.getName().equals(pairedDevices.getItem(looper)))
            {
                s="\n(Paired)";
                break;
            }
        }
        devices.add(device);
        availableDevices.add(device.getName() + s);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(availableDevices.getItem(position).contains("Paired"))
        {
            selectedDevice=devices.get(position);
            String selectedDeviceName=selectedDevice.getName();
            Toast.makeText(this,selectedDeviceName+" selected",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,controller.class);
            intent.putExtra("SelectedDevice",selectedDevice);
            bt.cancelDiscovery();
            startActivity(intent);
            finish();
        }
        else
            Toast.makeText(this,"Please pair the device first",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){}
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){}
    }

    public void Refresh(View v)
    {
        Refresh();
    }
}
