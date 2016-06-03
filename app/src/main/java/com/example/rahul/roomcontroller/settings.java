package com.example.rahul.roomcontroller;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class settings extends Activity
{
    EditText defBd, noBd;
    ListView listOfApps;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayAdapter<String> adapterOfDevices;
    ArrayList<String> nameOfApps;
    AlertDialog.Builder builder;
    EditText editText;
    int noOfDevices=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setup();
    }

    private void setup()
    {
        defBd = (EditText) findViewById(R.id.defaultBD);
        noBd = (EditText) findViewById(R.id.noOfApps);
        listOfApps = (ListView) findViewById(R.id.listOfApps);
        adapterOfDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        nameOfApps=new ArrayList<String>();
        listOfApps.setAdapter(adapterOfDevices);
        preferences = this.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        editor = preferences.edit();
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
        noBd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    adapterOfDevices.clear();
                    noOfDevices = Integer.parseInt(noBd.getText().toString());
                    editor.putInt("noOfApps", noOfDevices);
                    editor.commit();
                    for (int i = 0; i < noOfDevices; i++) {
                        adapterOfDevices.add("DEVICE" + (i + 1));
                        nameOfApps.add("DEVICE" + (i + 1));
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
                /*builder = new AlertDialog.Builder(getApplicationContext());
                editText=new EditText(getApplicationContext());
                editText.setLines(1);
                editText.setHint("Device Name");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(editText);
                builder.setMessage("Enter Custom Device Name");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        nameOfApps.set(position, editText.getText().toString());
                        adapterOfDevices.clear();
                        for (int i = 0; i < noOfDevices; i++)
                        {
                            adapterOfDevices.add(nameOfApps.get(i));
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog temp=builder.create();
                temp.show();*/

                DialogFragment dialogBox=new customDialog();
                dialogBox.show(getFragmentManager(),"device");
            }
        });
    }

}
