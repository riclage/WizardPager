package co.juliansuarez.libwizardpager.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;
import co.juliansuarez.libwizardpager.R;
import co.juliansuarez.libwizardpager.wizard.model.DatabaseListener;
import co.juliansuarez.libwizardpager.wizard.model.Page;
import co.juliansuarez.libwizardpager.wizard.model.ReminderPage;
import co.juliansuarez.libwizardpager.wizard.model.TimePage;

public class ReminderFragment extends Fragment implements DatabaseListener {
	
	protected static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private Page mPage;

	protected EditText mEditTextNumRepeat;
	protected Spinner mSpinnerTypeRepeat;
	
	protected AutoCompleteTextView mReminderText;
	protected Cursor mAutoCompleteCursor;
	protected SimpleCursorAdapter mCursorAdapter;

	public static ReminderFragment create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		ReminderFragment f = new ReminderFragment();
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
		View rootView = inflater.inflate(R.layout.fragment_page_reminder,
				container, false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());
		
		mReminderText = (AutoCompleteTextView)rootView.findViewById(R.id.reminder);
		
		mEditTextNumRepeat = (EditText) rootView.findViewById(R.id.editTextNumRepeat);
		mEditTextNumRepeat.setText(mPage.getData().getString(TimePage.TIME_NUM_REPEAT_DATA_KEY));
		mEditTextNumRepeat.setFilters(new InputFilter[]{new InputFilterMinMax("1", "50")});
		
		mSpinnerTypeRepeat = (Spinner)rootView.findViewById(R.id.spinnerTypeRepeat);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.time_values_plural, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerTypeRepeat.setAdapter(adapter);
		
		int index = adapter.getPosition(mPage.getData().getString(ReminderPage.TIME_TYPE_REPEAT_DATA_KEY));
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mCursorAdapter = new SimpleCursorAdapter(this.getActivity(),
				android.R.layout.simple_dropdown_item_1line, 
				null,
				new String[] { "text" }, 
				new int[] { android.R.id.text1 });
		
		// Set the CursorToStringConverter, to provide the labels for the
        // choices to be displayed in the AutoCompleteTextView.
		mCursorAdapter.setCursorToStringConverter(new CursorToStringConverter() {
            public String convertToString(Cursor cursor) {
                // Get the label for this row out of the "state" column
                final int columnIndex = cursor.getColumnIndexOrThrow("text");
                final String str = cursor.getString(columnIndex);
                return str;
            }
        });

		mPage.getDatabaseFilter(ReminderFragment.this);
		
		mReminderText.setAdapter(mCursorAdapter);
		
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
				mPage.getData().putString(TimePage.TIME_NUM_REPEAT_DATA_KEY,
						(editable != null) ? editable.toString() : null);
				mPage.notifyDataChanged();
			}

		});
		
		mSpinnerTypeRepeat.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selectedItem = (String)parent.getItemAtPosition(position);
				mPage.getData().putString(TimePage.TIME_TYPE_REPEAT_DATA_KEY,
						(selectedItem != null) ? selectedItem : null);
				
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

	@Override
	public void setFilter(FilterQueryProvider q) {
		mCursorAdapter.setFilterQueryProvider(q);
		
	}

}
