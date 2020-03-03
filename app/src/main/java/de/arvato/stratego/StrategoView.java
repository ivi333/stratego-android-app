package de.arvato.stratego;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

class StrategoView {

    public static final String TAG = "StrategoView";

    private StrategoViewBase strategoViewBase;
    private StrategoActivity parent;
    private LayoutInflater inflater;

    StrategoView(Activity activity) {
        super();
        parent = (StrategoActivity) activity;
        strategoViewBase = new StrategoViewBase(activity);

        inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View.OnClickListener ocl = new View.OnClickListener() {
            public void onClick(View arg0) {
                handleClick(strategoViewBase.getIndexOfButton(arg0));
            }
        };

    }

    public boolean handleClick(int index) {
        return false;
    }
}