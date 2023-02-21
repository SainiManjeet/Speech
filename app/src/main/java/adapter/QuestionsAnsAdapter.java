package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facetoface.speechapp.R;

import java.util.ArrayList;
import java.util.List;

import activities.SingleDB_Data;
import ModelClasses.DB_QA_Model;
import common_functions.CommonMethods;
import common_functions.RecyclerViewFastScroller;
import holder.QueAnsHolder;
import utility.DatabaseHandler;

public class QuestionsAnsAdapter extends RecyclerView.Adapter<QueAnsHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    List<DB_QA_Model> items;
    private Context mContext;
    private CommonMethods commonMethods;
    private List<DB_QA_Model> mCountriesModel;

    private String SearchQuery;

    public QuestionsAnsAdapter(Context mContext, ArrayList<DB_QA_Model> items) {
        this.mContext = mContext;
        this.items = items;
        commonMethods = CommonMethods.getInstance();

    }


    @Override
    public QueAnsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View subscribeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_q_a_listitem, parent, false);
        QueAnsHolder holderItem = new QueAnsHolder(subscribeView, this);
        return holderItem;
    }

    @Override
    public void onBindViewHolder(QueAnsHolder holder, final int position) {

        final QueAnsHolder countriesViewHolder = (QueAnsHolder) holder;
        final DB_QA_Model countriesModel = this.items.get(position);

        countriesViewHolder.setCountryName(countriesModel.getQuestion());
        SpannableString countryName = SpannableString.valueOf(countriesModel.getQuestion());

        if (SearchQuery == null) {
            holder.DB_questionName.setText(countryName, TextView.BufferType.NORMAL);
        } else {
            int index = TextUtils.indexOf(countriesModel.getQuestion().toLowerCase(), SearchQuery.toLowerCase());
            if (index >= 0) {
                countryName.setSpan(new ForegroundColorSpan(CommonMethods.getColor(mContext, R.color.colorBlack)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                countryName.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            holder.DB_questionName.setText(countryName, TextView.BufferType.SPANNABLE);


        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                /**/

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setMessage("Are you sure to delete ?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCountriesModel.remove(mCountriesModel.get(position).getId());
                        DatabaseHandler db = new DatabaseHandler(mContext);
                        boolean delete = db.deleteSingleDB(mCountriesModel.get(position).getId());
                        Log.e("", "return value is " + delete);
                        notifyDataSetChanged();
                        // FragmentTransaction fragmentTransaction = FragmentDatabase.fragmentTransaction;
                        //fragmentTransaction.detach().attach(FragmentDatabase.this).commit();
                        db.getAllContacts();
                        Log.e("", "get data method called ");
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

            }
        });

       // Update database values
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, SingleDB_Data.class)
                        .putExtra("QKEY", mCountriesModel.get(position).getQuestion())
                        .putExtra("AKEY", mCountriesModel.get(position).getAnswer())
                        .putExtra("idKEY", mCountriesModel.get(position).getId())
                );

            }
        });

        //till
    }


    public void animateTo(List<DB_QA_Model> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<DB_QA_Model> newModels) {
        int arraySize = mCountriesModel.size();
        for (int i = arraySize - 1; i >= 0; i--) {
            final DB_QA_Model model = mCountriesModel.get(i);
            if (!newModels.contains(model)) {
                //removeItem(i);  // Do uncomment


            }
        }
    }

    private void applyAndAnimateAdditions(List<DB_QA_Model> newModels) {
        int arraySize = newModels.size();
        for (int i = 0, count = arraySize; i < count; i++) {
            final DB_QA_Model model = newModels.get(i);
            if (!mCountriesModel.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<DB_QA_Model> newModels) {
        int arraySize = newModels.size();
        for (int toPosition = arraySize - 1; toPosition >= 0; toPosition--) {
            final DB_QA_Model model = newModels.get(toPosition);
            final int fromPosition = mCountriesModel.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private DB_QA_Model removeItem(int position) {
        final DB_QA_Model model = mCountriesModel.remove(position);
        notifyItemRemoved(position);
        return model;
    }


    private void addItem(int position, DB_QA_Model model) {
        mCountriesModel.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final DB_QA_Model model = mCountriesModel.remove(fromPosition);
        mCountriesModel.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<DB_QA_Model> getCountries() {
        return items;
    }

    public void setCountries(List<DB_QA_Model> mCountriesModel) {
        this.mCountriesModel = mCountriesModel;
        notifyDataSetChanged();
    }

    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return items.size();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return mCountriesModel.size() > pos ? Character.toString(mCountriesModel.get(pos).getQuestion().charAt(0)) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}