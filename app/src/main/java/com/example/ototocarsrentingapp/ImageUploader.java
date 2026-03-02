package com.example.ototocarsrentingapp;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageUploader {

    public Task<String> uploadImage(String path, Uri image) {
        // Create a storage reference from our app
        StorageReference ref = FirebaseStorage.getInstance().getReference(path);
        return ref.putFile(image)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl().continueWith(url -> {
                        if(!url.isSuccessful()) throw url.getException();
                        return url.getResult().toString();
                    });
                });
    }
}
