package com.example.tvsridescan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tvsridescan.R;

import java.util.ArrayList;


public class CustomAdapter_Config extends ArrayAdapter<DataModel_Config> implements View.OnClickListener{

    private ArrayList<DataModel_Config> dataSet;
    Context mContext;




    // View lookup cache
    private static class ViewHolder
    {
        TextView txtvciser;
        TextView txtexpdate;
    }

    public CustomAdapter_Config(ArrayList<DataModel_Config> data, Context context)
    {
        super(context, R.layout.row_item_config, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v)
    {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel_Config dataModel=(DataModel_Config) object;

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        DataModel_Config dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_config, parent, false);
            viewHolder.txtvciser = (TextView) convertView.findViewById(R.id.serial);
            viewHolder.txtexpdate = (TextView) convertView.findViewById(R.id.expdate);

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
        viewHolder.txtvciser.setText(dataModel.getVciserial());
        viewHolder.txtexpdate.setText(dataModel.getExpdate());

        return convertView;
    }


}
