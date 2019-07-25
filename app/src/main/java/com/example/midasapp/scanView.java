package com.example.midasapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class scanView extends AppCompatActivity {

    private TextView valueText;
    private Button capture;
    private TextureView display;

    private String cameraID;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_view);



        valueText = (TextView) findViewById(R.id.barcodeValue);
        display = (TextureView) findViewById(R.id.textureView);
        assert display != null;
        display.setSurfaceTextureListener(textureListener);
        capture = (Button) findViewById(R.id.captureButton);

    }


    public void process(View v)
    {
        if(cameraDevice == null)
        {
            valueText.setText("Camera error!");
            return;
        }
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try
        {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if(characteristics != null)
            {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 480;
            int height = 640;
            if(jpegSizes != null && jpegSizes.length > 0)
            {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(display.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //*Missing File IO things*/




        }
        catch(CameraAccessException e)
        {
            e.printStackTrace();
        }






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
    }


    private void createCameraPreview()
    {
        try
        {
            SurfaceTexture texture = display.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession)
                {
                    if(cameraDevice == null)
                    {
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@.NonNull CameraCaptureSession session)
                {
                    Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void updatePreview()
    {
        if(cameraDevice == null)
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try
        {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null, mBackgroundHandler);
        }
        catch(CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}
