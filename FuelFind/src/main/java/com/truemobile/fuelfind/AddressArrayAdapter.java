package com.truemobile.fuelfind;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Werner on 2013/10/06.
 */
public class AddressArrayAdapter extends ArrayAdapter<Address> {

    HashMap<Address, Integer> mIdMap = new HashMap<Address, Integer>();
    Context context;
    int layoutResourceId;
    public AddressArrayAdapter(Context context, int layoutResourceId,
                              List<Address> objects) {
        super(context, layoutResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            ((TextView)row.findViewById(R.id.search_location)).setText(getItem(position).getAddressLine(1));
            ((TextView)row.findViewById(R.id.search_province)).setText(getItem(position).getAddressLine(2));

            row.setTag(getItem(position));
        }

        return row;
    }

    @Override
    public long getItemId(int position) {
        Address item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}

