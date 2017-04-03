package com.clarifai.android.starter.api.v2.activity;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.clarifai.android.starter.api.v2.R;

public class LoadingScreenFragment extends Fragment {

    public static final String IMAGEDATA_ARGS_KEY = "ImageData";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        /**
         * creating and setting the image to the image view
         * */
        ImageView snappedImageViewer = (ImageView) view.findViewById(R.id.imageView_snapped_image);
        byte[] imageData = getArguments().getByteArray(IMAGEDATA_ARGS_KEY);
        snappedImageViewer.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
    }

    public static LoadingScreenFragment getInstance(){
        return new LoadingScreenFragment();
    }
}