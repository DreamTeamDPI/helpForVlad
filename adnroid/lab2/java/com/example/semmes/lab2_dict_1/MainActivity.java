package com.example.semmes.lab2_dict_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "sqllite";

    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;

    private NotificationManager nm;

    DBDictionary dbDictionary;
    SQLiteDatabase db;

    final String DIR_SD = "MyFiles";
    final String FILENAME_SD = "dictionary.txt";

    List<Pair> pairList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        btn1 = (Button) findViewById(R.id.btnAdd);
        btn2 = (Button) findViewById(R.id.btnExer);
        btn3 = (Button) findViewById(R.id.showAll);
        btn4 = (Button) findViewById(R.id.files);
        btn5 = (Button) findViewById(R.id.deleteRows);
        btn6 = (Button) findViewById(R.id.returnWords);

        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnAdd:{
                        Intent intent = new Intent(getApplicationContext(),AddActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    }
                    case R.id.btnExer:{
                        Intent intent = new Intent(getApplicationContext(),ExerActivity.class);
                        startActivityForResult(intent,2);
                        break;
                    }
                    case R.id.showAll:{
                        Intent intent = new Intent(getApplicationContext(),ShowAll.class);
                        startActivityForResult(intent,3);
                        break;
                    }
                    case R.id.files:{
                        readFileSD();
                        break;
                    }
                    case R.id.deleteRows:{
                        Log.d(LOG_TAG,"+" + 0 );
                        deleteRow();
                        break;
                    }
                    case R.id.returnWords:{
                        Log.d(LOG_TAG,"+" + 1 );
                        returnWord();
                        break;
                    }
                }
            }
        };
        btn1.setOnClickListener(cl);
        btn2.setOnClickListener(cl);
        btn3.setOnClickListener(cl);
        btn4.setOnClickListener(cl);
        btn5.setOnClickListener(cl);
        btn6.setOnClickListener(cl);


        dbDictionary = new DBDictionary(this);
        db = dbDictionary.getWritableDatabase();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    ///String s =  data.getStringExtra("date");
                    Log.d(LOG_TAG, data.getStringExtra("date"));
                    //xdb = dbDictionary.getWritableDatabase();
                    if(true) {
                        Cursor cursor = null;
                        cursor = db.query(DBDictionary.DATABASE_TABLE, null, null, null, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                String str;
                                do {
                                    str = "";
                                    for (String cn : cursor.getColumnNames()) {
                                        str = str.concat(cn + " = "
                                                + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                                    }
                                    Log.d(LOG_TAG, str);

                                } while (cursor.moveToNext());
                            }
                            cursor.close();
                        } else
                            Log.d(LOG_TAG, "Cursor is null");
                        cursor.close();
                    } else {

                    }
                    break;
                case 2:
                    ///String s =  data.getStringExtra("date");
                    Log.d(LOG_TAG, data.getStringExtra("date"));
                    //xdb = dbDictionary.getWritableDatabase();
                    if(data.getStringExtra("date").equals("0")){
                        showNoLearn(15);
                    }
                    break;
            }
        }
        else{
        }
    }

    private void returnWord() {

        Cursor c = null;
        List<Pair> pairsw = new ArrayList<>();
        String selection = "status = ?";
        String[] selectionArgs = new String[]{"0"};
        c = db.query(DBDictionary.DATABASE_TABLE, null, selection, selectionArgs, null, null, null);

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
                    pairsw.add(pair);
                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Cursor is null");

        ContentValues cv;
        for(Pair paire : pairsw){
            cv = new ContentValues();
            cv.put("russian", paire.getRus());
            cv.put("english", paire.getEngl());
            cv.put("status", 1);
            cv.put("count", 0);
            db.update(DBDictionary.DATABASE_TABLE, cv, "_id = ?", new String[]{String.valueOf(paire.getId())});
        }

        c.close();
    }

    private void readFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            Toast.makeText(getApplicationContext(),"no sd card",Toast.LENGTH_SHORT).show();
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            pairList = new ArrayList<>();
            while ((str = br.readLine()) != null) {
                String[] s = str.split(" ");
                ContentValues cv = new ContentValues();
                cv.put("russian", String.valueOf(s[1]));
                cv.put("english", String.valueOf(s[0]));
                cv.put("status", 0);
                cv.put("count", 0);
                db.insert(DBDictionary.DATABASE_TABLE, null, cv);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRow(){
        Log.d(LOG_TAG,"+" + 0 );
        int i = db.delete(DBDictionary.DATABASE_TABLE,null,null);
        Log.d(LOG_TAG,"+" + i );
    }

    public void showNoLearn(int id) {
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Resources res = context.getResources();
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.no)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.no))
                .setContentTitle("Нет слов")
                .setContentText("в базе нет слов")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("Ошибка");

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }
        nm.notify(id, notification);
    }


}
