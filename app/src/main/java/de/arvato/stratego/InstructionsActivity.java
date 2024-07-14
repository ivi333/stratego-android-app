package de.arvato.stratego;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        TextView textViewInstructions = findViewById(R.id.textViewInstructions);

        String instructions = getString(R.string.game_instructions);
        textViewInstructions.setText(getHtmlFormattedText(instructions));
        textViewInstructions.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @SuppressWarnings("deprecation")
    private Spanned getHtmlFormattedText(String htmlText) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(htmlText);
        }
    }

}