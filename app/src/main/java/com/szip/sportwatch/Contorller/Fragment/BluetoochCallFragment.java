package com.szip.sportwatch.Contorller.Fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.R;

/**
 * Created by Administrator on 2019/12/22.
 */

public class BluetoochCallFragment extends BaseFragment{

    private int flag = 0;
    private TextView text,text1;
    private ImageView imageView;

    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static BluetoochCallFragment newInstance(int flag){
        Bundle bundle = new Bundle();
        bundle.putInt("flag",flag);
        BluetoochCallFragment fragment = new BluetoochCallFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bluetooch_call;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        flag = getArguments().getInt("flag");
        text = getView().findViewById(R.id.text);
        text1 = getView().findViewById(R.id.text1);
        imageView = getView().findViewById(R.id.image);

        if (flag == 0){
            text.setText(getString(R.string.step1));
            text1.setText(getString(R.string.str1));
            if (getResources().getConfiguration().locale.getCountry().equals("CN")){
                imageView.setImageResource(R.mipmap.my_call_step1);
            }else{
                imageView.setImageResource(R.mipmap.my_call_step1_2);
            }
        }else {
            text.setText(getString(R.string.step2));
            text1.setText(getString(R.string.str2));
            if (getResources().getConfiguration().locale.getCountry().equals("CN")){
                imageView.setImageResource(R.mipmap.my_call_step2);
            }else{
                imageView.setImageResource(R.mipmap.my_call_step2_2);
            }

        }
    }
}
