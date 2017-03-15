package com.clarifai.android.starter.api.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.DataBase.DBHelper;
import com.clarifai.android.starter.api.v2.GameSingleton;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.Timer;

public class InGame extends AppCompatActivity {

    private final String LOGGER = "INGAME";
    private final int INTENT_TO_SNAP_CAM = 1000;

    private TextView countDown;
    private TextView game_word;
    private ImageView game_image;
    private ImageButton play;

    // countDown details
    private Timer timer;
    private Runnable r;

    // database
    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        bindpropertiesToLayout();
        InitInGame();
        implementTimer();
        timer.startTimer();
    }

    private void bindpropertiesToLayout(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = new DBHelper(InGame.this);

        countDown = (TextView) findViewById(R.id.textView_time_countdown);
        game_word = (TextView) findViewById(R.id.textView_game_word);
        game_image = (ImageView) findViewById(R.id.imageView_game_image);
        play = (ImageButton) findViewById(R.id.imageButton_play);
        play.setOnClickListener(OnPlayClicked());
        timer = new Timer();
    }

    private void InitInGame(){

        game_word.setText( GameSingleton.getInstance().getGenerated_word() );
        countDown.setText( String.valueOf(GameSingleton.getInstance().getCurrentRemainingTime()) );

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(InGame.this, Home.class);
        intentToReturn(Activity.RESULT_OK, i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.saveGameSingletonState();
        timer.stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // start the countDown
        Log.i(LOGGER, "onResume, start countDown");
        timer.startTimer();

    }

    private void implementTimer(){


        r =  new Runnable() {
            @Override
            public void run() {
                GameSingleton.getInstance().decrementCurrent_remaining_time(1000);

                Log.i("countDown", String.valueOf(GameSingleton.getInstance().getCurrentRemainingTime()));
                countDown.setText(String.valueOf(GameSingleton.getInstance().getCurrentRemainingTime()));

                if(GameSingleton.getInstance().getCurrentRemainingTime()>0) {
                    timer.startTimer();
                }else if (GameSingleton.getInstance().getCurrentRemainingTime() == 0){
                    endGame();
                }
            }
        };

        timer.initTimer(r);
    }

    private void endGame() {

        Log.i("INGAME", "time over, Game over");
        GameSingleton.getInstance().updateState( GameSingleton.getStateTimeUp() );
        Intent i = new Intent(InGame.this, Home.class);
        intentToReturn(Activity.RESULT_OK, i);

    }

    private View.OnClickListener OnPlayClicked(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_snap_cam_intent = new Intent(InGame.this, SnapCam.class);
                startActivityForResult(to_snap_cam_intent, INTENT_TO_SNAP_CAM);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_CANCELED){

            // continue the timer if returned back with no results
//            timer.startTimer();
            return;
        }

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case INTENT_TO_SNAP_CAM:
                GameSingleton.getInstance().updateState( GameSingleton.getStateWordSelected() );
                Intent i = new Intent(InGame.this, Home.class);
                intentToReturn(Activity.RESULT_OK, i);
                break;

        }
    }

    private void intentToReturn(int result, Intent intent){
        setResult(result, intent);
        finish();
    }
}
