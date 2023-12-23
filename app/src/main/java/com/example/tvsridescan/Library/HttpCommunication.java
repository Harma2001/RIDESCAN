
package com.example.tvsridescan.Library;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class HttpCommunication
{

    private static String ENDPOINT="https://amsmssi.com/DMS/api/"; //Element.ALIGN_LEFT
    public static String API_KEY = "34167243BFD235C8D318E95CD8C95";
    public static String email_id = "XYZ@gmail.com";
    static public String password = null;
    public static String  OTP = null;
    public static String  Device_id = null;
    public static String  Secured_Password = null;
    public static String Verify_Email = ENDPOINT+"verfy_email";
    public static String Get_OTP = ENDPOINT+"get_otp/";
    public static String Verify_OTP = ENDPOINT+"verify_otp/";
    public static String Login_URL = ENDPOINT+"login/";
    public static String Forgot_Password = ENDPOINT+"forgot_password/";
    public static String Get_Secure_OTP =ENDPOINT+"get_secure_otp";
    public static String Verify_secure_OTP = ENDPOINT+"verify_secure_otp";
    public static String Get_Secured_Email = ENDPOINT+"get_secure_emails";
    public static String Download_Path = null;
    public static String BIKEMODEL = ENDPOINT+"get_falsh_files";
    public static String JSONDATA = null;
    public static String PostUrl = null;
    public static InputStream is = null;
    //User Details
    public static String Username = null;
    public static String Status = null;
    public static String Address = null;
    public static ArrayList<String> Device = new ArrayList<>();
    public static String is_Verfied;

    public static String POSTDATA() //
    {
        String op = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(PostUrl);
        httpPost.setHeader("Content-type","application/json");

        try
        {
            JSONObject object = new JSONObject(JSONDATA);
            httpPost.getParams().setParameter("json",object);

            StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line ;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }
            is.close();
            op = sb.toString();

            Log.e("op",sb.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return op;
    }

}