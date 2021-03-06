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

package co.juliansuarez.libwizardpager.wizard.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * Represents a wizard model, including the pages/steps in the wizard, their dependencies, and their
 * currently populated choices/values/selections.
 *
 * To create an actual wizard model, extend this class and implement {@link #onNewRootPageList()}.
 */
public abstract class AbstractWizardModel implements ModelCallbacks, DatabaseCallbacks {
    protected Context mContext;

    private List<ModelCallbacks> mListeners = new ArrayList<ModelCallbacks>();
    private List<DatabaseCallbacks> mDatabaseListeners = new ArrayList<DatabaseCallbacks>();
    
    private PageList mRootPageList;
    
    private int mLastStepButtonResource;

    public AbstractWizardModel(Context context) {
    	mContext = context;
    	mRootPageList = onNewRootPageList();
    }

    /**
     * Override this to define a new wizard model.
     */
    protected abstract PageList onNewRootPageList();
    
    /**
     * Needs to be declared in the model to indicate whether a IntroPage is present or not (to deal with the CheckBox mark).
     * You should use shared settings to verify whether the IntroPage should be shown or not based on user preferences.
     * @return
     */
    public abstract Boolean hasIntroPage();
    
    protected void setLastStepButtonResource(int resource) {
    	mLastStepButtonResource = resource;
    }
    
    public int getLastStepButtonResource() {
    	return mLastStepButtonResource;
    }

    @Override
    public void onPageDataChanged(Page page) {
        // can't use for each because of concurrent modification (review fragment
        // can get added or removed and will register itself as a listener)
        for (int i = 0; i < mListeners.size(); i++) {
            mListeners.get(i).onPageDataChanged(page);
        }
    }

    @Override
    public void onPageTreeChanged() {
        // can't use for each because of concurrent modification (review fragment
        // can get added or removed and will register itself as a listener)
        for (int i = 0; i < mListeners.size(); i++) {
            mListeners.get(i).onPageTreeChanged();
        }
    }
    
    @Override
    public void getFilter(DatabaseListener listener) {
    	// can't use for each because of concurrent modification (review fragment
        // can get added or removed and will register itself as a listener)
        for (int i = 0; i < mDatabaseListeners.size(); i++) {
            mDatabaseListeners.get(i).getFilter(listener);
        }
    }
    
    

    public Page findByKey(String key) {
        return mRootPageList.findByKey(key);
    }

    public void load(Bundle savedValues) {
        for (String key : savedValues.keySet()) {
            mRootPageList.findByKey(key).resetData(savedValues.getBundle(key));
        }
    }

    public void registerListener(ModelCallbacks listener) {
        mListeners.add(listener);
    }
    
    public void registerListener(DatabaseCallbacks listener) {
        mDatabaseListeners.add(listener);
    }

    public Bundle save() {
        Bundle bundle = new Bundle();
        for (Page page : getCurrentPageSequence()) {
            bundle.putBundle(page.getKey(), page.getData());
        }
        return bundle;
    }

    /**
     * Gets the current list of wizard steps, flattening nested (dependent) pages based on the
     * user's choices.
     */
    public List<Page> getCurrentPageSequence() {
        ArrayList<Page> flattened = new ArrayList<Page>();
        mRootPageList.flattenCurrentPageSequence(flattened);
        return flattened;
    }

    public void unregisterListener(ModelCallbacks listener) {
        mListeners.remove(listener);
    }
    
    public void unregisterListener(DatabaseCallbacks listener) {
        mDatabaseListeners.remove(listener);
    }
}
