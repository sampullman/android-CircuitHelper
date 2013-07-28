package com.threeDBJ.circuithelper;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v4.app.Fragment;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ResistorFragment extends Fragment {
    static String TAG = "CircuitHelper";
    static int SIG_FIG1=0, SIG_FIG2=1, MULTIPLIER=2, TOLERANCE=3;
    static int NUM_BARS=4, BAR_STROKE=4;
    static int STROKE_COLOR = R.color.bar_stroke;
    static int[] barIds = { R.id.sigfig1, R.id.sigfig2, R.id.multiplier, R.id.tolerance };
    int[] barValues =       { 1,            0,            2,               0 };
    static int[] ids = { R.id.black, R.id.brown, R.id.red, R.id.orange, R.id.yellow,
			 R.id.green, R.id.blue, R.id.violet, R.id.gray, R.id.white,
			 R.id.gold, R.id.silver };
    static int[] colors = { R.color.black, R.color.brown, R.color.red, R.color.orange, R.color.yellow,
			    R.color.green, R.color.blue, R.color.violet, R.color.gray, R.color.white,
			    R.color.gold, R.color.silver };
    static int[] multipliers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -2 };
    static double[] tolerances = { 0, 0.01, 0.02, 0, 0.05, 0.005, 0.0025, 0.001, 0.0005, 0, 0.05, 0.1 };

    int selected = -1, currentBar = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setSelected(View root, int id) {
	DebugLog.e(TAG, "clicked: "+id);
	if(id != selected && selected != -1) {
	    LinearLayout prev = (LinearLayout)root.findViewById(selected);
	    ((RadioButton)prev.findViewById(R.id.radio)).setChecked(false);
	}
	((RadioButton)root.findViewById(id).findViewById(R.id.radio)).setChecked(true);
	selected = id;
    }

    public OnClickListener makeColorClickListener(final int i) {
	return new OnClickListener() {
	    public void onClick(View v) {
		setSelected(getView(), v.getId());
		// Change selected resistor bar color
		setBarColor(getView(), barIds[currentBar], i);
		barValues[currentBar] = i;
		setCurrentBar(getView(), currentBar + 1);

		updateResistorValue(getView());
	    }
	};
    }

    public OnClickListener makeBarClickListener(final int i) {
	return new OnClickListener() {
	    public void onClick(View v) {
		setCurrentBar(getView(), i);
	    }
	};
    }

    public void setCurrentBar(View root, int newBar) {
	ImageView oldBar = (ImageView)root.findViewById(barIds[currentBar]);
	setImageStroke(oldBar, false);
	currentBar = newBar % NUM_BARS;
	ImageView bar = (ImageView)root.findViewById(barIds[currentBar]);
	setImageStroke(bar, true);
	setSelected(root, ids[barValues[currentBar]]);
    }

    public void setImageStroke(ImageView image, boolean on) {
	GradientDrawable rect = (GradientDrawable)image.getDrawable();
	if(on) {
	    rect.setStroke(BAR_STROKE, getResources().getColor(STROKE_COLOR));
	} else {
	    rect.setStroke(BAR_STROKE, Color.TRANSPARENT);
	}
    }

    public void setBarColor(View root, int barId, int colorIndex) {
	ImageView bar = (ImageView)root.findViewById(barId);
	GradientDrawable rect = (GradientDrawable)bar.getDrawable();
	rect.setColor(getResources().getColor(colors[colorIndex]));
    }

    public void updateResistorValue(View root) {
	int exp = multipliers[barValues[MULTIPLIER]];
	double value = Double.parseDouble(barValues[SIG_FIG1]+""+barValues[SIG_FIG2]);
	value *= Math.pow(10, exp);
	double display;
	String valueString;
	if(value >= 1000000) {
	    display = value / 1000000.0;
	    valueString = " M Ohms";
	} else if(value >= 1000) {
	    display = value / 1000.0;
	    valueString = " K Omhs";
	} else {
	    display = value;
	    valueString = " Ohms";
	}
	DecimalFormat df = new DecimalFormat("#.####");
	df.setRoundingMode(RoundingMode.HALF_UP);
	valueString = df.format(display) + valueString;
	double tolerance = tolerances[barValues[TOLERANCE]];
	if(tolerance != 0) {
	    df = new DecimalFormat("#.######");
	    df.setRoundingMode(RoundingMode.HALF_UP);
	    valueString += " ± " + df.format(value*tolerance);
	}
	TextView valueText = (TextView)root.findViewById(R.id.resistor_value);
	valueText.setText(valueString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.resistor, container, false);
	for(int i=0;i<ids.length;i+=1) {
	    LinearLayout color = (LinearLayout)v.findViewById(ids[i]);
	    color.setOnClickListener(makeColorClickListener(i));
	}
	if(savedState != null) {
	    barValues[0] = savedState.getInt("sigFig1", barValues[0]);
	    barValues[1] = savedState.getInt("sigFig2", barValues[1]);
	    barValues[2] = savedState.getInt("multiplier", barValues[2]);
	    barValues[3] = savedState.getInt("tolerance", barValues[3]);
	    currentBar = savedState.getInt("currentBar", currentBar);
	    selected = savedState.getInt("selected", selected);
	}
	for(int i=0;i<barIds.length;i+=1) {
	    ImageView bar = (ImageView)v.findViewById(barIds[i]);
	    bar.setImageDrawable(getResources().getDrawable(R.drawable.resistor_bar));
	    setBarColor(v, barIds[i], barValues[i]);
	    setImageStroke(bar, false);
	    bar.setOnClickListener(makeBarClickListener(i));
	}
	updateResistorValue(v);
        return v;
    }

    @Override
    public void onResume() {
	super.onResume();
	setCurrentBar(getView(), currentBar);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
	saveState(savedState);
    }

    public void saveState(Bundle state) {
	state.putInt("sigFig1", barValues[0]);
	state.putInt("sigFig2", barValues[1]);
	state.putInt("multiplier", barValues[2]);
	state.putInt("tolerance",barValues[3]);
	state.putInt("currentBar", currentBar);
	state.putInt("selected", selected);
    }
}