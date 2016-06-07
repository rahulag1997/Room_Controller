package com.example.rahul.roomcontroller;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class settings extends AppCompatActivity implements customDialog.onCustomDialog,customDialog2.onCustomDialog2,customDialog3.onCustomDialog3
{
    EditText defBd, noBd;
    ListView listOfApps;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayAdapter<String> adapterOfDevices;
    int noOfDevices=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setup();
        setupValues();
    }

    private void setupValues()
    {
        preferences = this.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        if(!preferences.getString("defDevice",null).equals(null))
        {
            defBd.setText(preferences.getString("defDevice","HC-05"));
        }
        if(preferences.getInt("noOfApps",0)!=0)
        {
            noOfDevices=preferences.getInt("noOfApps",0);
            noBd.setText(Integer.toString(noOfDevices));
            //adapterOfDevices.clear();
            for(int i=0;i<preferences.getInt("noOfApps",0);i++)
            {
                adapterOfDevices.add(preferences.getString(("Device"+i),"DEVICE "+(i+1)));
            }
            adapterOfDevices.notifyDataSetChanged();
        }
    }

    public void setText(String name,int position)
    {
        preferences = this.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(("Device"+position),name);
        editor.commit();
        String itemToDelete=adapterOfDevices.getItem(position);
        adapterOfDevices.remove(itemToDelete);
        adapterOfDevices.insert(name,position);
        adapterOfDevices.notifyDataSetChanged();
        listOfApps.setAdapter(adapterOfDevices);
    }

    public void setDevice(String name)
    {
        defBd.setText(name);
        preferences = this.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(("defDevice"),name);
        editor.commit();
    }

    public void setVal(int val)
    {
        noBd.setText(""+val);
        adapterOfDevices.clear();
        noOfDevices = val;
        editor.putInt("noOfApps", noOfDevices);
        editor.commit();
        for (int i = 0; i < noOfDevices; i++) {
            adapterOfDevices.add("DEVICE " + (i + 1));
            editor.putString(("Device" + i), "DEVICE " + (i + 1));
            editor.commit();
        }
    }
    private void setup()
    {
        defBd = (EditText) findViewById(R.id.defaultBD);
        noBd = (EditText) findViewById(R.id.noOfApps);
        listOfApps = (ListView) findViewById(R.id.listOfApps);
        adapterOfDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listOfApps.setAdapter(adapterOfDevices);
        preferences = this.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        editor = preferences.edit();
        defBd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle =new Bundle();
                bundle.putString("currVal",defBd.getText().toString());
                DialogFragment dialogBox=new customDialog2();
                dialogBox.setArguments(bundle);
                dialogBox.show(getFragmentManager(),"device");
            }
        });
        defBd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editor.putString("defDevice", defBd.getText().toString());
                    editor.commit();
                    return true;
                }
                return false;
            }
        });
        noBd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle =new Bundle();
                bundle.putInt("currVal",Integer.parseInt(noBd.getText().toString()));
                DialogFragment dialogBox=new customDialog3();
                dialogBox.setArguments(bundle);
                dialogBox.show(getFragmentManager(),"device");
            }
        });
        noBd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    adapterOfDevices.clear();
                    noOfDevices = Integer.parseInt(noBd.getText().toString());
                    editor.putInt("noOfApps", noOfDevices);
                    editor.commit();
                    for (int i = 0; i < noOfDevices; i++) {
                        adapterOfDevices.add("DEVICE " + (i + 1));
                        editor.putString(("Device" + i), "DEVICE " + (i + 1));
                        editor.commit();
                    }
                    return true;
                }
                return false;
            }
        });
        listOfApps.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                Bundle bundle =new Bundle();
                bundle.putInt("pos",position);
                bundle.putString("currVal",listOfApps.getItemAtPosition(position).toString());
                DialogFragment dialogBox=new customDialog();
                dialogBox.setArguments(bundle);
                dialogBox.show(getFragmentManager(),"device");
            }
        });
    }

}
