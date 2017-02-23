package com.hbbmobile.bsnapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hbbmobile.bsnapp.discount.AdapterDiscount;
import com.hbbmobile.bsnapp.discount.ModelDis;

import java.util.ArrayList;
import java.util.List;

public class DiscountFragment extends Fragment {

    private ListView lv;
    private AdapterDiscount adapterDiscount;
    private List<ModelDis> modelDises = new ArrayList<ModelDis>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_discount, container, false);

        lv = (ListView) rootView.findViewById(R.id.lv);
        ModelDis data = new ModelDis("Villa Q.1 giảm giá 10% khi dăng ký thành viên BSN", "Nov 10,2016|2:20");
        ModelDis data1 = new ModelDis("Villa Q.2 giảm giá 10% khi dăng ký thành viên BSN1", "Nov 10,2016|2:21");
        ModelDis data2 = new ModelDis("Villa Q.3 giảm giá 10% khi dăng ký thành viên BSN1", "Nov 10,2016|2:22");
        modelDises.add(data);
        modelDises.add(data1);
        modelDises.add(data2);
        adapterDiscount = new AdapterDiscount(getActivity(), modelDises);
        lv.setAdapter(adapterDiscount);

        return rootView;
    }
}
