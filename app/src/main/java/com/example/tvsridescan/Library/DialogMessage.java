package com.example.tvsridescan.Library;

import android.app.AlertDialog;


public class DialogMessage {

    AlertDialog alertDialog;
    public void dialogMessageShow()
    {
        if(alertDialog!=null)
        {
            alertDialog.show();
        }
    }
    public  void dialogMessageHide()
    {
        if(alertDialog!=null)
        {
            if(alertDialog.isShowing())
            {
                alertDialog.dismiss();
            }
        }
    }
}
