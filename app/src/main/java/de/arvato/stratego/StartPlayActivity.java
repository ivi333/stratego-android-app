package de.arvato.stratego;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class StartPlayActivity extends StrategoActivity implements TextToSpeech.OnInitListener, GestureDetector.OnGestureListener {

//    private var mainLayout : RelativeLayout?=null;
// private val arrImages: Array<StrategoImageView?> = arrayOfNulls<StrategoImageView>(64)

    private StrategoView strategoView;
    private GestureDetector gestureDetector;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_play);

        strategoView = new StrategoView(this);

        gestureDetector = new GestureDetector(this, this);


        /*mainLayout = this.findViewById(R.id.LayoutMain) as RelativeLayout
        var imgView1 = mainLayout!!.findViewById<StrategoImageView>(R.id.a8)
        imgView1.setBackgroundResource(R.drawable.chess);

        var imgView2 = mainLayout!!.findViewById<StrategoImageView>(R.id.b8)
        imgView2.setBackgroundResource(R.drawable.chess);

        var imgView3 = mainLayout!!.findViewById<StrategoImageView>(R.id.c8)
        imgView3.setBackgroundResource(R.drawable.chess);*/

    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}