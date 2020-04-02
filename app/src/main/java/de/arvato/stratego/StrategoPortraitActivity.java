package de.arvato.stratego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class StrategoPortraitActivity extends Activity {

    public static final String TAG = "StrategoPortActivity";

    private Button playButton, playOnlineButton, profileButton, settingsButton;

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
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), StartPlayActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            }
        });
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
