package activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.facetoface.speechapp.R;

import java.util.ArrayList;

import ModelClasses.DB_QA_Model;
import utility.DatabaseHandler;

/**
 * Created by SSB on 13-03-2018.
 */

public class SingleDB_Data extends AppCompatActivity {

    private EditText questionTxt,answerTxt;
    private String string_question,string_anser,string_id;
    private Button updateBtn ;
    private ArrayList<DB_QA_Model> alUpdateList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.que_ans_item_activity);
        //find view
        questionTxt = findViewById(R.id.questionValueIS);
        answerTxt = findViewById(R.id.anwserValue);
        updateBtn = findViewById(R.id.updateData);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        string_question = bundle.getString("QKEY");
        string_anser = bundle.getString("AKEY");
        string_id = bundle.getString("idKEY");
        Log.e("q and a value si ","--->"+string_question+"\t"+string_anser+"\tid "+string_id);
        //set value intent in txt
        questionTxt.setText(string_question);
        answerTxt.setText(string_anser);

        //click event on button
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                string_question = questionTxt.getText().toString();
                string_anser = answerTxt.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put("question",string_question);
                cv.put("answer",string_anser);

                DatabaseHandler db = new DatabaseHandler(SingleDB_Data.this);
                boolean updateStatus = db.updateDB(string_id,cv);
                Log.e("boolean is ","update is "+updateStatus);
                //db.notify();
                db.getAllContacts();
               onBackPressed();
              /*  alUpdateList = DatabaseHandler.questionAnserArraylist;
                if(!alUpdateList.isEmpty() && alUpdateList.size() != 0){
                    for (int i =0;i<alUpdateList.size();i++){
                        String IDis = alUpdateList.get(i).getId();
                        Log.e("id is ","id"+IDis);
                    }
                }*/
                //db.updateDB()
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
