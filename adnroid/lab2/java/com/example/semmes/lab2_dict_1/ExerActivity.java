package com.example.semmes.lab2_dict_1;

import android.annotation.TargetApi;
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
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ExerActivity extends AppCompatActivity {

    boolean rusWord = true;

    boolean predicted = false;

    private NotificationManager nm;

    ContentValues cv;

    final String LOG_TAG = "sqllite";

    Button btn1, btn2;
    TextView word;

    ListView listView;
    List<Pair> list;
    List<String> strings;

    DBDictionary dbDictionary;
    SQLiteDatabase db;

    Pair mainPair;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exer);

        word = (TextView) findViewById(R.id.word);
        listView = (ListView) findViewById(R.id.listView);
        btn1 = (Button) findViewById(R.id.btnOk);
        btn2 = (Button) findViewById(R.id.btnExitExercise);

        dbDictionary = new DBDictionary(this);
        db = dbDictionary.getWritableDatabase();

        nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        createTask();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!predicted) {
                    String answer = strings.get(position);
                    Log.d(LOG_TAG, "click " + position + "  thing " + strings.get(position));
                    showNotification();
                    if (rusWord) {
                        if (mainPair.getEngl().equals(answer)) {
                            view.setBackgroundColor(Color.GREEN);
                            rightAnswer(mainPair);
                        } else {
                            view.setBackgroundColor(Color.RED);
                            wrongAnswer(mainPair);
                        }
                    } else {
                        if (mainPair.getRus().equals(answer)) {
                            view.setBackgroundColor(Color.GREEN);
                            rightAnswer(mainPair);
                        } else {
                            view.setBackgroundColor(Color.RED);
                            wrongAnswer(mainPair);
                        }
                    }
                    predicted = true;
                }
            }
        });

        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnOk: {
                        rusWord = false;
                        predicted = false;
                        createTask();
                        break;
                    }
                    case R.id.btnExitExercise: {
                        dbDictionary.close();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("date", "1");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                }
            }
        };

        btn1.setOnClickListener(cl);
        btn2.setOnClickListener(cl);

    }

    private void wrongAnswer(Pair mainPair) {
        showNoLearn(mainPair, 125);
        cv = new ContentValues();
        cv.put("russian", mainPair.getRus());
        cv.put("english", mainPair.getEngl());
        cv.put("status", 1);
        cv.put("count", 0);
        db.update(DBDictionary.DATABASE_TABLE, cv, "_id = ?", new String[]{String.valueOf(mainPair.getId())});
    }


    private void rightAnswer(Pair mainPair) {
        mainPair.setCount(mainPair.getCount() + 1);
        cv = new ContentValues();
        if (mainPair.getCount() >= 5) {
            showLearn(mainPair, 123);
            cv.put("russian", mainPair.getRus());
            cv.put("english", mainPair.getEngl());
            cv.put("status", 0);
            cv.put("count", mainPair.getCount());
            db.update(DBDictionary.DATABASE_TABLE, cv, "_id = ?", new String[]{String.valueOf(mainPair.getId())});
        } else {
            cv.put("russian", mainPair.getRus());
            cv.put("english", mainPair.getEngl());
            cv.put("status", 1);
            cv.put("count", mainPair.getCount());
            db.update(DBDictionary.DATABASE_TABLE, cv, "_id = ?", new String[]{String.valueOf(mainPair.getId())});

        }

    }

    private void createTask() {
        Cursor c = null;
        try {
            String selection = "status = ?";
            String[] selectionArgs = new String[]{"1"};
            c = db.query(DBDictionary.DATABASE_TABLE, null, selection, selectionArgs, null, null, null);

            list = new ArrayList<>();

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Pair pair = new Pair();
                        for (String cn : c.getColumnNames()) {
                            Log.d(LOG_TAG, cn);
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
            } else {
                Log.d(LOG_TAG, "Cursor is null");


            }

            Random random = new Random();

            int ind = random.nextInt(list.size());
            mainPair = new Pair();
            mainPair = list.get(ind);
            strings = new ArrayList<>(5);

            if (random.nextDouble() < 0.5)
                rusWord = true;
            else
                rusWord = false;

            if (rusWord) {
                createList(1, random);
            } else {
                createList(2, random);
            }
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("date", "0");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void createList(int num, Random random) {
        if (num == 1) {
            strings.add(mainPair.getEngl());
            for (int i = 0; i < 5; i++) {
                int s = random.nextInt(list.size());
                String stroke = list.get(s).getEngl();
                if (strings.contains(stroke))
                    i--;
                else
                    strings.add(stroke);
            }
            Collections.shuffle(strings);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
            word.setText(mainPair.getRus());
        } else {
            strings.add(mainPair.getRus());
            for (int i = 0; i < 5; i++) {
                int s = random.nextInt(list.size());
                String stroke = list.get(s).getRus();
                if (strings.contains(stroke))
                    i--;
                else
                    strings.add(stroke);
            }
            Collections.shuffle(strings);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
            word.setText(mainPair.getEngl());
        }
    }

    private void showNotification() {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, ExerActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)//.setSmallIcon(R.drawable.images).
                // setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.images))
                .setTicker("Движение не возмножно")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Движение не возмножно")
                .setContentText("Движение не возмножно").build();
        notification.contentIntent = contentIntent;
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    public void showLearn(Pair pair, int id) {
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Resources res = context.getResources();
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.yes)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.yes))
                .setContentTitle("Изучено новое слово")
                .setContentText("Вы выучили слово: " + pair.getRus() + " - " + pair.getEngl())
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("Изучено слово");

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }
        nm.notify(id, notification);
    }

    public void showNoLearn(Pair pair, int id) {
        Context context = getApplicationContext();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Resources res = context.getResources();
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.no)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.no))
                .setContentTitle("Вы ошиблись")
                .setContentText("Вы не выучили слово: " + pair.getRus() + " - " + pair.getEngl())
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
