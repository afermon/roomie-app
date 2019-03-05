package com.cosmicode.roomie.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UploadPictureService {

    private static final String TAG = "UploadService";

    private Context context;
    private OnUploadPictureListener listener;

    public UploadPictureService(Context context, OnUploadPictureListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public String uploadFile(Bitmap bitmap, Long id, PictureType type ){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String path = "";
        path = type.equals(PictureType.PROFILE) ? "profile" : "room";
        StorageReference ref = storageRef.child(path+"/roomieId"+id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bdata = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(bdata);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
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
