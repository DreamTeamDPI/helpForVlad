package com.example.semmes.lab2_dict_1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    final String LOG_TAG = "sqllite";

    Button btn1,btn2,btn3;
    EditText rusW, englW;

    DBDictionary dbDictionary;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        btn1 = (Button) findViewById(R.id.btnAdd);
        btn2 = (Button) findViewById(R.id.btnExit);
        btn3 = (Button) findViewById(R.id.btnClear);

        rusW = (EditText) findViewById(R.id.rW);
        englW = (EditText) findViewById(R.id.eW);


        dbDictionary = new DBDictionary(this);
        db = dbDictionary.getWritableDatabase();


        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnAdd: {
                        db.beginTransaction();
                        ContentValues cv = new ContentValues();
                        cv.put("russian", String.valueOf(rusW.getText()));
                        cv.put("english", String.valueOf(englW.getText()));
                        cv.put("status", 1);
                        cv.put("count", 0);
                        Log.d(LOG_TAG, "_id = " + db.insert(DBDictionary.DATABASE_TABLE, null, cv));
                        db.setTransactionSuccessful();
                        db.endTransaction();

                        break;
                    }
                    case R.id.btnClear:{
                        rusW.setText("");
                        englW.setText("");
                        break;
                    }
                    case R.id.btnExit:{
                        dbDictionary.close();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("date", "Данные записаны");
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    }
                }
            }
        };

        Log.d(LOG_TAG, "there");
        btn1.setOnClickListener(cl);
        btn2.setOnClickListener(cl);
        btn3.setOnClickListener(cl);

    }
}
