package fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facetoface.speechapp.R;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import utility.DatabaseHandler;
import utility.PlayGifView;

import static android.app.Activity.RESULT_OK;
/**
 * Created by Anu on 22/04/17.
 */
public class FragmentAsk extends Fragment implements TextToSpeech.OnInitListener,AIListener {

    private TextView questionValueIS,answerValueIS;
    private String string_questionValueIS,string_answerValueIS;
    private View view;
    private ImageView voiceButtonAsk;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    DatabaseHandler db ;
    private TextToSpeech ttsAsk;
    private PlayGifView playGifView;
    private int countMicimg = 0;
    private RelativeLayout Ask_relativeLayout;

    private TextToSpeech tts;
    private TextToSpeech t2s;
    private AIService aiService;

    public FragmentAsk() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_ask, container, false);

        initVoiceModule();
        tts = new TextToSpeech(getActivity(), null);

        ttsAsk = new TextToSpeech(getActivity(),this);
        //get gif img
        //playGifView = (PlayGifView) view.findViewById(R.id.viewGif);


        questionValueIS = (TextView)view.findViewById(R.id.questionTxtView);
        answerValueIS = (TextView)view.findViewById(R.id.anserTxtView);
         voiceButtonAsk = (ImageView) view.findViewById(R.id.imageView2);
        Ask_relativeLayout = (RelativeLayout) view.findViewById(R.id.askRelative);
        /*voiceButtonAsk.setVisibility(View.VISIBLE);
        playGifView.setVisibility(View.GONE);
*/
        Ask_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("relative called","called -");
            }
        });
      /*  playGifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countMicimg == 0){
                    playGifView.setImageResource(R.drawable.ic_gif_mic);
                    startVoiceInput();
                    countMicimg = 1;
                }else {
                    playGifView.setVisibility(View.GONE);
                    voiceButtonAsk.setVisibility(View.VISIBLE);
                    countMicimg = 0;
                }

            }
        });*/
        db = new DatabaseHandler(getActivity());
        voiceButtonAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startVoiceInput();
                voiceButtonAsk.setImageResource(R.drawable.micro_gray);
                startVoiceListening();


                /*
                if(countMicimg == 0){
                    playGifView.setVisibility(View.VISIBLE);
                    playGifView.setImageResource(R.drawable.ic_gif_mic);

                    countMicimg = 1;
                }*//*else   if(countMicimg == 0){
                    playGifView.setVisibility(View.VISIBLE);
                    playGifView.setImageResource(R.drawable.ic_gif_mic);
                    startVoiceInput();
                    countMicimg = 1;
                }*//*else {
                    playGifView.setVisibility(View.GONE);
                    voiceButtonAsk.setVisibility(View.VISIBLE);
                    countMicimg = 0;
                }*/
            }
        });
        return view;
    }
    private void startVoiceInput() {
        Log.e("alert box is ","alrt box is ");
        voiceButtonAsk.setVisibility(View.VISIBLE);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(20));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your question to search in Data Base.");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    questionValueIS.setVisibility(View.VISIBLE);
                    questionValueIS.setText(result.get(0));

                    Log.e("result.get(0) ==",";;;;"+result.get(0));

                    //-----------------------
                    //Toast.makeText(getActivity(), "question is "+questionValueIS.getText().toString(), Toast.LENGTH_SHORT).show();
                    db.getQuestionAnswer(result.get(0));
                    string_answerValueIS = db.answerIs_DB;
                    Log.e("text from voice is ","voice is "+string_answerValueIS);
                    //Toast.makeText(getActivity(), ""+db.answerIs_DB, Toast.LENGTH_SHORT).show();
                    if(string_answerValueIS != null){
                        answerValueIS.setVisibility(View.VISIBLE);
                        answerValueIS.setText(string_answerValueIS);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                 speakOut(string_answerValueIS);
                            }
                        },500);
                    }else {
                        string_answerValueIS = "No Answer for this question !!!";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                answerValueIS.setVisibility(View.GONE);
                                speakOut(string_answerValueIS);
                            }
                        },300);
                    }

                  /*  playGifView.setVisibility(View.GONE);
                    voiceButtonAsk.setVisibility(View.VISIBLE);*/

                }
                break;
            }
        }
    }

    private void speakOut(String string_answerValueIS) {
        ttsAsk.speak(string_answerValueIS, TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void onInit(int status) {
        Log.e("alert box is ","alrt box is second");
            if(status == TextToSpeech.SUCCESS){
                int result = ttsAsk.setLanguage(Locale.US);
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e("TTS", "This Language is not supported");
                }else {

                }
            }
    }


    @Override
    public void onResult(AIResponse result) {
        //do everything here
        Result response = result.getResult();
        //ArrayList<String> result1 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        questionValueIS.setVisibility(View.VISIBLE);
        questionValueIS.setText(response.getResolvedQuery());

        Log.e("result.get(0) ==",";;;;"+response.getResolvedQuery());

        //-----------------------
        //Toast.makeText(getActivity(), "question is "+questionValueIS.getText().toString(), Toast.LENGTH_SHORT).show();
        db.getQuestionAnswer(response.getResolvedQuery());
        string_answerValueIS = db.answerIs_DB;
        Log.e("text from voice is ","voice is "+string_answerValueIS);
        //Toast.makeText(getActivity(), ""+db.answerIs_DB, Toast.LENGTH_SHORT).show();
        if(string_answerValueIS != null){
            answerValueIS.setVisibility(View.VISIBLE);
            answerValueIS.setText(string_answerValueIS);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speakOut(string_answerValueIS);
                }
            },500);
        }else {
            string_answerValueIS = "No Answer for this question !!!";
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    answerValueIS.setVisibility(View.GONE);
                    speakOut(string_answerValueIS);
                }
            },300);
        }


        //till
    }

    @Override
    public void onError(AIError error) {
        voiceButtonAsk.setImageResource(R.drawable.micro_white);
    }

    /**
     * Event fires every time sound level changed. Use it to create visual feedback. There is no guarantee that this method will
     * be called.
     *
     * @param level the new RMS dB value
     */
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
        voiceButtonAsk.setImageResource(R.drawable.micro_white);
    }

    /**
     * Event fires when recognition engine finish listening
     */
    @Override
    public void onListeningFinished() {
        voiceButtonAsk.setImageResource(R.drawable.micro_white);
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

    // Start voice recognition
    private void startVoiceListening() {
        aiService.startListening();
        Animation zoomOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.zoomin);
        voiceButtonAsk.startAnimation(zoomOut);
    }
}