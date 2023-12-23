package com.example.tvsridescan.Library;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvsridescan.R;
import com.example.tvsridescan.SplashScreen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class AppVariables
{

    private static final String TAG = "AppVariables";
    public  static String  Sorteddata;
    public static String DTCCode = "480904";
    public static String Secured_Email_Id = "Not Selected";
    public static HashMap<String,String> dtclist = new HashMap<String,String>();
    public static ArrayList emsrp = new ArrayList();
    public static String StoreOTP ="";
    public static String BluetoothDeviceName ="MIC43_FLASHER_2";
    public static String BluetoothDeviceAdress ="";
    public static int BikeModel =0;
    public static String BikeModel_str =null;
    public static String Email = "jaks978@gmail.com";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static String email_id = "tetechzone@gmail.com" ;
    public static Date DailyCheck_date = null ;
    public static Date Current_date = null ;
    public static List vcino = null;
    public static int pwd = 0;
    public static String prop_data = "ABCDEFGHIJKLMNOPQRSTUVWX" ;
    public static String ref_data = "ABCDEFGHIJKLMNOP" ;
    public static String Serialnumber = "" ;
    public static String VIN = "" ;
    public static int VINway =0;
    public static ArrayList NRC = new ArrayList();
    public static int badgeems =0,badgeabs =0,badgecluster =0;
    public static String MessagePDI = "Please Complete All Steps";

    public static String CONNECTED_STR ="connected";
    public static String NOT_CONNECTED_STR ="not connected";

    public static boolean isaBoolean() {
        return aBoolean;
    }

    public static void setaBoolean(boolean aBoolean) {
        AppVariables.aBoolean = aBoolean;
    }

    public static boolean aBoolean = false;

    /* Link for Download User Details From cloud*/
    public static String Link_User_Details = "http://www.pickhr.com/i_connect_api/get_device/?product_id=2224";

    /* Link for Validate Email_id */
    public static String Link_Email_Valid = "https://realtimemonitroingsystem.000webhostapp.com/SendMail.php";



    public static byte[] currentcmd;
    public static int OTP()
    {
        Random lRand= new Random();
        int Low = 100000;
        int High = 999999;

        int lRandNum = (lRand.nextInt(High-Low) + Low);//(short) 0xA5A5;//
        return lRandNum;
    }
    public static  void Storepwd(Context context,int password)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putInt("pwd", password);
        editor.apply();
    }
    public static int Retpwd(Context context)
    {
        int i =0;
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        int restoredText = prefs.getInt("pwd", 0);
        if (restoredText != 0)
        {
             i = prefs.getInt("pwd", 0); //0 is the default value.
           // String[] GPXFILES1 = myset.toArray(new String[myset.size()]);
        }
        return i;
    }

    public static  void StoreSet(Context context,Set arr)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putStringSet("vci", arr);
        editor.apply();
    }
    public static Set RetSet(Context context)
    {
        Set set = new HashSet();
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        set = prefs.getStringSet("vci", null);
        if (set!=null)
        {
            set = prefs.getStringSet("vci",null); //0 is the default value.
        }
        return set;
    }

    public static String GenLogLine(String str)
    {
        String logline = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String strdate = sdf.format(date);
        logline = strdate+"  :  "+str;
        return logline;
    }

    public static String FormCmd(String siddid, String payload)
    {
        String cmdlen = null;
        int strlen=0;
        if(payload.equals(""))
        {
            strlen =0;
        }
        else
        {
            strlen = payload.length();//length of enter string
        }
        int len = strlen+4+4;//packetlen =4 cksum =4 pyload =?
        String finallen =  String.format("%X", len);
        if(len <16)
        {
            cmdlen = ("000"+finallen);
        }
        else
        {
            cmdlen = ("00"+finallen);
        }
        String Strcmd = cmdlen+siddid+payload;
        short temp = DataConversion.GetCheckSum(Strcmd.getBytes(),Strcmd.length());
        byte[] cksmbytes = DataConversion._HWordToPAN(temp);
        Strcmd = ":"+Strcmd+new String(cksmbytes)+"\r\n";
        return Strcmd;
    }



    public static  void Readfile(Context context,String filename)
    {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null)
            {
                getShortDescription(mLine);
            }
            Log.e("App Variables","Loaded : "+filename);
        } catch (IOException e) {
            //log the exception
            e.printStackTrace();
            Log.e("AppVariables.class",e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();
                }
            }
        }
    }



    //read dtcs with short description
    public static void getShortDescription(String dtcsnumber)
    {


        String line = dtcsnumber;
        String dtcdata[] = line.split(",");
        dtclist.put(dtcdata[0],dtcdata[1]);
    }
    public static String ParseDTCs(String str,String sentcmd)
    {

        str = str.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","");
        int pos = str.indexOf(sentcmd);
        if(pos<0)
        {
            pos = str.indexOf("59027F");
            sentcmd = "59027F";
        }
        if(pos==0)
        {
            str = "";
        }
        else
        {
            String strlen = str.substring(pos-4,pos);
            int len = Integer.parseInt(strlen,16)*2;
            str = str.substring(pos,len+4);
            str = str.replace(sentcmd,"");

        }
        return str;

    }

    public static String parsecmd(String strdata,String cmd)
    {
        String strlen;
        String finaldata = null;
        String str = strdata;
        str = str.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","");
        if(str.substring(0,2).equals("7F")&& str.length()<14)
        {
            str = str.substring(6,str.length());
        }
        else
        if(str.substring(0,2).equals("7F")&& str.length()>14)
        {
            int position = str.indexOf("62"+cmd);
            if(position>8)
            {
                strlen = str.substring(position-4,position);
                int len = (Integer.parseInt(strlen,16))*2;
                str = str.substring(position,len+position);
                str = str.replace("62"+cmd,"");
                finaldata = str;
            }
            else
            {
                str = str.substring(position,str.length());

                str = str.replace("62"+cmd,"");
                finaldata = str;
            }

        }
        else
        if(str.length()>14)
        {
            int position = str.indexOf("62"+cmd);
            strlen = str.substring(position-4,position);
            int len = (Integer.parseInt(strlen,16))*2;
            str = str.substring(position,len+position);
            str = str.replace("62"+cmd,"");
            finaldata = str;
        }
        else
        {
            str = str.replace("62"+cmd,"");
            finaldata = str;
        }


        return finaldata;
    }

    public static String strHexString(String strdata,int  hexopbytes)
    {
        //String  = str;
        long val = Long.parseLong(strdata);
        String hexdata = String.format("%X", val);
        int len = hexdata.length();
        int rem = hexopbytes-len;
        StringBuilder stbr = new StringBuilder();
        for(int i=0;i<rem;i++)
        {
            stbr.append("0");
        }
        stbr.append(hexdata);

        // returning finalhexdata

        return stbr.toString();
    }

    public static String parseKwp1acmd(String strdata,String cmd)
    {
        String finaldata = "";
        String str = strdata;
        String strlen;

        str = str.replace(" ","").replace(":","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","");

        int position = str.indexOf("5A"+cmd);

        str = str.substring(position,str.length());

        str = str.replace("5A"+cmd,"");
        finaldata = str;

        return finaldata;
    }

    public static String parseBosch1aAAResposne(String response,String cmd)
    {
        String tempResonse = response;
        if(response.length()>4)
        {
            String lengthString =  tempResonse.substring(0,4);

            int length = DataConversion._PanToHWord(lengthString.getBytes());

            tempResonse = tempResonse.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","").replace(":","");

            tempResonse = tempResonse.substring(4,tempResonse.length());

            int position = tempResonse.indexOf("5A"+cmd);

            tempResonse = tempResonse.substring(position,tempResonse.length());

            // tempResonse = tempResonse.replace("62"+cmd,"");
            tempResonse = tempResonse.substring(0,length*2);
            //tempResonse = tempResonse.replace("AA","").replace("AA","").replace("AA","").replace("AA","");
            tempResonse = tempResonse.replace("5A"+cmd,"");
            Log.e("PARSE",new String(tempResonse));
        }

        return tempResonse;
    }
    public static String parseBosch1acmd(String strdata,String cmd)
    {
        String finaldata = "";
        String str = strdata;
        String strlen;

        str = str.replace(" ","").replace(":","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","").replace(":","").replace("AA","");

        int position = str.indexOf("5A"+cmd);

        str = str.substring(position,str.length());

        str = str.replace("5A"+cmd,"");
        finaldata = str;

        return finaldata;
    }
    public static String parseBosch19AAResposne(String response)
    {

        String tempResonse = response;
        if(tempResonse.contains("0:"))
        {
            /*Removing spaces*/
            tempResonse = tempResonse.replace( " ","");

            /*Taking the length of Frame*/
            String lengthString =  tempResonse.substring(0,4);
            int length = DataConversion._PanToHWord(lengthString.getBytes());

            /*Removing Space and unwanted characters*/
            tempResonse = tempResonse.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","").replace(":","");

            tempResonse = tempResonse.substring(4,tempResonse.length());

            tempResonse = tempResonse.substring(0,length*2);
            tempResonse = tempResonse.substring(4,tempResonse.length());

            Log.e("PARSE",tempResonse);
        }
        else
        {
            tempResonse = tempResonse.replace( " ","");
             tempResonse =  tempResonse.substring(4,tempResonse.length());


            Log.e("PARSE",tempResonse);
        }

        return tempResonse;
    }
    public static String parseBosch22AAResposne(String response,String cmd)
    {
        String tempResonse = response;
        String lengthString =  tempResonse.substring(0,4);

        int length = DataConversion._PanToHWord(lengthString.getBytes());

        tempResonse = tempResonse.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","").replace(":","");

        tempResonse = tempResonse.substring(4,tempResonse.length());

        int position = tempResonse.indexOf("62"+cmd);

        tempResonse = tempResonse.substring(position,tempResonse.length());

       // tempResonse = tempResonse.replace("62"+cmd,"");
        tempResonse = tempResonse.substring(0,length*2);
        //tempResonse = tempResonse.replace("AA","").replace("AA","").replace("AA","").replace("AA","");
        tempResonse = tempResonse.replace("62"+cmd,"");
        Log.e("PARSE",new String(tempResonse));
        return tempResonse;
    }
    public static String parseBosch22cmd(String strdata,String cmd)
    {
        String finaldata = "";
        String str = strdata;
        String strlen;

        str = strdata.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","").replace(":","").replace("AA","");

        int position = str.indexOf("62"+cmd);

        str = str.substring(position,str.length());

        str = str.replace("62"+cmd,"");
        finaldata = str;

        return finaldata;
    }


    public static String parseKwp21cmd(String strdata,String cmd)
    {
        String finaldata = "";
        String str = strdata;
        String strlen;

        str = str.replace(" ","").replace(":","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","");

        int position = str.indexOf("61"+cmd);

        str = str.substring(position,str.length());

        str = str.replace("61"+cmd,"");
        finaldata = str;

        return finaldata;
    }

    public static String parseKwpdtcs(String strdata)
    {
        String finaldata = "";
        String str = strdata;
        String strlen;

        str = str.replace(" ","").replace(":","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","").replace("#","");

        int position = str.indexOf("58");

        str = str.substring(position,str.length());

        str = str.replace("58","");
        finaldata = str;

        return finaldata;
    }
    public static String parsecmd2(String strdata,String cmd)
    {
        String finaldata = null;
        if(!strdata.contains("NODATA"))
        {
            String strlen = null;
            String str = strdata;
            str = str.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","");

            int position = str.indexOf(cmd);
            if(position!=0)
            {
                strlen = str.substring(position - 4, position);
                int len = (Integer.parseInt(strlen, 16)) * 2;
                str = str.substring(position, len + position);
                str = str.replace(cmd, "");
                finaldata = str;
            }
            else
            {
                finaldata = "NO DATA";
            }

        }
        else
        {
            finaldata = "NO DATA";
        }

        return finaldata;
    }


    //commands used to communicate with VCI
      /*---read reference data*/
    public static String PING = "4100";//SIDDID

    /*---read nonce data*/
    public static String NONCE = "4D00";//SIDDID


    /*---read reference data*/
    public static String READ_REFERENCE_DATA = "2250";//SIDDID

    /*---read proprietary data*/
    public static String READ_PROPRIETARY_DATA = "2251";//SIDDID

    /*---read keylearnt status */
    public static String READ_KEY_LEARN_STATUS = "2252";//SIDDID

    /*---read serial number */
    public static String READ_SERIAL_NUMBER = "2253";//SIDDID

    /*---read aes key data*/
    public static String READ_AES_KEY = "0000";//SIDDID

    /*---read MAC data*/
    public static String READ_MAC_ADDRESS = "6C00";//

    /*---write reference data*/
    public static String WRITE_REFERENCE_DATA = "2E50";//SIDDID

    /*---write proprietary data*/
    public static String WRITE_PROPRIETARY_DATA = "2E51";//SIDDID

    /*---write btname data*/
    public static String WRITE_BT_NAME = "0000";//SIDDID

    /*---write  data*/
    public static String WRITE_SERIAL_NUMBER = "0000";//SIDDID

    /*---write proprietary data*/
    public static String WRITE_ENCRYPTED_DATA = "2E37";//SIDDID

    public static String NegRes(String str)
    {
        String data = null;
        if(str.equals("78"))
        {

        }
        else
        {
            for(int i=0;i<NRC.size();i++)
            {
                if(NRC.get(i).toString().contains(str))
                {
                    String arr[] = NRC.get(i).toString().split(",");
                    data = arr[1]+","+arr[2];
                }
            }
        }
        return  data;
    }
    public static String FilePath = "/sdcard/RideScan/"+HttpCommunication.BIKEMODEL+".HEX";


    public static String TrimDouble(String str)
    {
        String op= null;
        if(str.length()>3)
        {
            int pos = str.indexOf(".");
            String trimval = str.substring(0,pos+2);
            op = trimval;
        }
        else
        {
            op = str;
        }
        return op;
    }


    public static void DialogConnectionLost(final Context context)
    {
        final Dialog dialog  = new Dialog(context);
        dialog.setContentView(R.layout.dialog_connection_lost);
        Button b1 = dialog.findViewById(R.id.OK);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.hide();
                    Intent i = new Intent(context,SplashScreen.class);
                    context.startActivity(i);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dialog.show();

    }

    //store email_id
    public static  void StoreEmail(Context context,String str)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putString("email", str);
        editor.apply();
    }
    public static void RetEmail(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        String stremil = prefs.getString("email", null);
        if (stremil!=null)
        {
            email_id = prefs.getString("email",null); //0 is the default value.
        }

    }

    //store email_id
    public static  void StoreSecEmail(Context context,String str)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putString("secemail", str);
        editor.apply();
    }
    public static void RetSecEmail(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        String stremil = prefs.getString("secemail", null);
        if (stremil!=null)
        {
            AppVariables.Secured_Email_Id = prefs.getString("secemail",null); //0 is the default value.
        }

    }

    public static int Sessioncount = 0;

    public static  void StoreSSCount(Context context)
    {
        Sessioncount++;
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putInt("sessioncount", Sessioncount);
        editor.apply();
    }
    public static int RetSSCount(Context context)
    {
        int i =0;
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        int restoredText = prefs.getInt("sessioncount", 0);
        if (restoredText != 0)
        {
            i = prefs.getInt("sessioncount", 0); //0 is the default value.z
            Sessioncount = i;
        }
        return i;
    }

    public static boolean CheckInternet(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;

        } else
        {
            return false;
        }

    }

    //method to select bike
    public static int  Choose_Bike(String vin)
    {
        vin = vin.substring(0,5);
        switch (vin)
        {
            case "MD638" :   BikeModel =1; break; //RR310
            case "MD637" :   BikeModel =2; break; //RTR200FI4V
            case "MD634" :   BikeModel =3; break; //RTR160
            case "MD637CR" : BikeModel =4;break;  //APACHE2004V       //ABS
            //case "MD637AE" : BikeModel =5;break;  //APACHE1604V
            case "MD634BE" : BikeModel =5; break; //APACHE1602V        //ABS
            case "MD634CE" : BikeModel =6;break;  //APACHE1802V      //ABS
            default: BikeModel=0;break;


        }
        return BikeModel;
    }
    public static int DialogDelay = 2000;
   // static Dialog mdialog;

    public static void ShowDialog(final Dialog dialog, String msg, boolean val, int i)
    {

        //mdialog = new Dialog(context);
        ImageView iv1,iv2,iv3;
        TextView Msg;
        Animation animation1,animation2;
        dialog.setContentView(R.layout.activity_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        iv1 =  dialog.findViewById(R.id.loadiv1);
        iv2 =  dialog.findViewById(R.id.loadiv2);
        iv3 =  dialog.findViewById(R.id.imageView);
        Msg =  dialog.findViewById(R.id.msg);
        animation1 = AnimationUtils.loadAnimation(dialog.getContext(),R.anim.rotate);
        animation2 = AnimationUtils.loadAnimation(dialog.getContext(),R.anim.rotate1);
        if(val)
        {
            iv1.setVisibility(View.INVISIBLE);
            iv2.setVisibility(View.INVISIBLE);
            iv3.setVisibility(View.VISIBLE);
        }
        else
        {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv1.startAnimation(animation1);
            iv2.startAnimation(animation2);
            iv3.setVisibility(View.INVISIBLE);
        }

        Msg.setText(msg);

        if(val)
        {
            dialog.hide();

            if(i==1)
            {
                iv3.setImageResource(R.drawable.right);
            }
            else
            if(i==0)
            {
                iv3.setImageResource(R.drawable.wrong);
            }
            else if(i==10)
            {
                try {
                    if(dialog!=null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
                }
                catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                    Log.e(TAG,e.getMessage());

                } catch (final Exception e) {
                    // Handle or log or ignore
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                } finally {
                    Dialog dialog1 = dialog;
                    dialog1 = null;
                    Log.e(TAG,"DIALOG FINALY");
                }
            }
            else
            {

            }
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        if(dialog!=null && dialog.isShowing())
                        {
                            dialog.dismiss();
                        }
                    }
                    catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                        Log.e(TAG,e.getMessage());

                    } catch (final Exception e) {
                        // Handle or log or ignore
                        e.printStackTrace();
                        Log.e(TAG,e.getMessage());
                    } finally {
                        Dialog dialog1 = dialog;
                        dialog1 = null;
                        Log.e(TAG,"DIALOG FINALY");
                    }

                }
            },DialogDelay);
        }
        dialog.setCancelable(false);
        dialog.show();
    }

 /*   public  static  void dismissDialog()
    {
       if(mdialog!=null && mdialog.isShowing())
       {
        mdialog.dismiss();
       }

    }*/
    static DialogClass dialogClass ;
    public static void displayMessage(String msg,boolean val,int i,Context context)
    {
        dialogClass = new DialogClass(msg,val,i,context);
        //dialogClass.dialogMessageDismiss();
        dialogClass.dialogMessageShow();
    }
    public static  void dismissMessage()
    {
        if(dialogClass!=null)
        {
            dialogClass.dialogMessageDismiss();
        }
    }

    public static void ShowNotCancelableDialog(Dialog dialog,String msg,boolean val,int i)
    {
        final Dialog mdialog = dialog;
        ImageView iv1,iv2,iv3;
        TextView Msg;
        Animation animation1,animation2;
        mdialog.setContentView(R.layout.activity_dialog);
        mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mdialog.setCanceledOnTouchOutside(false);
        iv1 = (ImageView) mdialog.findViewById(R.id.loadiv1);
        iv2 = (ImageView) mdialog.findViewById(R.id.loadiv2);
        iv3 = (ImageView) mdialog.findViewById(R.id.imageView);
        Msg = (TextView) mdialog.findViewById(R.id.msg);
        animation1 = AnimationUtils.loadAnimation(mdialog.getContext(),R.anim.rotate);
        animation2 = AnimationUtils.loadAnimation(mdialog.getContext(),R.anim.rotate1);
        if(val)
        {
            iv1.setVisibility(View.INVISIBLE);
            iv2.setVisibility(View.INVISIBLE);
            iv3.setVisibility(View.VISIBLE);
        }
        else
        {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv1.startAnimation(animation1);
            iv2.startAnimation(animation2);
            iv3.setVisibility(View.INVISIBLE);
        }

        Msg.setText(msg);

        if(val)
        {
            mdialog.hide();



            if(i==1)
            {
                iv3.setImageResource(R.drawable.right);
            }
            else
            if(i==0)
            {
                iv3.setImageResource(R.drawable.wrong);
            }
            else
            {

            }
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mdialog.hide();
                }
            },DialogDelay);
        }
        mdialog.show();
        mdialog.setCancelable(false);
    }
    //screenshot
    public static void  takeScreenshot(View view)
    {
        Date now = new Date();
         android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/RideScan/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = view ;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            AppVariables.PlayCameraSound(view.getContext());
           // openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }



    //PDI Variables
    //bike model
    public static String bikemodel = "";
    //tvsm model
    public static String tvsm = "";
    //date of pdi
    public static String date_of_pdi = "";
    //chassis no
    public static String chassis_no = "";
    //date of inv
    public static String date_of_inv = "";
    //battery no
    public static String battery_no = "";

    //Miscellaneous points
    //washing Done
    public static String washingstr = "";
    public static String washingstr_reason = "-";
    //No dent & Damage
    public static String nodentstr = "";
    public static String nodentstr_reason = "-";
    //Battery Volatge With Load Tester
    public static String batvolstr = "";
    public static String batvolstr_reason = "-";

    //Lock operations
    //Ingition
    public static String Ignition ="";
    public static String Ignition_reason ="-";
    //Steering
    public static String Steering = "";
    public static String Steering_reason = "-";
    //Fuel Tank Cap
    public static String Fuel_Tank_Cap = "";
    public static String Fuel_Tank_Cap_reason = "-";

    //Operations
    public static String Free_play_adjustments = "";
    public static String Free_play_adjustments_reason = "-";
    //frontbrk
    public static String Br_front = "";
    public static String Br_front_reason = "-";
    //rearbrk
    public static String Br_rear = "";
    public static String Br_rear_reason = "-";
    //clutchbrk
    public static String Br_clutch = "";
    public static String Br_clutch_reason = "-";
    //Throttle
    public static String Br_Throttle = "";
    public static String Br_Throttle_reason = "-";
    //Service Remainder
    public static String Service_Rem = "";
    public static String Service_Rem_reason = "";

    //Drive Chain
    //alignment
    public static String alignment = "";
    public static String alignment_reason = "-";
    //slackness
    public static String slackness = "";
    public static String slackness_reason = "-";
    //brfld front
    public static String brfld_front = "";
    public static String brfld_front_reason = "-";
    //brfld rear
    public static String brfld_rear = "";
    public static String brfld_rear_reason = "-";
    //brfld engine_sus
    public static String brfld_eng_sup = "";
    public static String brfld_eng_sup_reason = "-";
    //brfld cool_lvl
    public static String brfld_cool_lvl = "";
    public static String brfld_cool_lvl_reason = "-";

    //Fasterners Check
    //Front Wheel Axle Nut
    public static String fast_front_wheel = "";
    public static String fast_front_wheel_reason = "-";
    //Fork Center Bolt
    public static String fast_fork_center = "";
    public static String fast_fork_center_reason = "-";
    //Handle Bar Mounting Bolt
    public static String fast_Handle_Bar = "";
    public static String fast_Handle_Bar_reason = "-";
    //Rear Shock Absorder Mounting Bolts
    public static String fast_Rear_shock_absorder = "";
    public static String fast_Rear_shock_absorder_reason = "-";
    //Rear Wheel Axle/Sprocket Shaft Nuts
    public static String fast_rear_wheel_axle = "";
    public static String fast_rear_wheel_axle_reason = "-";
    //Front & Rear Brake Callipers Mounting bolts
    public static String fast_front_rear_brake ="";
    public static String fast_front_rear_brake_reason ="-";
    //Engine Mounting Bolts
    public static String fast_eng_mount = "";
    public static String fast_eng_mount_reason = "-";
    //Silencer Mounting nut
    public static String silencer_mount = "";
    public static String silencer_mount_reason = "-";


    //Suspension
    //Check for any sticky Movement
    public static String sus_check_stick = "";
    public static String sus_check_stick_reason = "-";
    //Rear Shock Absorber
    public static String sus_rear_shock = "";
    public static String sus_rear_shock_reason = "-";
    //For Free Stroking & Setting on
    public static String sus_free_strok = "";
    public static String sus_free_strok_reason = "-";
    //wheels
    public static String sus_wheels = "";
    public static String sus_wheels_reason = "-";
    //tyre pressure front solo
    public static String tyre_front_solo = "";
    public static String tyre_front_solo_reason = "-";
    //tyre pressure front double
    public static String tyre_front_double = "";
    public static String tyre_front_double_reason = "-";
    //tyre pressure rear solo
    public static String tyre_rear_solo = "";
    public static String tyre_rear_solo_reason = "-";
    //tyre pressure rear double
    public static String tyre_rear_double = "";
    public static String tyre_rear_double_reason = "-";

    //Liquids
    //Engine Oil
    public static String liq_eng_oil = "";
    public static String liq_eng_oil_reason = "-";
    //Engine Oil Level Check
    public static String liq_eng_oil_lvl = "";
    public static String liq_eng_oil_lvl_reason = "-";
    //Check for any leakages
    public static String liq_check_leakage = "";
    public static String liq_check_leakage_reason = "-";
    //Coolant Level (Cold Condition)
    public static String liq_coolant_level = "";
    public static String liq_coolant_level_reason = "-";
    //Headlight Adjustment - Refer Annexure
    public static String lig_headlit_adj = "";
    public static String lig_headlit_adj_reason = "-";

    //Electricals
    //Left hand Switch Operation
    public static String Ele_left_hand_switch = "";
    public static String Ele_left_hand_switch_reason = "-";
    //Horn
    public static String Ele_horn = "";
    public static String Ele_horn_reason = "-";
    //Indicator
    public static String Ele_Indicator = "";
    public static String Ele_Indicator_reason = "-";
    //Pass Switch
    public static String Ele_pass_switch = "";
    public static String Ele_pass_switch_reason = "-";
    //All Bulbs Functioning
    public static String Ele_All_bulbs = "";
    public static String Ele_All_bulbs_reason = "-";
    //Kill Switch
    public static String Ele_kill_switch = "";
    public static String Ele_kill_switch_reason = "-";
    //Cluster
    public static String Ele_Cluster = "";
    public static String Ele_Cluster_reason = "-";
    //Time Setting
    public static String Ele_time_setting = "";
    public static String Ele_time_setting_reason = "-";
    //OverSpeed Setting
    public static String Ele_Overspeed_setting = "";
    public static String Ele_Overspeed_setting_reason = "-";

    //Legal Parts Installation
    //Front & Rear Number Plate
    public static String Legal_frnt_rear_num_plt = "";
    public static String Legal_frnt_rear_num_plt_reason = "-";
    //Rear View Mirror
    public static String Legal_rear_view_mirror = "";
    public static String Legal_rear_view_mirror_reason = "-";
    //First Aid Kid
    public static String Legal_first_aid_kit = "";
    public static String Legal_first_aid_kit_reason = "-";
    //Tool Kit
    public static String Legal_tool_kit = "";
    public static String Legal_tool_kit_reason = "-";
    //Saree Guard Fitment
    public static String Legal_saree_guard = "";
    public static String Legal_saree_guard_reason = "-";

    //Road Test
    //Starting
    public static String RoadTest_starting = "";
    //Gear Shifting Operation
    public static String RoadTest_Gear_shift_oper = "";
    //Clutch Operation
    public static String RoadTest_clutch_operation = "";
    //Brake Operation
    public static String RoadTest_brake_operation = "";
    //Instrument Cluster Working
    public static String RoadTest_ic_working = "";
    //Fuel Meter Operation
    public static String RoadTest_fuel_meter_operation = "";
    //Any ABS Lights
    public static String RoadTest_ABS_Lights = "";
    //Any Abnormal Noise
    public static String RoadTest_any_abnormal_noise = "";
    //Fan Test
    public static String RoadTest_fan_test = "";

    //Techician Name:
    public static String Techician_name = "";

    public static String Sign_name="";


public static void PlayCameraSound(Context context)
{
    MediaPlayer mp;
    mp = MediaPlayer.create(context, R.raw.camera_click);
    mp.start();

}


static ArrayList<String> negal ;

public static void loadNegativeResponses(Context context, String fileName)  //1//negres.csv
{
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(fileName)));
        negal = new ArrayList<>();
        // do reading, usually loop until end of file reading
        String mLine;
        while ((mLine = reader.readLine()) != null)
        {
            negal.add(mLine);
            //Log.e(TAG,"negative responses : "+mLine);
        }

    }
    catch (IOException e)
    {
        e.printStackTrace();
        Log.e(TAG,"Loading negative response Failed: "+e.getMessage());
    }
    finally {
        if (reader != null) {
            try {
                reader.close();
                AppVariables.NRC =  negal;
            } catch (IOException e) {
                Log.e(TAG,"error while closing Buffreader: "+e.getMessage() );
            }
        }
    }
}





}
