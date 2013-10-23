package com.donomobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.donomobileapp.R;

public class ColorMenuFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] colors = getResources().getStringArray(R.array.color_names);
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, android.R.id.text1, colors);
		setListAdapter(colorAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			newContent = new ColorFragment();
			args.putInt(ColorFragment.COLOR_KEY, R.color.red);
			break;
		case 1:
			newContent = new ColorFragment();
			args.putInt(ColorFragment.COLOR_KEY, R.color.green);
			break;
		case 2:
			newContent = new ColorFragment();
			args.putInt(ColorFragment.COLOR_KEY, R.color.blue);
			break;
		case 3:
			newContent = new ColorFragment();
			args.putInt(ColorFragment.COLOR_KEY, android.R.color.white);
			break;
		case 4:
			newContent = new ColorFragment();
			args.putInt(ColorFragment.COLOR_KEY,android.R.color.black);
			break;
		}
		if (newContent != null) {
			newContent.setArguments(args);
			switchFragment(newContent);
		}
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		} else if (getActivity() instanceof ResponsiveUIActivity) {
			ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
			ra.switchContent(fragment);
		}
	}
}
