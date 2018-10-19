package com.jessicathornsby.facerecog;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends BaseActivity  {

    private ImageView myImageView;
    private TextView myTextView;
    private Bitmap myBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = findViewById(R.id.textView);
        myImageView = findViewById(R.id.imageView);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WRITE_STORAGE:
                    checkPermission(requestCode);
                case CAMERA:
                    checkPermission(requestCode);
                    break;
                case SELECT_PHOTO:
                    Uri dataUri = data.getData();
                    String path = MyHelper.getPath(this, dataUri);
                    if (path == null) {
                        myBitmap = MyHelper.resizePhoto(photoFile, this, dataUri, myImageView);
                    } else {
                        myBitmap = MyHelper.resizePhoto(photoFile, path, myImageView);
                    }
                    if (myBitmap != null) {
                        myTextView.setText(null);
                        myImageView.setImageBitmap(myBitmap);
                        runFaceDetector(myBitmap);
                    }
                    break;
                case TAKE_PHOTO:
                    myBitmap = MyHelper.resizePhoto(photoFile, photoFile.getPath(), myImageView);
                    if (myBitmap != null) {
                        myTextView.setText(null);
                        myImageView.setImageBitmap(myBitmap);
                        runFaceDetector(myBitmap);
                    }
                    break;
            }
        }
    }

    private void runFaceDetector(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.FAST_MODE)
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setMinFaceSize(0.1f)
                .setTrackingEnabled(false)
                .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> faces) {
                myTextView.setText(runFaceRecog(faces));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure
                    (@NonNull Exception exception) {
                Toast.makeText(MainActivity.this,
                        "Exception", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String runFaceRecog(List<FirebaseVisionFace> faces) {
        StringBuilder result = new StringBuilder();
        float smilingProbability = 0;
        float rightEyeOpenProbability = 0;
        float leftEyeOpenProbability = 0;

        for (FirebaseVisionFace face : faces) {
            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                smilingProbability = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                rightEyeOpenProbability = face.getRightEyeOpenProbability ();
            }
            if (face.getLeftEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                leftEyeOpenProbability = face.getLeftEyeOpenProbability();
            }


            result.append("Smile: ");
            if (smilingProbability > 0.5) {
                result.append("Yes \nProbability: " + smilingProbability);
            } else {
                result.append("No");
            }
            result.append("\n\nRight eye: ");
            if (rightEyeOpenProbability > 0.5) {
                result.append("Open \nProbability: " + rightEyeOpenProbability);
            } else {
                result.append("Close");
            }
            result.append("\n\nLeft eye: ");
            if (leftEyeOpenProbability > 0.5) {
                result.append("Open \nProbability: " + leftEyeOpenProbability);
            } else {
                result.append("Close");
            }
            result.append("\n\n");
        }
        return result.toString();
    }


        }


