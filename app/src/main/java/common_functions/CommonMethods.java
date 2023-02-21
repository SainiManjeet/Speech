package common_functions;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;



public class CommonMethods extends Application {

    public static final String TAG = CommonMethods.class.getSimpleName();
    private static CommonMethods instance;
    public final int LIGHT = 0, MEDIUM = 1, BOLD = 2;
    AlertDialog alert11;
    ArrayList <String> customFonts = new ArrayList <>(Arrays.asList(
            "HelveticaNeueRE.ttf",
            "HelveticaNeuBold.ttf",
            "Verdanab.ttf"
    ));

    public CommonMethods() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static synchronized CommonMethods getInstance() {
        if (instance == null) {
            instance = new CommonMethods();
        }
        return instance;
    }
/*
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        // Log.e("in_common_method", "onCreate");
        instance = this;


    }


    public static int getColor(Context context, int id) {
        if (isAndroid5()) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    /**
     * method to check if android version is lollipop
     *
     * @return this return value
     */
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
