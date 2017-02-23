package com.hbbmobile.bsnapp.discount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hbbmobile.bsnapp.R;

import java.util.List;

/**
 * Created by Me on 11/23/2016.
 */

public class AdapterDiscount extends BaseAdapter {
    private List<ModelDis> listdata;
    private LayoutInflater layoutInflater;
    private Context context;

    public AdapterDiscount(Context aContext, List<ModelDis> listdata) {
        this.context = aContext;
        this.listdata = listdata;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.itemdiscount, null);
            holder = new ViewHolder();

            holder.tvtitle = (TextView) convertView.findViewById(R.id.tvtitle);
            holder.tvtime = (TextView) convertView.findViewById(R.id.tvtime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ModelDis country = this.listdata.get(position);
        holder.tvtitle.setText(country.getTitle());
        holder.tvtime.setText(country.getTime());
        return convertView;
    }


    public static class ViewHolder {
        TextView tvtitle, tvtime;
    }
}
