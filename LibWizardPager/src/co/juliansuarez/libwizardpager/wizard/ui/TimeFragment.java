package co.juliansuarez.libwizardpager.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import co.juliansuarez.libwizardpager.R;
import co.juliansuarez.libwizardpager.wizard.model.Page;

public class TimeFragment extends Fragment {
	
	protected static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private Page mPage;

	protected EditText mEditTextNumRepeat;
	protected Spinner mSpinnerTypeRepeat;

	public static TimeFragment create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		TimeFragment f = new TimeFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = mCallbacks.onGetPage(mKey);
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page_time,
				container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());

		mEditTextNumRepeat = (EditText) rootView.findViewById(R.id.editTextNumRepeat);
		TimePreferences t = mPage.getData().getParcelable(Page.SIMPLE_DATA_KEY);
		mEditTextNumRepeat.setText(t.getNumRepeat());
		
		mSpinnerTypeRepeat = (Spinner)rootView.findViewById(R.id.spinnerTypeRepeat);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.time_recurrence, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerTypeRepeat.setAdapter(adapter);
		
		int index = adapter.getPosition(t.getTypeRepeat());
		mSpinnerTypeRepeat.setSelection(index);
		
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks)) {
			throw new ClassCastException(
					"Activity must implement PageFragmentCallbacks");
		}

		mCallbacks = (PageFragmentCallbacks) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mEditTextNumRepeat.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				updatePreferences((editable != null) ? editable.toString() : null, null);
				mPage.notifyDataChanged();
			}

		});
		
		mSpinnerTypeRepeat.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedItem = (String)parent.getItemAtPosition(position);
				updatePreferences(null, (selectedItem != null) ? selectedItem : null);
				mPage.notifyDataChanged();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);

		// In a future update to the support library, this should override
		// setUserVisibleHint
		// instead of setMenuVisibility.
		if (mEditTextNumRepeat != null) {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (!menuVisible) {
				imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			}
		}
	}
	
	private void updatePreferences(String numRepeat, String typeRepeat) {
		TimePreferences t = mPage.getData().getParcelable(Page.SIMPLE_DATA_KEY);
		if (t == null) {
			t = new TimePreferences(numRepeat, typeRepeat);
		} else {
			if (numRepeat != null) t.setNumRepeat(numRepeat);
			if (typeRepeat != null) t.setTypeRepeat(typeRepeat);
		}
		mPage.getData().putParcelable(Page.SIMPLE_DATA_KEY, t);
	}
	
}

class TimePreferences implements Parcelable {

	private String mNumRepeat;
	private String mTypeRepeat;
	
	public TimePreferences(String numRepeat, String typeRepeat) {
		this.mNumRepeat = numRepeat;
		this.mTypeRepeat = typeRepeat;
	}
	
	public String getNumRepeat() {
		return mNumRepeat;
	}
	
	public String getTypeRepeat() {
		return mTypeRepeat;
	}
	
	public void setNumRepeat(String numRepeat) {
		mNumRepeat = numRepeat;
	}
	
	public void setTypeRepeat(String typeRepeat) {
		mTypeRepeat = typeRepeat;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
}
