package de.arvato.stratego;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextName;
    private Button buttonSave;
    private ToggleButton toggleMusic;
    private SharedPreferences sharedPreferences;
    public static final String KEY_NAME = "name";
    public static final String KEY_MUSIC = "music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextName = findViewById(R.id.editTextName);
        buttonSave = findViewById(R.id.buttonSave);
        toggleMusic = findViewById(R.id.toggleMusic);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(StrategoConstants.PREFERENCES_NAME, MODE_PRIVATE);

        // Load saved name
        String savedName = sharedPreferences.getString(KEY_NAME, "");
        editTextName.setText(savedName);

        boolean savedMusic = sharedPreferences.getBoolean(KEY_MUSIC, false);
        toggleMusic.setChecked(savedMusic);

        buttonSave.setOnClickListener(v -> {
            // Get the name from EditText
            String name = editTextName.getText().toString().trim();

            // Save name to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NAME, name);
            editor.apply();

            editor.putBoolean(KEY_MUSIC, toggleMusic.isChecked());
            editor.apply();

            // Show a confirmation message
            Toast.makeText(SettingsActivity.this, "Settings saved!", Toast.LENGTH_SHORT).show();
        });
    }
}