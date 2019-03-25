//package ca.canada.sttpost;
//
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.speech.RecognizerIntent;
//
//import java.util.Locale;
//
//import android.support.v7.app.AppCompatActivity;
//
//public class Speech {
//
//    private final int REQ_CODE_SPEECH_INPUT = 100;
//
//    public void askSpeechInput() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                "Hi speak something");
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//
//        }
//    }
//}
