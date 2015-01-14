package com.example.patientreminder;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderAdapter extends ArrayAdapter<ReminderModel> {
	private ArrayList<ReminderModel> items = null;
	private Context context = null;
	private TextView viewMedicineName = null;
	private TextView viewMedicineDose = null;
	private TextView viewTime = null;
	private TextView viewTimeZone = null;
	private TextView viewStartDate = null;
	private TextView viewEndDate = null;
	private ImageButton viewImageBtn = null;
	private ImageView viewIcon = null;

	public OrderAdapter(Context context, int textViewResourceId,
			ArrayList<ReminderModel> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listview_alarm_row, null);
		}

		final ReminderModel reminderModel = items.get(position);
		if (reminderModel != null) {
			viewMedicineName = (TextView) v
					.findViewById(R.id.text_medicine_name);
			viewMedicineDose = (TextView) v
					.findViewById(R.id.text_medicine_dose);
			viewTime = (TextView) v.findViewById(R.id.text_time);
			viewTimeZone = (TextView) v.findViewById(R.id.text_time_zone);
			viewImageBtn = (ImageButton) v.findViewById(R.id.btn_delete);
			viewIcon = (ImageView) v.findViewById(R.id.medicine_icon);
			viewStartDate = (TextView) v.findViewById(R.id.text_start_date);
			viewEndDate = (TextView) v.findViewById(R.id.text_end_date);

			viewImageBtn.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					items.remove(reminderModel);
					notifyDataSetChanged();
				}
			});

			if (viewMedicineName != null) {
				viewMedicineName.setText(reminderModel.medicineName);
			}
			if (viewMedicineDose != null) {
				viewMedicineDose.setText(reminderModel.medicineDose);
			}
			if (viewTimeZone != null) {
				viewTimeZone.setText(Conveter.getSavedTimeZone(reminderModel));
			}
			if (viewTime != null) {
				viewTime.setText(Conveter.getSavedTimeValue(reminderModel));
			}
			if (viewIcon != null) {
				if (!reminderModel.imageBitmapString.isEmpty()) {
					viewIcon.setImageBitmap(Conveter
							.convertStringToBitmap(reminderModel.imageBitmapString));
				}
			}
			if (viewStartDate != null) {
				if (reminderModel.startDate != null) {
					viewStartDate.setText(Conveter
							.getStringDate(reminderModel.startDate));
				}
			}
			if (viewEndDate != null) {
				if (reminderModel.startDate != null) {
					viewEndDate.setText(Conveter
							.getStringDate(reminderModel.endDate));
				}
			}

		}
		return v;
	}		
}