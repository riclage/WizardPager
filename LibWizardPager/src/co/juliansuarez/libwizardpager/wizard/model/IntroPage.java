package co.juliansuarez.libwizardpager.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.text.TextUtils;
import co.juliansuarez.libwizardpager.wizard.ui.IntroFragment;

public class IntroPage extends Page {

	public IntroPage(ModelCallbacks callbacks, String title, Spanned introText) {
		super(callbacks, title);
		mDescription = introText;
	}

	@Override
	public Fragment createFragment() {
		return IntroFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		Boolean checked = mData.getBoolean(SIMPLE_DATA_KEY);
		dest.add(new ReviewItem(getTitle(), checked.toString(),
				getKey()));

	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
	}

	public IntroPage setValue(Boolean value) {
		mData.putBoolean(SIMPLE_DATA_KEY, value);
		return this;
	}
	
	@Override
	public Boolean skipNextPage() {
		return false;
	}
}
