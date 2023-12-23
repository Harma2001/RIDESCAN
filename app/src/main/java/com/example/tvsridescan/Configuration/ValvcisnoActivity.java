package com.example.tvsridescan.Configuration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.R;

public class ValvcisnoActivity extends AppCompatActivity
{
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valvci_sno);
        TextView tv =  findViewById(R.id.appname);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "ridescan.ttf");
        tv.setTypeface(typeface);
        GetUserDetails.gatenumber = 0;

        editText =  findViewById(R.id.editText);
        Button b1 =   findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String emailstr = editText.getText().toString().replace(" ","");

                if(!emailstr.isEmpty())
                {
                   if(isValidEmail(emailstr))
                   {
                       if(AppVariables.CheckInternet(getApplicationContext()))
                       {
                           AppVariables.StoreEmail(getApplicationContext(),emailstr);
                           AppVariables.email_id = emailstr;
                           HttpCommunication.PostUrl =HttpCommunication.Get_OTP;
                           startActivity(new Intent(getApplicationContext(),GetUserDetails.class));
                           overridePendingTransition(R.anim.out,R.anim.in);

                       }
                       else
                       {
                           Toast.makeText(ValvcisnoActivity.this, "Internet Not Available", Toast.LENGTH_SHORT).show();
                       }

                   }
                   else
                   {
                       Toast.makeText(ValvcisnoActivity.this, "Please Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    Toast.makeText(ValvcisnoActivity.this, "Please Enter Email ID", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.out,R.anim.in);

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
