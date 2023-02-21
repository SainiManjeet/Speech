package ModelClasses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SSB on 13-03-2018.
 */

public class DB_QA_Model  {
    String id,question,answer;


    public DB_QA_Model(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public DB_QA_Model() {

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public DB_QA_Model(String id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }







}
