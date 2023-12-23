package com.example.tvsridescan;

import static com.example.tvsridescan.Library.AppVariables.*;
import static com.itextpdf.text.Element.ALIGN_CENTER;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.drawlib.TouchDrawView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PDIActivity extends AppCompatActivity
{


    Button startbutton;
    static final int DATE_OF_PDI = 1; //date_of_pdi
    static final int DATE_OF_INV = 2;//date_of_inv
    private int year;
    private int month;
    private int day;

    static String date2 = "empty";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdi);
        startbutton = (Button) findViewById(R.id.btnstart);

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                NormalDetails();
               // View view1 = getWindow().getDecorView().getRootView();
               // AppVariables.takeScreenshot(view1);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_OF_PDI:

                // open date picker dialog.
                // set date picker for current date
                // add pickerListener listener to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);

            case DATE_OF_INV:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {


            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            date2 =  (new StringBuilder().append(year).append("/").append(month + 1).append("/")
                    .append(day).append(" ")).toString();

            // Show selected date

            if(datecat==1)
            {
                date_of_pdi.setText("Date of PDI : "+date2+"YYYY/MM/DD");
                AppVariables.date_of_pdi = date2;
            }
            else
            if(datecat==2)
            {
                date_of_inv.setText("Date of Inv : "+date2+"YYYY/MM/DD");
                AppVariables.date_of_inv = date2;
            }
        }
    };

    Button date_of_pdi,date_of_inv;
    int datecat =0;
    //Normal details
    public void NormalDetails()
    {
        Button btnnext,btnprev;

        final EditText tvsm,chassis,bat;
        final String[] bikes = {"Apache RR 310","Apache RTR 200 Fi"};
        final TextView modelname;
        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.dialog_normaldet_pdi);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog1.setCanceledOnTouchOutside(false);
        btnnext = (Button) dialog1.findViewById(R.id.next);
        btnprev = (Button) dialog1.findViewById(R.id.prev);

        modelname = (TextView) dialog1.findViewById(R.id.modelname);
        tvsm = (EditText) dialog1.findViewById(R.id.tvsm);
        chassis = (EditText) dialog1.findViewById(R.id.chassis);
        bat = (EditText) dialog1.findViewById(R.id.bat);
        date_of_pdi = (Button) dialog1.findViewById(R.id.date_of_pdi);
        date_of_inv = (Button) dialog1.findViewById(R.id.date_of_inv);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        chassis.setText(AppVariables.VIN);
        chassis.setEnabled(false);
        if(AppVariables.BikeModel == 1)
        {
            modelname.setText(bikes[0]);
            AppVariables.bikemodel = bikes[0];
        }
        else
        if(AppVariables.BikeModel == 2)
        {
            modelname.setText(bikes[1]);
            AppVariables.bikemodel = bikes[1];
        }
        date_of_pdi.setText("Date of PDI : "+PresentDate());
        // showDialog(DATE_OF_PDI);
        AppVariables.date_of_pdi = PresentDate()+"(DDMMYYYY)";


        date_of_inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datecat = 2;
                showDialog(DATE_OF_INV);
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AppVariables.tvsm = tvsm.getText().toString();
                AppVariables.chassis_no = chassis.getText().toString();
                AppVariables.battery_no = bat.getText().toString();

                if(!AppVariables.tvsm.isEmpty() && !AppVariables.chassis_no.isEmpty() && !AppVariables.battery_no.isEmpty())
                {
                    dialog1.hide();
                    miss();
                }
                else
                {
                    Toast.makeText(PDIActivity.this, ""+AppVariables.MessagePDI, Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnprev.setVisibility(View.INVISIBLE);
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        dialog1.show();
    }
    //miscellanous points
    public void miss()
    {
        Button btnnext,btnprev;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_miscellanous_points_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final ToggleButton tbwashy,tbwashn;
        final ToggleButton tbnodenty,tbnodentn;
        final ToggleButton tbbatvolty,tbbatvoltn;

        tbwashy = (ToggleButton) dialog.findViewById(R.id.washy);
        tbwashn = (ToggleButton) dialog.findViewById(R.id.washn);

        tbnodenty = (ToggleButton) dialog.findViewById(R.id.denty);
        tbnodentn = (ToggleButton) dialog.findViewById(R.id.dentn);

        tbbatvolty = (ToggleButton) dialog.findViewById(R.id.batvoly);
        tbbatvoltn = (ToggleButton) dialog.findViewById(R.id.batvoln);

        tbwashy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbwashy.isChecked())
                {
                    AppVariables.washingstr = "YES";
                    tbwashy.setChecked(true);
                    tbwashn.setChecked(false);
                }
                else
                {
                    AppVariables.washingstr = "";

                    tbwashy.setChecked(false);
                    tbwashn.setChecked(true);
                }
            }
        });
        tbwashn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbwashn.isChecked())
                {
                    ReasonDailog("wash");
                    AppVariables.washingstr = "NO";
                    tbwashn.setChecked(true);
                    tbwashy.setChecked(false);
                }
                else
                {
                    AppVariables.washingstr = "";
                    tbwashn.setChecked(false);
                    tbwashy.setChecked(true);
                }
            }
        });

        tbnodenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbnodenty.isChecked())
                {
                    AppVariables.nodentstr = "YES";
                    tbnodenty.setChecked(true);
                    tbnodentn.setChecked(false);
                }
                else
                {
                    AppVariables.nodentstr = "";

                    tbnodenty.setChecked(false);
                    tbnodentn .setChecked(true);
                }
            }
        });
        tbnodentn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbwashn.isChecked())
                {
                    ReasonDailog("nodent");
                    AppVariables.nodentstr = "NO";
                    tbwashn.setChecked(true);
                    tbwashy.setChecked(false);
                }
                else
                {
                    AppVariables.nodentstr = "";
                    tbwashn.setChecked(false);
                    tbwashy.setChecked(true);
                }
            }
        });

        tbbatvolty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbbatvolty.isChecked())
                {
                    AppVariables.batvolstr = "YES";
                    tbbatvolty.setChecked(true);
                    tbbatvoltn.setChecked(false);
                }
                else
                {
                    AppVariables.batvolstr = "";

                    tbbatvolty.setChecked(false);
                    tbbatvoltn .setChecked(true);
                }
            }
        });
        tbbatvoltn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbbatvoltn.isChecked())
                {
                    ReasonDailog("balvol");
                    AppVariables.batvolstr = "NO";
                    tbbatvoltn.setChecked(true);
                    tbbatvolty.setChecked(false);
                }
                else
                {
                    AppVariables.batvolstr = "";
                    tbbatvoltn.setChecked(false);
                    tbbatvolty.setChecked(true);
                }
            }
        });

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(!AppVariables.washingstr.isEmpty() && !AppVariables.nodentstr.isEmpty() && !AppVariables.batvolstr.isEmpty())
                {
                    dialog.hide();
                    LocksOperations();
                }
                else
                {
                    Toast.makeText(PDIActivity.this, ""+AppVariables.MessagePDI, Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                NormalDetails();
            }
        });
        dialog.show();
    }

    public void ReasonDailog(final String  str)
    {
        final Dialog dialog = new Dialog(this);
        Button btnsbmt;
        dialog.setContentView(R.layout.dialog_reason_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final EditText etreason = (EditText) dialog.findViewById(R.id.reason);
        btnsbmt = (Button) dialog.findViewById(R.id.submit);
        btnsbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason = etreason.getText().toString();
                if(!reason.isEmpty())
                {
                    dialog.hide();
                    switch (str)
                    {
                        case "wash":AppVariables.washingstr_reason = reason;break;
                        case "nodent":AppVariables.nodentstr_reason = reason;break;
                        case "balvol":AppVariables.batvolstr_reason = reason;break;
                        case "Steering":AppVariables.Steering_reason = reason;break;
                        case "Fuel_Tank_Cap":AppVariables.Fuel_Tank_Cap_reason = reason;break;
                        case "Free_play_adjustments":AppVariables.Free_play_adjustments_reason = reason;break;
                        case "Br_front":AppVariables.Br_front_reason = reason;break;
                        case "Br_rear":AppVariables.Br_clutch_reason = reason;break;
                        case "Br_clutch":AppVariables.Fuel_Tank_Cap_reason = reason;break;
                        case "Br_Throttle":AppVariables.Br_Throttle_reason = reason;break;
                        case "Service_Rem":AppVariables.Service_Rem_reason = reason;break;
                        case "alignment":AppVariables.alignment_reason = reason;break;
                        case "slackness":AppVariables.slackness_reason = reason;break;
                        case "brfld_front":AppVariables.brfld_front_reason = reason;break;
                        case "brfld_rear":AppVariables.brfld_rear_reason = reason;break;
                        case "brfld_eng_sup":AppVariables.brfld_eng_sup_reason = reason;break;
                        case "brfld_cool_lvl":AppVariables.brfld_cool_lvl_reason = reason;break;
                        case "fast_front_wheel":AppVariables.fast_front_wheel_reason = reason;break;
                        case "fast_fork_center":AppVariables.fast_fork_center_reason = reason;break;
                        case "fast_Rear_shock_absorder":AppVariables.fast_Rear_shock_absorder_reason = reason;break;
                        case "fast_rear_wheel_axle":AppVariables.fast_rear_wheel_axle_reason = reason;break;
                        case "fast_front_rear_brake":AppVariables.fast_front_rear_brake_reason = reason;break;
                        case "fast_eng_mount":AppVariables.fast_eng_mount_reason = reason;break;
                        case "silencer_mount":AppVariables.silencer_mount_reason = reason;break;
                        case "sus_check_stick":AppVariables.sus_check_stick_reason = reason;break;
                        case "sus_rear_shock":AppVariables.sus_rear_shock_reason = reason;break;
                        case "sus_free_strok":AppVariables.sus_free_strok_reason = reason;break;
                        case "sus_wheels":AppVariables.sus_wheels_reason = reason;break;
                        case "tyre_front_solo":AppVariables.tyre_front_solo_reason = reason;break;
                        case "tyre_front_double":AppVariables.tyre_front_double_reason = reason;break;
                        case "tyre_rear_solo":AppVariables.tyre_rear_solo_reason = reason;break;
                        case "tyre_rear_double":AppVariables.tyre_rear_double_reason = reason;break;
                        case "liq_eng_oil":AppVariables.liq_eng_oil_reason = reason;break;
                        case "liq_eng_oil_lvl":AppVariables.liq_eng_oil_lvl_reason = reason;break;
                        case "liq_check_leakage":AppVariables.liq_check_leakage_reason = reason;break;
                        case "liq_coolant_level":AppVariables.liq_coolant_level_reason = reason;break;
                        case "lig_headlit_adj":AppVariables.lig_headlit_adj_reason = reason;break;
                        case "Ele_left_hand_switch":AppVariables.Ele_left_hand_switch_reason = reason;break;
                        case "Ele_horn":AppVariables.Ele_horn_reason = reason;break;
                        case "Ele_Indicator":AppVariables.Ele_Indicator_reason = reason;break;
                        case "Ele_pass_switch":AppVariables.Ele_pass_switch = reason;break;
                        case "Ele_All_bulbs":AppVariables.Ele_All_bulbs_reason = reason;break;
                        case "Ele_kill_switch":AppVariables.Ele_kill_switch_reason = reason;break;
                        case "Ele_Cluster":AppVariables.Ele_Cluster_reason = reason;break;
                        case "Ele_time_setting":AppVariables.Ele_time_setting_reason = reason;break;
                        case "Ele_Overspeed_setting":AppVariables.Ele_Overspeed_setting_reason = reason;break;
                        case "Legal_frnt_rear_num_plt":AppVariables.Legal_frnt_rear_num_plt_reason = reason;break;
                        case "Legal_rear_view_mirror":AppVariables.Legal_rear_view_mirror_reason = reason;break;
                        case "Legal_first_aid_kit":AppVariables.Legal_first_aid_kit_reason = reason;break;
                        case "Legal_tool_kit":AppVariables.Legal_tool_kit_reason = reason;break;
                        case "Legal_saree_guard":AppVariables.Legal_saree_guard_reason = reason;break;

                    }
                }
                else
                {
                    Toast.makeText(PDIActivity.this, "Please Enter Reason", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();

    }
    public void LocksOperations()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;
        final CheckBox cbignition,cbsteering,cbfuel_tank_cap;
        final ToggleButton tbignitiony,tbignitionn,tbsteeringy,tbsteeringn,tbfuely,tbfueln;
        dialog.setContentView(R.layout.dialog_locks_operations_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        tbignitiony = (ToggleButton) dialog.findViewById(R.id.ignitiony);
        tbignitionn = (ToggleButton) dialog.findViewById(R.id.ignitionn);

        tbsteeringy = (ToggleButton) dialog.findViewById(R.id.Steeringy);
        tbsteeringn = (ToggleButton) dialog.findViewById(R.id.Steeringn);

        tbfuely = (ToggleButton) dialog.findViewById(R.id.fuel_tanky);
        tbfueln = (ToggleButton) dialog.findViewById(R.id.fuel_tankn);


        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);

        tbignitiony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbignitiony.isChecked())
                {
                    AppVariables.Ignition = "YES";
                    tbignitiony.setChecked(true);
                    tbignitionn.setChecked(false);
                }
                else
                {
                    AppVariables.Ignition = "";

                    tbignitiony.setChecked(false);
                    tbignitionn .setChecked(true);
                }
            }
        });
        tbignitionn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbignitionn.isChecked())
                {
                    ReasonDailog("ignition");
                    AppVariables.Ignition= "NO";
                    tbignitionn.setChecked(true);
                    tbignitiony.setChecked(false);
                }
                else
                {
                    AppVariables.batvolstr = "";
                    tbignitionn.setChecked(false);
                    tbignitiony.setChecked(true);
                }
            }
        });

        tbsteeringy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbsteeringy.isChecked())
                {
                    AppVariables.Steering = "YES";
                    tbsteeringy.setChecked(true);
                    tbsteeringn.setChecked(false);
                }
                else
                {
                    AppVariables.Steering = "";

                    tbsteeringy.setChecked(false);
                    tbsteeringn .setChecked(true);
                }
            }
        });
        tbsteeringn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbsteeringn.isChecked())
                {
                    ReasonDailog("Steering");
                    AppVariables.Steering= "NO";
                    tbsteeringn.setChecked(true);
                    tbsteeringy.setChecked(false);
                }
                else
                {
                    AppVariables.Steering = "";
                    tbsteeringn.setChecked(false);
                    tbsteeringy.setChecked(true);
                }
            }
        });

        tbfuely.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfuely.isChecked())
                {
                    AppVariables.Fuel_Tank_Cap = "YES";
                    tbfuely.setChecked(true);
                    tbfueln.setChecked(false);
                }
                else
                {
                    AppVariables.Fuel_Tank_Cap = "";

                    tbfuely.setChecked(false);
                    tbfueln .setChecked(true);
                }
            }
        });
        tbfueln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfueln.isChecked())
                {
                    ReasonDailog("Fuel_Tank_Cap");
                    AppVariables.Fuel_Tank_Cap= "NO";
                    tbfueln.setChecked(true);
                    tbfuely.setChecked(false);
                }
                else
                {
                    AppVariables.Fuel_Tank_Cap = "";
                    tbsteeringn.setChecked(false);
                    tbsteeringy.setChecked(true);
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(!AppVariables.Ignition.isEmpty() && !AppVariables.Steering.isEmpty() && !AppVariables.Fuel_Tank_Cap.isEmpty())
                {
                    dialog.hide();
                    Operations();
                }
                else
                {
                    Toast.makeText(PDIActivity.this, ""+AppVariables.MessagePDI, Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                miss();
            }
        });
        dialog.show();
    }
    public void Operations()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;
        final ToggleButton tbfreeplayy,tbfreeplayn,tbfronty,tbfrontn,tbreary,tbrearn,tbclutchy,tbclutchn,tbtrottley,tbtrottlen,tbservericey,tbservericen;

        dialog.setContentView(R.layout.dialog_operations_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);

        tbfreeplayy = (ToggleButton) dialog.findViewById(R.id.freey);
        tbfreeplayn = (ToggleButton) dialog.findViewById(R.id.freen);

        tbfronty = (ToggleButton) dialog.findViewById(R.id.fronty);
        tbfrontn = (ToggleButton) dialog.findViewById(R.id.frontn);

        tbreary = (ToggleButton) dialog.findViewById(R.id.reary);
        tbrearn = (ToggleButton) dialog.findViewById(R.id.rearn);

        tbclutchy = (ToggleButton) dialog.findViewById(R.id.clutchy);
        tbclutchn = (ToggleButton) dialog.findViewById(R.id.clutchn);

        tbtrottley = (ToggleButton) dialog.findViewById(R.id.throttley);
        tbtrottlen = (ToggleButton) dialog.findViewById(R.id.throttlen);

        tbservericey = (ToggleButton) dialog.findViewById(R.id.serremy);
        tbservericen = (ToggleButton) dialog.findViewById(R.id.serremn);


        tbfreeplayy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfreeplayy.isChecked())
                {
                    AppVariables.Free_play_adjustments = "YES";
                    tbfreeplayy.setChecked(true);
                    tbfreeplayn.setChecked(false);
                }
                else
                {
                    AppVariables.Free_play_adjustments = "";
                    tbfreeplayy.setChecked(false);
                    tbfreeplayn .setChecked(true);
                }
            }
        });
        tbfreeplayn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfreeplayn.isChecked())
                {
                    ReasonDailog("Free_play_adjustments");
                    AppVariables.Free_play_adjustments= "NO";
                    tbfreeplayn.setChecked(true);
                    tbfreeplayy.setChecked(false);
                }
                else
                {
                    AppVariables.Free_play_adjustments = "";
                    tbfreeplayn.setChecked(false);
                    tbfreeplayy.setChecked(true);
                }
            }
        });


        tbfronty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfronty.isChecked())
                {
                    AppVariables.Br_front = "YES";
                    tbfronty.setChecked(true);
                    tbfrontn.setChecked(false);
                }
                else
                {
                    AppVariables.Br_front = "";
                    tbfronty.setChecked(false);
                    tbfrontn .setChecked(true);
                }
            }
        });
        tbfrontn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfrontn.isChecked())
                {
                    ReasonDailog("Br_front");
                    AppVariables.Br_front= "NO";
                    tbfrontn.setChecked(true);
                    tbfronty.setChecked(false);
                }
                else
                {
                    AppVariables.Br_front = "";
                    tbfrontn.setChecked(false);
                    tbfronty.setChecked(true);
                }
            }
        });

        tbreary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbreary.isChecked())
                {
                    AppVariables.Br_rear = "YES";
                    tbreary.setChecked(true);
                    tbrearn.setChecked(false);
                }
                else
                {
                    AppVariables.Br_rear = "";

                    tbfronty.setChecked(false);
                    tbfrontn .setChecked(true);
                }
            }
        });
        tbrearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbrearn.isChecked())
                {
                    ReasonDailog("Br_rear");
                    AppVariables.Br_rear= "NO";
                    tbrearn.setChecked(true);
                    tbreary.setChecked(false);
                }
                else
                {
                    AppVariables.Br_rear = "";
                    tbfrontn.setChecked(false);
                    tbfronty.setChecked(true);
                }
            }
        });

        tbclutchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbclutchy.isChecked())
                {
                    AppVariables.Br_clutch = "YES";
                    tbclutchy.setChecked(true);
                    tbclutchn.setChecked(false);
                }
                else
                {
                    AppVariables.Br_clutch = "";

                    tbclutchy.setChecked(false);
                    tbclutchn .setChecked(true);
                }
            }
        });
        tbclutchn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbclutchn.isChecked())
                {
                    ReasonDailog("Br_clutch");
                    AppVariables.Br_clutch= "NO";
                    tbclutchn.setChecked(true);
                    tbclutchy.setChecked(false);
                }
                else
                {
                    AppVariables.Br_clutch = "";
                    tbclutchn.setChecked(false);
                    tbclutchy.setChecked(true);
                }
            }
        });

        tbtrottley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbtrottley.isChecked())
                {
                    AppVariables.Br_Throttle = "YES";
                    tbtrottley.setChecked(true);
                    tbtrottlen.setChecked(false);
                }
                else
                {
                    AppVariables.Br_Throttle = "";

                    tbtrottley.setChecked(false);
                    tbtrottlen .setChecked(true);
                }
            }
        });
        tbtrottlen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbtrottlen.isChecked())
                {
                    ReasonDailog("Br_Throttle");
                    AppVariables.Br_Throttle= "NO";
                    tbtrottlen.setChecked(true);
                    tbtrottley.setChecked(false);
                }
                else
                {
                    AppVariables.Br_Throttle = "";
                    tbtrottlen.setChecked(false);
                    tbtrottley.setChecked(true);
                }
            }
        });

        tbservericey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbservericey.isChecked())
                {
                    AppVariables.Service_Rem = "YES";
                    tbservericey.setChecked(true);
                    tbservericen.setChecked(false);
                }
                else
                {
                    AppVariables.Service_Rem = "";
                    tbservericey.setChecked(false);
                    tbservericen .setChecked(true);
                }
            }
        });
        tbservericen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbservericen.isChecked())
                {
                    ReasonDailog("Service_Rem");
                    AppVariables.Service_Rem= "NO";
                    tbservericen.setChecked(true);
                    tbservericey.setChecked(false);
                }
                else
                {
                    AppVariables.Service_Rem = "";
                    tbservericen.setChecked(false);
                    tbservericey.setChecked(true);
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //if(!AppVariables.Free_play_adjustments.isEmpty() && !AppVariables.Br_front.isEmpty() && !AppVariables.Br_rear.isEmpty() && )
                dialog.hide();
                DriveChain();
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                LocksOperations();
            }
        });
        dialog.show();
    }
    public void DriveChain()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;
        final ToggleButton tbaligny,tbalignn,tbslacky,tbslackn,tbfronty,tbfrontn,tbreary,tbrearn,tbbeng_susy,tbbeng_susn,tbcoolntlvly,tbcoolntlvln;
        dialog.setContentView(R.layout.dialog_drive_chain_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);

        tbaligny = (ToggleButton) dialog.findViewById(R.id.aligny);
        tbalignn = (ToggleButton) dialog.findViewById(R.id.alignn);

        tbslacky = (ToggleButton) dialog.findViewById(R.id.slacky);
        tbslackn = (ToggleButton) dialog.findViewById(R.id.slackn);

        tbfronty = (ToggleButton) dialog.findViewById(R.id.fronty);
        tbfrontn = (ToggleButton) dialog.findViewById(R.id.frontn);

        tbreary = (ToggleButton) dialog.findViewById(R.id.reary);
        tbrearn = (ToggleButton) dialog.findViewById(R.id.rearn);

        tbbeng_susy = (ToggleButton) dialog.findViewById(R.id.eng_susy);
        tbbeng_susn = (ToggleButton) dialog.findViewById(R.id.eng_susn);

        tbcoolntlvly = (ToggleButton) dialog.findViewById(R.id.cool_lvly);
        tbcoolntlvln = (ToggleButton) dialog.findViewById(R.id.cool_lvln);


        tbaligny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbaligny.isChecked())
                {
                    AppVariables.alignment = "YES";
                    tbaligny.setChecked(true);
                    tbalignn.setChecked(false);
                }
                else
                {
                    AppVariables.alignment = "";

                    tbaligny.setChecked(false);
                    tbalignn .setChecked(true);
                }
            }
        });
        tbalignn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbalignn.isChecked())
                {
                    ReasonDailog("alignment");
                    AppVariables.alignment= "NO";
                    tbalignn.setChecked(true);
                    tbaligny.setChecked(false);
                }
                else
                {
                    AppVariables.alignment = "";
                    tbalignn.setChecked(false);
                    tbaligny.setChecked(true);
                }
            }
        });


        tbslacky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbslacky.isChecked())
                {
                    AppVariables.slackness = "YES";
                    tbslacky.setChecked(true);
                    tbslackn.setChecked(false);
                }
                else
                {
                    AppVariables.slackness = "";

                    tbslacky.setChecked(false);
                    tbslackn .setChecked(true);
                }
            }
        });
        tbslackn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbslackn.isChecked())
                {
                    ReasonDailog("slackness");
                    AppVariables.slackness= "NO";
                    tbslackn.setChecked(true);
                    tbslacky.setChecked(false);
                }
                else
                {
                    AppVariables.slackness = "";
                    tbslackn.setChecked(false);
                    tbslacky.setChecked(true);
                }
            }
        });

        tbfronty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfronty.isChecked())
                {
                    AppVariables.brfld_front = "YES";
                    tbfronty.setChecked(true);
                    tbfrontn.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_front = "";

                    tbfronty.setChecked(false);
                    tbfrontn .setChecked(true);
                }
            }
        });
        tbfrontn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfrontn.isChecked())
                {
                    ReasonDailog("brfld_front");
                    AppVariables.brfld_front= "NO";
                    tbfrontn.setChecked(true);
                    tbfronty.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_front = "";
                    tbfrontn.setChecked(false);
                    tbfronty.setChecked(true);
                }
            }
        });

        tbreary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbreary.isChecked())
                {
                    AppVariables.brfld_rear = "YES";
                    tbreary.setChecked(true);
                    tbrearn.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_rear = "";

                    tbreary.setChecked(false);
                    tbrearn .setChecked(true);
                }
            }
        });
        tbrearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbrearn.isChecked())
                {
                    ReasonDailog("brfld_rear");
                    AppVariables.brfld_rear= "NO";
                    tbrearn.setChecked(true);
                    tbreary.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_rear = "";
                    tbalignn.setChecked(false);
                    tbaligny.setChecked(true);
                }
            }
        });

        tbbeng_susy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbbeng_susy.isChecked())
                {
                    AppVariables.brfld_eng_sup = "YES";
                    tbbeng_susy.setChecked(true);
                    tbbeng_susn.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_eng_sup = "";

                    tbbeng_susy.setChecked(false);
                    tbbeng_susn .setChecked(true);
                }
            }
        });
        tbbeng_susn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbbeng_susn.isChecked())
                {
                    ReasonDailog("brfld_eng_sup");
                    AppVariables.brfld_eng_sup= "NO";
                    tbbeng_susn.setChecked(true);
                    tbbeng_susy.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_eng_sup = "";
                    tbbeng_susn.setChecked(false);
                    tbbeng_susy.setChecked(true);
                }
            }
        });

        tbcoolntlvly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbcoolntlvly.isChecked())
                {
                    AppVariables.brfld_cool_lvl = "YES";
                    tbcoolntlvly.setChecked(true);
                    tbcoolntlvln.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_cool_lvl = "";

                    tbcoolntlvly.setChecked(false);
                    tbcoolntlvln .setChecked(true);
                }
            }
        });
        tbcoolntlvln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbalignn.isChecked())
                {
                    ReasonDailog("brfld_cool_lvl");
                    AppVariables.brfld_cool_lvl= "NO";
                    tbcoolntlvln.setChecked(true);
                    tbcoolntlvly.setChecked(false);
                }
                else
                {
                    AppVariables.brfld_cool_lvl = "";
                    tbalignn.setChecked(false);
                    tbcoolntlvly.setChecked(true);
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Fasterners_Check();
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Operations();
            }
        });
        dialog.show();

    }
    public void Fasterners_Check ()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;

        final ToggleButton  tbfrontwheely,tbfrontwheeln,
                            tbfork_centery,tbfork_centern,
                            tbmounting_bolty,tbmounting_boltn,
                            tbrear_shocky,tbrear_shockn,
                            tbwheel_axley,tbwheel_axlen,
                            tbfront_reary,tbfront_rearn,
                            tbeng_mounty,tbeng_mountn,
                            tbsil_mounty,tbsil_mountn;
        dialog.setContentView(R.layout.dialog_fasterners_check_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);


        tbfrontwheely = (ToggleButton) dialog.findViewById(R.id.front_wheely);
        tbfrontwheeln = (ToggleButton) dialog.findViewById(R.id.front_wheeln);

        tbfork_centery = (ToggleButton) dialog.findViewById(R.id.fork_centery);
        tbfork_centern = (ToggleButton) dialog.findViewById(R.id.fork_centern);

        tbmounting_bolty = (ToggleButton) dialog.findViewById(R.id.handle_bary);
        tbmounting_boltn= (ToggleButton) dialog.findViewById(R.id.handle_barn);

        tbrear_shocky = (ToggleButton) dialog.findViewById(R.id.rear_shocky);
        tbrear_shockn = (ToggleButton) dialog.findViewById(R.id.rear_shockn);

        tbwheel_axley = (ToggleButton) dialog.findViewById(R.id.rear_wheely);
        tbwheel_axlen = (ToggleButton) dialog.findViewById(R.id.rear_wheeln);

        tbfront_reary = (ToggleButton) dialog.findViewById(R.id.front_reary);
        tbfront_rearn = (ToggleButton) dialog.findViewById(R.id.front_rearn);

        tbeng_mounty = (ToggleButton) dialog.findViewById(R.id.engine_mounty);
        tbeng_mountn = (ToggleButton) dialog.findViewById(R.id.engine_mountn);

        tbsil_mounty = (ToggleButton) dialog.findViewById(R.id.silencer_mounty);
        tbsil_mountn = (ToggleButton) dialog.findViewById(R.id.silencer_mountn);

        tbfrontwheely.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfrontwheely.isChecked())
                {
                    AppVariables.fast_front_wheel = "YES";
                    tbfrontwheely.setChecked(true);
                    tbfrontwheeln.setChecked(false);
                }
                else
                {
                    AppVariables.fast_front_wheel = "";

                    tbfrontwheely.setChecked(false);
                    tbfrontwheeln .setChecked(true);
                }
            }
        });
        tbfrontwheeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfrontwheeln.isChecked())
                {
                    ReasonDailog("fast_front_wheel");
                    AppVariables.fast_front_wheel= "NO";
                    tbfrontwheeln.setChecked(true);
                    tbfrontwheely.setChecked(false);
                }
                else
                {
                    AppVariables.fast_front_wheel = "";
                    tbfrontwheeln.setChecked(false);
                    tbfrontwheely.setChecked(true);
                }
            }
        });

        tbfork_centery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfork_centery.isChecked())
                {
                    AppVariables.fast_fork_center = "YES";
                    tbfork_centery.setChecked(true);
                    tbfork_centern.setChecked(false);
                }
                else
                {
                    AppVariables.fast_fork_center = "";

                    tbfork_centery.setChecked(false);
                    tbfork_centern .setChecked(true);
                }
            }
        });
        tbfork_centern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfork_centern.isChecked())
                {
                    ReasonDailog("fast_fork_center");
                    AppVariables.fast_fork_center= "NO";
                    tbfork_centern.setChecked(true);
                    tbfork_centery.setChecked(false);
                }
                else
                {
                    AppVariables.fast_fork_center = "";
                    tbfork_centern.setChecked(false);
                    tbfork_centery.setChecked(true);
                }
            }
        });

        tbmounting_bolty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbmounting_bolty.isChecked())
                {
                    AppVariables.fast_Handle_Bar = "YES";
                    tbmounting_bolty.setChecked(true);
                    tbmounting_boltn.setChecked(false);
                }
                else
                {
                    AppVariables.fast_Handle_Bar = "";

                    tbmounting_bolty.setChecked(false);
                    tbmounting_boltn .setChecked(true);
                }
            }
        });
        tbmounting_boltn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbmounting_boltn.isChecked())
                {
                    ReasonDailog("fast_Handle_Bar");
                    AppVariables.fast_Handle_Bar= "NO";
                    tbmounting_boltn.setChecked(true);
                    tbmounting_bolty.setChecked(false);
                }
                else
                {
                    AppVariables.fast_Handle_Bar = "";
                    tbmounting_boltn.setChecked(false);
                    tbmounting_bolty.setChecked(true);
                }
            }
        });

        tbrear_shocky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbrear_shocky.isChecked())
                {
                    AppVariables.fast_Rear_shock_absorder = "YES";
                    tbrear_shocky.setChecked(true);
                    tbrear_shockn.setChecked(false);
                }
                else
                {
                    AppVariables.fast_Rear_shock_absorder = "";

                    tbrear_shocky.setChecked(false);
                    tbrear_shockn .setChecked(true);
                }
            }
        });
        tbrear_shockn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbrear_shockn.isChecked())
                {
                    ReasonDailog("fast_Rear_shock_absorder");
                    AppVariables.fast_Rear_shock_absorder= "NO";
                    tbrear_shockn.setChecked(true);
                    tbrear_shocky.setChecked(false);
                }
                else
                {
                    AppVariables.fast_Rear_shock_absorder = "";
                    tbrear_shockn.setChecked(false);
                    tbrear_shocky.setChecked(true);
                }
            }
        });

        tbwheel_axley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbwheel_axley.isChecked())
                {
                    AppVariables.fast_rear_wheel_axle = "YES";
                    tbwheel_axley.setChecked(true);
                    tbwheel_axlen.setChecked(false);
                }
                else
                {
                    AppVariables.fast_rear_wheel_axle = "";

                    tbwheel_axley.setChecked(false);
                    tbwheel_axlen .setChecked(true);
                }
            }
        });
        tbwheel_axlen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbwheel_axlen.isChecked())
                {
                    ReasonDailog("fast_rear_wheel_axle");
                    AppVariables.fast_rear_wheel_axle= "NO";
                    tbwheel_axlen.setChecked(true);
                    tbwheel_axley.setChecked(false);
                }
                else
                {
                    AppVariables.fast_rear_wheel_axle = "";
                    tbwheel_axlen.setChecked(false);
                    tbwheel_axley.setChecked(true);
                }
            }
        });

        tbfront_reary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfront_reary.isChecked())
                {
                    AppVariables.fast_front_rear_brake = "YES";
                    tbfront_reary.setChecked(true);
                    tbfront_rearn.setChecked(false);
                }
                else
                {
                    AppVariables.fast_front_rear_brake = "";

                    tbfront_reary.setChecked(false);
                    tbfront_rearn .setChecked(true);
                }
            }
        });
        tbfront_rearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfront_rearn.isChecked())
                {
                    ReasonDailog("fast_front_rear_brake");
                    AppVariables.fast_front_rear_brake= "NO";
                    tbfront_rearn.setChecked(true);
                    tbfront_reary.setChecked(false);
                }
                else
                {
                    AppVariables.fast_front_rear_brake = "";
                    tbfront_rearn.setChecked(false);
                    tbfront_reary.setChecked(true);
                }
            }
        });

        tbeng_mounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbeng_mounty.isChecked())
                {
                    AppVariables.fast_eng_mount = "YES";
                    tbeng_mounty.setChecked(true);
                    tbeng_mountn.setChecked(false);
                }
                else
                {
                    AppVariables.fast_eng_mount = "";

                    tbeng_mounty.setChecked(false);
                    tbeng_mountn .setChecked(true);
                }
            }
        });
        tbeng_mountn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbeng_mountn.isChecked())
                {
                    ReasonDailog("fast_eng_mount");
                    AppVariables.fast_eng_mount= "NO";
                    tbeng_mountn.setChecked(true);
                    tbeng_mounty.setChecked(false);
                }
                else
                {
                    AppVariables.fast_eng_mount = "";
                    tbeng_mountn.setChecked(false);
                    tbeng_mounty.setChecked(true);
                }
            }
        });

        tbsil_mounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbsil_mounty.isChecked())
                {
                    AppVariables.silencer_mount = "YES";
                    tbsil_mounty.setChecked(true);
                    tbsil_mountn.setChecked(false);
                }
                else
                {
                    AppVariables.silencer_mount = "";

                    tbsil_mounty.setChecked(false);
                    tbsil_mountn .setChecked(true);
                }
            }
        });
        tbsil_mountn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbsil_mountn.isChecked())
                {
                    ReasonDailog("silencer_mount");
                    AppVariables.silencer_mount= "NO";
                    tbsil_mountn.setChecked(true);
                    tbsil_mounty.setChecked(false);
                }
                else
                {
                    AppVariables.silencer_mount = "";
                    tbsil_mountn.setChecked(false);
                    tbsil_mounty.setChecked(true);
                }
            }
        });


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Suspension();
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                DriveChain();
            }
        });
        dialog.show();

    }
    public void Suspension ()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;

        final ToggleButton tbsticky,tbstickn,tbcbrear_shocky,tbcbrear_shockn,tbfreestroky,tbfreestrokn,tbwheelsy,tbwheelsn;
        final ToggleButton tbfrontsoloy,tbfrontsolon,tbfrontdoubley,tbfrontdoublen,tbrear_soloy,tbrear_solon,tbrear_doubley,tbrear_doublen;
        dialog.setContentView(R.layout.dialog_suspension_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);

        tbsticky = (ToggleButton) dialog.findViewById(R.id.stickyy);
        tbstickn = (ToggleButton) dialog.findViewById(R.id.stickyn);

        tbcbrear_shocky = (ToggleButton) dialog.findViewById(R.id.rear_shocky);
        tbcbrear_shockn = (ToggleButton) dialog.findViewById(R.id.rear_shockn);

        tbfreestroky = (ToggleButton) dialog.findViewById(R.id.free_stroky);
        tbfreestrokn = (ToggleButton) dialog.findViewById(R.id.free_strokn);

        tbwheelsy = (ToggleButton) dialog.findViewById(R.id.wheelsy);
        tbwheelsn = (ToggleButton) dialog.findViewById(R.id.wheelsn);

        tbfrontsoloy = (ToggleButton) dialog.findViewById(R.id.front_soloy);
        tbfrontsolon = (ToggleButton) dialog.findViewById(R.id.front_solon);

        tbfrontdoubley = (ToggleButton) dialog.findViewById(R.id.front_doubley);
        tbfrontdoublen = (ToggleButton) dialog.findViewById(R.id.front_doublen);

        tbrear_soloy = (ToggleButton) dialog.findViewById(R.id.rear_soloy);
        tbrear_solon = (ToggleButton) dialog.findViewById(R.id.rear_solon);

        tbrear_doubley = (ToggleButton) dialog.findViewById(R.id.rear_doubley);
        tbrear_doublen = (ToggleButton) dialog.findViewById(R.id.rear_doublen);

        tbsticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbsticky.isChecked())
                {
                    AppVariables.sus_check_stick = "YES";
                    tbsticky.setChecked(true);
                    tbstickn.setChecked(false);
                }
                else
                {
                    AppVariables.sus_check_stick = "";

                    tbsticky.setChecked(false);
                    tbstickn .setChecked(true);
                }
            }
        });
        tbstickn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbstickn.isChecked())
                {
                    ReasonDailog("sus_check_stick");
                    AppVariables.sus_check_stick= "NO";
                    tbstickn.setChecked(true);
                    tbsticky.setChecked(false);
                }
                else
                {
                    AppVariables.sus_check_stick = "";
                    tbstickn.setChecked(false);
                    tbsticky.setChecked(true);
                }
            }
        });

        tbcbrear_shocky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbcbrear_shocky.isChecked())
                {
                    AppVariables.sus_rear_shock = "YES";
                    tbcbrear_shocky.setChecked(true);
                    tbcbrear_shockn.setChecked(false);
                }
                else
                {
                    AppVariables.sus_rear_shock = "";

                    tbcbrear_shocky.setChecked(false);
                    tbcbrear_shockn .setChecked(true);
                }
            }
        });
        tbcbrear_shockn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbcbrear_shockn.isChecked())
                {
                    ReasonDailog("sus_rear_shock");
                    AppVariables.sus_rear_shock= "NO";
                    tbcbrear_shockn.setChecked(true);
                    tbcbrear_shocky.setChecked(false);
                }
                else
                {
                    AppVariables.sus_rear_shock = "";
                    tbcbrear_shockn.setChecked(false);
                    tbcbrear_shocky.setChecked(true);
                }
            }
        });

        tbfreestroky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfreestroky.isChecked())
                {
                    AppVariables.sus_free_strok = "YES";
                    tbfreestroky.setChecked(true);
                    tbfreestrokn.setChecked(false);
                }
                else
                {
                    AppVariables.sus_free_strok = "";

                    tbsticky.setChecked(false);
                    tbstickn .setChecked(true);
                }
            }
        });
        tbfreestrokn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfreestrokn.isChecked())
                {
                    ReasonDailog("sus_free_strok");
                    AppVariables.sus_free_strok= "NO";
                    tbfreestrokn.setChecked(true);
                    tbfreestroky.setChecked(false);
                }
                else
                {
                    AppVariables.sus_free_strok = "";
                    tbfreestrokn.setChecked(false);
                    tbfreestroky.setChecked(true);
                }
            }
        });

        tbwheelsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbwheelsy.isChecked())
                {
                    AppVariables.sus_wheels = "YES";
                    tbwheelsy.setChecked(true);
                    tbwheelsn.setChecked(false);
                }
                else
                {
                    AppVariables.sus_wheels = "";

                    tbwheelsy.setChecked(false);
                    tbwheelsn .setChecked(true);
                }
            }
        });
        tbwheelsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbwheelsn.isChecked())
                {
                    ReasonDailog("sus_wheels");
                    AppVariables.sus_wheels= "NO";
                    tbwheelsn.setChecked(true);
                    tbwheelsy.setChecked(false);
                }
                else
                {
                    AppVariables.sus_wheels = "";
                    tbwheelsn.setChecked(false);
                    tbwheelsy.setChecked(true);
                }
            }
        });

        tbfrontsoloy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfrontsoloy.isChecked())
                {
                    AppVariables.tyre_front_solo = "YES";
                    tbfrontsoloy.setChecked(true);
                    tbfrontsolon.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_front_solo = "";

                    tbfrontsoloy.setChecked(false);
                    tbfrontsolon.setChecked(true);
                }
            }
        });
        tbfrontsolon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfrontsolon.isChecked())
                {
                    ReasonDailog("tyre_front_solo");
                    AppVariables.tyre_front_solo= "NO";
                    tbfrontsolon.setChecked(true);
                    tbfrontsoloy.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_front_solo = "";
                    tbfrontsolon.setChecked(false);
                    tbfrontsoloy.setChecked(true);
                }
            }
        });

        tbfrontdoubley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfrontdoubley.isChecked())
                {
                    AppVariables.tyre_front_double = "YES";
                    tbfrontdoubley.setChecked(true);
                    tbfrontdoublen.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_front_double = "";

                    tbfrontdoubley.setChecked(false);
                    tbfrontdoublen .setChecked(true);
                }
            }
        });
        tbfrontdoublen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbfrontdoublen.isChecked())
                {
                    ReasonDailog("tyre_front_double");
                    AppVariables.tyre_front_double= "NO";
                    tbfrontdoublen.setChecked(true);
                    tbfrontdoubley.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_front_double = "";
                    tbfrontdoublen.setChecked(false);
                    tbfrontdoubley.setChecked(true);
                }
            }
        });

        tbrear_soloy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbrear_soloy.isChecked())
                {
                    AppVariables.tyre_rear_solo = "YES";
                    tbrear_soloy.setChecked(true);
                    tbrear_solon.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_rear_solo = "";

                    tbrear_soloy.setChecked(false);
                    tbrear_solon .setChecked(true);
                }
            }
        });
        tbrear_solon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbrear_solon.isChecked())
                {
                    ReasonDailog("tyre_rear_solo");
                    AppVariables.tyre_rear_solo= "NO";
                    tbrear_solon.setChecked(true);
                    tbrear_soloy.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_rear_solo = "";
                    tbrear_solon.setChecked(false);
                    tbrear_soloy.setChecked(true);
                }
            }
        });

        tbrear_doubley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbrear_doubley.isChecked())
                {
                    AppVariables.tyre_rear_double = "YES";
                    tbrear_doubley.setChecked(true);
                    tbrear_doublen.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_rear_double = "";

                    tbrear_doubley.setChecked(false);
                    tbrear_doublen .setChecked(true);
                }
            }
        });
        tbrear_doublen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbrear_doublen.isChecked())
                {
                    ReasonDailog("tyre_rear_double");
                    AppVariables.tyre_rear_double= "NO";
                    tbrear_doublen.setChecked(true);
                    tbrear_doubley.setChecked(false);
                }
                else
                {
                    AppVariables.tyre_rear_double = "";
                    tbrear_doublen.setChecked(false);
                    tbrear_doubley.setChecked(true);
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Liquids();
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Fasterners_Check();
            }
        });
        dialog.show();

    }
    public void Liquids ()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;


        final ToggleButton tbeng_oily,tbeng_oiln,tbeng_oil_lvly,tbeng_oil_lvln;
        final ToggleButton tbcheck_any_leaky,tbcheck_any_leakn,tbcoolant_lvly,tbcoolant_lvln;
        final ToggleButton tbheadlt_adjy,tbheadlt_adjn;

        dialog.setContentView(R.layout.dialog_liquids_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        tbeng_oily = (ToggleButton) dialog.findViewById(R.id.eng_oily);
        tbeng_oiln = (ToggleButton) dialog.findViewById(R.id.eng_oiln);

        tbeng_oil_lvly = (ToggleButton) dialog.findViewById(R.id.eng_oil_lvly);
        tbeng_oil_lvln = (ToggleButton) dialog.findViewById(R.id.eng_oil_lvln);

        tbcheck_any_leaky = (ToggleButton) dialog.findViewById(R.id.check_leaky);
        tbcheck_any_leakn = (ToggleButton) dialog.findViewById(R.id.check_leakn);

        tbcoolant_lvly = (ToggleButton) dialog.findViewById(R.id.coolant_lvly);
        tbcoolant_lvln = (ToggleButton) dialog.findViewById(R.id.coolant_lvln);

        tbheadlt_adjy = (ToggleButton) dialog.findViewById(R.id.headlt_adjy);
        tbheadlt_adjn = (ToggleButton) dialog.findViewById(R.id.headlt_adjn);


        tbeng_oily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbeng_oily.isChecked())
                {
                    AppVariables.liq_eng_oil = "YES";
                    tbeng_oily.setChecked(true);
                    tbeng_oiln.setChecked(false);
                }
                else
                {
                    AppVariables.liq_eng_oil = "";

                    tbeng_oily.setChecked(false);
                    tbeng_oiln .setChecked(true);
                }
            }
        });
        tbeng_oiln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbeng_oiln.isChecked())
                {
                    ReasonDailog("liq_eng_oil");
                    AppVariables.liq_eng_oil= "NO";
                    tbeng_oiln.setChecked(true);
                    tbeng_oily.setChecked(false);
                }
                else
                {
                    AppVariables.liq_eng_oil = "";
                    tbeng_oiln.setChecked(false);
                    tbeng_oily.setChecked(true);
                }
            }
        });

        tbeng_oil_lvly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbeng_oil_lvly.isChecked())
                {
                    AppVariables.liq_eng_oil_lvl = "YES";
                    tbeng_oil_lvly.setChecked(true);
                    tbeng_oil_lvln.setChecked(false);
                }
                else
                {
                    AppVariables.liq_eng_oil_lvl = "";

                    tbeng_oil_lvly.setChecked(false);
                    tbeng_oil_lvln .setChecked(true);
                }
            }
        });
        tbeng_oil_lvln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbeng_oil_lvln.isChecked())
                {
                    ReasonDailog("liq_eng_oil_lvl");
                    AppVariables.liq_eng_oil_lvl= "NO";
                    tbeng_oil_lvln.setChecked(true);
                    tbeng_oil_lvly.setChecked(false);
                }
                else
                {
                    AppVariables.liq_eng_oil_lvl = "";
                    tbeng_oil_lvln.setChecked(false);
                    tbeng_oil_lvly.setChecked(true);
                }
            }
        });

        tbcheck_any_leaky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbcheck_any_leaky.isChecked())
                {
                    AppVariables.liq_check_leakage = "YES";
                    tbcheck_any_leaky.setChecked(true);
                    tbcheck_any_leakn.setChecked(false);
                }
                else
                {
                    AppVariables.liq_check_leakage = "";

                    tbcheck_any_leaky.setChecked(false);
                    tbcheck_any_leakn .setChecked(true);
                }
            }
        });
        tbcheck_any_leakn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbcheck_any_leakn.isChecked())
                {
                    ReasonDailog("liq_check_leakage");
                    AppVariables.liq_check_leakage= "NO";
                    tbcheck_any_leakn.setChecked(true);
                    tbcheck_any_leaky.setChecked(false);
                }
                else
                {
                    AppVariables.liq_check_leakage = "";
                    tbcheck_any_leakn.setChecked(false);
                    tbcheck_any_leaky.setChecked(true);
                }
            }
        });

        tbcoolant_lvly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbcoolant_lvly.isChecked())
                {
                    AppVariables.liq_coolant_level = "YES";
                    tbcoolant_lvly.setChecked(true);
                    tbcoolant_lvln.setChecked(false);
                }
                else
                {
                    AppVariables.liq_coolant_level = "";

                    tbcoolant_lvly.setChecked(false);
                    tbcoolant_lvln .setChecked(true);
                }
            }
        });
        tbcoolant_lvln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbcoolant_lvln.isChecked())
                {
                    ReasonDailog("liq_coolant_level");
                    AppVariables.liq_coolant_level= "NO";
                    tbcoolant_lvln.setChecked(true);
                    tbcoolant_lvly.setChecked(false);
                }
                else
                {
                    AppVariables.liq_coolant_level = "";
                    tbcoolant_lvln.setChecked(false);
                    tbcoolant_lvly.setChecked(true);
                }
            }
        });

        tbheadlt_adjy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbheadlt_adjy.isChecked())
                {
                    AppVariables.lig_headlit_adj = "YES";
                    tbheadlt_adjy.setChecked(true);
                    tbheadlt_adjn.setChecked(false);
                }
                else
                {
                    AppVariables.lig_headlit_adj = "";

                    tbheadlt_adjy.setChecked(false);
                    tbheadlt_adjn .setChecked(true);
                }
            }
        });
        tbheadlt_adjn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbheadlt_adjn.isChecked())
                {
                    ReasonDailog("lig_headlit_adj");
                    AppVariables.lig_headlit_adj= "NO";
                    tbheadlt_adjn.setChecked(true);
                    tbheadlt_adjy.setChecked(false);
                }
                else
                {
                    AppVariables.lig_headlit_adj = "";
                    tbheadlt_adjn.setChecked(false);
                    tbheadlt_adjy.setChecked(true);
                }
            }
        });


        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Electricals();
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Suspension();
            }
        });
        dialog.show();

    }
    public void Electricals()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;
        dialog.setContentView(R.layout.dialog_electricals_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final ToggleButton tbleft_nhd_switchy,tbleft_nhd_switchn;
        final ToggleButton tbhorny,tbhornn;
        final ToggleButton tbindicatory,tbindicatorn;
        final ToggleButton tbpass_switchy,tbpass_switchn;
        final ToggleButton tballbulbsy,tballbulbsn;
        final ToggleButton tbkill_switchy,tbkill_switchn;
        final ToggleButton tbclustery,tbclustern;
        final ToggleButton tbtime_settingy,tbtime_settingn;
        final ToggleButton tboverspeed_sety,tboverspeed_setn;

        tbleft_nhd_switchy = (ToggleButton) dialog.findViewById(R.id.left_hand_switchy);
        tbleft_nhd_switchn = (ToggleButton) dialog.findViewById(R.id.left_hand_switchn);

        tbhorny = (ToggleButton) dialog.findViewById(R.id.horny);
        tbhornn = (ToggleButton) dialog.findViewById(R.id.hornn);

        tbindicatory = (ToggleButton) dialog.findViewById(R.id.indicatory);
        tbindicatorn = (ToggleButton) dialog.findViewById(R.id.indicatorn);

        tbpass_switchy = (ToggleButton) dialog.findViewById(R.id.pass_switchy);
        tbpass_switchn = (ToggleButton) dialog.findViewById(R.id.pass_switchn);

        tballbulbsy = (ToggleButton) dialog.findViewById(R.id.allbulbsy);
        tballbulbsn = (ToggleButton) dialog.findViewById(R.id.allbulbsn);

        tbkill_switchy = (ToggleButton) dialog.findViewById(R.id.kill_switchy);
        tbkill_switchn = (ToggleButton) dialog.findViewById(R.id.kill_switchn);

        tbclustery = (ToggleButton) dialog.findViewById(R.id.clustery);
        tbclustern = (ToggleButton) dialog.findViewById(R.id.clustern);

        tbtime_settingy = (ToggleButton) dialog.findViewById(R.id.time_settingy);
        tbtime_settingn = (ToggleButton) dialog.findViewById(R.id.time_settingn);

        tboverspeed_sety = (ToggleButton) dialog.findViewById(R.id.overspeed_setngy);
        tboverspeed_setn = (ToggleButton) dialog.findViewById(R.id.overspeed_setngn);


        tbleft_nhd_switchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbleft_nhd_switchy.isChecked())
                {
                    AppVariables.Ele_left_hand_switch = "YES";
                    tbleft_nhd_switchy.setChecked(true);
                    tbleft_nhd_switchn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_left_hand_switch = "";

                    tbleft_nhd_switchy.setChecked(false);
                    tbleft_nhd_switchn .setChecked(true);
                }
            }
        });
        tbleft_nhd_switchn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbleft_nhd_switchn.isChecked())
                {
                    ReasonDailog("Ele_left_hand_switch");
                    AppVariables.Ele_left_hand_switch= "NO";
                    tbleft_nhd_switchn.setChecked(true);
                    tbleft_nhd_switchy.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_left_hand_switch = "";
                    tbleft_nhd_switchn.setChecked(false);
                    tbleft_nhd_switchy.setChecked(true);
                }
            }
        });


        tbhorny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbhorny.isChecked())
                {
                    AppVariables.Ele_horn = "YES";
                    tbhorny.setChecked(true);
                    tbhornn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_horn = "";

                    tbhorny.setChecked(false);
                    tbhornn .setChecked(true);
                }
            }
        });
        tbhornn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbhornn.isChecked())
                {
                    ReasonDailog("Ele_horn");
                    AppVariables.Ele_horn= "NO";
                    tbhornn.setChecked(true);
                    tbhorny.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_horn = "";
                    tbhornn.setChecked(false);
                    tbhorny.setChecked(true);
                }
            }
        });

        tbindicatory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbindicatory.isChecked())
                {
                    AppVariables.Ele_Indicator = "YES";
                    tbindicatory.setChecked(true);
                    tbindicatorn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_Indicator = "";

                    tbindicatory.setChecked(false);
                    tbindicatorn .setChecked(true);
                }
            }
        });
        tbindicatorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbindicatorn.isChecked())
                {
                    ReasonDailog("Ele_Indicator");
                    AppVariables.Ele_Indicator= "NO";
                    tbindicatorn.setChecked(true);
                    tbindicatory.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_Indicator = "";
                    tbindicatorn.setChecked(false);
                    tbindicatory.setChecked(true);
                }
            }
        });

        tbpass_switchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbpass_switchy.isChecked())
                {
                    AppVariables.Ele_pass_switch = "YES";
                    tbpass_switchy.setChecked(true);
                    tbpass_switchn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_pass_switch = "";

                    tbpass_switchy.setChecked(false);
                    tbpass_switchn .setChecked(true);
                }
            }
        });

        tbpass_switchn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbpass_switchn.isChecked())
                {
                    ReasonDailog("Ele_pass_switch");
                    AppVariables.Ele_pass_switch= "NO";
                    tbpass_switchn.setChecked(true);
                    tbpass_switchy.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_pass_switch = "";
                    tbpass_switchn.setChecked(false);
                    tbpass_switchy.setChecked(true);
                }
            }
        });

        tballbulbsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tballbulbsy.isChecked())
                {
                    AppVariables.Ele_All_bulbs = "YES";
                    tballbulbsy.setChecked(true);
                    tballbulbsn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_All_bulbs = "";

                    tballbulbsy.setChecked(false);
                    tballbulbsn .setChecked(true);
                }
            }
        });
        tballbulbsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tballbulbsn.isChecked())
                {
                    ReasonDailog("Ele_All_bulbs");
                    AppVariables.Ele_All_bulbs= "NO";
                    tballbulbsn.setChecked(true);
                    tballbulbsy.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_All_bulbs = "";
                    tballbulbsn.setChecked(false);
                    tballbulbsy.setChecked(true);
                }
            }
        });

        tbkill_switchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbkill_switchy.isChecked())
                {
                    AppVariables.Ele_kill_switch = "YES";
                    tbkill_switchy.setChecked(true);
                    tbkill_switchn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_kill_switch = "";

                    tbkill_switchy.setChecked(false);
                    tbkill_switchn .setChecked(true);
                }
            }
        });
        tbkill_switchn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbkill_switchn.isChecked())
                {
                    ReasonDailog("Ele_kill_switch");
                    AppVariables.Ele_kill_switch= "NO";
                    tbkill_switchn.setChecked(true);
                    tbkill_switchy.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_kill_switch = "";
                    tbkill_switchn.setChecked(false);
                    tbkill_switchy.setChecked(true);
                }
            }
        });

        tbclustery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbclustery.isChecked())
                {
                    AppVariables.Ele_Cluster = "YES";
                    tbclustery.setChecked(true);
                    tbclustern.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_Cluster = "";

                    tbclustery.setChecked(false);
                    tbclustern.setChecked(true);
                }
            }
        });
        tbclustern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbclustern.isChecked())
                {
                    ReasonDailog("Ele_Cluster");
                    AppVariables.Ele_Cluster= "NO";
                    tbclustern.setChecked(true);
                    tbclustery.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_Cluster = "";
                    tbclustern.setChecked(false);
                    tbclustery.setChecked(true);
                }
            }
        });

        tbtime_settingy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbtime_settingy.isChecked())
                {
                    AppVariables.Ele_time_setting = "YES";
                    tbtime_settingy.setChecked(true);
                    tbtime_settingn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_time_setting = "";

                    tbtime_settingy.setChecked(false);
                    tbtime_settingn.setChecked(true);
                }
            }
        });
        tbtime_settingn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tbtime_settingn.isChecked())
                {
                    ReasonDailog("Ele_time_setting");
                    AppVariables.Ele_time_setting= "NO";
                    tbtime_settingn.setChecked(true);
                    tbtime_settingy.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_time_setting = "";
                    tbtime_settingn.setChecked(false);
                    tbtime_settingy.setChecked(true);
                }
            }
        });

        //tboverspeed_sety

        tboverspeed_sety.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if(tboverspeed_sety.isChecked())
                {
                    AppVariables.Ele_Overspeed_setting = "YES";
                    tboverspeed_sety.setChecked(true);
                    tboverspeed_setn.setChecked(false);
                }
                else
                {
                    AppVariables.Ele_Overspeed_setting = "";

                    tboverspeed_sety.setChecked(false);
                    tboverspeed_setn .setChecked(true);
                }
            }
        });

        tboverspeed_setn.setOnClickListener(new View.OnClickListener()
        {
                                               @Override
           public void onClick(View view) {
               if(tboverspeed_setn.isChecked())
               {
                   ReasonDailog("Ele_Overspeed_setting");
                   AppVariables.Ele_Overspeed_setting= "NO";
                   tboverspeed_setn.setChecked(true);
                   tboverspeed_sety.setChecked(false);
               }
               else
               {
                   AppVariables.Ele_Overspeed_setting = "";
                   tboverspeed_setn.setChecked(false);
                   tboverspeed_sety.setChecked(true);
               }
           }
        });

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Legal_Parts_Installation();

            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Liquids();
            }
        });
        dialog.show();

    }
    public void Legal_Parts_Installation()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;

        final ToggleButton tbfront_rear_platey,tbfront_rear_platen;
        final ToggleButton tbrearmirrory,tbrearmirrorn;
        final ToggleButton tbfirst_aid_kity,tbfirst_aid_kitn;
        final ToggleButton tbtool_kity,tbtool_kitn;
        final ToggleButton tbsaree_guardy,tbsaree_guardn;

        dialog.setContentView(R.layout.dialog_legal_parts_installation_pdi);

        tbfront_rear_platey = (ToggleButton) dialog.findViewById(R.id.front_rear_no_platey);
        tbfront_rear_platen = (ToggleButton) dialog.findViewById(R.id.front_rear_no_platen);

        tbrearmirrory = (ToggleButton) dialog.findViewById(R.id.rear_view_mirrory);
        tbrearmirrorn = (ToggleButton) dialog.findViewById(R.id.rear_view_mirrorn);

        tbfirst_aid_kity = (ToggleButton) dialog.findViewById(R.id.first_aid_kity);
        tbfirst_aid_kitn = (ToggleButton) dialog.findViewById(R.id.first_aid_kitn);

        tbtool_kity = (ToggleButton) dialog.findViewById(R.id.toolkity);
        tbtool_kitn = (ToggleButton) dialog.findViewById(R.id.toolkitn);

        tbsaree_guardy = (ToggleButton) dialog.findViewById(R.id.saree_guardy);
        tbsaree_guardn = (ToggleButton) dialog.findViewById(R.id.saree_guardn);

        tbfront_rear_platey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfront_rear_platey.isChecked())
                {
                    AppVariables.Legal_frnt_rear_num_plt = "YES";
                    tbfront_rear_platey.setChecked(true);
                    tbfront_rear_platen.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_frnt_rear_num_plt = "";

                    tbfront_rear_platey.setChecked(false);
                    tbfront_rear_platen .setChecked(true);
                }
            }
        });
        tbfront_rear_platen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(tbfront_rear_platen.isChecked())
                {
                    ReasonDailog("Legal_frnt_rear_num_plt");
                    AppVariables.Legal_frnt_rear_num_plt= "NO";
                    tbfront_rear_platen.setChecked(true);
                    tbfront_rear_platey.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_frnt_rear_num_plt = "";
                    tbfront_rear_platen.setChecked(false);
                    tbfront_rear_platey.setChecked(true);
                }
            }
        });

        tbrearmirrory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbrearmirrory.isChecked())
                {
                    AppVariables.Legal_rear_view_mirror = "YES";
                    tbrearmirrory.setChecked(true);
                    tbrearmirrorn.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_frnt_rear_num_plt = "";

                    tbrearmirrory.setChecked(false);
                    tbrearmirrorn .setChecked(true);
                }
            }
        });
        tbrearmirrorn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(tbrearmirrorn.isChecked())
                {
                    ReasonDailog("Legal_rear_view_mirror");
                    AppVariables.Legal_rear_view_mirror= "NO";
                    tbrearmirrorn.setChecked(true);
                    tbrearmirrory.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_frnt_rear_num_plt = "";
                    tbrearmirrorn.setChecked(false);
                    tbrearmirrory.setChecked(true);
                }
            }
        });

        tbfirst_aid_kity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbfront_rear_platey.isChecked())
                {
                    AppVariables.Legal_first_aid_kit = "YES";
                    tbfront_rear_platey.setChecked(true);
                    tbfront_rear_platen.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_first_aid_kit = "";

                    tbfront_rear_platey.setChecked(false);
                    tbfront_rear_platen .setChecked(true);
                }
            }
        });
        tbfirst_aid_kitn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(tbfront_rear_platen.isChecked())
                {
                    ReasonDailog("Legal_first_aid_kit");
                    AppVariables.Legal_first_aid_kit= "NO";
                    tbfront_rear_platen.setChecked(true);
                    tbfront_rear_platey.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_first_aid_kit = "";
                    tbfront_rear_platen.setChecked(false);
                    tbfront_rear_platey.setChecked(true);
                }
            }
        });

        //tbtool_kity
        tbtool_kity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbtool_kity.isChecked())
                {
                    AppVariables.Legal_tool_kit = "YES";
                    tbtool_kity.setChecked(true);
                    tbtool_kitn.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_tool_kit = "";

                    tbtool_kity.setChecked(false);
                    tbtool_kitn .setChecked(true);
                }
            }
        });
        tbtool_kitn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(tbtool_kitn.isChecked())
                {
                    ReasonDailog("Legal_tool_kit");
                    AppVariables.Legal_tool_kit= "NO";
                    tbtool_kitn.setChecked(true);
                    tbtool_kity.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_tool_kit = "";
                    tbtool_kitn.setChecked(false);
                    tbtool_kity.setChecked(true);
                }
            }
        });


        tbsaree_guardy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tbsaree_guardy.isChecked())
                {
                    AppVariables.Legal_saree_guard = "YES";
                    tbsaree_guardy.setChecked(true);
                    tbsaree_guardn.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_saree_guard = "";
                    tbsaree_guardy.setChecked(false);
                    tbsaree_guardn .setChecked(true);
                }
            }
        });
        tbsaree_guardn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(tbsaree_guardn.isChecked())
                {
                    ReasonDailog("Legal_saree_guard");
                    AppVariables.Legal_saree_guard= "NO";
                    tbsaree_guardn.setChecked(true);
                    tbsaree_guardy.setChecked(false);
                }
                else
                {
                    AppVariables.Legal_saree_guard = "";
                    tbsaree_guardn.setChecked(false);
                    tbsaree_guardy.setChecked(true);
                }
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Final();
            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Electricals();
            }
        });
        dialog.show();

    }
    public void Final()
    {
        final Dialog dialog = new Dialog(this);
        Button btnnext,btnprev;

        final Button  btncustomsign,btnpdiinchargesign;
        final EditText editText_Tech_Name;
        dialog.setContentView(R.layout.dialog_sign_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        btnnext = (Button) dialog.findViewById(R.id.next);
        btnprev = (Button) dialog.findViewById(R.id.prev);
        editText_Tech_Name = (EditText) dialog.findViewById(R.id.techname);
        btncustomsign = (Button) dialog.findViewById(R.id.custbtn);
        btnpdiinchargesign = (Button) dialog.findViewById(R.id.inch_btn);

        btncustomsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppVariables.Sign_name = "customer_Signature";
                DialogWindow();
            }
        });

        btnpdiinchargesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppVariables.Sign_name = "PDI_Incharge_Signature";
                DialogWindow();
            }
        });


        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                dialog.hide();
                Legal_Parts_Installation();
            }
        });
        btnnext.setText("Done");
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppVariables.Techician_name = editText_Tech_Name.getText().toString();
                GenPDF();
                dialog.hide();
            }
        });
        dialog.show();
    }

    public String PresentDate()
    {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        str = sdf.format(date);
        return str;
    }

    public void DialogWindow()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_draw_pdi);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final TouchDrawView touchDrawView;
        Button btnclear,btnclose,btnsave;
        touchDrawView = (TouchDrawView) dialog.findViewById(R.id.touch);
        btnclear = (Button) dialog.findViewById(R.id.clear);
        btnsave = (Button) dialog.findViewById(R.id.save);
        btnclose = (Button) dialog.findViewById(R.id.close);

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touchDrawView.clear();
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touchDrawView.saveFile("saved_signature",AppVariables.Sign_name);
            }
        });
        dialog.show();
    }

    public void GenPDF()
    {
        Document doc = new Document();
        PdfWriter docWriter = null;
        DecimalFormat df = new DecimalFormat("0.00");

        try {

            //special font sizes
            Font bfBold12 = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(FontFamily.TIMES_ROMAN, 12);

            File file = new File("sdcard/RideScan/PDI.pdf");
            docWriter = PdfWriter.getInstance(doc , new FileOutputStream(file));

            //document header attributes
            doc.addAuthor("betterThanZero");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("MySampleCode.com");
            doc.addTitle("Report with Column Headings");
            doc.setPageSize(PageSize.LETTER);

            //open document
            doc.open();

            //create a paragraph
            Paragraph paragraph = new Paragraph("Pre - Delivery Inspection Checklist.");

            paragraph.setAlignment(ALIGN_CENTER);
            //specify column widths
            float[] columnWidths = {5f, 1.5f,1.5f,2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setWidthPercentage(90f);

            double orderTotal, total = 0;

            //just some random data to fill


            insertCell(table, "Model" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, AppVariables.bikemodel , Element.ALIGN_LEFT, 3, bf12);
            insertCell(table, "TVSM Inv. No." , Element.ALIGN_LEFT, 1, bf12);insertCell(table, AppVariables.tvsm , Element.ALIGN_LEFT, 3, bf12);
            insertCell(table, "Date of PDI" , Element.ALIGN_LEFT, 1, bf12);insertCell(table, AppVariables.date_of_pdi , Element.ALIGN_LEFT, 3, bf12);
            insertCell(table, "Chassis No." , Element.ALIGN_LEFT, 1, bf12);insertCell(table, AppVariables.chassis_no , Element.ALIGN_LEFT, 3, bf12);
            insertCell(table, "Date of Inv." , Element.ALIGN_LEFT, 1, bf12);insertCell(table, AppVariables.date_of_inv , Element.ALIGN_LEFT, 3, bf12);
            insertCell(table, "Battery No." , Element.ALIGN_LEFT, 1, bf12);insertCell(table, AppVariables.battery_no , Element.ALIGN_LEFT, 3, bf12);
            // orderTotal = Double.valueOf(df.format(Math.random() * 1000));
            // total = total + orderTotal;
            // insertCell(table, df.format(orderTotal), Element.ALIGN_RIGHT, 1, bf12);


            //repeat the same as above to display another location
            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Miscellaneous Points", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Washing Done" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, washingstr , Element.ALIGN_LEFT, 1, bf12);insertCell(table, washingstr_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "No dent & Damage" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, nodentstr , Element.ALIGN_LEFT, 1, bf12);insertCell(table, nodentstr_reason, Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Battery Volatge With Load Tester " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, batvolstr , Element.ALIGN_LEFT, 1, bf12);insertCell(table, batvolstr_reason, Element.ALIGN_LEFT, 2, bf12);


            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Locks Operations", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Ingition" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ignition , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ignition_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Steering" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Steering , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Steering_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Fuel Tank Cap" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Fuel_Tank_Cap , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Fuel_Tank_Cap_reason , Element.ALIGN_LEFT, 2, bf12);

            insertCell(table, "", Element.ALIGN_LEFT, 4, bfBold12);
            insertCell(table, "Operations", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Free Play Adjusments:" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Free_play_adjustments, Element.ALIGN_LEFT, 1, bf12);insertCell(table, Free_play_adjustments_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Brake" , ALIGN_CENTER, 4, bf12);
            insertCell(table, "Front (3 - 5 mm)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Br_front , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Free_play_adjustments_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Rear  (3- 5 mm)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Br_rear , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Br_rear_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Clutch ( 10- 15 mm)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Br_clutch , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Br_clutch_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Throttle ( 3- 7 mm)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Br_Throttle , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Br_Throttle_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Service Reminder-Kms Set Up-Date Set Up-" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Service_Rem , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Service_Rem_reason , Element.ALIGN_LEFT, 2, bf12);


            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Drive Chain", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Alignment " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, alignment , Element.ALIGN_LEFT,1, bf12);insertCell(table, alignment_reason , Element.ALIGN_LEFT,2, bf12);
            insertCell(table, "Slackness(As per Specs)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, "" , Element.ALIGN_LEFT, 3, bf12);
            insertCell(table, "Brake Fluid Level " , ALIGN_CENTER, 4, bf12);
            insertCell(table, "Front" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, brfld_front , Element.ALIGN_LEFT, 1, bf12);insertCell(table, brfld_front_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Rear " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, brfld_rear , Element.ALIGN_LEFT, 1, bf12);insertCell(table, brfld_rear_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Engine Supression test " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, brfld_eng_sup , Element.ALIGN_LEFT, 1, bf12);insertCell(table, brfld_eng_sup_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Coolant level (Cold Condition)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, brfld_cool_lvl , Element.ALIGN_LEFT, 1, bf12);insertCell(table, brfld_cool_lvl_reason , Element.ALIGN_LEFT, 2, bf12);

            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Fasterners Check", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Front Wheel Axle Nut" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_front_wheel , Element.ALIGN_LEFT, 1, bf12);insertCell(table, fast_front_wheel_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Fork center Bolt" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_fork_center , Element.ALIGN_LEFT, 1, bf12);insertCell(table, fast_fork_center_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Handle Bar Mounting Bolt" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_Handle_Bar , Element.ALIGN_LEFT, 1, bf12);insertCell(table, fast_Handle_Bar_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Rear Shock Absorber Mounting Bolts" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_Rear_shock_absorder , Element.ALIGN_LEFT, 1, bf12);insertCell(table, fast_Rear_shock_absorder_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Rear Wheel Axle/Sprocket Shaft Nuts" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_rear_wheel_axle , Element.ALIGN_LEFT, 1, bf12);insertCell(table, fast_rear_wheel_axle_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Front & Rear Brake Callipers mounting bolts" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_front_rear_brake , Element.ALIGN_LEFT,1, bf12);insertCell(table, fast_front_rear_brake_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Engine Mounting Bolts" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, fast_eng_mount , Element.ALIGN_LEFT, 1, bf12);insertCell(table, fast_eng_mount_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Silencer Mounting nut" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, silencer_mount , Element.ALIGN_LEFT, 1, bf12);insertCell(table, silencer_mount_reason , Element.ALIGN_LEFT, 2, bf12);

            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Suspension", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Check for Any sticky Movement " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, sus_check_stick , Element.ALIGN_LEFT, 1, bf12);insertCell(table, sus_check_stick_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Rear Shock Absorber " , Element.ALIGN_LEFT, 1, bf12);insertCell(table, sus_rear_shock , Element.ALIGN_LEFT, 1, bf12);insertCell(table, sus_rear_shock_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "For Free stroking & Setting on " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, sus_free_strok , Element.ALIGN_LEFT, 1, bf12);insertCell(table, sus_free_strok_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Wheels" , Element.ALIGN_LEFT, 1, bf12);insertCell(table, sus_wheels , Element.ALIGN_LEFT, 1, bf12);insertCell(table, sus_wheels_reason, Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Tyre Pressure " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, "Solo " , Element.ALIGN_LEFT, 1, bf12);insertCell(table, "Double " , Element.ALIGN_LEFT, 1, bf12);insertCell(table, "" , Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, "Front " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, tyre_front_solo , Element.ALIGN_LEFT, 1, bf12);insertCell(table, tyre_front_double , Element.ALIGN_LEFT, 1, bf12);insertCell(table, "Solo :"+tyre_front_solo_reason+";\nDouble:"+tyre_front_double_reason , Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, "Rear  " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, tyre_rear_solo , Element.ALIGN_LEFT, 1, bf12);insertCell(table, tyre_rear_double , Element.ALIGN_LEFT, 1, bf12);insertCell(table, "Solo :"+tyre_rear_solo_reason+";\nDouble:"+tyre_rear_double_reason , Element.ALIGN_LEFT, 1, bf12);

            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Liquids", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Engine oil" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, liq_eng_oil , Element.ALIGN_LEFT,1, bf12);insertCell(table, liq_eng_oil_reason , Element.ALIGN_LEFT,2, bf12);
            insertCell(table, "Engine Oil Level Check " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, liq_eng_oil_lvl , Element.ALIGN_LEFT, 1, bf12);insertCell(table, liq_eng_oil_lvl_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Check for any leakages" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, liq_check_leakage , Element.ALIGN_LEFT, 1, bf12);insertCell(table, liq_check_leakage_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Coolant level (Cold Condition)" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, liq_coolant_level , Element.ALIGN_LEFT, 1, bf12);insertCell(table, liq_coolant_level_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Headlight Adjustment - Refer Annexure" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, lig_headlit_adj , Element.ALIGN_LEFT, 1, bf12);insertCell(table, lig_headlit_adj_reason , Element.ALIGN_LEFT, 2, bf12);


            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Electricals", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Left hand Switch operation" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_left_hand_switch , Element.ALIGN_LEFT,1, bf12);insertCell(table, Ele_left_hand_switch_reason , Element.ALIGN_LEFT,2, bf12);
            insertCell(table, "Horn " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_horn , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_horn_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Indicator" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_Indicator , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_Indicator_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Pass switch" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_pass_switch , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_pass_switch_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "All Bulbs Functioning " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_All_bulbs , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_All_bulbs_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Kill Switch" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_kill_switch , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_kill_switch_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Cluster  " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_Cluster , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_Cluster_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Time Setting " , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_time_setting , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_time_setting_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Overspeed Setting" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Ele_Overspeed_setting , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Ele_Overspeed_setting_reason , Element.ALIGN_LEFT, 2, bf12);

            insertCell(table, "", Element.ALIGN_LEFT,4, bfBold12);
            insertCell(table, "Legal Parts Installation", ALIGN_CENTER, 4, bfBold12);

            insertCell(table, "Front & Rear Number Plate" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Legal_frnt_rear_num_plt , Element.ALIGN_LEFT,1, bf12);insertCell(table, Legal_frnt_rear_num_plt_reason , Element.ALIGN_LEFT,2, bf12);
            insertCell(table, "Rear View Mirror" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Legal_rear_view_mirror , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Legal_rear_view_mirror_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "First Aid Kid" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Legal_first_aid_kit , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Legal_first_aid_kit_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Tool Kit" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Legal_tool_kit , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Legal_tool_kit_reason , Element.ALIGN_LEFT, 2, bf12);
            insertCell(table, "Saree Guard Fitment" , Element.ALIGN_LEFT, 1, bf12); insertCell(table, Legal_saree_guard , Element.ALIGN_LEFT, 1, bf12);insertCell(table, Legal_saree_guard_reason , Element.ALIGN_LEFT, 2, bf12);


            //add the PDF table to the paragraph
            paragraph.add(table);
            // add the paragraph to the document
            doc.add(paragraph);
            Image image1,image2;
            try {
                // get input stream
                String root = Environment.getExternalStorageDirectory().toString();
                File f1 = new File(root + "/saved_signature/"+"customer_Signature.png");
                FileInputStream inputStream = new FileInputStream(f1);
                Bitmap bmp1 = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                bmp1.compress(Bitmap.CompressFormat.PNG, 10, stream1);
                image1 = Image.getInstance(stream1.toByteArray());
                image1.setAlignment(Image.LEFT);
                image1.scaleToFit(200f,100f);

                File f2 = new File(root + "/saved_signature/"+"PDI_Incharge_Signature.png");
                FileInputStream inputStream1 = new FileInputStream(f2);
                Bitmap bmp2 = BitmapFactory.decodeStream(inputStream1);
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                bmp2.compress(Bitmap.CompressFormat.PNG, 10, stream2);
                image2 = Image.getInstance(stream2.toByteArray());
                image2.setAlignment(Image.RIGHT);
                image2.scaleToFit(200f,100f);

            }
            catch(IOException ex)
            {
                return;
            }

            PdfPTable table2 = new PdfPTable(3);
            table2.setWidthPercentage(90f);
            PdfPCell cell11 = new PdfPCell(new Phrase("Techinician Signature"));
            PdfPCell cell22 = new PdfPCell(new Phrase("Techician Name"));
            PdfPCell cell33 = new PdfPCell(new Phrase("PDI Incharge Signature"));

            cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell22.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell33.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table2.addCell(cell11);
            table2.addCell(cell22);
            table2.addCell(cell33);


            PdfPTable table3 = new PdfPTable(3);
            table3.setWidthPercentage(90f);
            PdfPCell cell1 = new PdfPCell(image1);
            PdfPCell cell2 = new PdfPCell(new Phrase(AppVariables.Techician_name));
            PdfPCell cell3 = new PdfPCell(image2);

            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell1.setPadding(10f);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setPadding(10f);

            table3.addCell(cell1);
            table3.addCell(cell2);
            table3.addCell(cell3);

            doc.add(table2);
            doc.add(table3);
        }
        catch (DocumentException dex)
        {
            dex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (doc != null){
                //close the document
                doc.close();
            }
            if (docWriter != null){
                //close the writer
                docWriter.close();
            }
        }
    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }


}
