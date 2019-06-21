package com.example.placa;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.placa.Help.GraphyOver;
import com.example.placa.Help.TexyGraphy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    CameraView cameraView;
    AlertDialog alertDialog;
    GraphyOver graphyOver;
    Button btn_capture;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialog=new SpotsDialog.Builder().setCancelable(false).setMessage("Please wait").setContext(this).build();
        graphyOver=(GraphyOver)findViewById(R.id.graphic_overlay);
        cameraView=(CameraView)findViewById(R.id.camera_view);
        btn_capture=(Button)findViewById(R.id.button_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphyOver.clear();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                    alertDialog.show();
                    //
                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();

                recognizeTEXT(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void recognizeTEXT(Bitmap bitmap) {

        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionCloudTextRecognizerOptions options=
                new FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList("en"))//LENGUAJE
                .build();
        FirebaseVisionTextRecognizer textRecognizer=FirebaseVision.getInstance()
                .getCloudTextRecognizer(options);

        textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                drawTextResult(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EDMT:ERROR",e.getMessage());
            }
        });
    }
    private void drawTextResult(FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.TextBlock> blocks=firebaseVisionText.getTextBlocks();
        if (blocks.size() == 0){
            Toast.makeText(this,"Not text Found",Toast.LENGTH_SHORT).show();
            return;
        }
        graphyOver.clear();
        for (int i=0;i<blocks.size();i++){
            List<FirebaseVisionText.Line> lines=blocks.get(i).getLines();
            for (int j=0;j<lines.size();j++){
                List<FirebaseVisionText.Element> elements=lines.get(j).getElements();
             for (int k=0;k<elements.size();k++ ){
                 TexyGraphy texyGraphy=new TexyGraphy(graphyOver,elements.get(k));
                 graphyOver.add(texyGraphy);

             }
            }
        }
        alertDialog.dismiss();
    }
}
