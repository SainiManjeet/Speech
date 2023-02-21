package utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



import java.util.ArrayList;
import java.util.List;

import ModelClasses.DB_QA_Model;

/**
 * Created by SSB on 12-03-2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
   public static Contact contact;
    //All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    public static String answerIs_DB = null;

    // Database Name
    private static final String DATABASE_NAME = "contactsManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";
    ////---------------
    public  static ArrayList<DB_QA_Model> questionAnserArraylist = new ArrayList<>();

//@M
    public  static ArrayList<DB_QA_Model> questionAnserArraylist2 = new ArrayList<>();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUESTION + " TEXT,"
                + KEY_ANSWER + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }
    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, contact.getQuestion()); // Contact Name
        values.put(KEY_ANSWER, contact.getAnswer()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        questionAnserArraylist.clear();

        questionAnserArraylist2.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.setAnswer(cursor.getString(1));
                contact.setQuestion(cursor.getString(2));
                Log.e("-------","q and a "+cursor.getColumnCount()+"\t"+cursor.getString(2));

                questionAnserArraylist.add(new DB_QA_Model(cursor.getString(0),cursor.getString(1),cursor.getString(2)));

                questionAnserArraylist2.add(new DB_QA_Model(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
                Log.e("=====","---- size of arraylist is "+questionAnserArraylist.size());
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }
    //TODO getting question answer from dabase with match condition
 public List<Contact> getQuestionAnswer(String question) {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
     Log.e("--q---","===is==="+question);
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+ KEY_QUESTION+" = '"+question+"'";
        Log.e("","return value of query is "+selectQuery.isEmpty());
        Log.e("","return value of query is "+selectQuery);
      SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     int count = cursor.getCount();

     if(count != 0){

         Log.e("count in condition "," is "+count);
         if (cursor.moveToFirst()) {
             do {
                 Log.e("cursore is ",""+cursor.getString(2));
                 answerIs_DB = cursor.getString(2);
                 Log.e("cursore is ",""+answerIs_DB);
                /*Contact contact = new Contact();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.setAnswer(cursor.getString(1));
                contact.setQuestion(cursor.getString(2));
                Log.e("-------","q and a "+cursor.getString(1)+"\t"+cursor.getString(2));
                questionAnserArraylist.add(new DB_QA_Model(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
                Log.e("=====","---- size of arraylist is "+questionAnserArraylist.size());
                // Adding contact to list
                contactList.add(contact);*/
             } while (cursor.moveToNext());
         }
     }
     else {
         answerIs_DB = null;
     }
     Log.e("count in else"," is "+count);
        // looping through all rows and adding to list
      /*  if (cursor.moveToFirst()) {
            do {
                Log.e("cursore is ",""+cursor.getString(2));
                answerIs_DB = cursor.getString(2);
                Log.e("cursore is ",""+answerIs_DB);
                *//*Contact contact = new Contact();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.setAnswer(cursor.getString(1));
                contact.setQuestion(cursor.getString(2));
                Log.e("-------","q and a "+cursor.getString(1)+"\t"+cursor.getString(2));
                questionAnserArraylist.add(new DB_QA_Model(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
                Log.e("=====","---- size of arraylist is "+questionAnserArraylist.size());
                // Adding contact to list
                contactList.add(contact);*//*
            } while (cursor.moveToNext());
        }*/

        // return contact list
        return contactList;
    }

    public boolean deleteSingleDB(String e) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.e("-----\t", "-----" + e);
        //db.execSQL("DELETE FROM contacts where id='" + e + "' ");
        db.delete(TABLE_CONTACTS,KEY_ID +"= ?",new String[]{e});
        db.close();

        return true;
    }


    public boolean updateDB(String string_id, ContentValues cv) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_CONTACTS,cv,KEY_ID + "= ?",new String[]{string_id});
        //String sql = ""
        return true;
    }
}
