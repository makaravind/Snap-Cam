package com.clarifai.android.starter.api.v2.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

import com.clarifai.android.starter.api.v2.Game;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class RecognizeConceptsAdapter extends RecyclerView.Adapter<RecognizeConceptsAdapter.Holder> {

    private final String LOG = "RecognizeConceptsAdapt";

  @NonNull private List<Concept> concepts = new ArrayList<>();
  private Game gamestate;

  public RecognizeConceptsAdapter setData(@NonNull List<Concept> concepts, Game gamestate) {
    this.concepts = concepts;
    this.gamestate = gamestate;
    notifyDataSetChanged();
    return this;
  }

  public void clearData(){
      if(concepts.size() >= 0)
          setData(Collections.<Concept>emptyList(), null);
  }

  @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concept_1, parent, false));
  }

  @Override public void onBindViewHolder(final Holder holder, final int position) {
    final Concept concept = concepts.get(position);
    final String concept_name = concept.name() != null ? concept.name() : concept.id();

    holder.label.setText(concept_name);
    holder.probability.setText(String.valueOf(concept.value()));

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
