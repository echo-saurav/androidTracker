package com.ironman.tracker.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.ironman.tracker.R;
import com.ironman.tracker.bottomSheets.ImageSelectionBottomSheet;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnImageSelectionListener;
import com.ironman.tracker.valueHolder.UserValueHolder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.ironman.tracker.database.Firebase.getUserInfo;
import static com.ironman.tracker.database.Firebase.updateUserInfo;
import static com.ironman.tracker.database.Firebase.uploadImage;

public class EditUserActivity extends AppCompatActivity implements OnImageSelectionListener {
    private static final String TAG ="MY_LOG" ;
    private ProgressDialog progressDialog;
    private EditText userName,description;
    public static int MAX_HEIGHT = 1024;
    public static int MAX_WIDTH = 1024;
    private Bitmap bitmap;
    private ImageView imageView;
    //
    private static int PERMISSION_GALLERY_REQUEST_CODE =1;
    private static int PERMISSION_CAMERA_REQUEST_CODE=2;
    private static int REQUEST_CODE_CAMERA_IMAGE =3;
    public static int REQUEST_CODE_GALLERY_IMAGE =4;
    private ImageSelectionBottomSheet imageSelectionBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_activity);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading, Please wait");
        progressDialog.setCancelable(false);
        userName=findViewById(R.id.user_name);
        description=findViewById(R.id.user_description);
        imageView=findViewById(R.id.image_view);

        imageSelectionBottomSheet=new ImageSelectionBottomSheet(this);

        findViewById(R.id.select_image).setOnClickListener(v ->{
                    imageSelectionBottomSheet.show(getSupportFragmentManager(),"select_image");
        });

        setProfileInfo();
        findViewById(R.id.save_edit_user_button).setOnClickListener(v -> saveUserInfo());
    }


    @Override
    public void selectFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_GALLERY_IMAGE);
    }
    @Override
    public void takeFromCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA_IMAGE);
    }

    void setValue(String userName,String email,String description,String imageUrl){
        Toast.makeText(this,"Updating user info....",Toast.LENGTH_LONG).show();

        // make hashMap from object
        HashMap<String,Object> userHashMap= new HashMap<>();
        if(!userName.isEmpty())
            userHashMap.put("userName",userName);
        if(!description.isEmpty())
            userHashMap.put("description",description);
        if(imageUrl!=null)
            userHashMap.put("imageUrl",imageUrl);

        Firebase.updateUserInfo(userHashMap, task -> {
            Toast.makeText(this,"User info updated successfully",Toast.LENGTH_LONG).show();
        });
    }

    void setProfileInfo(){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            ((TextView)findViewById(R.id.user_email)).setText(firebaseUser.getEmail());

            getUserInfo(firebaseUser.getUid(), (documentSnapshot, e) -> {
                if (e != null) {
                    Toast.makeText(EditUserActivity.this,"Error :"+e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    UserValueHolder userValueHolder=documentSnapshot.toObject(UserValueHolder.class);
                    if(userValueHolder==null)
                        return;

                    ((TextView)findViewById(R.id.user_name)).setText(userValueHolder.userName);
                    ((TextView)findViewById(R.id.user_description)).setText(userValueHolder.description);
                    if(!userValueHolder.imageUrl.isEmpty())
                    Picasso.get().load(userValueHolder.imageUrl).error(R.mipmap.ic_launcher_round).into((ImageView)findViewById(R.id.image_view));

                } else {
                    Log.d(TAG, "Current data: null");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageSelectionBottomSheet.dismiss();

        if (requestCode == REQUEST_CODE_GALLERY_IMAGE  && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(TAG, "onActivityResult: from gallery image");
            Uri imageFileUri = data.getData();
            try {
                this.bitmap = handleSamplingAndRotationBitmap(this, imageFileUri);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == REQUEST_CODE_CAMERA_IMAGE  && resultCode == RESULT_OK && data != null && data.getExtras()!=null) {
            Log.d(TAG, "onActivityResult: from camera image");
            this.bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean isReadPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        // always true if bellow version code M
        else {
            return true;
        }
    }

    @Override
    public void getImageSelectionPermission() {
        ActivityCompat          // get permission call
                .requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_GALLERY_REQUEST_CODE);
    }

    @Override
    public void getCameraPermission() {
        ActivityCompat          // get permission call
                .requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
    }

    @Override
    public boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        else {  // always true if bellow version code M
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && requestCode == PERMISSION_GALLERY_REQUEST_CODE) {

            if( grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                selectFromGallery();
            }


        }else {
            Toast.makeText(this, "Please give permission for profile image", Toast.LENGTH_LONG).show();
        }

        if (grantResults.length > 0 && requestCode ==PERMISSION_CAMERA_REQUEST_CODE) {

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takeFromCamera();
            }

        }else {
            Toast.makeText(this, "Please give permission for profile image", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {

        // First decode with inJustDecodeBounds=true to checkRequirement dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    void saveUserInfo(){
        String userNameString=userName.getText().toString().trim();
        String descriptionString = description.getText().toString().trim();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(!userNameString.isEmpty() && firebaseUser!=null){

            if(bitmap!=null){
                progressDialog.show();
                uploadImage(bitmap, firebaseUser.getUid(), taskSnapshot -> {
                    progressDialog.dismiss();

                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            setValue(userNameString,firebaseUser.getEmail(),descriptionString,task.getResult().toString());
                            bitmap=null;
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }, e -> {
                    Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }, taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    Log.d(TAG, "saveUserInfo: progress:"+progress);
                    progressDialog.setMessage("Upload " + (int) progress + "%");
                },this);
            }else {
                setValue(userNameString,firebaseUser.getEmail(),descriptionString,null);
            }
        }else {
            Toast.makeText(this,"Please choose a user name, Description can be empty",Toast.LENGTH_LONG).show();
        }
    }


}