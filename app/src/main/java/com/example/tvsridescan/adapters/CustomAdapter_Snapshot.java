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


public class CustomAdapter_Snapshot extends ArrayAdapter<DataModel_Snapshot> implements View.OnClickListener{

    private ArrayList<DataModel_Snapshot> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder
    {
        TextView val;
        TextView desc;
    }

    public CustomAdapter_Snapshot(ArrayList<DataModel_Snapshot> data, Context context)
    {
        super(context, R.layout.row_item_snapshot, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v)
    {
        int position=(Integer) v.getTag();
        Object object= getItem(position);

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        DataModel_Snapshot dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_snapshot, parent, false);
            viewHolder.val = (TextView) convertView.findViewById(R.id.code);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.shrtdesc);
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
        viewHolder.val.setText(dataModel.getVal());
        viewHolder.desc.setText(dataModel.getShrtdesc());

        return convertView;
    }


}
