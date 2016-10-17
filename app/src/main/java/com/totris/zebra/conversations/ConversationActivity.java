package com.totris.zebra.conversations;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;
import com.totris.zebra.messages.MessageType;
import com.totris.zebra.R;
import com.totris.zebra.utils.EventBus;
import com.totris.zebra.utils.OnlineStorage;
import com.totris.zebra.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class ConversationActivity extends AppCompatActivity implements ConversationFragment.ConversationListener {
    private static final String TAG = "ConversationActivity";

    private static final int RESULT_LOAD_IMAGE_FROM_GALLERY = 1;
    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 2;
    private static final int MAX_IMAGE_SIZE = 500;
    private static final int PERMISSIONS_REQUEST_READ_MEDIA = 1;

    private Group group;
    private String title;
    private ConversationFragment currentFragment;

    // Facetracking vars

    private CameraSource mCameraSource = null;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /****
         * Initialisation
         */

        setContentView(R.layout.activity_conversation);

        group = (Group) getIntent().getSerializableExtra("group");
        title = getIntent().getStringExtra("title");

        if (group != null) {
            group.addChildEventListener();
        }

        currentFragment = new ConversationFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_conversation, currentFragment)
                    .commit();
        }

        /****
         * FaceDetector
         */

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: PERMISSION_GRANTED");
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        /****
         * UI
         */

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
        EventBus.register(currentFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(currentFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    public Group getGroup() {
        return group;
    }

    /*************
     * Facetracking
     */

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, listener)
//                .show();
    }

    private void createCameraSource() {

        Log.d(TAG, "createCameraSource");

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(5.0f)
                .build();
    }

    private void startCameraSource() {
        Log.d(TAG, "startCameraSource");
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission();
                    return;
                }
                mCameraSource.start();
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker();
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {

        GraphicFaceTracker() {
            Log.d(TAG, "GraphicFaceTracker");
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            Log.d(TAG, "FaceOnNewItem");
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
//            Log.d(TAG, "FaceOnUpdate - leftOpen : " + face.getIsLeftEyeOpenProbability() + " - rightOpen : " + face.getIsRightEyeOpenProbability());

            face.getIsLeftEyeOpenProbability();
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            Log.d(TAG, "FaceOnMissing");
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            Log.d(TAG, "FaceGood");
        }
    }

    /*************
     * Message submission
     */

    @Override
    public void onSubmitMessage(String message) {
        Log.d(TAG, "onSubmitMessage: " + message);

        ToggleButton leetToggle = (ToggleButton) findViewById(R.id.leetToggle); // TODO: see how to get that binding from the fragment

        if(leetToggle.isChecked()) {
            message = StringUtils.leetify(message);
        }

        Message messageObj = new Message(message, MessageType.TEXT);

        //currentFragment.addMessage(messageObj);

        messageObj.setCreatedAt(new Date());

        group.sendMessage(messageObj);
    }

    private void fixBitmapOrientation() { //TODO: fix picture orientation after camera capture

    }

    public void sendPicture(Bitmap imageBitmap) {
        //Bitmap bitmap = fixBitmapOrientation(mCurrentPhotoPath);

        // Reduce size of big images before uploading
        if (imageBitmap.getHeight() > MAX_IMAGE_SIZE) {
            double height = imageBitmap.getHeight();
            double width = imageBitmap.getWidth();
            double ratio = height / width;
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (MAX_IMAGE_SIZE / ratio), MAX_IMAGE_SIZE, true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArrayImage = baos.toByteArray();

        Message messageObj = new Message(OnlineStorage.RandomIdGenerator() + ".jpg", MessageType.IMAGE);

        group.sendImageMessage(messageObj, byteArrayImage);
    }

    @Override
    public void onTakePictureFromCameraClick() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, RESULT_LOAD_IMAGE_FROM_CAMERA);
        }
    }

    @Override
    public void onTakePictureFromGalleryClick() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_MEDIA);
        } else {
            openGallery();
        }
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery();
                }
                break;

            case RC_HANDLE_CAMERA_PERM:

                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Camera permission granted - initialize the camera source");

                    createCameraSource();
                    return;
                }

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_LOAD_IMAGE_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    sendPicture(imageBitmap);
                }

                return;
            case RESULT_LOAD_IMAGE_FROM_GALLERY:
                if (resultCode == RESULT_OK && null != data.getData()) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

                    sendPicture(imageBitmap);
                }

                return;
        }
    }
}
