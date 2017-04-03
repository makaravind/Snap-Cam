package com.clarifai.android.starter.api.v2.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.PlayedWordsAdapter;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;

import java.util.ArrayList;

public class PredictedWordsListFragment extends Fragment implements View.OnClickListener {

    public static String PREDICTEDWORDS_ARGS_KEY = "PredictedWords";
    private Context context;
    Activity activity;

    private RecyclerView preditedWordsRecyclerList;
    private RecognizeConceptsAdapter adapter;

    private Button captureAgainButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_predicted_words_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        int s = getPredictedWords().size();
        Toast.makeText(getActivity(), " " + s, Toast.LENGTH_SHORT).show();

        captureAgainButton = (Button) view.findViewById(R.id.capture_again_button);
        captureAgainButton.setOnClickListener(this);

        // for displaying the results from API in the recycler view
        onCreateResultsList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        this.context = context;
    }

    private void onCreateResultsList() {

        adapter = new RecognizeConceptsAdapter();
        preditedWordsRecyclerList = (RecyclerView) getActivity().findViewById(R.id.resultsList);
        preditedWordsRecyclerList.setLayoutManager(new GridLayoutManager(context, 1, 1, false));
        preditedWordsRecyclerList.setAdapter(adapter);
        preditedWordsRecyclerList.addOnItemTouchListener(onResultsListItemClicked());

        // populating the data
        adapter.setData(getPredictedWords());
    }

    private mRecyclerClickListener onResultsListItemClicked() {

        return new mRecyclerClickListener(context, new mRecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // send the selected item to activity for further process...

                try{
                    ((onClickListener)activity).getSelectedItem(getPredictedWords().get(position));
                }catch (ClassCastException ignored){}
            }
        });

    }

    @Override
    public void onClick(View v) {
        if( v == captureAgainButton ){
            ((onClickListener)activity).snapAgain();
        }
    }

    private ArrayList<String> getPredictedWords() {

        ArrayList<String> words;
        try {
            words = getArguments().getStringArrayList(PREDICTEDWORDS_ARGS_KEY);
        } catch (NullPointerException e) {
            return new ArrayList<>(0);
        }
        return words;
    }

    public static Fragment newInstance() {
        return new PredictedWordsListFragment();
    }

    public interface onClickListener {
        void getSelectedItem(String selectedGameWord);
        void snapAgain();
    }

}
