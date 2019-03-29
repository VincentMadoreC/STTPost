package ca.canada.sttpost;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TextZoomActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnConfirm;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_zoom);

        editText = findViewById(R.id.editText);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        // Fills the EditText with the text from the MainActivity
        Intent intent = getIntent();
        editText.setText(intent.getStringExtra("EXTRA_VOICE_INPUT"));

        // Tap the confirm button to save the changes
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        // Tap the cancel button to exit without saving changes
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void confirm() {
        Intent data = new Intent();
        String newText = editText.getText().toString();
        data.setData(Uri.parse(newText));
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void cancel() {
        finish();
    }
}
