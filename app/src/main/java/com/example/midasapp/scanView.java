package com.example.midasapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import java.util.Collections;


public class scanView extends AppCompatActivity {

    private TextView valueText;
    private TextureView textureView;
    private Button button;

    final static int CAMERA_REQUEST_CODE = 1;

    CameraManager cameraManager;
    TextureView.SurfaceTextureListener surfaceTextureListener;
    int cameraFacing;
    Size previewSize;
    String cameraId;
    CameraDevice.StateCallback stateCallback;
    Handler backgroundHandler;
    HandlerThread backgroundThread;
    private CameraDevice cameraDevice;
    CameraCaptureSession cameraCaptureSession;
    CaptureRequest captureRequest;
    CaptureRequest.Builder captureRequestBuilder;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);

        textureView = (TextureView)  findViewById(R.id.textureView);
        valueText = (TextView) findViewById(R.id.txtContent);
        button = (Button) findViewById(R.id.button);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                scanView.this.cameraDevice = cameraDevice;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                cameraDevice.close();
                scanView.this.cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                scanView.this.cameraDevice = null;
            }
        };
    }
    private void setUpCamera()
    {
        try
        {
            for (String cameraId : cameraManager.getCameraIdList())
            {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing)
                {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;
                }
            }
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
    private void openCamera()
    {
        try
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
    private void openBackgroundThread()
    {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        openBackgroundThread();
        if(textureView.isAvailable())
        {
            setUpCamera();
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        closeCamera();
        closeBackgroundThread();
    }

    private void closeCamera()
    {
        if(cameraCaptureSession != null)
        {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if(cameraDevice != null)
        {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeBackgroundThread()
    {
        if (backgroundHandler != null)
        {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void createPreviewSession()
    {
        try
        {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback()
                    {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession)
                        {
                            if (cameraDevice == null)
                            {
                                return;
                            }
                            try
                            {
                                captureRequest = captureRequestBuilder.build();
                                scanView.this.cameraCaptureSession = cameraCaptureSession;
                                scanView.this.cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler);
                            } catch (CameraAccessException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void lock()
    {
        try
        {
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }




    public void process(View v)
    {
        lock();

        Bitmap b = textureView.getBitmap();
/*
        ImageView i = (ImageView) findViewById(R.id.textureView);
        Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.v6);*/

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
            valueText.setText(barcodes.size() + " barcodes found!");
        }
        else
        {
            Barcode code = barcodes.valueAt(0);
            valueText.setText(code.rawValue);
            if(checkCode(code.rawValue))
            {
                Toast.makeText(this, "Code matches!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(scanView.this, orderView.class);
//                //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.putExtra("code", code.rawValue);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
            else
            {
                Toast.makeText(this, "Code not in database!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkCode(String code)
    {
        Intent i = getIntent();
        String[] codeList = i.getStringArrayExtra("codes");
        for(int a = 0; a < codeList.length; a++)
        {
            if(code.compareToIgnoreCase(codeList[a]) == 0)
            {
                return true;
            }
        }
        return false;
    }
}
