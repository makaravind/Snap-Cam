package com.clarifai.android.starter.api.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.android.starter.api.v2.GameSingleton;
import com.clarifai.android.starter.api.v2.R;
import com.clarifai.android.starter.api.v2.adapter.App;
import com.clarifai.android.starter.api.v2.adapter.RecognizeConceptsAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.clarifai.android.starter.api.v2.ClarifaiUtil.filterPredictions;

public class SnapCam extends AppCompatActivity implements Camera.PictureCallback, SurfaceHolder.Callback{

    public static final String EXTRA_CAMERA_DATA = "camera_data";
    private static final String KEY_IS_CAPTURING = "is_capturing";
    private static final int SNAP_CAM_INTENT_KEY = 1000;
    private static final int SNAP_CAM_CAPTURE_IMAGE = 1001;


    @BindView(R.id.content_root) protected View root;

    private Camera mCamera;
    private RecyclerView resultsList;
    private RecognizeConceptsAdapter adapter;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private ImageView snapped_image;
    private TextView status;
    private byte[] mCameraData;
    private boolean mIsCapturing;
    private int backpressed_count = 0;

    // API
//    @NonNull
//    private Game GameSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_cam);

        // setting up camera
        onCreateCameraStuff();

        // for displaying the results from API in the recycler view
        onCreateResultsList();

        status = (TextView) findViewById(R.id.textView_status);
        snapped_image = (ImageView) findViewById(R.id.imageView_snap);

        mIsCapturing = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Log.e("SNAPCAM", e.toString());
                Toast.makeText(SnapCam.this, "Unable to open camera. onResume", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
        switch (backpressed_count){
            case 0:
                setupImageCapture();
                backpressed_count+=1;
                break;
            case 1:
                backpressed_count+=1;
                break;
            case 2:
                super.onBackPressed();
                break;
            case SNAP_CAM_CAPTURE_IMAGE:
                setupImageCapture();
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        // API call, setting resultslist data
        setupImageDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (IOException e) {
                Toast.makeText(SnapCam.this, "Unable to start camera preview. surface changed", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private View.OnClickListener mCaptureImageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();
        }
    };

    // API
    private mRecyclerClickListener onResultsListItemClicked(){

        return new mRecyclerClickListener(this, new mRecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView selected_concept = (TextView) view.findViewById(R.id.label_1);
                String concept_name = selected_concept.getText().toString();
                Log.i("MAIN", "item clicked at " + position+ " -> " + concept_name);
                if(position >= 0){
                    boolean status = GameSingleton.getInstance().checkIfValid(concept_name);
                    if(status) {
                        GameSingleton.getInstance().updateChaining(status, concept_name);
                        displayResults();
                    }
                }

            }
        });

    }

    // camera utils
    private void onImagePicked(@NonNull final byte[] imageBytes) {
        // Now we will upload our image to the Clarifai API
//        setBusy(true);
        status.setText("your image is being scanned !");
        mCaptureImageButton.setVisibility(GONE);

        // Make sure we don't show a list of old concepts while the image is being uploaded
        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {

            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images
                final ConceptModel generalModel = App.get().clarifaiClient().getDefaultModels().generalModel();

                // Use this model to predict, with the image that the user just selected as the input
                return generalModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
//                setBusy(false);
                status.setText("Image is classified intelligently!");
                mCaptureImageButton.setVisibility(VISIBLE);
                mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

                if (!response.isSuccessful()) {
//                    showErrorSnackbar(R.string.error_while_contacting_api);
                    Toast.makeText(SnapCam.this, "error while contacting api", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<ClarifaiOutput<Concept>> predictions = response.get();
                //filter the predictions with top 5 items only

                if (predictions.isEmpty()) {
//                    showErrorSnackbar(R.string.no_results_from_api);
                    Toast.makeText(SnapCam.this, "no results from API", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Concept> filteredPredictions = filterPredictions(predictions.get(0).data());

                status.setText("choose one");
                resultsList.setVisibility(VISIBLE);
                snapped_image.setVisibility(VISIBLE);
                adapter.setData(filteredPredictions);

                snapped_image.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                snapped_image.setAlpha(0.45f);
            }

            private void showErrorSnackbar(@StringRes int errorString) {
                Snackbar.make(
                        root,
                        errorString,
                        Snackbar.LENGTH_INDEFINITE
                ).show();
            }
        }.execute();
    }

    private void captureImage() {
        backpressed_count = SNAP_CAM_CAPTURE_IMAGE;
        mCamera.takePicture(null, null, this);
    }

    private void setupImageCapture() {
        backpressed_count = 0;

        mCaptureImageButton.setVisibility(VISIBLE);
        resultsList.setVisibility(View.INVISIBLE);
        snapped_image.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        mCaptureImageButton.setText("snap now!");
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    private void setupImageDisplay() {
        Log.i("CAMERA3", "call api here");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
//        snapped_image.setVisibility(View.VISIBLE);
//        snapped_image.setImageBitmap(bitmap);
        mCamera.stopPreview();
        mCameraPreview.setVisibility(View.INVISIBLE);
        mCaptureImageButton.setVisibility(View.INVISIBLE);

        onImagePicked(mCameraData);
    }

    void onCreateCameraStuff(){
        mCameraPreview = (SurfaceView) findViewById(R.id.surfaceView_preview);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton = (Button) findViewById(R.id.button_capture_image);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
        mCaptureImageButton.setVisibility(View.INVISIBLE);
    }

    void onCreateResultsList(){
        adapter = new RecognizeConceptsAdapter();
        resultsList = (RecyclerView) findViewById(R.id.resultsList);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setLayoutManager(new GridLayoutManager(this, 1, 1, false));
        resultsList.setAdapter(adapter);
        resultsList.setVisibility(View.INVISIBLE);
        resultsList.addOnItemTouchListener(onResultsListItemClicked());
    }

    private void displayResults() {
        // displaying results after choosing the option
        Intent i = new Intent(this, Home.class);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

}
