package com.clarifai.android.starter.api.v2.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.Util;
import com.clarifai.android.starter.api.v2.adapter.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 *  Uses Clarifi AI API to process the image to classify the image
 * */
public class ImageClassifier extends AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>{

    public AsyncResponse delegate = null;

    private Context context;
    private final byte[] imageData;

    public ImageClassifier(Context context, byte[] imageData){
        this.context = context;
        this.imageData = imageData;
    }

    private static ArrayList<Concept> filterPredictions(List<Concept> predictions, int noOfPredictions){

        final int size = noOfPredictions;
        int count = 0;
        ArrayList<Concept> filteredPredictions = new ArrayList<>(size);

        // shuffling the predictions before taking top n elements
        Collections.shuffle(predictions);

        // taking only valid top n predicitons
        for(Concept item: predictions){

            if(count == size){
                break;
            }

            if( Util.isValid(item.name()) ){
                filteredPredictions.add(item);
                count++;
            }
        }
        return filteredPredictions;
    }

    @Override
    protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
        // The default Clarifai model that identifies concepts in images
        final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

        // Use this model to predict, with the image that the user just selected as the input
        return generalModel.predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageData)))
                .executeSync();
    }

    @Override
    protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
        if (!response.isSuccessful()) {
            Toast.makeText(context, "error while contacting api", Toast.LENGTH_SHORT).show();
            return;
        }
        List<ClarifaiOutput<Concept>> predictions = response.get();
        //filter the predictions with top 5 items only

        if (predictions.isEmpty()) {
            Toast.makeText(context, "no results from API", Toast.LENGTH_SHORT).show();
            return;
        }

        delegate.processImagePrediction(filterPredictions(predictions.get(0).data(), 3));
    }

    public interface AsyncResponse{
        void processImagePrediction(List<Concept> results);
    }
}
