/* source : http://www.android-graphview.org */

package com.clarifai.android.starter.api.v2.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.clarifai.android.starter.api.v2.DataBase.DBHelper;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.Score;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;


public class LeaderBoard extends AppCompatActivity {

    private final String LOGGER = "LBoard";

    private TextView scoreOnTap;

    private GraphView graph;
    private final int MAX_DATA_POINTS = 50;
    private LineGraphSeries<DataPoint> scoresSeries;
    private ArrayList<Score> scoresDB;

    // DB
    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        bindPropertiesToLayout();
        populateScoresFromDB();
    }

    private void bindPropertiesToLayout() {
        scoreOnTap = (TextView) findViewById(R.id.score_textView);
        scoresDB = new ArrayList<>();

        graph = (GraphView) findViewById(R.id.graph);
        scoresSeries = new LineGraphSeries<>();
        scoresSeries.setOnDataPointTapListener(onDataPointTapped());
        graph.addSeries(scoresSeries);

        database = new DBHelper(this);
    }

    private void populateScoresFromDB() {

        scoresDB = database.getGameInformation();

        for (Score score : scoresDB) {
            int s = score.getScore();
            int g_no = score.getGame_no();

            scoresSeries.appendData(new DataPoint(g_no, s), true, MAX_DATA_POINTS);
        }
    }

    private OnDataPointTapListener onDataPointTapped(){
        return new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                scoreOnTap.setText(String.valueOf(dataPoint.getY()));
            }
        };
    }
}