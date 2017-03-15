package com.clarifai.android.starter.api.v2.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.clarifai.android.starter.api.v2.GameSingleton;
import com.clarifai.android.starter.api.v2.Score;
import com.google.gson.Gson;

import java.sql.Blob;
import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static final String LOGGER = "DB";

    private static final String DATABASE_NAME = "Snapcam.DB";

    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "Leaderboard";
    private static final String TABLE_NAME_GAME = "InGame";

    private static final String TABLE_ID = "id";
    private static final String TIMESTAMP_COL = "timestamp";
    private static final String SCORE_COL = "score";
    private static final String GAMENDED_COL = "isEnded";
    private static final String GAMESINGLETON_OBJECT_COL = "game_object";

//    private static final String MIN_TIME_COL = "minimum_time";
//    private static final String MAX_TIME_COL = "maximum_time";

    // queries
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
            TABLE_ID + " int " + "primary key, " +
            TIMESTAMP_COL + " text, " +
            SCORE_COL + " int, " +
            GAMENDED_COL + " INTEGER DEFAULT 0" +
            " )";

    private static final String CREATE_GAME_TABLE = "CREATE TABLE " + TABLE_NAME_GAME + " (" +
            TABLE_ID + " int primary key, " +
            GAMESINGLETON_OBJECT_COL + " BLOB )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_GAME_TABLE);

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GAME);

        onCreate(database);
    }

    public void insertScore(String timeStamp, int score, int isEnded) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIMESTAMP_COL, timeStamp);
        values.put(SCORE_COL, score);
        values.put(GAMENDED_COL, isEnded);
        sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    public void saveGameSingletonState() {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(GAMESINGLETON_OBJECT_COL, gson.toJson(GameSingleton.getInstance()).getBytes());
        sqLiteDatabase.insert(TABLE_NAME_GAME, null, values);
    }

    public ArrayList<Score> getGameInformation() {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
//        cursor.moveToPosition(3);

        ArrayList<Score> scores = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            int s = cursor.getInt(cursor.getColumnIndex(SCORE_COL));
            scores.add(new Score(s, cursor.getPosition()));
/*
            Log.i(LOGGER, cursor.getString(cursor.getColumnIndex(SCORE_COL)));
            Log.i(LOGGER, cursor.getString(cursor.getColumnIndex(GAMENDED_COL)));
            Log.i(LOGGER, cursor.getString(cursor.getColumnIndex(TIMESTAMP_COL)));
*/
            cursor.moveToNext();
        }
        return scores;
    }

    public void loadGameSingleton() {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_GAME , null);
        cursor.moveToFirst();

        byte[] blob = cursor.getBlob( cursor.getColumnIndex(GAMESINGLETON_OBJECT_COL) );
        String json = new String(blob);
        Gson gson = new Gson();
        GameSingleton gs = gson.fromJson(json, GameSingleton.class);
        GameSingleton.setInstance(gs);
    }

    public void clearAllInformation() {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, null, null);

    }
}