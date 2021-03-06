package com.clarifai.android.starter.api.v2.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.clarifai.android.starter.api.v2.GameSingleton;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.PlayedWordsAdapter;

public class GameStatistics extends AppCompatActivity {

    private final String LOGGER = "GAMESTATISTICS";

    private TextView currentWord;
    private TextView currentScore;
    private RecyclerView chainedWordsList;
    private PlayedWordsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_statistics);

        bindPropertiesToLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();

        showStats();
    }

    void PopulatePlayedWordsList(){
        adapter.setData(GameSingleton.getInstance().getWordsChain());
    }

    private void bindPropertiesToLayout(){

        // ading app bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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

        if(GameSingleton.getInstance().getState() != 0){
            currentWord.setText(GameSingleton.getInstance().getGenerated_word());
            currentScore.setText(String.valueOf(GameSingleton.getInstance().getScore()));
        }else{
            currentWord.setText("-");
            currentScore.setText("-");
        }
    }


    private void intentToReturnHome(int result, Intent intent){
//        intent.putExtra(String.valueOf(R.string.game_object_intent), GameSingleton.getInstance());
        setResult(result, intent);
        finish();
    }
}
