package com.example.rahul.roomcontroller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class controller extends AppCompatActivity
{
    Switch fan,tube,bulb1,bulb2,all;
    ImageView fanImg,tubeImg,bulb1Img,bulb2Img;
    Button connectButton;
    ImageButton voice;
    ConnectedThread connectedThread;
    ConnectThread connect;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") ;
    private static final int SUCCESS_CONNECT =0 ;
    private static final int MESSAGE_READ = 1;
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
    boolean connectStatus=false;
    final static int SPEAK=1;
    ArrayList<String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        setup();
        if(getIntent().getExtras()==null)
            Toast.makeText(this,"No item Received",Toast.LENGTH_SHORT).show();
        else
        {
            BluetoothDevice selectedDevice= (BluetoothDevice) getIntent().getExtras().get("SelectedDevice");
            if (selectedDevice != null)
            {
                connect = new ConnectThread(selectedDevice);
                connect.run();
                Toast.makeText(getApplicationContext(),"Connecting to "+selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                connectStatus=true;
            }
            else
                Toast.makeText(getApplicationContext(),"Null Device Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setup()
    {
        connectButton=(Button)findViewById(R.id.connectButton);
        voice=(ImageButton)findViewById(R.id.voiceButton);
        fan=(Switch)findViewById(R.id.fanSwitch);
        tube=(Switch)findViewById(R.id.tubeSwitch);
        bulb1=(Switch)findViewById(R.id.bulb1Switch);
        bulb2=(Switch)findViewById(R.id.bulb2Switch);
        all=(Switch)findViewById(R.id.allSwitch);
        fanImg=(ImageView)findViewById(R.id.fanImage);
        tubeImg=(ImageView)findViewById(R.id.tubeImage);
        bulb1Img=(ImageView)findViewById(R.id.bulb1Image);
        bulb2Img=(ImageView)findViewById(R.id.bulb2Image);
        fanImg.setImageResource(R.drawable.fan);
        tubeImg.setImageResource(R.drawable.bulboff);
        bulb1Img.setImageResource(R.drawable.bulboff);
        bulb2Img.setImageResource(R.drawable.bulboff);
        fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    fanOn();
                else
                    fanOff();
            }
        });
        tube.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    lightOn();
                else
                    lightOff();
            }
        });
        bulb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    bulb1On();
                else
                    bulb1Off();
            }
        });
        bulb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                    bulb2On();
                else
                    bulb2Off();
            }
        });
        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if(isChecked)
                    allOn();
                else
                    allOff();
            }
        });
    }


    private void fanOff()
    {
        if(connectStatus)
        {
            String s="*F0#";
            connectedThread.write(s.getBytes());
        }
        fanImg.setBackgroundResource(R.drawable.fan);
    }

    private void fanOn()
    {
        if(connectStatus)
        {
            String s="*F1#";
            connectedThread.write(s.getBytes());
        }
        fanImg.setBackgroundResource(R.drawable.fanon);
    }

    private void lightOff()
    {
        if(connectStatus)
        {
            String s="*L0#";
            connectedThread.write(s.getBytes());
        }
        tubeImg.setBackgroundResource(R.drawable.bulboff);
    }

    private void lightOn()
    {
        if(connectStatus)
        {
            String s="*L1#";
            connectedThread.write(s.getBytes());
        }
        tubeImg.setBackgroundResource(R.drawable.yellowon);
    }

    private void bulb1Off()
    {
        if(connectStatus)
        {
            String s="*B0#";
            connectedThread.write(s.getBytes());
        }
        bulb1Img.setBackgroundResource(R.drawable.bulboff);
    }

    private void bulb1On()
    {
        if(connectStatus)
        {
            String s="*B1#";
            connectedThread.write(s.getBytes());
        }
        bulb1Img.setBackgroundResource(R.drawable.redon);
    }

    private void bulb2Off()
    {
        if(connectStatus)
        {
            String s="*b0#";
            connectedThread.write(s.getBytes());
        }
        bulb2Img.setBackgroundResource(R.drawable.bulboff);
    }

    private void bulb2On()
    {
        if(connectStatus)
        {
            String s="*b1#";
            connectedThread.write(s.getBytes());
        }
        bulb2Img.setBackgroundResource(R.drawable.blueon);
    }

    private void allOff()
    {
        if(connectStatus)
        {
            String s="*A0#";
            connectedThread.write(s.getBytes());
        }
    }

    private void allOn()
    {
        if(connectStatus)
        {
            String s="*A1#";
            connectedThread.write(s.getBytes());
        }
    }

    public void reConnect(View view)
    {
        Intent i = new Intent(this,connect.class);
        startActivity(i);
        finish();
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
            vIn=vIn.toLowerCase();
            if(vIn.contains("all"))
            {
                if(vIn.contains("on"))
                    all.setChecked(true);
                else if(vIn.contains("off") || vIn.contains("close") || vIn.contains("of"))
                    all.setChecked(false);
            }
            else if(vIn.contains("red"))
            {
                if (vIn.contains("on"))
                    bulb1.setChecked(true);
                else if (vIn.contains("off") || vIn.contains("close") || vIn.contains("of"))
                    bulb1.setChecked(false);
                else
                    bulb1.toggle();
            }
            else if(vIn.contains("blue"))
            {
                if(vIn.contains("on"))
                    bulb2.setChecked(true);
                else if(vIn.contains("off") || vIn.contains("close") || vIn.contains("of"))
                    bulb2.setChecked(false);
                else
                    bulb2.toggle();
            }
            else if(vIn.contains("light") || vIn.contains("tube"))
            {
                if(vIn.contains("on"))
                    tube.setChecked(true);
                else if(vIn.contains("off") || vIn.contains("close") || vIn.contains("of"))
                    tube.setChecked(false);
                else
                    tube.toggle();
            }
            else if(vIn.contains("fan"))
            {
                if(vIn.contains("on"))
                    fan.setChecked(true);
                else if(vIn.contains("off") || vIn.contains("close") || vIn.contains("of"))
                    fan.setChecked(false);
                else
                    fan.toggle();
            }
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
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        connect.cancel();
    }
}
