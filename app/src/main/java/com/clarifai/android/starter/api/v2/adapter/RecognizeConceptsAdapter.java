package com.clarifai.android.starter.api.v2.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

import com.clarifai.android.starter.api.v2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class RecognizeConceptsAdapter extends RecyclerView.Adapter<RecognizeConceptsAdapter.Holder> {

    private final String LOG = "RecognizeConceptsAdapt";

  @NonNull private List<String> concepts = new ArrayList<>();

  public RecognizeConceptsAdapter setData(@NonNull List<String> concepts) {
    this.concepts = concepts;
    notifyDataSetChanged();
    return this;
  }

  public void clearData(){
      if(concepts.size() >= 0){
          setData(new ArrayList<String>());
      }
  }

  @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concept_1, parent, false));
  }

  @Override public void onBindViewHolder(final Holder holder, final int position) {
    final String concept = concepts.get(position);

    holder.label.setText(concept);
//    holder.probability.setText(String.valueOf(concept.value()));
    holder.probability.setText("");

    Random rnd = new Random();
    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    holder.layout.setBackgroundColor(color);
  }

  @Override public int getItemCount() {
    return concepts.size();
  }

  static final class Holder extends RecyclerView.ViewHolder {

    @BindView(R.id.label_1) TextView label;
    @BindView(R.id.probability_1) TextView probability;
    @BindView(R.id.item_layout)
    RelativeLayout layout;

    public Holder(View root) {
      super(root);
      ButterKnife.bind(this, root);
    }
  }
}
