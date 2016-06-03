package com.example.rahul.roomcontroller;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class customDialog extends DialogFragment
{
    //IS NOT CURRENTLY USED

    public String newName="No Name";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText=new EditText(getActivity());
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
                newName=editText.getText().toString();
                Toast.makeText(getActivity(),newName, Toast.LENGTH_SHORT).show();

                SharedPreferences preferences=getActivity().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);;
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("name",newName);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}
