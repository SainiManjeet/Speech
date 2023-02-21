package utility;

/**
 * Created by SSB on 12-03-2018.
 */

public class Contact {

    //private variables
    int _id;
    String question;
    String answer;

    // Empty constructor
    public Contact(){

    }
    // constructor
    public Contact(int id, String name, String answer){
        this._id = id;
        this.question = name;
        this.answer = answer;
    }

  // constructor
    public Contact(String name, String answer){
        this.question = name;
        this.answer = answer;
    }


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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
}