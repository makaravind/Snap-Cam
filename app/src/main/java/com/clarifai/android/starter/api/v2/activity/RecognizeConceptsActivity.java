package com.clarifai.android.starter.api.v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;

import com.clarifai.android.starter.api.v2.Game;
import com.clarifai.android.starter.api.v2.Util;
import com.clarifai.android.starter.api.v2.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class RecognizeConceptsActivity extends HomeBaseActivity {

    public static final int PICK_IMAGE = 100;
    public static final int SNAPPED_IMAGE = 101;
    private static final int SNAP_CAM_INTENT_KEY = 1000;


    // displays game word
    @BindView(R.id.game_word_TextView)
    TextView game_word;

    // the FAB that the user clicks to select an image
    @BindView(R.id.fab)
    View fab;

    @BindView(R.id.camera_btn)
    Button camera_btn;

    // timer details
    private int MAXTIME = Util.getMinToMilli(2);
    private int max_time = MAXTIME;
    private Handler handler;
    private Runnable r;

    @NonNull
    private Game gamestate = new Game();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // setting the camera_btn invisble
        camera_btn.setVisibility(GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int current_state = gamestate.getState();
        Toast.makeText(this, "resumed " + current_state, Toast.LENGTH_LONG).show();
        switch (current_state){
            case 1:
                // game just started
                clearGame();
                break;
            case 2:
                // camera back button clicked in between
                camera_btn.setVisibility(VISIBLE);
                handler.postDelayed(r, 1000);
                break;
            default:
                Toast.makeText(this, "current state not handled " + current_state, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // timer stops
        handler.removeCallbacks(r);
    }

    @OnClick(R.id.fab)
    void playGame() {
//    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), PICK_IMAGE);
//        clearGame();
        String word = Util.generateRandomWord();
        gamestate.setGeneratedWord(word);
        showGameWord(word);

        // timer starts
        Toast.makeText(this, "timer started", Toast.LENGTH_LONG).show();
        handler.postDelayed(r, 1000);

        // click take picture btn
        camera_btn.setVisibility(VISIBLE);
        Toast.makeText(this, "fab disabled", Toast.LENGTH_SHORT).show();
        fab.setClickable(false);
    }


    @OnClick(R.id.camera_btn)
    void pickImage() {
        gamestate.updateState(2);
//        Intent start_camerat_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(start_camerat_intent, SNAPPED_IMAGE);
        Intent to_snap_cam_intent = new Intent(this, SnapCam.class);
        to_snap_cam_intent.putExtra("GAMESTATE", gamestate);
        startActivityForResult(to_snap_cam_intent, SNAP_CAM_INTENT_KEY);

    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode) {
            case SNAP_CAM_INTENT_KEY:
                Toast.makeText(this, "working", Toast.LENGTH_LONG).show();
                Log.i("RCA", "back agian");
                gamestate = (Game) data.getExtras().getSerializable("GAMESTATE");
                displayResults();
                break;
        }
    }

    // utility functions
    private void displayResults() {
        // displaying results after choosing the option
        clearGame();
        // update UI for score
        game_word.setText("chained " + gamestate.getWordsChain().size() + " score " + gamestate.getScore());
        for(String item: gamestate.getWordsChain()){
            Toast.makeText(RecognizeConceptsActivity.this, item, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearGame(){
        fab.setClickable(true);
        gamestate.updateState(1);
        timerInit();
    }

    private void timerInit(){
        handler = new Handler();
        max_time = MAXTIME;
        r =  new Runnable() {
            @Override
            public void run() {
                Log.i("timer", String.valueOf(max_time));
                camera_btn.setText(String.valueOf(max_time/1000));
                max_time -= 1000;
                if(max_time>0) {
                    handler.postDelayed(r, 1000);
                }else if (max_time == 0){
                    camera_btn.setText("time over");
                    fab.setClickable(false);
                }
            }
        };
    }

    private void showGameWord(String game_word){
        this.game_word.setText(game_word);
    }

  @Override protected int layoutRes() { return R.layout.activity_recognize; }

}
