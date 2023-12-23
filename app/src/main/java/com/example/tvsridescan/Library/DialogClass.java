package com.example.tvsridescan.Library;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvsridescan.R;

public class DialogClass {

    Dialog mdialog;
    int DialogDelay = 2000;

    public  DialogClass(String msg,boolean val,int i,Context context)
    {

        mdialog = new Dialog(context);
        ImageView iv1,iv2,iv3;
        TextView Msg;
        Animation animation1,animation2;
        mdialog.setContentView(R.layout.activity_dialog);
        mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mdialog.setCanceledOnTouchOutside(false);
        iv1 =  mdialog.findViewById(R.id.loadiv1);
        iv2 =  mdialog.findViewById(R.id.loadiv2);
        iv3 =  mdialog.findViewById(R.id.imageView);
        Msg =  mdialog.findViewById(R.id.msg);
        animation1 = AnimationUtils.loadAnimation(context,R.anim.rotate);
        animation2 = AnimationUtils.loadAnimation(context,R.anim.rotate1);
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
            new android.os.Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mdialog.hide();
                }
            },DialogDelay);
        }

    }
    public  void dialogMessageShow()
    {
        if(  mdialog!=null)
        {
            mdialog.show();
        }
    }

    public void dialogMessageDismiss()
    {
        if(  mdialog!=null && mdialog.isShowing())
        {
            mdialog.dismiss();
        }
    }

}
