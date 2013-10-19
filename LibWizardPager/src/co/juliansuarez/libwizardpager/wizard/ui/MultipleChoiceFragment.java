/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.juliansuarez.libwizardpager.wizard.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import co.juliansuarez.libwizardpager.R;
import co.juliansuarez.libwizardpager.wizard.model.Choice;
import co.juliansuarez.libwizardpager.wizard.model.MultipleFixedChoicePage;
import co.juliansuarez.libwizardpager.wizard.model.Page;

public class MultipleChoiceFragment extends ListFragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks mCallbacks;
	private String mKey;
	private List<String> mChoices;
	private Page mPage;

	public static MultipleChoiceFragment create(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		MultipleChoiceFragment fragment = new MultipleChoiceFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public MultipleChoiceFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mKey = args.getString(ARG_KEY);
		mPage = mCallbacks.onGetPage(mKey);

		MultipleFixedChoicePage fixedChoicePage = (MultipleFixedChoicePage) mPage;
		mChoices = new ArrayList<String>();
		for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
			mChoices.add(fixedChoicePage.getOptionAt(i));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_page, container,
				false);
		((TextView) rootView.findViewById(android.R.id.title)).setText(mPage
				.getTitle());

		final ListView listView = (ListView) rootView
				.findViewById(android.R.id.list);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_multiple_choice,
				android.R.id.text1, mChoices));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// Pre-select currently selected items.
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				ArrayList<String> selectedItems = mPage.getData()
						.getStringArrayList(Page.SIMPLE_DATA_KEY);
				if (selectedItems == null || selectedItems.size() == 0) {
					return;
				}

				Set<String> selectedSet = new HashSet<String>(selectedItems);

				for (int i = 0; i < mChoices.size(); i++) {
					if (selectedSet.contains(mChoices.get(i))) {
						listView.setItemChecked(i, true);
					}
				}
			}
		});

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
	public void onListItemClick(ListView l, View v, int position, long id) {
		SparseBooleanArray checkedPositions = getListView()
				.getCheckedItemPositions();
		ArrayList<String> selections = new ArrayList<String>();
		ArrayList<Choice> choices = new ArrayList<Choice>();
		
		Boolean skipNextPage = false;
		for (int i = 0; i < checkedPositions.size(); i++) {
			if (checkedPositions.valueAt(i)) {
				String item = getListAdapter().getItem(
						checkedPositions.keyAt(i)).toString();
				selections.add(item);

				Choice c = mPage.getChoice(item);
				choices.add(c);
				
				if (c.skipNextPage()) {
					skipNextPage = true;
				}
			}
		}
		mPage.getData().putBoolean(Page.SKIP_NEXT_PAGE, skipNextPage);
		mPage.getData().putParcelableArrayList(Page.CHOICE_DATA_KEY, choices);
		mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
		mPage.notifyDataChanged();
	}

	/**
	 * Must be called from a
	 * ViewPager.SimpleOnPageChangeListener.onPageSelected() to treat Choices
	 * that are allowed to be checked based on selected choices from a previous
	 * page. Calling code example:
	 * 
	 * <pre>
	 * {@code
	 * if (position >= mWizardModel.getCurrentPageSequence().size()) return;
	 * 								
	 * FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) mPager.getAdapter();
	 * final Fragment fragment = (Fragment) a.instantiateItem(mPager, position);
	 * if (fragment instanceof MultipleChoiceFragment) {
	 * 	MultipleChoiceFragment f = (MultipleChoiceFragment) fragment;
	 * 	f.setRequiredChoices();
	 * }
	 * }
	 * </pre>
	 */
	public void setRequiredChoices() {
		// View v = mPager.getChildAt(position);
		final ListView listView = this.getListView(); // (ListView)v.findViewById(android.R.id.list);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < listView.getChildCount(); i++) {
					View v = listView.getChildAt(i);
					CheckedTextView c = (CheckedTextView) v
							.findViewById(android.R.id.text1);

					c.setClickable(false);
					c.setFocusable(false);

					Object dependentView = c.getTag();
					if (dependentView != null && (Boolean) dependentView) {
						c.setTextColor(getResources().getColor(
								android.R.color.black));
						listView.setItemChecked(i, false);
						c.setTag(false);
						
						MultipleChoiceFragment.this.onListItemClick(
								listView, v, i, -1);
					}

				}

				// Set mandatory items to selected and disabled
				Set<String> mandatorySet = new HashSet<String>();

				Page previousPage = mPage.getPreviousPage();
				if (previousPage != null) {
					ArrayList<Choice> selectedItems = previousPage.getData()
							.getParcelableArrayList(Page.CHOICE_DATA_KEY);

					if (selectedItems == null || selectedItems.size() == 0) {
						return;
					}

					for (Choice c : selectedItems) {
						if (c.getMandatoryNextPageChoiceString() != null) {
							mandatorySet.add(c
									.getMandatoryNextPageChoiceString());
						}
					}

					for (int i = 0; i < listView.getChildCount(); i++) {

						Log.d("NewSimpleRule",
								(String) listView.getItemAtPosition(i));
						View v = listView.getChildAt(i);
						CheckedTextView c = (CheckedTextView) v
								.findViewById(android.R.id.text1);

						if (mandatorySet.contains(c.getText().toString())) {
							listView.setItemChecked(i, true);
							c.setTag(true);
							c.setClickable(true);
							c.setFocusable(true);
							c.setTextColor(getResources().getColor(
									android.R.color.darker_gray));

							MultipleChoiceFragment.this.onListItemClick(
									listView, v, i, -1);
						}
					}

				}
			}
		});

	}
}
