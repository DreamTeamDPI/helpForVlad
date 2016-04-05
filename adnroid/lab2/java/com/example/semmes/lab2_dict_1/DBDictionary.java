package com.example.semmes.lab2_dict_1;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by SemmEs on 31.03.2016.
 */
public class DBDictionary extends SQLiteOpenHelper implements BaseColumns {

    // названия столбцов
    public static final String RUSSIAN_WORD_COLUMN = "russian";
    public static final String ENGLISH_WORD_COLUMN = "english";
    public static final String STATUS_WORD = "status";
    public static final String COUNT_RIGHT_ANSWER = "count";
    // имя базы данных
    private static final String DATABASE_NAME = "dictionary.db";
    // версия базы данных
    private static final int DATABASE_VERSION = 1;
    // имя таблицы
    public static final String DATABASE_TABLE = "dictionary";
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + RUSSIAN_WORD_COLUMN
            + " text not null, " + ENGLISH_WORD_COLUMN + " text not null, " + STATUS_WORD
            + " integer not null, " + COUNT_RIGHT_ANSWER + " integer not null);";


    public DBDictionary(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBDictionary(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBDictionary(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        // Создаём новую таблицу
        onCreate(db);
    }
}
