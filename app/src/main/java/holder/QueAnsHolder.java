package holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facetoface.speechapp.R;

import adapter.QuestionsAnsAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import common_functions.CommonMethods;


public class QueAnsHolder extends RecyclerView.ViewHolder {

    public
    @BindView(R.id.dbIDTxt)
    TextView DB_questionName;
    public
    @BindView(R.id.dbTxt)
    TextView DB_id;

    public
    @BindView(R.id.queAnsCard)
    CardView cardView;

    public
    @BindView(R.id.deleteButtonTv)
    TextView deleteBtn;

    public
    @BindView(R.id.relativeLayout)
    RelativeLayout mainLayout;


    private CommonMethods commonMethods;

    public QueAnsHolder(View itemView, QuestionsAnsAdapter adapter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        commonMethods = CommonMethods.getInstance();
    }

    public void setCountryName(String countryN) {
        DB_questionName.setText(countryN);
    }
}