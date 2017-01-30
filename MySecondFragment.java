package babarshamsi92.drawerapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import babarshamsi92.drawerapp.R;

/**
 * Created by Lenovo on 1/29/2017.
 */

public class MySecondFragment  extends Fragment{



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.my_second_fragment,container,false);


        return rootview;
    }


}
