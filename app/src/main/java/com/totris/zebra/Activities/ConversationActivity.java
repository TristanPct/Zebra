package com.totris.zebra.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;

import com.totris.zebra.Fragments.ConversationFragment;
import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.Message;
import com.totris.zebra.Models.MessageType;
import com.totris.zebra.R;
import com.totris.zebra.Utils.EventBus;
import com.totris.zebra.Utils.OnlineStorage;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class ConversationActivity extends AppCompatActivity implements ConversationFragment.ConversationListener {
    private static final int RESULT_LOAD_IMAGE_FROM_GALLERY = 1;
    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 2;
    private static final int MAX_IMAGE_SIZE = 500;
    private static final int PERMISSIONS_REQUEST_READ_MEDIA = 1;
    static String TAG = "ConversationActivity";
    private Group group;
    private ConversationFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        group = (Group) getIntent().getSerializableExtra("group");

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.register(currentFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.unregister(currentFragment);
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public void onSubmitMessage(String message) {
        Log.d(TAG, "onSubmitMessage: " + message);

        Message messageObj = new Message(message, MessageType.TEXT);

        //currentFragment.addMessage(messageObj);

        messageObj.setCreatedAt(new Date());

        group.sendMessage(messageObj);
    }

    private void fixBitmapOrientation() {

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
