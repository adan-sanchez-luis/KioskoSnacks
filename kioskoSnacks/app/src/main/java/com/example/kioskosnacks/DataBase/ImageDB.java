package com.example.kioskosnacks.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ImageDB extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_Nombre = "ImageDB";
    private static final String TB_Nombre = "ImageTable";

    public ImageDB(@Nullable Context context) {
        super(context, DB_Nombre, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TB_Nombre +
                "(id varchar(50)," +
                "path text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TB_Nombre);
        onCreate(sqLiteDatabase);
    }
}
