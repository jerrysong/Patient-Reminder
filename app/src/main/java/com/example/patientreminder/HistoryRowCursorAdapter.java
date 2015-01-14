package com.example.patientreminder;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryRowCursorAdapter extends SimpleCursorAdapter {

	//private Context context = null;
	
	public HistoryRowCursorAdapter(Context context, int layout, Cursor cursor,
			String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		//this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int pos = position;
		ListView listView = (ListView) parent;
		final Cursor cursor = (Cursor) listView.getItemAtPosition(pos);
		String imageBitmapString = cursor.getString(cursor
				.getColumnIndexOrThrow(HistoryDB.KEY_IMAGE));
		String completionString = cursor.getString(cursor
				.getColumnIndexOrThrow(HistoryDB.KEY_COMPLETITION));
		String actualTimeString = cursor.getString(cursor
				.getColumnIndexOrThrow(HistoryDB.KEY_ACTUAL_TIME_ZONE))
				+ " "
				+ cursor.getString(cursor
						.getColumnIndexOrThrow(HistoryDB.KEY_ACTUAL_TIME));
		String scheduledTimeString = cursor.getString(cursor
				.getColumnIndexOrThrow(HistoryDB.KEY_SCHEDULED_TIME_ZONE))
				+ " "
				+ cursor.getString(cursor
						.getColumnIndexOrThrow(HistoryDB.KEY_SCHEDULED_TIME));

		setTextScheduledTime(view, scheduledTimeString);
		setTextCompletionCondition(view, completionString, actualTimeString);
		setImageView(view, imageBitmapString);

		return view;
	}

	private void setImageView(View view, String imageBitmapString) {
		ImageView icon = (ImageView) view
				.findViewById(R.id.history_image_medicine_icon);
		if (!imageBitmapString.isEmpty()) {
			icon.setImageBitmap(Conveter
					.convertStringToBitmap(imageBitmapString));
		} else {
			icon.setImageResource(R.drawable.medicine_icon);
		}
	}

	private void setTextCompletionCondition(View view, String completionString, String actualTimeString) {
		TextView text = (TextView) view
				.findViewById(R.id.history_text_completion_condition);
		if (completionString.equals("true")) {
			text.setText(actualTimeString);
		} else if (completionString.equals("false")) {
			text.setText("Not Take");
		}
	}
	
	private void setTextScheduledTime(View view, String scheduledTimeString) {
		TextView text = (TextView) view
				.findViewById(R.id.history_text_scheduled_time);
			text.setText(scheduledTimeString);
	}
}
