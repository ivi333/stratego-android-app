package de.arvato.stratego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.IOException;
import java.io.InputStream;

import de.arvato.stratego.colyseum.ColyseusManager;
import de.arvato.stratego.colyseum.GameState;
import de.arvato.stratego.colyseum.interfaces.JoinRoomCallback;
import de.arvato.stratego.model.PlayerView;
import de.arvato.stratego.services.AudioService;
import io.colyseus.Room;

public class StrategoPortraitActivity extends Activity {

    public static final String TAG = "StrategoPortActivity";

    private Button playButton, instructionsButton, playOnlineButton, profileButton, settingsButton;

    private Button btnCreateRoom, btnJoinRoom, btnCreatePrivateRoom;
    private EditText editTextRoomId;

    private ImageView blueMultiPlayerSelector;
    private ImageView redMultiPlayerSelector;

    private MutableLiveData<PlayerView> playerReadyLive;

    private AudioService audioService;
    private boolean bound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stratego_main);

        if (getIntent().getBooleanExtra("RESTART", false)) {
            finish();
            Intent intent = new Intent(this, StrategoPortraitActivity.class);
            startActivity(intent);
        }

        playButton = findViewById(R.id.PlayButton);
        playOnlineButton = findViewById(R.id.PlayOnlineButton);
        instructionsButton = findViewById(R.id.instructionsButton);
        settingsButton = findViewById(R.id.SettingsButton);

        playButton.setOnClickListener(v -> initDialogSelectColor ());

        playOnlineButton.setOnClickListener(v -> initMultiplayerDialogSelectColor ());

        instructionsButton.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setClass(getApplicationContext(), InstructionsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        });

        settingsButton.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setClass(getApplicationContext(), SettingsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        });

        // Start the audio service
        Intent intent = new Intent(this, AudioService.class);
        startService(intent);

        // Bind to the audio service
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
                audioService = binder.getService();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        }, Context.BIND_AUTO_CREATE);

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

    private void initMultiplayerDialogSelectColor() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_multiplayerplayer_selector, null);

        playerReadyLive = new MutableLiveData<>();

        blueMultiPlayerSelector = dialogView.findViewById(R.id.blueMultiPlayerSelector);
        redMultiPlayerSelector = dialogView.findViewById(R.id.redMultiPlayerSelector);
        btnCreateRoom = dialogView.findViewById(R.id.btnCreateRoom);
        btnCreatePrivateRoom = dialogView.findViewById(R.id.btnCreatPrivateeRoom);
        btnJoinRoom = dialogView.findViewById(R.id.btnJoinRoom);
        editTextRoomId = dialogView.findViewById(R.id.editTextRoomId);

        loadImageFromAssets (blueMultiPlayerSelector, "bandera_blue.png");
        loadImageFromAssets (redMultiPlayerSelector, "bandera_red.png");

        blueMultiPlayerSelector.setSelected(false);
        redMultiPlayerSelector.setSelected(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("MultiPlayer Game");

        btnJoinRoom.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                String roomId = editTextRoomId.getText().toString().trim();
                Log.d(TAG, "Joining to roomID:" + roomId);
                 ColyseusManager colyseusManager = ColyseusManager.getInstance(StrategoConstants.ENDPOINT_COLYSEUS);
                 colyseusManager.setPlayerReadyLive(playerReadyLive);
                 colyseusManager.join(getPreferredUserName(), 0, roomId, new JoinRoomCallback() {
                     @Override
                     public void onSuccess(Room<GameState> room) {
                         runOnUiThread(() -> {
                             Toast.makeText(getApplicationContext(), "Successfully joined the room", Toast.LENGTH_SHORT).show();
                         });
                     }

                     @Override
                     public void onError(Exception e) {
                         runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                         });
                     }
                 });

             }
        });


        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int which = -1;
                if (blueMultiPlayerSelector.isSelected()) {
                    which = 1;
                } else if (redMultiPlayerSelector.isSelected()) {
                    which = 0;
                }
                if (which == -1) {
                    Toast.makeText(getApplicationContext(), "Select a color first.!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ColyseusManager colyseusManager = ColyseusManager.getInstance(StrategoConstants.ENDPOINT_COLYSEUS);
                colyseusManager.joinOrCreate(getPreferredUserName(), which);
                colyseusManager.setPlayerReadyLive(playerReadyLive);
            }
        });

        btnCreatePrivateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int which = -1;
                if (blueMultiPlayerSelector.isSelected()) {
                    which = 1;
                } else if (redMultiPlayerSelector.isSelected()) {
                    which = 0;
                }
                if (which == -1) {
                    Toast.makeText(getApplicationContext(), "Select a color first.!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ColyseusManager colyseusManager = ColyseusManager.getInstance(StrategoConstants.ENDPOINT_COLYSEUS);
                colyseusManager.createPrivateRoom(getPreferredUserName(), which);
                colyseusManager.setPlayerReadyLive(playerReadyLive);
            }
        });

        blueMultiPlayerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle left image click
                handleMultiPlayerDialogImageClick(StrategoConstants.BLUE, blueMultiPlayerSelector);
            }
        });

        redMultiPlayerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle right image click
                handleMultiPlayerDialogImageClick(StrategoConstants.RED, redMultiPlayerSelector);
            }

        });

        playerReadyLive.observeForever(new Observer<>() {
            @Override
            public void onChanged(PlayerView playerView) {
                Log.d(TAG, "player Ready Live:" + playerView.toString());
                runOnUiThread(() -> {
                    stopAudio();
                    Intent i = new Intent();
                    i.setClass(getApplicationContext(), StartPlayActivity.class);
                    i.putExtra("select_color", playerView.getColor());
                    i.putExtra("multiplayer", true);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                });
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleMultiPlayerDialogImageClick(int which, ImageView playerSelector) {
        blueMultiPlayerSelector.setSelected(false);
        redMultiPlayerSelector.setSelected(false);
        playerSelector.setSelected(true);
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
        stopAudio ();
        Intent i = new Intent();
        i.setClass(getApplicationContext(), StartPlayActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("select_color", which);
        i.putExtra("multiplayer", false);
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

    public String getPreferredUserName () {
        SharedPreferences sharedPreferences  = getSharedPreferences(StrategoConstants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SettingsActivity.KEY_NAME, "Hidden_Name");
    }

    /*public boolean isMusicActivated () {
        SharedPreferences sharedPreferences  = getSharedPreferences(StrategoConstants.PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SettingsActivity.KEY_MUSIC, false);
    }*/

    private void stopAudio () {
        Intent intent = new Intent(this, AudioService.class);
        stopService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (bound && audioService != null) {
            audioService.startAudio(); // Restart audio when activity resumes (if needed)
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (bound) {
            if (audioService != null) {
                audioService.stopAudio(); // Stop audio when activity is paused
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (bound) {
            unbindService(new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            });
            bound = false;
        }
    }
}
