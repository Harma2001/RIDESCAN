package com.example.tvsridescan.Configuration;

import static com.example.tvsridescan.SplashScreen.firstRunPrefs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvsridescan.R;

public class ChooseLangActivity extends AppCompatActivity {

    Context context;
    private static String TAG = "ChooseLangActivity";
    Button confirm,cancel;
    Spinner mLanguage;
    ArrayAdapter<String> mAdapter;
    TextView heading;

    int selectedLangValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lang);
        init();
    }

    private void init()
    {
        context = ChooseLangActivity.this;

        heading  =findViewById(R.id.chooselangheading);
        heading.setText(getString(R.string.chooseanylang));
        confirm = findViewById(R.id.ok);
        cancel = findViewById(R.id.cncl);
        mLanguage =  findViewById(R.id.spLanguage);
        mAdapter = new ArrayAdapter<>(ChooseLangActivity.this, R.layout.changelangspinnertext, getResources().getStringArray(R.array.language_option));
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mLanguage.setAdapter(mAdapter);


//        if (LocaleHelper.getLanguage(ChooseLangActivity.this).equalsIgnoreCase("en")) {
//            mLanguage.setSelection(mAdapter.getPosition("English"));
//        }
//        else if (LocaleHelper.getLanguage(ChooseLangActivity.this).equalsIgnoreCase("hi")) {
//            mLanguage.setSelection(mAdapter.getPosition("Hindi"));
//        }


        mLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        selectedLangValue = (int) adapterView.getSelectedItemId();
                        //context = LocaleHelper.setLocale(ChooseLangActivity.this, "en");
                        //resources = context.getResources();
                        //mAdapter = new ArrayAdapter<>(ChooseLangActivity.this, R.layout.changelangspinnertext,resources.getStringArray(R.array.language_option));
                        //mLanguage.setAdapter(mAdapter);
                       // confirm.setText(getString(R.string.confirm));
                       // heading.setText(getString(R.string.chooseanylang));

                        //mTextView.setText(resources.getString(R.string.text_translation));
                        break;
                    case 1:
                        selectedLangValue = (int) adapterView.getSelectedItemId();

                        //context = LocaleHelper.setLocale(ChooseLangActivity.this, "hi");
                      //  resources = context.getResources();
                       // mAdapter = new ArrayAdapter<>(ChooseLangActivity.this, R.layout.changelangspinnertext,resources.getStringArray(R.array.language_option));
                       // mLanguage.setAdapter(mAdapter);
                       // confirm.setText(getString(R.string.confirm));
                       // heading.setText(getString(R.string.chooseanylang));

                        // mTextView.setText(resources.getString(R.string.text_translation));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context;
                SharedPreferences.Editor editor = firstRunPrefs.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.apply();
                switch (selectedLangValue) {

                    case 0:
                        context = LocaleHelper.setLocale(ChooseLangActivity.this, "en");
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        if (i != null) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.out,R.anim.in);
                        break;
                    case 1:
                        context = LocaleHelper.setLocale(ChooseLangActivity.this, "hi");
                        i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        if (i != null) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.out,R.anim.in);
                        break;
                    default:
                        context = LocaleHelper.setLocale(ChooseLangActivity.this, "en");
                        i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        if (i != null) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        finish();
                        startActivity(i);
                        overridePendingTransition(R.anim.out,R.anim.in);

                        break;
                }


            }
        });

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
