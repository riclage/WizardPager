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
		dest.add(new ReviewItem("Recurrence type", mData.getString(TIME_TYPE_REPEAT_DATA_KEY), getKey(), -1));
        //TODO: show review with the type of recurrence at the end. E.g., if hourly, show "1 hour", etc.
		dest.add(new ReviewItem("Repeat every", mData.getString(TIME_NUM_REPEAT_DATA_KEY), getKey(), -1));
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
