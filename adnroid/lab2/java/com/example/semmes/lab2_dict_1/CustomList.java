package com.example.semmes.lab2_dict_1;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SemmEs on 04.04.2016.
 */
public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final List<Pair> pair;
    private final List<String> rusList;

    public CustomList(Activity context,List<Pair> pair, List<String> rusList) {
        super(context, R.layout.custom_list,rusList);


        this.context = context;
        this.pair = pair;
        this.rusList = rusList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.custom_list, null, true);

        Log.d("sqllite",pair.get(position).getRus() + " hi");

        TextView txtRus = (TextView) rowView.findViewById(R.id.rustxt);
        TextView txtEngl = (TextView) rowView.findViewById(R.id.engltxt);
        TextView txtStatus = (TextView) rowView.findViewById(R.id.status);
        TextView txtCount = (TextView) rowView.findViewById(R.id.count);

        txtRus.setText(pair.get(position).getRus() + "   -    ");
        txtEngl.setText(pair.get(position).getEngl());
        txtStatus.setText("status: " + pair.get(position).getStatus());
        txtCount.setText("count: " + pair.get(position).getCount());
        return rowView;
    }
}