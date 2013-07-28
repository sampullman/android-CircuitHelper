package com.threeDBJ.circuithelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class CircuitHelperActivity extends FragmentActivity {
    private FragmentTabHost tabHost;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
	tabHost.setup(this, getSupportFragmentManager(), R.id.tab_layout);
	tabHost.addTab(tabHost.newTabSpec("resistor").setIndicator("Resistor"),
		       ResistorFragment.class, null);
	tabHost.addTab(tabHost.newTabSpec("divider").setIndicator("Voltage Divider"),
		       VoltageDividerFragment.class, null);
	tabHost.addTab(tabHost.newTabSpec("capacitor").setIndicator("Capacitor"),
		       CapacitorFragment.class, null);
    }
}
