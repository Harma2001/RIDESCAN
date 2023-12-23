package com.example.tvsridescan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tvsridescan.R;

import java.util.ArrayList;


public class CustomAdapter_CardView extends ArrayAdapter<DataModelCardView> {

    private ArrayList<DataModelCardView> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtsno;
        TextView txtshrtdesc;
        TextView txtvalue1;
    }

    public CustomAdapter_CardView(ArrayList<DataModelCardView> data, Context context)
    {
        super(context, R.layout.row_item_view1, data);
        this.dataSet = data;
        this.mContext=context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        final DataModelCardView dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_view1, parent, false);
            viewHolder.txtsno = (TextView) convertView.findViewById(R.id.sn);
            viewHolder.txtshrtdesc = (TextView) convertView.findViewById(R.id.desc);
            viewHolder.txtvalue1 = (TextView) convertView.findViewById(R.id.val);


            result=convertView;
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
      //  result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtsno.setText(dataModel.getLpno());
        viewHolder.txtshrtdesc.setText(dataModel.getLpname());
        viewHolder.txtvalue1.setText(dataModel.getLpval1());

        int i=dataModel.getColorcode();

        if(0==i)
        {
            viewHolder.txtvalue1.setTextColor(Color.WHITE);
        }
        else
        if(1==i)
        {
            viewHolder.txtvalue1.setTextColor(Color.parseColor("#d6d4d4"));
        }

        return convertView;
    }


}
