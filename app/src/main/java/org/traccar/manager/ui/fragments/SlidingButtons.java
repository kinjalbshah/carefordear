package org.traccar.manager.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import org.traccar.manager.R;

import java.util.ArrayList;



public class SlidingButtons extends Fragment {

    private ArrayList buttonsList = null;
    private ArrayList<TranslateAnimation> animationList = null;

    public SlidingButtons() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sliding_buttons, container, false);



        buttonsList = new ArrayList();
        animationList = new ArrayList<TranslateAnimation>();

        Button tmpButton = null;
        int counter = 1;


        while((tmpButton = (Button) view.findViewWithTag("Button"+counter))!=null)
        {
            System.out.println(tmpButton);
            buttonsList.add(tmpButton);
            //TranslateAnimation animation = new TranslateAnimation (0, 0, tmpButton.getY(), tmpButton.getY() + (counter * tmpButton.getHeight() + (counter * 5)));
            TranslateAnimation animation = new TranslateAnimation (0, 0, 0, 100 * (counter-1));
            animation.setDuration(1000);
            animation.setRepeatCount(0);
            animationList.add(animation);
            counter++;
        }

        final Button lastButton = (buttonsList.size() > 1) ? (Button) (buttonsList.get(buttonsList.size()-1)):null;

        if(lastButton != null)
        {
            lastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    view.startAnimation(animTranslate);
//                    animTranslate1.setDuration(1500);
//                    ((Button) buttonsList.get(0)).startAnimation(animTranslate1);

                    int buttonIndex = 0;
                    for(TranslateAnimation animation : animationList)
                    {
                        ((Button) buttonsList.get(buttonIndex)).startAnimation(animation);
                        buttonIndex++;
                    }
                }
            });
        }

//        animTranslate.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//                if(lastButton != null)
//                lastButton.setY(buttonsList.size()*lastButton.getHeight());
//
//                ((Button) buttonsList.get(0)).setY(70);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });


        return view;
    }
}
