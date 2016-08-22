package org.traccar.manager.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import org.traccar.manager.R;

import java.util.ArrayList;



public class SlidingButtons extends Fragment {

    private ArrayList<ImageButton> buttonsList = null;
    private ArrayList<TranslateAnimation> forwardAnimationList = null;
    private ArrayList<TranslateAnimation> reverseAnimationList = null;
    private ArrayList<Integer> yPixelPositionList = null;
    private ArrayList<Integer> yDPPositionList = null;
    private boolean buttonsExpanded = false;

    public SlidingButtons() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sliding_buttons, container, false);




        buttonsList = new ArrayList();
        yPixelPositionList = new ArrayList();
        yDPPositionList = new ArrayList();
        forwardAnimationList = new ArrayList<TranslateAnimation>();
        reverseAnimationList = new ArrayList<TranslateAnimation>();

        ImageButton tmpButton = null;
        int counter = 1;


        while((tmpButton = (ImageButton) view.findViewWithTag("Button"+counter))!=null) {
            System.out.println(tmpButton);
            buttonsList.add(tmpButton);
            counter++;
        }

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();



        int forwardTranslateRatio = buttonsList.size()-1;
        int reverseTranslateRatio = 0;
        for(ImageButton button: buttonsList)
        {
            int yStartPx = 0;//Math.round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            int yStartDP = ((forwardTranslateRatio*button.getHeight())+(forwardTranslateRatio*60));
            int yDeltaPx = Math.round((yStartDP) * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            yPixelPositionList.add(yDeltaPx+Math.round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));// relative margin
            yDPPositionList.add(yStartDP+10);
            Toast.makeText(getContext(), "From "+yStartPx +" to " +yDeltaPx, Toast.LENGTH_SHORT);
            TranslateAnimation forwardAnimation = //= new TranslateAnimation (0, 0, 0, 100 * forwardTranslateRatio );

            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, yStartPx, Animation.ABSOLUTE,yDeltaPx);

            forwardAnimation.setDuration(1000);
            forwardAnimation.setRepeatCount(0);
            forwardAnimationList.add(forwardAnimation);


            TranslateAnimation reverseAnimation =
                    new TranslateAnimation (Animation.RELATIVE_TO_SELF, 0,Animation.RELATIVE_TO_SELF,0, Animation.ABSOLUTE, 0,  Animation.ABSOLUTE, -yDeltaPx);
            reverseAnimation.setDuration(1000);
            reverseAnimation.setRepeatCount(0);
            reverseAnimation.setRepeatMode(TranslateAnimation.REVERSE);
            reverseAnimationList.add(reverseAnimation);
            forwardTranslateRatio--;
            reverseTranslateRatio++;
        }

        final ImageButton scroller =  (ImageButton) view.findViewWithTag("scroll");

        int scrollStartPx =  Math.round(scroller.getY() * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        final int scrollDeltaDP =  (buttonsList.size() * 95) ;

        final int scrollDeltaPx =   yPixelPositionList.get(0) + Math.round(((buttonsList.get(0).getHeight()) *  (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));
        Toast.makeText(getContext(), "From "+scrollStartPx +" to " +scrollDeltaPx, Toast.LENGTH_SHORT);


        final TranslateAnimation forwardScrollerAnimation =
                new TranslateAnimation (Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, scrollStartPx, Animation.ABSOLUTE, scrollDeltaPx  );
        //new TransateAnimation(0, Animation.RELATIVE)
        forwardScrollerAnimation.setDuration(1000);

        final TranslateAnimation reverseScrollerAnimation = //new TranslateAnimation (0, 0,  300,0  );
                new TranslateAnimation (Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE,0,  Animation.ABSOLUTE, -scrollDeltaPx  );
        reverseScrollerAnimation.setDuration(1000);
        //reverseScrollerAnimation.setRepeatMode(Animation.REVERSE);


        // final ImageButton lastButton = (buttonsList.size() > 1) ? (ImageButton) (buttonsList.get(buttonsList.size()-1)):null;


            scroller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int buttonIndex = 0;
                    if(buttonsExpanded)
                    {
                        view.startAnimation(reverseScrollerAnimation);
                        for(TranslateAnimation animation : reverseAnimationList)
                        {
                            ((ImageButton) buttonsList.get(buttonIndex)).startAnimation(animation);
                            buttonIndex++;
                        }

                        buttonsExpanded = false;
                    }
                    else
                    {
                        view.startAnimation(forwardScrollerAnimation);
                        for(TranslateAnimation animation : forwardAnimationList)
                        {
                            ((ImageButton) buttonsList.get(buttonIndex)).startAnimation(animation);
                            buttonIndex++;
                        }
                        buttonsExpanded = true;
                    }

                }
            });


        forwardScrollerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                scroller.setY(scrollDeltaDP);
                int buttonIndex = buttonsList.size()-1;
                for(int i=buttonIndex; buttonIndex >= 0; buttonIndex--) {
                    buttonsList.get(buttonIndex).setY(yPixelPositionList.get(buttonIndex));

                }

               // scroller.setY(yPosStart-20);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        reverseScrollerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                scroller.setY(60+30); //including the margin
                int buttonIndex = buttonsList.size()-1;
                for(int i=buttonIndex; buttonIndex >= 0; buttonIndex--) {
                    buttonsList.get(buttonIndex).setY(15);

                }

                // scroller.setY(yPosStart-20);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        return view;
    }
}
