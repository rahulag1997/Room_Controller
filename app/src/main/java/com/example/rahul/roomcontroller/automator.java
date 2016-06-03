package com.example.rahul.roomcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class automator extends AppCompatActivity
{
    String defaultDevice="HC-05";
    BluetoothAdapter bt;
    BroadcastReceiver receiver;
    BluetoothDevice device;
    IntentFilter filter1=new IntentFilter(BluetoothDevice.ACTION_FOUND);
    private static final int REQUEST_ENABLE_BT = 11;
    Set<BluetoothDevice> arrayOfDevices;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") ;
    private static final int SUCCESS_CONNECT =0 ;
    private static final int MESSAGE_READ = 1;
    ConnectedThread connectedThread;
    ConnectThread connect;
    boolean connectStatus=false;
    final static int SPEAK=1;
    ArrayList<String> result;

    Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case SUCCESS_CONNECT:
                    connectedThread = new ConnectedThread((BluetoothSocket) msg.obj);
                    String s = "*successfully connected#";
                    connectedThread.write(s.getBytes());
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String commandIn = new String(readBuf);
                    Toast.makeText(getApplicationContext(),"kuch read hua",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), commandIn, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setup();
    }

    private void setup()
    {
        Thread connector=new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                SharedPreferences preferences=getApplicationContext().getSharedPreferences("MY_PREFS",MODE_PRIVATE);
                defaultDevice =preferences.getString("defDevice","HC-05");
                setupBackground();
                connectAuto();
            }
        };
        connector.run();
    }

    private void setupBackground()
    {
        bt= BluetoothAdapter.getDefaultAdapter();
        if(bt==null)
        {
            Toast.makeText(this,"This application needs Bluetooth. Cannot proceed further.",Toast.LENGTH_SHORT).show();
            finish();
        }
        receiver=new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device.getName().equals(defaultDevice))
                    {
                        if(checkPaired())
                        {
                            connect = new ConnectThread(device);
                            connect.run();
                            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                            connectStatus = true;
                            bt.cancelDiscovery();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Default Device is not paired",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        registerReceiver(receiver,filter1);
    }

    private void connectAuto()
    {
        if(!bt.isEnabled())
        {
            switchOnBT();
        }
        bt.startDiscovery();
    }

    private void switchOnBT()
    {
        Intent i=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i,REQUEST_ENABLE_BT);
    }

    private boolean checkPaired()
    {
        arrayOfDevices=bt.getBondedDevices();
        if(arrayOfDevices.size()>0)
        {
            for(BluetoothDevice device2:arrayOfDevices)
            {
                if(device2.getName().equals(defaultDevice))
                    return true;
            }
        }
        return false;
    }


    private void setLayout()
    {
        //CREATE CUSTOM LAYOUT

        //get values
        //SharedPreferences prefs=this.getSharedPreferences("MY_PREFS")
    }

    private void switchOn(char a)
    {
        if(connectStatus)
        {
            String s="*" + a + "1#";
            connectedThread.write(s.getBytes());
        }
    }

    private void switchOff(char a)
    {
        if(connectStatus)
        {
            String s="*" + a + "0#";
            connectedThread.write(s.getBytes());
        }
    }

    public void voiceIn(View view)
    {
        Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"PLEASE SPEAK");
        startActivityForResult(i,SPEAK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED && requestCode==REQUEST_ENABLE_BT)
        {
            Toast.makeText(this,"This application needs Bluetooth. Cannot proceed further.",Toast.LENGTH_SHORT).show();
            finish();
        }
        if((requestCode==SPEAK) && (resultCode==RESULT_OK))
        {
            result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            processInput();
        }
    }

    private void processInput()
    {
        for(String vIn:result)
        {
            vIn = vIn.toLowerCase();
            //add customs
        }
    }


    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device)
        {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try
            {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run()
        {
            try
            {
                mmSocket.connect();
            } catch (IOException connectException)
            {
                try
                {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            manageConnectedSocket(mmSocket);
        }

        private void manageConnectedSocket(BluetoothSocket mmSocket)
        {
            if(mmSocket!=null)
                mHandler.obtainMessage(SUCCESS_CONNECT,mmSocket).sendToTarget();
            else
                Toast.makeText(getApplicationContext(), "socket is null", Toast.LENGTH_SHORT).show();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true)
            {
                try
                {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e)
                {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes)
        {
            try
            {
                mmOutStream.write(bytes);
            } catch (IOException ignored) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            } catch (IOException ignored) { }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            connectedThread.cancel();
            unregisterReceiver(receiver);
        }catch (Exception ignored){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            connectedThread.cancel();
            unregisterReceiver(receiver);
        }catch (Exception ignored){}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            connectedThread.cancel();
            unregisterReceiver(receiver);
        }catch (Exception ignored){}
    }
}