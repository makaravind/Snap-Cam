package com.clarifai.android.starter.api.v2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.DataBase.DBHelper;
import com.clarifai.android.starter.api.v2.GameSingleton;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.Timer;
import com.clarifai.android.starter.api.v2.Util;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

public final class Home extends AppCompatActivity {

    private final int INTENT_INGAME = 1010;
    private final int INTENT_GAME_STATISTICS = 1011;
    private final int INTENT_LEADERBOARD = 1012;

    private final int MAX_TIME = 10;

    private Button new_game;
    private View leaderboard;
    private TextView status_text;
    private ProgressBar timerBar;
    private ImageButton statButton;
    private View game_status_countainer;

    // timer details
    private Timer timer;
    private Runnable r;

    // DB details
    DBHelper database;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindPropertiesToLayout();

        database.loadGameSingleton();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer.startTimer();
        /*SharedPreferences previewSizePref = getSharedPreferences("PREF",MODE_PRIVATE);
        if (previewSizePref.contains("timer")) {
            //your saved data exists
            Toast.makeText(this, "started " + previewSizePref.getInt("timer", 0), Toast.LENGTH_SHORT).show();
            GameSingleton.getInstance().setCurrent_remaining_time(previewSizePref.getInt("timer", 0));
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*SharedPreferences sharedPref = Home.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("timer", GameSingleton.getInstance().getCurrentRemainingTime());
        editor.apply();*/
        database.saveGameSingletonState();
        timer.stopTimer();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void bindPropertiesToLayout() {

        // ading app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        new_game = (Button) findViewById(R.id.button_new_game);
        new_game.setOnClickListener(OnNewGameButtonClicked());
        new_game.setBackgroundColor(Color.YELLOW);

        statButton = (ImageButton) findViewById(R.id.imageButton_stat);
        statButton.setOnClickListener(onShowStatisticsClicked());

        status_text = (TextView) findViewById(R.id.textView_game_status);

        timerBar = (ProgressBar) findViewById(R.id.progressBar_timer);
        timerBar.setMax(Util.getMinToMilli(GameSingleton.getInstance().getMAX_TIME()));
        timer = new Timer();

        game_status_countainer = findViewById(R.id.layout_player_game);
        game_status_countainer.setOnClickListener(OnShowGameWordClick());
        game_status_countainer.setVisibility(View.INVISIBLE);

        database = new DBHelper(Home.this);
    }

    private void InitGame() {

        // start the game, should get from persistent
        GameSingleton.getInstance().updateState( GameSingleton.getStateGameStarted() );
        GameSingleton.getInstance().setNewGame();

        timerBar.setProgress(Util.getMinToMilli(GameSingleton.getInstance().getMAX_TIME()));

        // update status text
        status_text.setText("click to see the word");

    }

    private void continueGame(){

        GameSingleton.getInstance().setNewGameWord();
        GameSingleton.getInstance().updateState( GameSingleton.getStateGameStarted() );
        timerBar.setProgress(Util.getMinToMilli(GameSingleton.getInstance().getMAX_TIME()));

    }

    private View.OnClickListener onShowStatisticsClicked() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("HOME", "start");
                Intent intent = new Intent(Home.this, GameStatistics.class);
                intent.putExtra(String.valueOf(R.string.game_object_intent), GameSingleton.getInstance());
                startActivityForResult(intent, INTENT_GAME_STATISTICS);
            }
        };

    }

    private View.OnClickListener OnShowGameWordClick() {

        Log.i("HOME", "layout clicked");
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("HOME", "layout");

                // navigate to InGame activity
                if( GameSingleton.getInstance().getState() == GameSingleton.getStateWordSelected() )
                    continueGame();

                GameSingleton.getInstance().updateState( GameSingleton.getStateInGame() );
                Intent intent = new Intent(Home.this, InGame.class);
                startActivityForResult(intent, INTENT_INGAME);
            }
        };
    }

    private View.OnClickListener OnNewGameButtonClicked() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // check game state and alert whether to continue or not
                // it gives a new word
                // if game is ended then start a new game
                if ( GameSingleton.getInstance().getState() == GameSingleton.getStateGameEnded() ){

                    InitGame();
                    updateStatusLayoutInfo("start");

                } else {
                    // alert
                    showAlert("sure?", "you can continue with old game");
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.leaderboard_menu:
                Intent i = new Intent(Home.this, LeaderBoard.class);
                startActivityForResult(i, INTENT_LEADERBOARD);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_CANCELED) {

//            timer.startTimer();
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case INTENT_INGAME:

                if ( GameSingleton.getInstance().getState() == GameSingleton.getStateWordSelected() ) {
                    updateStatusLayoutInfo("wordDone");

                    // new word again
//                    continueGame();

                } else if ( GameSingleton.getInstance().getState() == GameSingleton.getStateTimeUp() ) {

                    Log.i("HOME", "end the game; Should save the game details for leaderboard");
                    showAlert("Game Over","Time's up ! Do you want to play again ?");

                } else {

                    implementTimer();
                    timer.startTimer();

                }
                break;
        }
    }

    private void endGame() {

        Toast.makeText(this, "time is up!", Toast.LENGTH_SHORT).show();
        // save the details
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        database.insertScore(currentDateTimeString, GameSingleton.getInstance().getScore(), GameSingleton.getStateGameEnded());

        GameSingleton.getInstance().updateState( GameSingleton.getStateGameEnded() );
        GameSingleton.getInstance().setCurrent_remaining_time(0);
        game_status_countainer.setVisibility( View.INVISIBLE );
    }

    private void showAlert(String title, String message) {

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
                        InitGame();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("HOME", "no click");
                        endGame();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void implementTimer() {

        r = new Runnable() {
            @Override
            public void run() {
                GameSingleton.getInstance().decrementCurrent_remaining_time(1000);
                Log.i("timer", String.valueOf(GameSingleton.getInstance().getCurrentRemainingTime()));

                timerBar.setProgress(GameSingleton.getInstance().getCurrentRemainingTime());

                if (GameSingleton.getInstance().getCurrentRemainingTime() > 0) {
                    timer.startTimer();
                } else if (GameSingleton.getInstance().getCurrentRemainingTime() == 0) {
                    Log.i("HOME", "HOME, time over, Game over");
                    endGame();
                }
            }
        };

        timer.initTimer(r);
    }

    private void updateStatusLayoutInfo(String currentStatus) {

        if ("start".equalsIgnoreCase(currentStatus)) {
            game_status_countainer.setVisibility(View.VISIBLE);
            status_text.setText("click to see the word");
        } else if ("wordDone".equalsIgnoreCase(currentStatus)) {
            status_text.setText("continue");
            timerBar.setAlpha(0.5f);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Home Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
