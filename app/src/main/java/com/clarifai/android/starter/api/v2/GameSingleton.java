package com.clarifai.android.starter.api.v2;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.clarifai.android.starter.api.v2.DataBase.DBHelper;
import com.clarifai.android.starter.api.v2.activity.Home;

import java.io.Serializable;
import java.util.ArrayList;

    /*
    state : 1-> game just started
            2-> camera back button clicked in between
            10-> word is selected from list
            100 -> game ended
     */

public class GameSingleton implements Serializable {

    private static GameSingleton mInstance = null;

    private final String LOG = "GAME CLASS";
    private final int MAX_TIME = 1;

    private final static Integer STATE_GAME_STARTED = 1;
    private final static Integer STATE_IN_GAME = 99;
    private final static Integer STATE_CAMERA_BACK = 2;
    private final static Integer STATE_WORD_SELECTED = 10;
    private final static Integer STATE_GAME_ENDED = 100;
    private final static Integer STATE_TIME_UP = 11;

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
        state = STATE_GAME_ENDED;

    }

    public static GameSingleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new GameSingleton();
        }
        return mInstance;
    }

    public static void setInstance(GameSingleton gameSingleton){

        if(gameSingleton != null)
            mInstance = gameSingleton;
    }

    public void setNewGame(){

        mInstance = new GameSingleton();
        GameSingleton.getInstance().updateState(STATE_GAME_STARTED);
        setNewGameWord();
    }

    public void setNewGameWord(){

        String word = Util.generateRandomWord();
        GameSingleton.getInstance().setGeneratedWord(word);
        GameSingleton.getInstance().setCurrent_remaining_time( Util.getMinToMilli(MAX_TIME) );
    }

    public void generateWord(){
        String word = Util.generateRandomWord();
        GameSingleton.getInstance().setGeneratedWord(word);
    }

    public void setGeneratedWord(String generated_word){
        this.generated_word = generated_word;
        wordsChain.add(generated_word);
    }

    public boolean checkIfValid(String picked_word){
        // generated word end letter == picked word first letter
//       return generated_word.charAt(generated_word.length()-1) == picked_word.charAt(0);

        // generated word first letter == picked word first letter
        return generated_word.charAt(0) == picked_word.charAt(0);
//        return true;
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

    public int getCurrentRemainingTime() {
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

    public static Integer getStateInGame() {
        return STATE_IN_GAME;
    }

    public static Integer getStateGameStarted() {
        return STATE_GAME_STARTED;
    }

    public static Integer getStateCameraBack() {
        return STATE_CAMERA_BACK;
    }

    public static Integer getStateWordSelected() {
        return STATE_WORD_SELECTED;
    }

    public static Integer getStateGameEnded() {
        return STATE_GAME_ENDED;
    }

    public static Integer getStateTimeUp() {
        return STATE_TIME_UP;
    }

    public void updateState(int new_state){
        Log.i("STATE", String.valueOf(state));
        state = new_state;
    }

}
