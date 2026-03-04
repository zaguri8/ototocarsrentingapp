package com.example.ototocarsrentingapp.main.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ototocarsrentingapp.model.Renter;
import com.example.ototocarsrentingapp.model.Result;
import com.example.ototocarsrentingapp.model.Seller;
import com.example.ototocarsrentingapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Result<User>> userLive = new MutableLiveData<>();
    private ListenerRegistration userDocumentListener;

    private final FirebaseAuth.AuthStateListener authListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            // User Logged in, attach Database Listener
            loadUser(uid).addOnSuccessListener(userType -> {
                // stop listening first
                if(userDocumentListener != null) userDocumentListener.remove();//לוודא שאין לנו שני מאזינים באותו הזמן
                userDocumentListener = FirebaseFirestore.getInstance()
                        .collection(userType)
                        .document(uid)
                        .addSnapshotListener((value, error) -> {
                            if(error != null) {
                                userLive.setValue(Result.failure(error));
                                return;
                            }
                            Log.d("MainViewModel", "user was updated");
                            User user;
                            if("renters".equals(userType)) {
                                user = value.toObject(Renter.class);
                            } else {
                                user = value.toObject(Seller.class);
                            }
                            Log.d("MainViewModel", "user:"+user);

                            userLive.setValue(Result.success(user));
                        });
            }).addOnFailureListener(e -> userLive.setValue(Result.failure(e)));
        } else if(userDocumentListener != null) {
            userLive.setValue(null);
            userDocumentListener.remove();
            userDocumentListener = null;
        }
        else {
            userLive.setValue(null);
        }
    };
    public MainViewModel() {
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public LiveData<Result<User>> getUserLive() {
        return userLive;
    }
    private Task<String> loadUser(String uid) {
        return FirebaseFirestore.getInstance()
                .collection("sellers")
                .document(uid)
                .get()
                .continueWith(task -> {
                    if(!task.isSuccessful() || !task.getResult().exists()) {
                        return "renters";
                    }
                    else {
                        return "sellers";
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        FirebaseAuth.getInstance().removeAuthStateListener(authListener);
        if(userDocumentListener != null) userDocumentListener.remove();
    }
}
