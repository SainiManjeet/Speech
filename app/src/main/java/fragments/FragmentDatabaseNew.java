package fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.facetoface.speechapp.R;
import java.util.ArrayList;
import java.util.List;

import adapter.QuestionsAnsAdapter;
import adapter.TextWatcherAdapter;
import ModelClasses.DB_QA_Model;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common_functions.RecyclerViewFastScroller;
import utility.DatabaseHandler;

public class FragmentDatabaseNew extends Fragment {
    public FragmentTransaction fragmentTransaction;
    public List<DB_QA_Model> beanList;
    @BindView(R.id.database_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_input)
    TextInputEditText searchInput;
    @BindView(R.id.clear_btn_search_view)
    AppCompatImageView clearBtn;
    QuestionsAnsAdapter resturantAdapter;
    private View view;
    private ArrayList<DB_QA_Model> arrayList_QA;



    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    public FragmentDatabaseNew() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       //view = inflater.inflate(R.layout.fragment_databasew_new, container, false);
       view = inflater.inflate(R.layout.custom_layout, container, false);
        ButterKnife.bind(this, view);
        fragmentTransaction = getFragmentManager().beginTransaction();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setDataOnList();

        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        searchInput.addTextChangedListener(new TextWatcherAdapter() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               try {
                   resturantAdapter.setString(s.toString());
                   Search(s.toString().trim());
               }
               catch (Exception e){
                   e.printStackTrace();
               }


            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    Log.e("afterTextChanged", "afterTextChanged");
                    clearData();

                }
            }
        });

        return view;
    }

    @OnClick({R.id.clear_btn_search_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_btn_search_view:
                clearSearchView();
                break;

        }
    }

    /**
     * method to clear/reset search view content
     */
    public void clearSearchView() {
        if (searchInput.getText() != null) {
            searchInput.setText("");
            clearData();
        }

    }

    /**
     * method to start searching
     *
     * @param string this is parameter of Search method
     */
    public void Search(String string) {

        final List<DB_QA_Model> filteredModelList;
        filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {
            resturantAdapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
        }
    }

    /**
     * method to filter the list
     *
     * @param query this is parameter of FilterList method
     * @return this for what method return
     */
    private List<DB_QA_Model> FilterList(String query) {
        query = query.toLowerCase();
        List<DB_QA_Model> countriesModelList = resturantAdapter.getCountries();
        final List<DB_QA_Model> filteredModelList = new ArrayList<>();
        for (DB_QA_Model countriesModel : countriesModelList) {
            final String name = countriesModel.getQuestion().toLowerCase();
            if (name.contains(query)) {
                filteredModelList.add(countriesModel);
            }
        }
        return filteredModelList;
    }


    private void setDataOnList() {

        beanList = new ArrayList<>();
        arrayList_QA = DatabaseHandler.questionAnserArraylist;

        Log.e("---DB--->", "size of database arraylist size is \t" + arrayList_QA.size());

        if (arrayList_QA.size() != 0) {

            resturantAdapter = new QuestionsAnsAdapter(getActivity(), arrayList_QA);
            recyclerView.setAdapter(resturantAdapter);

            //hh

            List<DB_QA_Model> list = arrayList_QA;
            resturantAdapter.setCountries(list);


            // set recycler view to fastScroller
            fastScroller.setRecyclerView(recyclerView);
            fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);


        }
    }

   private void clearData() {

        ArrayList<DB_QA_Model> arrayList_QANew = new ArrayList<>();

        arrayList_QANew = DatabaseHandler.questionAnserArraylist2;


       Log.e("---DATAis--->", "size of database arraylist size is \t" + arrayList_QANew.toString());
        Log.e("---DBMMMMMMKK--->", "size of database arraylist size is \t" + arrayList_QANew.size());

        if (arrayList_QANew.size() != 0) {
            resturantAdapter = new QuestionsAnsAdapter(getActivity(), arrayList_QANew);
            recyclerView.setAdapter(resturantAdapter);

            List<DB_QA_Model> list = arrayList_QANew;
            resturantAdapter.setCountries(list);
        }
    }


}
