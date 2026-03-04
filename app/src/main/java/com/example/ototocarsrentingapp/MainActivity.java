
package com.example.ototocarsrentingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ototocarsrentingapp.auth.AuthActivity;
import com.example.ototocarsrentingapp.main.ViewModel.MainViewModel;
import com.example.ototocarsrentingapp.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getUserLive().observe(this, result -> {
            if (result == null) {
                finish();
                startActivity(new Intent(this, AuthActivity.class));
                return;
            }
            Log.d("MainActivity", "user live data changed Fail: " + result.isFailure() + " Unset: " + result.isUnset() + " Success: " + result.isSuccess() + " Error: " + result.isFailure());
            if (result.isSuccess()) {
                User user = result.getData();
                renderUserData(user);
            } else if (result.isFailure()) {
                Log.e("MainActivity", "Error loading user", result.getError());
                Toast.makeText(this, "Oops something went wrong, try re signing it", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, AuthActivity.class));
            }
        });

        findViewById(R.id.btnMenu).setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.inflate(R.menu.main_menu);
            NavController navController = getNavController();
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == navController.getGraph().getStartDestinationId()) {
                // We are on the "Home" screen
                menu.getMenu().findItem(R.id.backOpt).setVisible(false);
            }
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.signOutOpt) {
                    viewModel.signOut();
                    return true;
                }
                if(item.getItemId() == R.id.backOpt) {
                    navController.popBackStack();
                    return true;
                }
                return false;
            });

            menu.show();
        });

    }

    private NavController getNavController() {
        NavHostFragment frag = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        return frag.getNavController();
    }

    private void renderUserData(User user) {
        NavController navController = getNavController();
        if (user.isRenter()) {
            navController.setGraph(R.navigation.renter_nav_graph);
        } else {
            navController.setGraph(R.navigation.seller_nav_graph);
        }
    }


}