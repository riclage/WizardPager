package co.juliansuarez.libwizardpager.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import co.juliansuarez.libwizardpager.wizard.ui.ReminderFragment;

public class ReminderPage extends Page {
	public static final String TIME_NUM_REPEAT_DATA_KEY = "numRepeat";
    public static final String TIME_TYPE_REPEAT_DATA_KEY = "typeRepeat";

	public ReminderPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}
	
	public ReminderPage(ModelCallbacks callbacks, DatabaseCallbacks databaseCallbacks, String title) {
		super(callbacks, databaseCallbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return ReminderFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		String recurrenceType = mData.getString(TIME_TYPE_REPEAT_DATA_KEY);
		String numRepeat = mData.getString(TIME_NUM_REPEAT_DATA_KEY);
		
		//dest.add(new ReviewItem("Recurrence type", recurrenceType, getKey(), -1));
		if (numRepeat != null) {
			dest.add(new ReviewItem("Repeat every", numRepeat + " " + recurrenceType, getKey(), 1));
		}		

	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(TIME_NUM_REPEAT_DATA_KEY));
	}

	@Override
	public Boolean skipNextPage() {
		return false;
	}
	
}
