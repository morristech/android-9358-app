package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-6-20.
 */

public class TechPosterChoiceModelFragment extends BaseFragment {

    @Bind(R.id.img_select_model)
    RoundImageView imgselectedModel;
    @Bind(R.id.img_model_square)
    ImageView imgModelSquare;
    @Bind(R.id.img_model_circular)
    ImageView imgModelCircular;
    @Bind(R.id.img_model_flower)
    ImageView imgModelFlower;

    private List<View> mViews;
    private int selectedMode; //被选中的模型

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tech_poster_choice_poster_model, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mViews = new ArrayList<>();
        mViews.add(imgModelSquare);
        mViews.add(imgModelCircular);
        mViews.add(imgModelFlower);
        selectedMode = Constant.TECH_POSTER_FLOWER_MODEL;
        selectedModelView(imgModelFlower);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.img_model_square, R.id.img_model_circular, R.id.img_model_flower})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_model_square:
                selectedMode = Constant.TECH_POSTER_SQUARE_MODEL;
                selectedModelView(imgModelSquare);
                Glide.with(getActivity()).load(R.drawable.img_poster_square_big).into(imgselectedModel);
                break;
            case R.id.img_model_circular:
                selectedMode = Constant.TECH_POSTER_CIRCULAR_MODEL;
                selectedModelView(imgModelCircular);
                Glide.with(getActivity()).load(R.drawable.img_poster_circular_big).into(imgselectedModel);
                break;
            case R.id.img_model_flower:
                selectedMode = Constant.TECH_POSTER_FLOWER_MODEL;
                selectedModelView(imgModelFlower);
                Glide.with(getActivity()).load(R.drawable.img_poster_flower_big).into(imgselectedModel);
                break;
        }
    }

    private void selectedModelView(View viewSelect) {
        for (View view : mViews) {
            view.setSelected(false);
        }
        viewSelect.setSelected(true);
        ((EditTechPosterActivity)getActivity()).setSelectedModel(selectedMode);
    }


}
