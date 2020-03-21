package de.arvato.stratego;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class StrategoView {

    public static final String TAG = "StrategoView";

    private StrategoViewBase strategoViewBase;
    private StrategoActivity parent;
    private LayoutInflater inflater;
    private StrategoControl strategoControl;

    public StrategoView(Activity activity) {
        super();
        parent = (StrategoActivity) activity;
        strategoViewBase = new StrategoViewBase(activity);
        inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        strategoControl = new StrategoControl();

        View.OnClickListener ocl = new View.OnClickListener() {
            public void onClick(View arg0) {
                handleClick(strategoViewBase.getIndexOfButton(arg0));
            }
        };

        View.OnLongClickListener olcl = new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                handleClick(strategoViewBase.getIndexOfButton(view));
                return true;
            }
        };

        strategoViewBase.init(ocl, olcl);

        paintBoard();
    }

    public boolean handleClick(int index) {
        Log.d(TAG, "handleClick at index:" + index);

        int nextMov[] = strategoControl.getPossibleMovements (index);

        StringBuilder sb = new StringBuilder();
        for (int i : nextMov) {
            sb.append(i);
            sb.append(",");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
        Log.d(TAG, "possible movements at index:" + index + " = " + sb.toString());

        strategoViewBase.paintBoard(strategoControl, index, null);
        return false;
    }

    public void paintBoard () {

        strategoViewBase.paintBoard(strategoControl, -1, null);


    }
}