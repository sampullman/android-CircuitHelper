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

    /**
     * Deselect the previous color selection and select a new one.
     *
     * @param root The root of the view containing the color list
     * @param id The id of the new color view
     */
    public void setSelected(View root, int id) {
	if(id != selected && selected != -1) {
	    LinearLayout prev = (LinearLayout)root.findViewById(selected);
	    ((RadioButton)prev.findViewById(R.id.radio)).setChecked(false);
	}
	((RadioButton)root.findViewById(id).findViewById(R.id.radio)).setChecked(true);
	selected = id;
    }

    /**
     * Predicate that indicates whether a color is a valid choice for a resistor bar.
     *
     * @param colorIndex The index of the color to check
     * @param barIndex The bar to check color validity for
     */
    public boolean colorIsValid(int colorIndex, int barIndex) {
	if(barIndex == SIG_FIG1 || barIndex == SIG_FIG2) {
	    return colorIndex < 10;
	} else {
	    return true;
	}
    }

    /**
     * Makes a click listener for the i'th color in the color list.
     *
     * @param i The index of the color
     * @return A click listener that updates the resistor.
     */
    public OnClickListener makeColorClickListener(final int i) {
	return new OnClickListener() {
	    public void onClick(View v) {
		if(colorIsValid(i, currentBar)) {
		    // Change selected resistor bar color
		    setBarColor(getView(), barIds[currentBar], i);
		    barValues[currentBar] = i;
		    setCurrentBar(getView(), currentBar + 1);

		    updateResistorValue(getView());
		}
	    }
	};
    }

    /**
     * Makes a click listener for the resistor bars.
     *
     * @param i The index of the bar (0 - 3)
     * @param A click listener that sets the current bar.
     */
    public OnClickListener makeBarClickListener(final int i) {
	return new OnClickListener() {
	    public void onClick(View v) {
		setCurrentBar(getView(), i);
	    }
	};
    }

    /**
     * Set the current (active) resistor bar. Called when a bar is clicked or a new color is selected
     * and the next bar is selected.
     *
     * @param root The root view containing the resistor views.
     * @param newBar The index of the new bar to select.
     */
    public void setCurrentBar(View root, int newBar) {
	ImageView oldBar = (ImageView)root.findViewById(barIds[currentBar]);
	setImageStroke(oldBar, false);
	currentBar = newBar % NUM_BARS;
	ImageView bar = (ImageView)root.findViewById(barIds[currentBar]);
	setImageStroke(bar, true);
	setSelected(root, ids[barValues[currentBar]]);
    }

    /**
     * A helper method for setCurrentBar that selects or deselects a bar by showing or hiding
     * the ImageView's outline.
     *
     * @param image The ImageView graphic representation of a bar.
     * @param on Whether the outline should be "turned" on or off
     */
    public void setImageStroke(ImageView image, boolean on) {
	GradientDrawable rect = (GradientDrawable)image.getDrawable();
	if(on) {
	    rect.setStroke(BAR_STROKE, getResources().getColor(STROKE_COLOR));
	} else {
	    rect.setStroke(BAR_STROKE, Color.TRANSPARENT);
	}
    }

    /**
     * Sets the color of a resistor bar.
     *
     * @param root The root view containing the bar.
     * @param barId The id of the ImageView that represents the bar.
     * @param colorIndex The colors array index of the color to set the bar to.
     */
    public void setBarColor(View root, int barId, int colorIndex) {
	ImageView bar = (ImageView)root.findViewById(barId);
	GradientDrawable rect = (GradientDrawable)bar.getDrawable();
	rect.setColor(getResources().getColor(colors[colorIndex]));
    }

    /**
     * Calculate and display the value of the resistor from the current bar values.
     *
     * @param root The root view containing the bar.
     */
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
	    valueString = " K Ohms";
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

    /**
     * Inflate and initialize the root View of this Fragment, then restore any previous state.
     *
     * @param inflater
     * @param container
     * @param savedState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.resistor, container, false);
	for(int i=0;i<ids.length;i+=1) {
	    LinearLayout color = (LinearLayout)v.findViewById(ids[i]);
	    color.setOnClickListener(makeColorClickListener(i));
	}
	restoreState(savedState);
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

    /**
     * Save the current GUI state to a bundle.
     *
     * @param state The Bundle to save state to
     */
    public void saveState(Bundle state) {
	state.putInt("sigFig1", barValues[0]);
	state.putInt("sigFig2", barValues[1]);
	state.putInt("multiplier", barValues[2]);
	state.putInt("tolerance",barValues[3]);
	state.putInt("currentBar", currentBar);
	state.putInt("selected", selected);
    }

    /**
     * Restore GUI state to the values in a Bundle if they exist, otherwise retain default state
     *
     * @param state The Bundle to restore state from
     */
    public void restoreState(Bundle state) {
	if(state != null) {
	    barValues[0] = state.getInt("sigFig1", barValues[0]);
	    barValues[1] = state.getInt("sigFig2", barValues[1]);
	    barValues[2] = state.getInt("multiplier", barValues[2]);
	    barValues[3] = state.getInt("tolerance", barValues[3]);
	    currentBar = state.getInt("currentBar", currentBar);
	    selected = state.getInt("selected", selected);
	}
    }
}