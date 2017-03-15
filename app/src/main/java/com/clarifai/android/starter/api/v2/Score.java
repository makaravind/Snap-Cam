package com.clarifai.android.starter.api.v2;


public class Score {

    private int score;
    private int game_no;


    public Score(int score, int game_no) {
        this.score = score;
        this.game_no = game_no;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGame_no() {
        return game_no;
    }

    public void setGame_no(int game_no) {
        this.game_no = game_no;
    }
}
