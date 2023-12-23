package com.example.tvsridescan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.tvsridescan.Library.AppVariables;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppVariables.DialogConnectionLost(getApplicationContext());
            }
        });

    }
    public void DialogConnectionLost(final Context context)
    {
        final Dialog dialog  = new Dialog(this);
        dialog.setContentView(R.layout.dialog_connection_lost);
        Button b1 = dialog.findViewById(R.id.OK);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.hide();
                    Intent i = new Intent(getApplicationContext(),SplashScreen.class);
                    startActivity(i);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dialog.show();

    }
}
