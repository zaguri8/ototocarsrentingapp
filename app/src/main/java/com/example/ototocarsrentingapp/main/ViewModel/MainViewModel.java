package com.example.ototocarsrentingapp.main.ViewModel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ototocarsrentingapp.model.CarRequest;
import com.example.ototocarsrentingapp.model.Renter;
import com.example.ototocarsrentingapp.model.Result;
import com.example.ototocarsrentingapp.model.Seller;
import com.example.ototocarsrentingapp.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Result<User>> userLive = new MutableLiveData<>();
    private final MutableLiveData<List<CarRequest>> requests = new MutableLiveData<>();
    private ListenerRegistration userDocumentListener, requestsDocumentsListener;

    private final FirebaseAuth.AuthStateListener authListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            // User Logged in, attach Database Listener
            loadUser(uid).addOnSuccessListener(userType -> {
                // stop listening first
                if (userDocumentListener != null)
                    userDocumentListener.remove();//לוודא שאין לנו שני מאזינים באותו הזמן
                userDocumentListener = FirebaseFirestore.getInstance()
                        .collection(userType)
                        .document(uid)
                        .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                userLive.setValue(Result.failure(error));
                                return;
                            }
                            Log.d("MainViewModel", "user was updated");
                            User user;
                            if ("renters".equals(userType)) {
                                user = value.toObject(Renter.class);
                                requestsDocumentsListener = registerRenterCarRequestsListener();
                            } else {
                                user = value.toObject(Seller.class);
                                requestsDocumentsListener = registerSellerCarRequestsListener();
                            }
                            Log.d("MainViewModel", "user:" + user);

                            userLive.setValue(Result.success(user));
                        });
            }).addOnFailureListener(e -> userLive.setValue(Result.failure(e)));
        } else if (userDocumentListener != null) {
            userLive.setValue(null);
            userDocumentListener.remove();
            userDocumentListener = null;
        } else {
            userLive.setValue(null);
        }
    };

    private ListenerRegistration registerSellerCarRequestsListener() {
        return FirebaseFirestore.getInstance()
                .collection("sellers")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("receivedRequests")
                .addSnapshotListener((value, error) -> {
                    if (value == null) {
                        requests.postValue(new ArrayList<>());
                        return;
                    }
                    List<CarRequest> requestsList = value.toObjects(CarRequest.class);
                    requests.postValue(requestsList);
                });
    }

    private ListenerRegistration registerRenterCarRequestsListener() {
        return FirebaseFirestore.getInstance()
                .collection("renters")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("sentRequests")
                .addSnapshotListener((value, error) -> {
                    if (value == null) {
                        requests.postValue(new ArrayList<>());
                        return;
                    }
                    List<CarRequest> requestsList = value.toObjects(CarRequest.class);
                    requests.postValue(requestsList);
                });
    }

    public MainViewModel() {
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public LiveData<Result<User>> getUserLive() {
        return userLive;
    }

    public boolean canSendCarRequest(Seller someSeller) {
        if (requests.getValue() == null) return false;
        return requests.getValue()
                .stream()
                .noneMatch(request -> request.getSeller().getId().equals(someSeller.getId()));
    }

    private Task<String> loadUser(String uid) {
        return FirebaseFirestore.getInstance()
                .collection("sellers")
                .document(uid)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful() || !task.getResult().exists()) {
                        return "renters";
                    } else {
                        return "sellers";
                    }
                });
    }

    public Task<List<Seller>> getSellers() {
        return FirebaseFirestore.getInstance()
                .collection("sellers")
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return task.getResult().toObjects(Seller.class);
                });
    }

    public Task<Void> sendCarRequest(Seller seller, long start, long end) {
        if (userLive.getValue() == null || !userLive.getValue().isSuccess()) {
            return Tasks.forException(new Exception("Something went wrong.. Please try again later"));
        }
        CarRequest request = new CarRequest((Renter) userLive.getValue().getData(), seller, start, end);
        DocumentReference refSeller = FirebaseFirestore
                .getInstance()
                .collection("sellers")
                .document(request.getSeller().getId())
                .collection("receivedRequests")
                .document(request.getId());
        DocumentReference refRenter = FirebaseFirestore
                .getInstance()
                .collection("renters")
                .document(request.getRenter().getId())
                .collection("sentRequests")
                .document(request.getId());
        return refSeller.set(request)
                .continueWithTask(t -> {
                    if (!t.isSuccessful()) throw t.getException();
                    return refRenter.set(request);
                });
    }


    public LiveData<List<CarRequest>> getRequests() {
        return requests;
    }

    public Task<CarRequest> updateCarRequestStatus(CarRequest request, CarRequest.RequestStatus status) {
        request.setStatus(status);
        DocumentReference refSeller = FirebaseFirestore
                .getInstance()
                .collection("sellers")
                .document(request.getSeller().getId())
                .collection("receivedRequests")
                .document(request.getId());
        DocumentReference refRenter = FirebaseFirestore
                .getInstance()
                .collection("renters")
                .document(request.getRenter().getId())
                .collection("sentRequests")
                .document(request.getId());
        return refSeller.set(request)
                .continueWithTask(t -> {
                    if (!t.isSuccessful()) throw t.getException();
                    return refRenter.set(request).continueWith(t2 -> {
                        if (!t2.isSuccessful()) throw t2.getException();
                        return request;
                    });
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        FirebaseAuth.getInstance().removeAuthStateListener(authListener);
        if (userDocumentListener != null) userDocumentListener.remove();
        if (requestsDocumentsListener != null) requestsDocumentsListener.remove();
    }
}
