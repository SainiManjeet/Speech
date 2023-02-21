package fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facetoface.speechapp.R;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import utility.PlayGifView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Anu on 22/04/17.
 */


public class FragmentRepeat extends Fragment implements TextToSpeech.OnInitListener, View.OnClickListener, AIListener {

    final static int RQS_OPEN_AUDIO_MP3 = 1;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageView miceImgBtn, repeatImgBtn, cancelImgBtn;
    private ImageView shareBtn;
    private TextToSpeech tts;
    private String string_miceTxtData = null;
    private EditText miceTextDataET;
    private View view;
    private File file = null, root, dir;
    private int miceImgCount = 0;
    private PlayGifView playGifViewRepeat;
    private int countMicimg = 0;
    private SpeechRecognizer recognizer;

    private TextToSpeech t2s;
    private AIService aiService;

    public FragmentRepeat() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_repeat, container, false);

        initVoiceModule();

        tts = new TextToSpeech(getActivity(), null);


        //TODO image view id
        miceImgBtn = view.findViewById(R.id.miceImgView);
        repeatImgBtn = view.findViewById(R.id.repeatImgView);
        cancelImgBtn = view.findViewById(R.id.cancelImgView);
        //TODO edit text view id
        miceTextDataET = view.findViewById(R.id.editTextRepeat);
        //TODO button view id
        shareBtn = view.findViewById(R.id.shareButton);
        //
        //get gif img
       /* playGifViewRepeat = (PlayGifView) view.findViewById(R.id.viewGifrepeat);
        playGifViewRepeat.setVisibility(View.GONE);
        miceImgBtn.setVisibility(View.VISIBLE);*/
        //add to click listener
        miceImgBtn.setOnClickListener(this);
        repeatImgBtn.setOnClickListener(this);
        cancelImgBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);


        return view;

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelImgView:
                miceTextDataET.setText("");
                break;
            case R.id.repeatImgView:
                //get edit text data to string
                Log.e("text is ", "\t" + string_miceTxtData);
                string_miceTxtData = miceTextDataET.getText().toString();
                if (!string_miceTxtData.equalsIgnoreCase("") && string_miceTxtData != null) {
                    repatText(string_miceTxtData);
                } else {
                    Toast.makeText(getActivity(), "No Data For Repeating !!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.miceImgView:
                miceImgCount = 1;
                // repartTxtToVoice();
                miceImgBtn.setImageResource(R.drawable.micro_gray);
                startVoiceListening();
                break;

           /* case R.id.shareButton:
                testSpeechModule();
                break;*/
            case R.id.shareButton:
                //get edit text data to string
                string_miceTxtData = miceTextDataET.getText().toString();

                if (!string_miceTxtData.equalsIgnoreCase("") && miceImgCount == 0) {
                    String string_miceTxtDataIS = miceTextDataET.getText().toString();
                    root = android.os.Environment.getExternalStorageDirectory();
                    Log.e("path is ", "===" + root.getAbsolutePath());
                    dir = new File(root.getAbsolutePath() + "/download");
                    //dir = new File(root.getAbsolutePath());
                    dir.mkdirs();
                    Log.e("dir is ", "-----" + dir.exists());
                    String audioIS = System.currentTimeMillis() + "_voice_audio.mp3";
                    file = new File(dir, "voice_audio.mp3");
                    Log.e("file is ", "fileis " + file);

                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        int test = tts.synthesizeToFile(string_miceTxtDataIS, null, file, "file");
                        Log.e("int value if", "====" + test);
                    } else {
                        Log.e("int value else", "====");
                    }

                    //==========================
                    Log.e("not count clicked", "----" + miceImgCount);
                    Uri uri = Uri.parse(String.valueOf(file));
                    if (file != null) {
                        Log.e("here1===", "here1===");
                        uri = Uri.fromFile(file);
                        Log.e("uri:", "" + uri);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setData(uri);
                        share.setType("audio/mp3");
                        share.putExtra(Intent.EXTRA_EMAIL, new String[]{"xxx@example.com"});
                        share.putExtra(Intent.EXTRA_SUBJECT, "subject here");
                        share.putExtra(Intent.EXTRA_TEXT, "Your Speak Audio");
                        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Your Audio..."));
                    } else {
                        Log.e("file is ", "not found or null");
                    }
                } else if (miceImgCount > 0) {
                    Log.e("not count 2 clicked", "----" + miceImgCount);
                    Uri uri = Uri.parse(String.valueOf(file));
                    if (file != null) {
                        Log.e("here2===", "here2===");
                        uri = Uri.fromFile(file);
                        Log.e("uri:", "" + uri);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setData(uri);
                        share.setType("audio/mp3");
                        share.putExtra(Intent.EXTRA_EMAIL, new String[]{"xxx@example.com"});
                        share.putExtra(Intent.EXTRA_SUBJECT, "subject here");
                        share.putExtra(Intent.EXTRA_TEXT, "Your Speak Audio");
                        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Your Audio..."));
                    } else {
                        Log.e("file is ", "not found or null");
                    }
                } else {
                    Toast.makeText(getActivity(), "No Data For Sending !!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void repatText(String string_miceTxtData) {
        tts.speak(string_miceTxtData, TextToSpeech.QUEUE_FLUSH, null);
        tts.setSpeechRate((float) 0.7);


    }

    private void repartTxtToVoice() {
        //count = 0 + count;
        Intent repeat = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        repeat.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        repeat.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        repeat.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now ");
        try {
            startActivityForResult(repeat, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                //recognizer.cancel();
                //count = count+1;
                // Log.e("count is ","---"+count);
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    miceTextDataET.setText(result.get(0));
                    Log.e("speak txt is ", "data is " + result.get(0));

                /*    countMicimg = 0;
                    //Toast.makeText(getActivity(), "count is "+countMicimg, Toast.LENGTH_SHORT).show();
                    playGifViewRepeat.setVisibility(View.GONE);
                    miceImgBtn.setVisibility(View.VISIBLE);*/

                    root = android.os.Environment.getExternalStorageDirectory();
                    Log.e("root is ", "---" + root);
                    Log.e("path is ", "===" + root.getAbsolutePath());
                    dir = new File(root.getAbsolutePath() + "/download");
                    //dir = new File(root.getAbsolutePath());
                    dir.mkdirs();
                    Log.e("dir is ", "-----" + dir.exists());
                    String audioIS = System.currentTimeMillis() + "_voice_audio.mp3";
                    file = new File(dir, "voice_audio.mp3");
                    Log.e("file is ", "fileis " + file);


                    int test = 0;
                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        test = tts.synthesizeToFile(result.get(0), null, file, "file");
                        Log.e("int value if", "====" + test);
                    } else {
                        Log.e("int value else", "====" + test);
                    }

                }
            }
            break;
        }
    }

    @Override
    public void onDestroyView() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            Log.e("google speech cloase ", "=======close");
        }
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("google speech cloase ", "=======stop");
    }


    private void testSpeechModule() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.google.android.voicesearch", "com.google.android.voicesearch.VoiceSearchPreferences");
        // intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.voicesearch.VoiceSearchPreferences");
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

        if (response.getResolvedQuery() != null) {
Log.e("Result is","Result is"+response.getResolvedQuery());
            miceTextDataET.setText(response.getResolvedQuery());
            t2s.speak(response.getResolvedQuery(), TextToSpeech.QUEUE_FLUSH, null, null);

        }
    }


    @Override
    public void onError(AIError error) {
        miceImgBtn.setImageResource(R.drawable.micro_white);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    /**
     * Event fires when recognition engine start listening
     */
    @Override
    public void onListeningStarted() {

    }

    /**
     * Event fires when recognition engine cancel listening
     */
    @Override
    public void onListeningCanceled() {
        miceImgBtn.setImageResource(R.drawable.micro_white);
    }

    /**
     * Event fires when recognition engine finish listening
     */
    @Override
    public void onListeningFinished() {
        miceImgBtn.setImageResource(R.drawable.micro_white);
    }

    // Start voice recognition
    private void startVoiceListening() {
        aiService.startListening();
        Animation zoomOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoomin);
        miceImgBtn.startAnimation(zoomOut);
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
}

