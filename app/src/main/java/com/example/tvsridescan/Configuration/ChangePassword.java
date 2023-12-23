package com.example.tvsridescan.Configuration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.Library.HttpCommunication;
import com.example.tvsridescan.R;
import com.example.tvsridescan.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends Activity
{

    EditText et1,et2,et3;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        if(!AppVariables.CheckInternet(getApplicationContext()))
        {
            Toast.makeText(this, getString(R.string.interntUnavail), Toast.LENGTH_SHORT).show();
            finish();
        }
        PushNotification  pushNotification = new PushNotification();
        pushNotification.start();

        et1 =  findViewById(R.id.editText2);
        et2 =  findViewById(R.id.editText3);
        et3 =  findViewById(R.id.editText4);
        textView =  findViewById(R.id.email_det);
        textView.setText(getString(R.string.otphasbeensent)+AppVariables.email_id);
        Button b1 =   findViewById(R.id.ok);
        Button b2 =   findViewById(R.id.resend);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String strotp = et1.getText().toString().replace("\n\n","").replace(" ","");
                String np = et2.getText().toString();
                String cnp = et3.getText().toString();

                if(strotp.isEmpty() || np.isEmpty() || cnp.isEmpty())
                {
                    Toast.makeText(ChangePassword.this, getString(R.string.pleaseenterfulldetails), Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(np.isEmpty() && cnp.isEmpty() && np.length()==4)
                    {
                        Toast.makeText(ChangePassword.this, getString(R.string.enterpassword), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(!np.equals(cnp))
                        {
                            Toast.makeText(ChangePassword.this, getString(R.string.pwddidntmatch), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            HttpCommunication.OTP = strotp;
                            HttpCommunication.password = np;
                            AppVariables.Storepwd(getApplicationContext(), Integer.parseInt(np));
                            Verify_OTP verify_otp = new Verify_OTP();
                            verify_otp.start();


                        }
                    }
                }



            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushNotification  pushNotification = new PushNotification();
                pushNotification.start();
            }
        });
    }

    public class PushNotification extends Thread
    {
        @Override
        public void run()
        {
            HttpCommunication.JSONDATA="{\"api_key\" : \""+HttpCommunication.API_KEY+"\",\"email\" : \""+AppVariables.email_id+"\"}" ;
            String Getop = HttpCommunication.POSTDATA();
            try
            {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");
                if(HttpCommunication.Status.equals("Success"))
                {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(ChangePassword.this, getString(R.string.otphasbeensenttyourmail), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("TRUE",Err_MSG);
                }
                else
                if(HttpCommunication.Status.equals("false"))
                {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePassword.this, Err_MSG, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("False",Err_MSG);
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Log.e("ChangePassword",e.getMessage());

            }



            super.run();
        }
    }

    public class Verify_OTP extends Thread
    {
        @Override
        public void run()
        {
            HttpCommunication.JSONDATA="{\"api_key\":\""+HttpCommunication.API_KEY+"\",\"email\":\"" + AppVariables.email_id + "\",\"otp\":\""+HttpCommunication.OTP+"\",\"password\":\""+HttpCommunication.password+"\"}" ;
            HttpCommunication.PostUrl =HttpCommunication.Verify_OTP;
            String Getop = HttpCommunication.POSTDATA();
            try {
                JSONObject object = new JSONObject(Getop);
                HttpCommunication.Status = object.getString("status");

                if (HttpCommunication.Status.equals("Success")) {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(),SplashScreen.class));
                            overridePendingTransition(R.anim.out,R.anim.in);
                            Toast.makeText(ChangePassword.this, getString(R.string.success), Toast.LENGTH_LONG).show();
                        }
                    });

                    Log.e("False", Err_MSG);
                } else if (HttpCommunication.Status.equals("false")) {
                    final String Err_MSG = object.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePassword.this, Err_MSG, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("False", Err_MSG);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

                super.run();
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
