package com.example.midasapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;



public class scanView extends AppCompatActivity {

    private TextView valueText;
    private TextureView textureView;
    private Button button;

    CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);


        valueText = (TextView) findViewById(R.id.txtContent);

        try
        {
            CameraDevice.StateCallback

        }catch(CameraAccessException e)
        {
            e.printStackTrace();
        }





    }


    public void process(View v)
    {




        /*// BARCODE DETECTION: REQUIRES ADJUSTMENT TO TEXTUREVIEW
        ImageView i = (ImageView) findViewById(R.id.textureView);
        Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.v6);
        i.setImageBitmap(b);

        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.CODE_39).build();
        //Make sure detector is operational
        if(!detector.isOperational())
        {
            valueText.setText("Failed to set up detector. Internet connection is required for first time setup");
            return;
        }

        //Create an array of barcodes detected in the frame (bitmap image)
        Frame frame = new Frame.Builder().setBitmap(b).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        //Display the barcode if the size of the array is 1
        if(barcodes.size() == 0)
        {
            valueText.setText("No barcodes found!");
        }
        else if(barcodes.size() > 1)
        {
            valueText.setText("Multiple barcodes found!");
        }
        else
        {
            Barcode code = barcodes.valueAt(0);
            valueText.setText(code.rawValue);
        }
        */
    }
}
