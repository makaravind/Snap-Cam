package com.clarifai.android.starter.api.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.Game;
import com.clarifai.android.starter.api.v2.R;

public class InGame extends AppCompatActivity {

    private final String LOGGER = "INGAME";
    private final int INTENT_TO_SNAP_CAM = 1000;
    private Game gamestate;

    private TextView timer;
    private TextView game_word;
    private ImageView game_image;
    private ImageButton play;

    // timer details
    private Handler handler;
    private Runnable r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{

            gamestate = (Game) getIntent().getExtras().getSerializable(String.valueOf(R.string.game_object_intent));
            Log.i("INGAME", gamestate.getGenerated_word());

        }catch (NullPointerException e){
            Log.i(LOGGER, e.toString());
            intentToReturnHome(Activity.RESULT_CANCELED, new Intent(InGame.this, Home.class));
        }

        bindpropertiesToLayout();

        InitInGame();
        InitTimer();
    }

    private void bindpropertiesToLayout(){

        timer = (TextView) findViewById(R.id.textView_time_countdown);
        game_word = (TextView) findViewById(R.id.textView_game_word);
        game_image = (ImageView) findViewById(R.id.imageView_game_image);
        play = (ImageButton) findViewById(R.id.imageButton_play);
        play.setOnClickListener(OnPlayClicked());
    }

    private void InitInGame(){

        game_word.setText(gamestate.getGenerated_word());
        timer.setText(String.valueOf(gamestate.getCurrent_remaining_time()));
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent i = new Intent(InGame.this, Home.class);
        i.putExtra(String.valueOf(R.string.game_object_intent), gamestate);
        intentToReturnHome(Activity.RESULT_OK, i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // start the timer
        Log.i(LOGGER, "onResume, start timer");
        startTimer();
    }

    private void InitTimer(){

        handler = new Handler();
        r =  new Runnable() {
            @Override
            public void run() {
                gamestate.decrementCurrent_remaining_time(1000);

                Log.i("timer", String.valueOf(gamestate.getCurrent_remaining_time()));
                timer.setText(String.valueOf(gamestate.getCurrent_remaining_time()));

                if(gamestate.getCurrent_remaining_time()>0) {
                    handler.postDelayed(r, 1000);
                }else if (gamestate.getCurrent_remaining_time() == 0){
                    Log.i("INGAME", "time over, Game over");
                }
            }
        };
    }

    private View.OnClickListener OnPlayClicked(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gamestate.updateState(2);
                Intent to_snap_cam_intent = new Intent(InGame.this, SnapCam.class);
                to_snap_cam_intent.putExtra(String.valueOf(R.string.game_object_intent), gamestate);
                startActivityForResult(to_snap_cam_intent, INTENT_TO_SNAP_CAM);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_CANCELED){
            Toast.makeText(InGame.this, "no data sent, game is not build properly", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case INTENT_TO_SNAP_CAM:
                gamestate.updateState(10);
                Intent i = new Intent(InGame.this, Home.class);
                intentToReturnHome(Activity.RESULT_OK, i);
                break;
        }
    }

    private void startTimer(){
        handler.postDelayed(r, 1000);
    }

    private void stopTimer() { handler.removeCallbacks(r);}

    private void intentToReturnHome(int result, Intent intent){
        intent.putExtra(String.valueOf(R.string.game_object_intent), gamestate);
        setResult(result, intent);
        finish();
    }
}
