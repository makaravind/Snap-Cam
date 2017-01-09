package com.clarifai.android.starter.api.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.clarifai.android.starter.api.v2.Game;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.PlayedWordsAdapter;

public class GameStatistics extends AppCompatActivity {

    private final String LOGGER = "GAMESTATISTICS";
    private Game gamestate;

    private TextView currentWord;
    private TextView currentScore;
    private RecyclerView chainedWordsList;
    private PlayedWordsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statistics);

        getIntentObject();
        bindPropertiesToLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();

        showStats();
    }

    void PopulatePlayedWordsList(){
        adapter.setData(gamestate.getWordsChain());
    }

    private void bindPropertiesToLayout(){

        currentWord = (TextView) findViewById(R.id.textView_current_word);
        currentScore = (TextView) findViewById(R.id.textView_current_score);

        adapter = new PlayedWordsAdapter();
        chainedWordsList = (RecyclerView) findViewById(R.id.chained_words_list);
        chainedWordsList.setLayoutManager(new LinearLayoutManager(this));
        chainedWordsList.setAdapter(adapter);
        //        resultsList.addOnItemTouchListener(onResultsListItemClicked());
    }

    public void showStats(){

        showCurrentValues();
        PopulatePlayedWordsList();
    }

    private void showCurrentValues(){

        if(gamestate.getState() != 0){
            currentWord.setText(gamestate.getGenerated_word());
            currentScore.setText(String.valueOf(gamestate.getScore()));
        }else{
            currentWord.setText("-");
            currentScore.setText("-");
        }
    }

    private void getIntentObject(){

        try{

            gamestate = (Game) getIntent().getExtras().getSerializable(String.valueOf(R.string.game_object_intent));

        }catch (NullPointerException e){
            Log.i(LOGGER, e.toString());
            intentToReturnHome(Activity.RESULT_CANCELED, new Intent(GameStatistics.this, Home.class));
        }
    }

    private void intentToReturnHome(int result, Intent intent){
        intent.putExtra(String.valueOf(R.string.game_object_intent), gamestate);
        setResult(result, intent);
        finish();
    }
}
