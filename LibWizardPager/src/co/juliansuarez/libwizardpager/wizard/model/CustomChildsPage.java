package co.juliansuarez.libwizardpager.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import co.juliansuarez.libwizardpager.wizard.ui.TextFragment;

public class CustomChildsPage extends Page {

	public CustomChildsPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return TextFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY),
				getKey()));

	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
	}

	public CustomChildsPage setValue(String value) {
		mData.putString(SIMPLE_DATA_KEY, value);
		return this;
	}
}