package com.example.tvsridescan.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tvsridescan.KwpABS.KwpAbsMenu;
import com.example.tvsridescan.Library.AppVariables;
import com.example.tvsridescan.R;
import com.example.tvsridescan.StartDiagnosis;
import com.example.tvsridescan.abs.ABSMenu;
import com.example.tvsridescan.rtr1602v_1ch.RTR1602v1chMenu;
import com.example.tvsridescan.rtr1604v1chefi.RTR1604v1chefiMenu;
import com.example.tvsridescan.rtr2004v1chefi.RTR2004v1chefiMenu;
import com.example.tvsridescan.rtr2004v2chefi.RTR2004vchefi;

import java.util.List;

//import com.bumptech.glide.Glide;


/**
 * Created by kamal_bunkar on 09-01-2018.
 */

public class NewProductRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NewProductModel> mNewProdModelList;
    private String TAG ="NewProd_adapter";
    private int mScrenwith;
    MediaPlayer mp;
    Animation animFadein1;
    public NewProductRecyclerAdapter(Context context, List<NewProductModel> list, int screenwidth ){
        mContext = context;
        mNewProdModelList = list;
        mScrenwith =screenwidth;

    }

    private class NewProductHolder extends RecyclerView.ViewHolder {
        ImageView prod_img;
        TextView prod_name;
        LinearLayout cardView;

        public NewProductHolder(View itemView) {
            super(itemView);
            prod_img  =  itemView.findViewById(R.id.prod_imgre);
            prod_name = itemView.findViewById(R.id.prod_name);
            cardView  =  itemView.findViewById(R.id.card_view);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( mScrenwith - (mScrenwith/100*40), LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10,10,10,10);
            cardView.setLayoutParams(params);
            cardView.setPadding(5,5,5,5);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_newproduct, parent,false);
        Log.e(TAG, "  view created ");
        mp = MediaPlayer.create( mContext,R.raw.buttonclick);

        return new NewProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final NewProductModel model = mNewProdModelList.get(position);

        Log.e(TAG, " assign value ");
        ((NewProductHolder) holder).prod_name.setText(model.getProd_name());
        ((NewProductHolder) holder).prod_img.setImageResource(model.getImg_url());

        ((NewProductHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String whichBike = model.getProd_id();

                switch (whichBike)
                {
                    case "1":
                        animFadein1 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);

                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        AppVariables.BikeModel_str="RR310";
                        AppVariables.BikeModel = 1;
                        mp.start();
                        ((NewProductHolder) holder).cardView.startAnimation(animFadein1);

                        Intent intent = new Intent(mContext,StartDiagnosis.class);
                        Activity activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);

                        break;
                    case "2":
                        Animation  animFadein2 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();
                        AppVariables.BikeModel_str="RTR2004v_2CH-CARB";  //                                         RTR2004v_2CH_CARB
                        AppVariables.BikeModel = 2;
                        ((NewProductHolder) holder).cardView.startAnimation(animFadein2);

                        intent = new Intent(mContext, ABSMenu.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);

                        break;
                    case "3":
                        Animation  animFadein3 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();
                        AppVariables.BikeModel_str="RTR2004v_2CH-EFI";  //                                                      RTR2004v_2CH_EFI
                        AppVariables.BikeModel = 3;

                        ((NewProductHolder) holder).cardView.startAnimation(animFadein3);

                        intent = new Intent(mContext, RTR2004vchefi.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;
                    case "4":
                        Animation  animFadein4 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();
                        AppVariables.BikeModel_str="RTR1604v_1CH-CARB";
                        AppVariables.BikeModel = 4;

                        ((NewProductHolder) holder).cardView.startAnimation(animFadein4);

                        intent = new Intent(mContext, KwpAbsMenu.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;
                    case "5":
                        Animation  animFadein5 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();
                        AppVariables.BikeModel_str="RTR1604v_1CH-EFI";                                    //  RTR200FI4V        RTR1604v_1CH_EFI
                        AppVariables.BikeModel = 5;

                        ((NewProductHolder) holder).cardView.startAnimation(animFadein5);

                        intent = new Intent(mContext, RTR1604v1chefiMenu.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;
                    case "6":
                        Animation  animFadein6 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();

                        /*BOSCH ABS*/
                        AppVariables.BikeModel_str="RTR1602v_1CH";
                        AppVariables.BikeModel = 6;
                        ((NewProductHolder) holder).cardView.startAnimation(animFadein6);

                        intent = new Intent(mContext, RTR1602v1chMenu.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;
                    case "7":
                        Animation  animFadein7 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();

                        AppVariables.BikeModel_str="RTR2004v_1CH-CARB";
                        AppVariables.BikeModel = 7;
                        ((NewProductHolder) holder).cardView.startAnimation(animFadein7);

                        intent = new Intent(mContext, KwpAbsMenu.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;
                    case "8":
                        Animation  animFadein8 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();
                        ((NewProductHolder) holder).cardView.startAnimation(animFadein8);

                        AppVariables.BikeModel_str="RTR2004v_1CH-EFI";                             //            RTR200FI4V  RTR2004v_1CH_EFI
                        AppVariables.BikeModel = 8;
                        intent = new Intent(mContext, RTR2004v1chefiMenu.class);
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;


                    case "9":
                        Animation  animFadein9 = AnimationUtils.loadAnimation(mContext, R.anim.clickani);
                        if(mp.isPlaying())
                        {
                            mp.stop();
                        }
                        mp.start();
                        ((NewProductHolder) holder).cardView.startAnimation(animFadein9);

                        AppVariables.BikeModel_str="RTR1802v_1CH";                             //            RTR180
                        AppVariables.BikeModel = 9;
                        intent = new Intent(mContext, RTR1602v1chMenu.class); //because rtr 1602v1ch & 180
                        activity = (Activity) mContext;
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.out, R.anim.in);
                        break;
                }


               /* mp.start();
                ((NewProductHolder) holder).cardView.startAnimation(animFadein1);
                Toast.makeText(mContext, "Toast", Toast.LENGTH_SHORT).show();*/
            }
        });
       /* Glide.with(mContext)
                .load(model.getImg_url())
                .into(((NewProductHolder) holder).prod_img);*/
        // imageview glider lib to get imagge from url

    }

    @Override
    public int getItemCount() {
        return mNewProdModelList.size();
    }
}

