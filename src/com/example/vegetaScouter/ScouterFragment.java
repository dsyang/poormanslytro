package com.example.vegetaScouter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: dsyang
 * Date: 7/28/13
 * Time: 4:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScouterFragment extends Fragment {
    private static final String TAG = "ScouterFragment";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    private int mFocusAreaIndex;
    private ArrayList<Camera.Area> mFocusAreas;
    /** A simple algorithm to get the largest size available. For a more
     * robust version, see CameraPreview.java in the ApiDemos
     * sample app from Android. */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
    private void takeOnePhoto(Camera.Area focusArea) {
        Log.e(TAG, "taking one photo");
        Camera.Parameters params = mCamera.getParameters();
        ArrayList<Camera.Area> fa = new ArrayList<Camera.Area>();
        fa.add(focusArea);
        params.setFocusAreas(fa);
        mCamera.setParameters(params);
        Log.e(TAG, "right before took picture");
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                (new MediaActionSound()).play(MediaActionSound.FOCUS_COMPLETE);
                mCamera.takePicture(mShutterCb, null, mSaveCb);
            }
        });


    }
    public static ScouterFragment newInstance() {
        return new ScouterFragment();
    }

    private Camera.ShutterCallback mShutterCb = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.e(TAG, "shuttering");
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mSaveCb = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // Create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            Log.e(TAG, path);
            boolean success = true;
            FileOutputStream os = null;
            try {
                File out = new File(path + "/" + mFocusAreaIndex + "--" + filename);
                Log.e(TAG, "writing photo: " + mFocusAreaIndex + "--" + filename);
                os = new FileOutputStream(out);
                os.write(data);
                //BitmapFactory.decodeStream(os);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file stream", e);
                success = false;
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
            }

            if (success) {
                Log.i(TAG, "JPEG saved at " + filename);
            }

            mFocusAreaIndex++;
            if(mFocusAreaIndex >= mFocusAreas.size()) {
                getActivity().finish();
            } else {
                mCamera.stopPreview();
                mProgressContainer.setVisibility(View.INVISIBLE);
                mCamera.startPreview();
                Log.e(TAG, "One pic done. Taking #" + mFocusAreaIndex);


                takeOnePhoto(mFocusAreas.get(mFocusAreaIndex));
            }
        }
    };



    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_scouter,container, false);

        Button scanButton = (Button) v.findViewById(R.id.scouter_takePicture);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCamera != null) {
                    int max = mCamera.getParameters().getMaxNumFocusAreas();
                    Log.e(TAG, "MaxNumFocusAreas: "+ max);
                    mFocusAreas = new ArrayList<Camera.Area>();

                    mFocusAreas.add(new Camera.Area(new Rect(-900, -900, -800, -800), 100));
                    mFocusAreas.add(new Camera.Area(new Rect(-250, -150, -100, -100), 23));
                    mFocusAreas.add(new Camera.Area(new Rect(-50, -50, 50, 50), 100));
                    mFocusAreas.add(new Camera.Area(new Rect(600, 600, 700, 700), 100));

                    Log.e(TAG, "We have " + mFocusAreas.size() + " focus areas");

                    mFocusAreaIndex = 0;
                    takeOnePhoto(mFocusAreas.get(mFocusAreaIndex));

                    Camera.Parameters params = mCamera.getParameters();
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                    mCamera.setParameters(params);
                }
            }
        });
        mProgressContainer = v.findViewById(R.id.progressBar);
        mProgressContainer.setVisibility(View.INVISIBLE);

        mSurfaceView = (SurfaceView) v.findViewById(R.id.scouter_surfaceView);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCamera != null) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            (new MediaActionSound()).play(MediaActionSound.FOCUS_COMPLETE);
                        }
                    });
                }
            }
        });
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if(mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(mCamera != null) {
                    mCamera.stopPreview();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if(mCamera == null) return;

                Camera.Parameters params = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(params.getSupportedPreviewSizes(),width, height);
                params.setPreviewSize(s.width, s.height);

                mCamera.setParameters(params);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });
        return v;
    }
}
