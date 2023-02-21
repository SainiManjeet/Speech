package fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facetoface.speechapp.R;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import utility.Contact;
import utility.DatabaseHandler;
import utility.PlayGifView;

import static android.app.Activity.RESULT_OK;
import static viewpagerexample.MainActivity.MY_PERMISSIONS_REQUEST;


public class Fragment_tech extends Fragment implements TextToSpeech.OnInitListener, AIListener {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_SPEECH_INPUT_SECOND = 200;
    private View view;
    private EditText voiceFirst, voiceSec;
    private ImageView speakButtonFirst, speakButtonSec;
    private String firstAns = null, secAns = null;
    private Button nextbutton;
    private LinearLayout linearLayout;
    private TextToSpeech textToSpeech;
    private PlayGifView playGifViewQue, playGifViewAns;
    private int countMicimg = 0;

    private int varClickBtn = 0;

    private TextToSpeech t2s;
    private AIService aiService;

    public Fragment_tech() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_teach, container, false);

        checkPermissionIS();

       initVoiceModule();

        textToSpeech = new TextToSpeech(getActivity(), this);

        voiceFirst = view.findViewById(R.id.editText);
        voiceSec = view.findViewById(R.id.ans_et);

        speakButtonFirst = view.findViewById(R.id.imageView2);

        speakButtonSec = view.findViewById(R.id.ans_mic);

        nextbutton = view.findViewById(R.id.btn2);
        linearLayout = view.findViewById(R.id.linearQA);

        //get gif img
        //playGifViewQue = (PlayGifView) view.findViewById(R.id.viewGifTechQ);
        //playGifViewAns = (PlayGifView) view.findViewById(R.id.viewGifTechAns);
        //-------------------------------
        //playGifViewQue.setVisibility(View.GONE);
        //playGifViewAns.setVisibility(View.GONE);
        //speakButtonSec.setVisibility(View.VISIBLE);
        //speakButtonFirst.setVisibility(View.VISIBLE);

        Log.e("---", "\t" + voiceFirst);
        speakButtonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startVoiceInput(); // Working before


                varClickBtn = 0;

                speakButtonFirst.setImageResource(R.drawable.micro_gray);
                aiService.startListening();
                Animation zoomOut = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.zoomin);
                speakButtonFirst.startAnimation(zoomOut);

            }
        });
        speakButtonSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                varClickBtn = 1;
                speakButtonSec.setImageResource(R.drawable.micro_gray);
                aiService.startListening();
                Animation zoomOut = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.zoomin);
                speakButtonSec.startAnimation(zoomOut);
                //startVoiceInputSEC();


            }
        });
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDataBase();
                voiceFirst.setText("");
                voiceSec.setText("");
            }
        });
        return view;
    }

    private void SaveDataBase() {

        firstAns = voiceFirst.getText().toString();
        secAns = voiceSec.getText().toString();
        Log.e("===== answers ====", "first value " + firstAns + "\tsec ans\t" + secAns);
        DatabaseHandler db = new DatabaseHandler(getActivity());
        int i = 0;
        // Inserting Contacts
        if (!firstAns.equals("") && !secAns.equals("")) {

            Log.d("Insert: ", "Inserting ..");

            db.addContact(new Contact(i, firstAns, secAns));
            i = 1;

            // Reading all contacts
            Log.d("Reading: ", "Reading all contacts..");
            List<Contact> contacts = db.getAllContacts();

            for (Contact cn : contacts) {
                String log = "Id: " + cn.get_id() + " ,Name: " + cn.getAnswer() + " ,Phone: " + cn.getQuestion();
                // Writing Contacts to log
                Log.d("Name: ", log);
            }
            Toast.makeText(getActivity(), "Data Saved !!!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Question and Answer required !!!", Toast.LENGTH_SHORT).show();
        }


    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, Speak your question?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void startVoiceInputSEC() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, What is your answer?");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_SECOND);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void initVoiceModule() {
        final AIConfiguration config = new AIConfiguration("b92ec0bc3ac149c8935cd555a25016bb",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(getActivity(), config);
        aiService.setListener(this);
        t2s = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t2s.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceFirst.setText(result.get(0));

                    //=============
                    //countMicimg = 0;
                    //Toast.makeText(getActivity(), "count is "+countMicimg, Toast.LENGTH_SHORT).show();
                    //playGifViewQue.setVisibility(View.GONE);
                    //speakButtonFirst.setVisibility(View.VISIBLE);

                    speakOut();
                }
                break;
            }
            case REQ_CODE_SPEECH_INPUT_SECOND: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceSec.setText(result.get(0));

                    //=============
                    //countMicimg = 0;
                    //Toast.makeText(getActivity(), "count is "+countMicimg, Toast.LENGTH_SHORT).show();
                    //playGifViewAns.setVisibility(View.GONE);
                    //speakButtonSec.setVisibility(View.VISIBLE);
                    speakOut(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public void onDestroyView() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroyView();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // speakButtonFirst.setEnabled(true);
                //speakOut();

            }
        }
    }

    private void speakOut(String s) {

        if (s != null) {
            Log.e("text is ", "speak text is" + s);
            textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
            textToSpeech.setPitch((float) 0.6);
            textToSpeech.setSpeechRate((float) 1.5);
        }

    }

    private void speakOut() {

        String text = voiceFirst.getText().toString();
        if (text != null) {
            Log.e("text is ", "speak text is" + text);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            textToSpeech.setPitch((float) 0.6);
            textToSpeech.setSpeechRate((float) 1.5);
        }

    }

    private void startAnimation() {
        Animation zoomOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoom_out);
        speakButtonFirst.startAnimation(zoomOut);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResult(AIResponse result) {
        Result response = result.getResult();


        //here

        try{
            t2s=new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status!=TextToSpeech.ERROR){
                        t2s.setLanguage(Locale.ENGLISH);
                    }
                }
            });}
        catch (Exception e){
            e.printStackTrace();
        }

        //till
        // Get parameters
        String parameterString = "";
        if (response.getParameters() != null && !response.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : response.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        if (response.getResolvedQuery() != null) {
            // Show results in TextView.
            Log.e("REsult is:", "REsult=" +varClickBtn+"=>"+ result);
            if (varClickBtn == 0) {
                voiceFirst.setText(response.getResolvedQuery());
            } else {
                voiceSec.setText(response.getResolvedQuery());
            }

            /*resultTextView.setText("Query:" + response.getResolvedQuery() +
                    "\nAction: " + response.getAction() +
                    "\nParameters: " + parameterString);*/
            t2s.speak(response.getResolvedQuery(), TextToSpeech.QUEUE_FLUSH, null, null);
            //  t2s.speak(response.getResolvedQuery(), TextToSpeech.QUEUE_FLUSH, null);//Depricated
        }


    }


    @Override
    public void onError(AIError error) {
        // resultTextView.setText(error.toString());

        Log.e("error","error");
        speakButtonFirst.setImageResource(R.drawable.micro_white);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {
        Log.e("onListeningCanceled","onListeningCanceled");
        speakButtonFirst.setImageResource(R.drawable.micro_white);
    }

    @Override
    public void onListeningFinished() {
        Log.e("onListeningFinished","onListeningFinished");
        speakButtonSec.setImageResource(R.drawable.micro_white);
    }

    private boolean checkPermissionIS() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("permission is necessary!!!");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST);
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
                    Toast.makeText(getActivity(), "Please allow permission", Toast.LENGTH_SHORT).show();
                    //  finish();
                }
                break;
        }
    }
}