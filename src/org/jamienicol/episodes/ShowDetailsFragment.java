/*
 * Copyright (C) 2013 Jamie Nicol <jamie@thenicols.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jamienicol.episodes;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import java.text.DateFormat;
import java.util.Date;
import org.jamienicol.episodes.db.ShowsTable;
import org.jamienicol.episodes.db.ShowsProvider;

public class ShowDetailsFragment extends SherlockFragment
	implements LoaderManager.LoaderCallbacks<Cursor>
{
	private int showId;
	private Cursor showData;
	private TextView overviewView;
	private TextView firstAiredView;

	public static ShowDetailsFragment newInstance(int showId) {
		ShowDetailsFragment instance = new ShowDetailsFragment();

		Bundle args = new Bundle();
		args.putInt("showId", showId);

		instance.setArguments(args);
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showId = getArguments().getInt("showId");
	}

	public View onCreateView(LayoutInflater inflater,
	                         ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.show_details_fragment,
		                             container,
		                             false);

		overviewView = (TextView)view.findViewById(R.id.overview);
		firstAiredView = (TextView)view.findViewById(R.id.first_aired);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle loaderArgs = new Bundle();
		loaderArgs.putInt("showId", showId);
		getLoaderManager().initLoader(0, loaderArgs, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		int showId = args.getInt("showId");
		Uri uri = Uri.withAppendedPath(ShowsProvider.CONTENT_URI_SHOWS,
		                               new Integer(showId).toString());
		String[] projection = {
			ShowsTable.COLUMN_OVERVIEW,
			ShowsTable.COLUMN_FIRST_AIRED
		};
		return new CursorLoader(getActivity(),
		                        uri,
		                        projection,
		                        null,
		                        null,
		                        null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		showData = data;
		refreshViews();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		showData = null;
		refreshViews();
	}

	private void refreshViews() {
		if (showData != null && showData.moveToFirst()) {

			int overviewColumnIndex =
				showData.getColumnIndexOrThrow(ShowsTable.COLUMN_OVERVIEW);
			if (showData.isNull(overviewColumnIndex)) {
				overviewView.setVisibility(View.INVISIBLE);
			} else {
				overviewView.setText(showData.getString(overviewColumnIndex));
				overviewView.setVisibility(View.VISIBLE);
			}

			int firstAiredColumnIndex =
				showData.getColumnIndexOrThrow(ShowsTable.COLUMN_FIRST_AIRED);
			if (showData.isNull(firstAiredColumnIndex)) {
				firstAiredView.setVisibility(View.INVISIBLE);
			} else {
				Date firstAired =
					new Date(showData.getLong(firstAiredColumnIndex) * 1000);
				DateFormat df = DateFormat.getDateInstance();
				String firstAiredText =
					String.format(getString(R.string.first_aired),
					              df.format(firstAired));
				firstAiredView.setText(firstAiredText);
				firstAiredView.setVisibility(View.VISIBLE);
			}

		} else {
			overviewView.setVisibility(View.INVISIBLE);
			firstAiredView.setVisibility(View.INVISIBLE);
		}
	}
}
