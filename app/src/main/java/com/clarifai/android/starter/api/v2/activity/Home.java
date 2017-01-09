package com.clarifai.android.starter.api.v2.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.Game;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.Util;

public final class Home extends AppCompatActivity{

    private final int INTENT_INGAME = 1010;
    private final int INTENT_GAME_STATISTICS = 1011;
    private final int MAX_TIME = 10;

    private Button new_game;
    private View leaderboard;
    private TextView status_text;
    private ProgressBar timerBar;
    private ImageButton statButton;
    private View game_status_countainer;

    private Game gamestate;

    // timer details
    private Handler handler;
    private Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindPropertiesToLayout();

        InitGame();
    }

    private void bindPropertiesToLayout(){

        new_game = (Button) findViewById(R.id.button_new_game);
        new_game.setOnClickListener(OnStartButtonClicked());

        leaderboard = findViewById(R.id.view_leaderboard);

        statButton = (ImageButton) findViewById(R.id.imageButton_stat);
        statButton.setOnClickListener(onShowStatClicked());

        status_text = (TextView) findViewById(R.id.textView_game_status);

        timerBar = (ProgressBar) findViewById(R.id.progressBar_timer);
        timerBar.setMax(Util.getMinToMilli(MAX_TIME));
        timerBar.setProgress(Util.getMinToMilli(MAX_TIME));

        game_status_countainer = findViewById(R.id.layout_player_game);
        game_status_countainer.setOnClickListener(OnShowGameWordClick());

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    private void InitGame(){

        // start the game, should get from persistent
        gamestate = new Game();
        game_status_countainer.setVisibility(View.INVISIBLE);
    }

    private View.OnClickListener onShowStatClicked(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("HOME", "stat");
                Intent intent = new Intent(Home.this, GameStatistics.class);
                intent.putExtra(String.valueOf(R.string.game_object_intent), gamestate);
                startActivityForResult(intent, INTENT_GAME_STATISTICS);
            }
        };

    }

    private View.OnClickListener OnShowGameWordClick(){

        Log.i("HOME", "layout clicked");
        new_game.setBackgroundColor(android.graphics.Color.YELLOW);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("HOME", "layout");
                // navigate to InGame activity
                Intent intent = new Intent(Home.this, InGame.class);
                intent.putExtra(String.valueOf(R.string.game_object_intent), gamestate);
                startActivityForResult(intent, INTENT_INGAME);
            }
        };
    }

    private View.OnClickListener OnStartButtonClicked(){
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // check game state and alert whether to continue or not
                // it gives a new word
                if("".equalsIgnoreCase(gamestate.getGenerated_word())){
                    // generate a word
                    String word = Util.generateRandomWord();
                    gamestate.setGeneratedWord(word);
                    updateStatusLayoutInfo("start");

                }else{
                    // alert
                    showAlert("sure?", "you can continue with old game");
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_CANCELED){
            Toast.makeText(Home.this, "no data sent, game is not build properly", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case INTENT_INGAME:
                gamestate = (Game) data.getExtras().getSerializable(String.valueOf(R.string.game_object_intent));
                game_status_countainer.setVisibility(View.VISIBLE);

                if(gamestate.getState() == 10){
                    updateStatusLayoutInfo("wordDone");

                    // new word again
                    gamestate.updateState(1);
                }else{
                    InitTimer();
                    startTimer();
                }
                break;
        }
    }

    private void showAlert(String title, String message){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // store current game
                        // start a new game
                        Log.i("HOME", "yes click");
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("HOME", "no click");
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void InitTimer(){

        handler = new Handler();
        r =  new Runnable() {
            @Override
            public void run() {
                gamestate.decrementCurrent_remaining_time(1000);
                Log.i("timer", String.valueOf(gamestate.getCurrent_remaining_time()));

                timerBar.setProgress(gamestate.getCurrent_remaining_time());

                if(gamestate.getCurrent_remaining_time()>0) {
                    handler.postDelayed(r, 1000);
                }else if (gamestate.getCurrent_remaining_time() == 0){
                    Log.i("INGAME", "HOME, time over, Game over");
                }
            }
        };
    }

    private void startTimer(){
        if(handler!=null && r != null)
            handler.postDelayed(r, 1000);
    }

    private void stopTimer() {
        if(handler!=null && r != null)
            handler.removeCallbacks(r);
    }

    private void updateStatusLayoutInfo(String currentStatus){

        if ("start".equalsIgnoreCase(currentStatus)){
            game_status_countainer.setVisibility(View.VISIBLE);
            status_text.setText("click to see the word");
        }
        else if("wordDone".equalsIgnoreCase(currentStatus)) {
            status_text.setText("continue");
            timerBar.setAlpha(0.5f);
        }
    }
}
