package com.cosmicode.roomie.service;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPictureService {

    private static final String TAG = "UploadService";

    private OnUploadPictureListener listener;
    private Context context;

    public UploadPictureService(Context context, OnUploadPictureListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public String uploadFile(Uri uri, Long id, PictureType type ){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String path = type.equals(PictureType.PROFILE) ? "profile" : "room";
        StorageReference ref = type.equals(PictureType.PROFILE)? storageRef.child(path+"/roomieId"+id) : storageRef.child(path+"roompic");

        UploadTask uploadTask = ref.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    listener.onUploaddSuccess(task.getResult().toString());
                } else {
                    listener.onUploadError(task.getException().getMessage());
                }
            }

        });
        return null;
    }

    public interface OnUploadPictureListener {
        void onUploaddSuccess(String url);
        void onUploadError(String error);
    }

    public enum PictureType{
        PROFILE, ROOM
    }

}
