package com.example.rahul.roomcontroller;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class customDialog extends DialogFragment
{
    public int position;

    public String newName="No Name",currVal;
    onCustomDialog  listener ;
    public interface onCustomDialog
    {
        void setText(String name,int position);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            listener = (onCustomDialog) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        position=getArguments().getInt("pos");
        currVal=getArguments().getString("currVal");
        final EditText editText=new EditText(getActivity());
        editText.setText(currVal);
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
                if(newName !=null)
                    listener.setText(newName,position);
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
