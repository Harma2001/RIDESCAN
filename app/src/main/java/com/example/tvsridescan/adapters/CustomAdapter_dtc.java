package com.example.tvsridescan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tvsridescan.R;

import java.util.ArrayList;


public class CustomAdapter_dtc extends ArrayAdapter<DataModel_dtc> implements View.OnClickListener{

    private ArrayList<DataModel_dtc> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtName;
        TextView txtshrtdesc;
        TextView txtstatus;
        ImageView button;
    }

    public CustomAdapter_dtc(ArrayList<DataModel_dtc> data, Context context)
    {
        super(context, R.layout.row_item_dtc, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v)
    {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel_dtc dataModel=(DataModel_dtc) object;

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        DataModel_dtc dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_dtc, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.code);
            viewHolder.txtshrtdesc = (TextView) convertView.findViewById(R.id.shrtdesc);
            viewHolder.txtstatus = (TextView) convertView.findViewById(R.id.status);
          //  viewHolder.button = (ImageView)convertView.findViewById(R.id.info);
            result=convertView;
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtName.setText(dataModel.getDtccode());
        viewHolder.txtshrtdesc.setText(dataModel.getShrtdesc());
        viewHolder.txtstatus.setText(dataModel.getStatus());
        int i=dataModel.getColorcode();
        if(i==0)
        {
            viewHolder.txtstatus.setTextColor(Color.RED);
        }
        else
        if(i==1)
        {
            viewHolder.txtstatus.setTextColor(Color.parseColor("#ff9c00"));
        }
        else
        if(i==2)
        {
            viewHolder.txtstatus.setTextColor(Color.RED);
        }
        else
        if(i==3)
        {
            viewHolder.txtstatus.setTextColor(Color.parseColor("#FFC7CBCE"));
        }
        return convertView;
    }


}
