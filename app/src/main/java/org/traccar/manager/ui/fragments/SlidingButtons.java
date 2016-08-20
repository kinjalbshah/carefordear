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
import android.widget.ImageButton;

import org.traccar.manager.R;

import java.util.ArrayList;



public class SlidingButtons extends Fragment {

    private ArrayList<ImageButton> buttonsList = null;
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

        ImageButton tmpButton = null;
        int counter = 1;


        while((tmpButton = (ImageButton) view.findViewWithTag("Button"+counter))!=null) {
            System.out.println(tmpButton);
            buttonsList.add(tmpButton);
            counter++;
        }

        int moveRatio = buttonsList.size()-1;
        for(ImageButton button: buttonsList)
        {
            //TranslateAnimation animation = new TranslateAnimation (0, 0, tmpButton.getY(), tmpButton.getY() + (counter * tmpButton.getHeight() + (counter * 5)));
            TranslateAnimation animation = new TranslateAnimation (0, 0, 0, 100 * moveRatio );
            animation.setDuration(1000);
            animation.setRepeatCount(0);
            animationList.add(animation);
            //counter++;
            moveRatio--;
        }

        ImageButton scroller =  (ImageButton) view.findViewWithTag("scroll");
        final TranslateAnimation scrollerAnimation = new TranslateAnimation (0, 0, 0, 300  );
        scrollerAnimation.setDuration(1000);

       // final ImageButton lastButton = (buttonsList.size() > 1) ? (ImageButton) (buttonsList.get(buttonsList.size()-1)):null;


            scroller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.startAnimation(scrollerAnimation);
                    int buttonIndex = 0;
                    for(TranslateAnimation animation : animationList)
                    {
                        ((ImageButton) buttonsList.get(buttonIndex)).startAnimation(animation);
                        buttonIndex++;
                    }
                }
            });


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
