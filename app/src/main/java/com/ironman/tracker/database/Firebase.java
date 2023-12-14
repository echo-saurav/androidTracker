package com.ironman.tracker.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ironman.tracker.utills.OnCreateUserListener;
import com.ironman.tracker.utills.OnGetTrackingUsers;
import com.ironman.tracker.utills.OnMyCompleteListener;
import com.ironman.tracker.valueHolder.AppUsageValueHolder;
import com.ironman.tracker.valueHolder.CallLogValueHolder;
import com.ironman.tracker.valueHolder.LocationValueHolder;
import com.ironman.tracker.valueHolder.NotificationValueHolder;
import com.ironman.tracker.valueHolder.RequestValueHolder;
import com.ironman.tracker.valueHolder.UserValueHolder;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;


public class Firebase {
    public static String TAG="MY_LOG";

    public static boolean isUserLoggedIn(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }


    public static void getUserInfo(String uid,EventListener<DocumentSnapshot> eventListener ){
        DocumentReference documentReference= FirebaseFirestore.getInstance()
                .collection("users").document(uid);
        documentReference.addSnapshotListener(eventListener);
    }

    public static void updateUserInfo(HashMap<String,Object> userHashMap, OnCompleteListener<Void> onCompleteListener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            DocumentReference documentReference= FirebaseFirestore.getInstance()
                    .collection("users").document(firebaseUser.getUid());

            documentReference.update(userHashMap).addOnCompleteListener(onCompleteListener);
        }
    }

    public static void createNewUser(String email, String password,String userName, OnCreateUserListener onCreateUserListener){
        // create user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null){
                            // set new user value
                            DocumentReference documentReference= FirebaseFirestore.getInstance()
                                    .collection("users").document(user.getUid());
                            updateFcmForNewUser();

                            documentReference.set(new UserValueHolder(userName,email,"","",user.getUid()))
                                    .addOnCompleteListener(task1 -> {

                                        onCreateUserListener.onComplete();
                                    });
                        }

                    }else if(task.getException()!=null){ // error
                        onCreateUserListener.onError(task.getException().toString());
                    }
                });
    }

    static void updateFcmForNewUser(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    if(!token.isEmpty()){
                        Firebase.updateFcmToken(token);
                    }


                });

        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    public static void uploadImage(Bitmap bitmap, String path, OnSuccessListener<UploadTask.TaskSnapshot> successListener,
                                   OnFailureListener failureListener,
                                   OnProgressListener<UploadTask.TaskSnapshot> onProgressListener,
                                   Context context){
        try {
            Uri uri=getImageUri(context,bitmap);
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(path);
            storageReference.putFile(uri)
                    .addOnSuccessListener(successListener)
                    .addOnProgressListener(onProgressListener)
                    .addOnFailureListener(failureListener);

        }catch (Exception e){
            Log.d(TAG, "uploadImage: "+e);
        }
    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ""+new Random().nextInt(), null);
        return Uri.parse(path);
    }

    public static void sendRequestCode(String code, OnMyCompleteListener onMyCompleteListener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            if(code.equals(firebaseUser.getUid())){
                onMyCompleteListener.error("invalid code, this is your own code");
                return;
            }

            DocumentReference userRef= FirebaseFirestore.getInstance()
                    .collection("users").document(code);
            CollectionReference requestedUserRef= FirebaseFirestore.getInstance()
                    .collection("requestRef").document(code).collection("request");

            RequestValueHolder requestValueHolder=new RequestValueHolder(firebaseUser.getUid());

            // check if user exist
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> trackers = (List<String>) document.get("tracker");

                    if(trackers!=null)
                        if(trackers.contains(firebaseUser.getUid())){
                            onMyCompleteListener.error("already tracking this user");
                            return;
                        }

                    if (document.exists() ) {
                        // write on requested collection
                        requestedUserRef.document(firebaseUser.getUid()).set(requestValueHolder).addOnCompleteListener(task1 -> {
                           if(task1.isSuccessful()){
                               onMyCompleteListener.success();
                           }else {
                               onMyCompleteListener.error("error : "+ task1.getException().getMessage());
                           }
                        });
                    } else {
                        onMyCompleteListener.error("Invalid code");
                    }
                } else {
                        onMyCompleteListener.error("Request sending failed");
                }
            });

        }
    }


    public static void acceptRequest(String uid,OnMyCompleteListener onMyCompleteListener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {

            DocumentReference currentUserRequestRef = FirebaseFirestore.getInstance()
                    .collection("requestRef").document(firebaseUser.getUid()).collection("request").document(uid);

            // add to array of tracker
            DocumentReference requestedUser = FirebaseFirestore.getInstance()
                    .collection("users").document(firebaseUser.getUid());
            requestedUser.update("tracker", FieldValue.arrayUnion(uid))
//            requestedUser.update("tracker", FieldValue.arrayUnion(firebaseUser.getUid()))

                    .addOnSuccessListener(aVoid -> {

                        // remove request doc
                        currentUserRequestRef.delete().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                onMyCompleteListener.success();
                            }else {
                                onMyCompleteListener.error(task.getException().getMessage());
                            }

                        });

                    })
                    .addOnFailureListener(e -> {

                        onMyCompleteListener.error(e.getMessage());
                    });




//            currentUserRequestRef.update("isAccepted",true)
//                    .addOnCompleteListener(task -> {
//                        if(task.isSuccessful()){
//                            onMyCompleteListener.success();
//                        }else {
//                            onMyCompleteListener.error(task.getException().getMessage());
//                        }
//
//                    });

        }
    }


    public static void cancelRequest(String uid,OnMyCompleteListener onMyCompleteListener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            DocumentReference currentUserRequestRef = FirebaseFirestore.getInstance()
                    .collection("requestRef").document(firebaseUser.getUid()).collection("request").document(uid);


            // remove request doc
            currentUserRequestRef.delete().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    onMyCompleteListener.success();
                }else {
                    onMyCompleteListener.error(task.getException().getMessage());
                }

            });
//            currentUserRequestRef.update("isAccepted",false)
//                    .addOnCompleteListener(task -> {
//                        if(task.isSuccessful()){
//                            onMyCompleteListener.success();
//                        }else {
//                            onMyCompleteListener.error(task.getException().getMessage());
//                        }
//
//                    });

        }
    }

    public static CollectionReference getRequestRef(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            return FirebaseFirestore.getInstance()
                    .collection("requestRef").document(firebaseUser.getUid()).collection("request");
        }
        return null;
    }
    public static Query getNotificationRef(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            return FirebaseFirestore.getInstance()
                    .collection("notifications").document(firebaseUser.getUid()).collection("userNotifications")
                    .orderBy("timeStamp", Query.Direction.DESCENDING);
        }
        return null;
    }

    public static void updateTrackingInfo(ArrayList<CallLogValueHolder> callLogValueHolders){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            CollectionReference collectionReference= FirebaseFirestore.getInstance()
                    .collection("trackingInfo").document(firebaseUser.getUid()).collection("callLogs");

            for(CallLogValueHolder callLogValueHolder:callLogValueHolders){
                collectionReference.document(callLogValueHolder.time+"").set(callLogValueHolder);
            }
        }
    }

    public static void updateAppUsageInfo(ArrayList<AppUsageValueHolder> appUsageValueHolder){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            CollectionReference collectionReference= FirebaseFirestore.getInstance()
                    .collection("trackingInfo").document(firebaseUser.getUid()).collection("appUsages");

            for(AppUsageValueHolder valueHolder:appUsageValueHolder){
                collectionReference.document(valueHolder.packageName).set(valueHolder);
            }
        }
    }

    public static void addNewLocationUpdate(LocationValueHolder locationValueHolder){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            CollectionReference collectionReference = FirebaseFirestore.getInstance()
                    .collection("trackingInfo").document(firebaseUser.getUid()).collection("locations");

            collectionReference.document().set(locationValueHolder);
        }
    }

    public static void getTrackingUsers(OnGetTrackingUsers onGetTrackingUsers){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {

            ArrayList<UserValueHolder> userValueHolders=new ArrayList<>();
            Query query = FirebaseFirestore.getInstance()
                    .collection("users").whereArrayContains("tracker",firebaseUser.getUid());
            query.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (e != null) {
                    onGetTrackingUsers.error();
                    return;
                }
                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        UserValueHolder userValueHolder=documentSnapshot.toObject(UserValueHolder.class);
                        userValueHolder.uid=documentSnapshot.getId();
                        userValueHolders.add(userValueHolder);
                    }
                    onGetTrackingUsers.onUserGetComplete(userValueHolders);
                }

            });
        }else {
            onGetTrackingUsers.error();
        }

    }

    public static Query getAppUsageRef(String uid){
        return  FirebaseFirestore.getInstance()
                .collection("trackingInfo").document(uid).collection("appUsages").orderBy("totalTimeVisible", Query.Direction.DESCENDING);
    }


    public static Query getCallLogRef(String uid){
        return  FirebaseFirestore.getInstance()
                .collection("trackingInfo").document(uid).collection("callLogs").orderBy("time", Query.Direction.DESCENDING);
    }


    public static Query getLocationsRef(String uid){
        return  FirebaseFirestore.getInstance()
                .collection("trackingInfo").document(uid).collection("locations").orderBy("time", Query.Direction.DESCENDING);
    }

    public static void updateFcmToken(String token){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)return;

        DocumentReference userDocRef=FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid());

        userDocRef.update("fcmToken",token);
    }
}
