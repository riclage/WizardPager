package co.juliansuarez.libwizardpager.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import co.juliansuarez.libwizardpager.wizard.ui.TimeFragment;

public class TimePage extends Page {
	public static final String TIME_NUM_REPEAT_DATA_KEY = "numRepeat";
    public static final String TIME_TYPE_REPEAT_DATA_KEY = "typeRepeat";

	public TimePage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return TimeFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		String recurrenceType = mData.getString(TIME_TYPE_REPEAT_DATA_KEY);
		int numRepeat = Integer.valueOf(mData.getString(TIME_NUM_REPEAT_DATA_KEY));
		//dest.add(new ReviewItem("Recurrence type", recurrenceType, getKey(), -1));
		dest.add(new ReviewItem("Repeat every", numRepeat + " " + recurrenceType, getKey(), 1));
	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
	}

	public TimePage setValue(String value) {
		mData.putString(SIMPLE_DATA_KEY, value);
		return this;
	}
	
}
