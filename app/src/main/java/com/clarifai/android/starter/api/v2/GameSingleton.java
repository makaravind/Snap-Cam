package com.clarifai.android.starter.api.v2;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AMAR on 1/9/2017.
 */

public class GameSingleton implements Serializable {

    private static GameSingleton mInstance = null;

    private final String LOG = "GAME CLASS";
    private final int MAX_TIME = 10;


    private String generated_word;
    private ArrayList<String> wordsChain = new ArrayList<>();
    private ArrayList<Integer> times;
    private int state;
    private int score; // updates after every right word
    private int current_remaining_time;


    private GameSingleton(){
        score = 0;
        generated_word = "";
        current_remaining_time = Util.getMinToMilli(MAX_TIME);
        times = new ArrayList<>();

        // game just started
        state = 1;
    }

    public static GameSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new GameSingleton();
        }
        return mInstance;
    }

    public void setGeneratedWord(String generated_word){
        this.generated_word = generated_word;
        wordsChain.add(generated_word);
    }

    public boolean checkIfValid(String picked_word){
//       return generated_word.charAt(generated_word.length()-1) == picked_word.charAt(0);
        return true;
    }

    public void updateChaining(boolean current_status, String word_played){
        wordsChain.add(word_played);
        if (current_status)
            score+=1;

        Log.i(LOG, word_played + " " +String.valueOf(current_status) + " " + String.valueOf(times));
    }

    public void setTimes(int t){
        times.add(t);
    }

    public ArrayList<String> getWordsChain(){
        return wordsChain;
    }

    public int getScore(){ return score; }

    public int getState(){ return state; }

    public int getTimes() { return times.get(times.size()-1); }

    public void setCurrent_remaining_time(int current_remaining_time) {
        this.current_remaining_time = current_remaining_time;
    }

    public int getCurrent_remaining_time() {
        return current_remaining_time;
    }

    public void decrementCurrent_remaining_time(int by){
        current_remaining_time -= by;
    }

    public String getGenerated_word() {
        return generated_word;
    }

    public int getMAX_TIME() {
        return MAX_TIME;
    }

    public void updateState(int new_state){
        Log.i("STATE", String.valueOf(state));
        state = new_state;
    }

}
