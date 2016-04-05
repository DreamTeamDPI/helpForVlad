package com.example.semmes.lab2_dict_1;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShowAll extends AppCompatActivity {

    final String LOG_TAG = "sqllite";

    ListView listView;
    List<Pair> list;

    DBDictionary dbDictionary;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        dbDictionary = new DBDictionary(this);
        db = dbDictionary.getWritableDatabase();

        Cursor c = null;
        c = db.query(DBDictionary.DATABASE_TABLE, null, null, null, null, null, null);
        list = new ArrayList<>();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Pair pair = new Pair();
                    for (String cn : c.getColumnNames()) {
                        int index = c.getColumnIndex(cn);
                        switch (index) {
                            case 0:
                                pair.setId(c.getInt(index));
                                break;
                            case 1:
                                pair.setRus(c.getString(index));
                                break;
                            case 2:
                                pair.setEngl(c.getString(index));
                                break;
                            case 3:
                                pair.setStatus(c.getInt(index));
                                break;
                            case 4:
                                pair.setCount(c.getInt(index));
                                break;
                        }
                    }
                    list.add(pair);
                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Cursor is null");
        Log.d(LOG_TAG, "Cursor is " + list);

        List<String> rusList = new ArrayList<>();
        for(Pair pair1 : list)
            rusList.add(pair1.getRus());

        CustomList adapter = new CustomList(ShowAll.this,list,rusList);
        listView=(ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });
    }
}
