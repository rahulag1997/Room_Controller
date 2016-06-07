package com.example.rahul.roomcontroller;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.NumberPicker;

public class customDialog3 extends DialogFragment
{
    onCustomDialog3  listener ;
    public interface onCustomDialog3
    {
        void setVal(int val);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            listener = (onCustomDialog3) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        final NumberPicker numberPicker=new NumberPicker(getActivity());
        numberPicker.setOrientation(NumberPicker.VERTICAL);
        numberPicker.setGravity(Gravity.CENTER_HORIZONTAL);
        numberPicker.setMaxValue(26);
        numberPicker.setMinValue(1);
        numberPicker.setValue(getArguments().getInt("currVal"));
        builder.setView(numberPicker);
        builder.setMessage("No. Of Appliances");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                listener.setVal(numberPicker.getValue());
                dialog.dismiss();
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
        return builder.create();
    }
}
