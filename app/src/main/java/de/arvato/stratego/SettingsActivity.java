package de.arvato.stratego;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextName;
    private Button buttonSave;
    private SharedPreferences sharedPreferences;
    private static final String KEY_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextName = findViewById(R.id.editTextName);
        buttonSave = findViewById(R.id.buttonSave);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(StrategoConstants.PREFERENCES_NAME, MODE_PRIVATE);

        // Load saved name
        String savedName = sharedPreferences.getString(KEY_NAME, "");
        editTextName.setText(savedName);

        buttonSave.setOnClickListener(v -> {
            // Get the name from EditText
            String name = editTextName.getText().toString().trim();

            // Save name to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NAME, name);
            editor.apply();

            // Show a confirmation message
            Toast.makeText(SettingsActivity.this, "Name saved", Toast.LENGTH_SHORT).show();
        });
    }
}