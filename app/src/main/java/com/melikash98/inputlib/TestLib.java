package com.melikash98.inputlib;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.melikash98.customeditinputs.CustomInput;

import java.util.regex.Pattern;

public class TestLib extends AppCompatActivity {
    private Button themeToggleButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String THEME_KEY = "current_theme";
    private AppCompatButton test;

    private CustomInput editOne, editTwo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
       /* sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }*/
        setContentView(R.layout.activity_test_lib);
        themeToggleButton = findViewById(R.id.themeToggleButton);

        /*themeToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme();
            }
        });*/
        editOne = findViewById(R.id.edit_one);
        editTwo = findViewById(R.id.edit_two);
        test = findViewById(R.id.testBtn);


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOne.hideAlert();
                editTwo.hideHelper();

                if (editOne.getInputText().equals("gg")) {
                    editOne.setAlertText(" Your Email Address Is Failed");
                } else {
                    editOne.setHelperText(" Name is OK");
                }
                if (!editTwo.getInputText().isEmpty() && !editTwo.isEmail()) {
                    editTwo.setAlertText(" Invalid Email Address");
                } else if (!editTwo.getInputText().isEmpty()) {
                    editTwo.setHelperText(" Your Email Address Is '" + editTwo.getInputText() + "'");
                } else {
                    editTwo.setAlertText(" Email is required");
                }
            }
        });

    }
   /* private void toggleTheme() {
        boolean isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean(THEME_KEY, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean(THEME_KEY, true);
        }
        editor.apply();

        recreate();
    }*/
}