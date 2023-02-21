package viewpagerexample;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facetoface.speechapp.R;
import com.google.gson.JsonElement;

import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static viewpagerexample.MainActivity.MY_PERMISSIONS_REQUEST;


public class SpeechDemo extends AppCompatActivity implements AIListener{

    private Button listenButton;
    private TextView resultTextView;
    private AIService aiService;
    private TextToSpeech t2s;

    @BindView(R.id.linear_main)
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_test);
        ButterKnife.bind(this, this);
        checkPermissionIS();


        listenButton = (Button) findViewById(R.id.btn_listen);
        resultTextView = (TextView) findViewById(R.id.response);

        final AIConfiguration config = new AIConfiguration("b92ec0bc3ac149c8935cd555a25016bb",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        t2s=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    t2s.setLanguage(Locale.ENGLISH);
                }
            }
        });


    }
    @OnClick({R.id.linear_main})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_main:
                Log.e("Play video", "click");
                finish();
                startMainActivity();
                break;

        }
    }
    private void startMainActivity() {
        Intent in = new Intent(SpeechDemo.this,MainActivity.class);
        startActivity(in);
    }

    public void listenButtonOnClick(final View view) {
        aiService.startListening();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResult(AIResponse result) {
        Result response = result.getResult();

        // Get parameters
        String parameterString = "";
        if (response.getParameters() != null && !response.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : response.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        if(response.getResolvedQuery()!=null) {
            // Show results in TextView.
            resultTextView.setText("Query:" + response.getResolvedQuery() +
                    "\nAction: " + response.getAction() +
                    "\nParameters: " + parameterString);
            t2s.speak(response.getResolvedQuery(), TextToSpeech.QUEUE_FLUSH, null, null);
            //  t2s.speak(response.getResolvedQuery(), TextToSpeech.QUEUE_FLUSH, null);//Depricated
        }
    }

    @Override
    public void onError(AIError error) {
        resultTextView.setText(error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }
    @Override
    public void onListeningFinished() {

    }

    private boolean checkPermissionIS() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("permission is necessary!!!");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SpeechDemo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(SpeechDemo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST);
            }
            return false;
        } else {
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //code for deny
                    Toast.makeText(SpeechDemo.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    private void startNextActivity() {
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SpeechDemo.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
