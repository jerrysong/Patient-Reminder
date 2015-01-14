package com.example.patientreminder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AlarmRowCursorAdapter extends SimpleCursorAdapter {

	private Context context = null;

	public AlarmRowCursorAdapter(Context context, int layout, Cursor cursor,
			String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int pos = position;
		ListView listView = (ListView) parent;
		final Cursor cursor = (Cursor) listView.getItemAtPosition(pos);
		String id = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB._ID));
		String imageBitmapString = cursor.getString(cursor
				.getColumnIndexOrThrow(UserCatalogDB.KEY_IMAGE));
				
		setImageView(view, imageBitmapString);
		setDeleteButton(view, cursor, id);
						
		return view;
	}
	
	private void setImageView(View view, String imageBitmapString){
		ImageView icon = (ImageView) view.findViewById(R.id.medicine_icon);		
		if(!imageBitmapString.isEmpty()){
			icon.setImageBitmap(Conveter.convertStringToBitmap(imageBitmapString));
		}		
		else {
			icon.setImageResource(R.drawable.medicine_icon);
		}
	}
	
	private void setDeleteButton(View view, final Cursor cursor, final String id){
		ImageButton button = (ImageButton) view.findViewById(R.id.btn_delete);		
		button.setOnClickListener(new View.OnClickListener() {
			// Implement the delete button
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// Get the cursor, positioned to the corresponding row in the
				// result set				
				Uri uri = Uri.parse(MyContentProvider.CONTENT_URI_USER_CATALOG + "/" + id);
				context.getContentResolver().delete(uri, null, null);
				cursor.requery();
				notifyDataSetChanged();
			}
		});
	}
}
