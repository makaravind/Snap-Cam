package com.clarifai.android.starter.api.v2.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.clarifai.android.starter.api.v2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlayedWordsAdapter extends RecyclerView.Adapter<PlayedWordsAdapter.Holder> {

    private final String LOG = "RecognizeConceptsAdapt";

    @NonNull private List<String> concepts = new ArrayList<>();

    public PlayedWordsAdapter setData(@NonNull List<String> concepts) {
        this.concepts = concepts;
        notifyDataSetChanged();
        return this;
    }

    public void clearData(){
        if(concepts.size() >= 0)
            setData(Collections.<String>emptyList());
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.played_words_list_item, parent, false));
    }

    @Override public void onBindViewHolder(final Holder holder, final int position) {
        final String concept = concepts.get(position);
        holder.word.setText(concept);
    }

    @Override public int getItemCount() {
        return concepts.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.chained_word) TextView word;

        public Holder(View root) {
            super(root);
            ButterKnife.bind(this, root);
        }
    }
}
