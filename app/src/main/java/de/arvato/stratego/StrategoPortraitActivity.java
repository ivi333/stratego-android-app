package de.arvato.stratego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class StrategoPortraitActivity extends Activity {

    public static final String TAG = "StrategoPortActivity";

    private Button playButton, instructionsButton, playOnlineButton, profileButton, settingsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stratego_main);

        if (getIntent().getBooleanExtra("RESTART", false)) {
            finish();
            Intent intent = new Intent(this, StrategoPortraitActivity.class);
            startActivity(intent);
        }

        playButton = findViewById(R.id.PlayButton);
        instructionsButton = findViewById(R.id.instructionsButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialogSelectColor ();
                //TODO moved to selection player
                /*Intent i = new Intent();
                i.setClass(getApplicationContext(), StartPlayActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);*/
            }
        });

        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), InstructionsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            }
        });
    }

    private void loadImageFromAssets(ImageView imageView, String file) {
        // Open the assets folder and read the image file
        try {
            InputStream inputStream = getAssets().open("highres/".concat(file));
            // Decode the InputStream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            // Set the Bitmap to the ImageView
            imageView.setImageBitmap(bitmap);
            // Close the InputStream
            inputStream.close();
        } catch (IOException e) {
            Log.e("PORTRAIT", e.getMessage(), e);
        }
    }

    private void initDialogSelectColor() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_player_selector, null);

        ImageView bluePlayerSelector = dialogView.findViewById(R.id.bluePlayerSelector);
        ImageView redPlayerSelector = dialogView.findViewById(R.id.redPlayerSelector);

        loadImageFromAssets (bluePlayerSelector, "bandera_blue.png");
        loadImageFromAssets (redPlayerSelector, "bandera_red.png");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Choose your color");

        bluePlayerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle left image click
                handleDialogImageClick(StrategoConstants.BLUE);
            }
        });

        redPlayerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle right image click
                handleDialogImageClick(StrategoConstants.RED);
            }

        });

        // Show the dialog
        AlertDialog dialog = builder.create();

        // Set dialog size after it is shown to avoid NullPointerException
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(800, 600); // Width and Height in pixels
            }
        });

        dialog.show();
    }

    private void handleDialogImageClick(int which) {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), StartPlayActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("select_color", which);
        startActivity(i);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume");
            /*val getData =
                    getSharedPreferences("StrategoPlayer", Context.MODE_PRIVATE);
            if (getData.getBoolean("RESTART", false)) {
                finish()
                val intent = Intent(this, MainActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val editor = getData.edit()
                editor.putBoolean("RESTART", false)
                editor.apply()
                startActivity(intent)
            }*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode:" + requestCode +  " resultCode:" + resultCode);
        if(resultCode == 1){
            Log.i(TAG, "finish and restart");
            Intent intent = new Intent(this, StrategoPortraitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("RESTART", true);
            startActivity(intent);
        }
    }
}
