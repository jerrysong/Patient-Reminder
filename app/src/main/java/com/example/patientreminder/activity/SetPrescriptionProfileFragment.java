package com.example.patientreminder.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.patientreminder.Conveter;
import com.example.patientreminder.R;

public class SetPrescriptionProfileFragment extends Fragment {

	private static boolean skipPause = false;
	private final static int TAKE_PICTURE_CODE = 1;
	private final static int SELECT_PICTURE_CODE = 2;
	private Button btn_set_medicine_image = null;
	private EditText edit_medicine_name = null;
	private EditText edit_medicine_dose = null;
	private EditText edit_medicine_reason = null;
	private EditText edit_medicine_doctor = null;
	private Spinner dose_selector = null;
	private ImageView edit_imageview = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(
				R.layout.fragment_set_prescription_profile, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		importViews();
		initSpinner();
		setOnTouchListener();
		readTempContent();
		Log.v("SetPrescriptionProfileFragment", "Call Start");
	}

	@Override
	public void onPause() {
		super.onPause();
		if (skipPause) {
			skipPause = false;
			return;
		}
		saveTempContent();
		Log.v("SetPrescriptionProfileFragment", "Call Stop");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("SetPrescriptionProfileFragment", "Call Result");
		super.onActivityResult(requestCode, resultCode, data);
		String imageString = null;
		switch (requestCode) {
		case SELECT_PICTURE_CODE:
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getActivity().getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				Bitmap tempBitmap = BitmapFactory.decodeFile(picturePath);
				imageString = Conveter
						.convertBitmapToString(resizeBitmap(tempBitmap));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TAKE_PICTURE_CODE:
			try {
				Bitmap tempBitmap = (Bitmap) data.getExtras().get("data");
				imageString = Conveter
						.convertBitmapToString(resizeBitmap(tempBitmap));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

		SharedPreferences sharedPref = getActivity().getSharedPreferences("AddPrescriptionActivity",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("image", imageString);
		editor.apply();
		skipPause = true;
	}

	private void saveTempContent() {
		SharedPreferences sharedPref = getActivity().getSharedPreferences("AddPrescriptionActivity",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("medicineName", edit_medicine_name.getText()
				.toString());
		editor.putString("medicineDose", edit_medicine_dose.getText()
				.toString());
		editor.putString("doseUnit", dose_selector.getSelectedItem().toString());
		editor.putString("reason", edit_medicine_reason.getText().toString());
		editor.putString("doctor", edit_medicine_doctor.getText().toString());
		Bitmap bitmap = ((BitmapDrawable) edit_imageview.getDrawable())
				.getBitmap();
		String imageString = Conveter.convertBitmapToString(bitmap);
		editor.putString("image", imageString);
		editor.apply();
	}

	private void readTempContent() {
		String[] dose_unit_array = getResources().getStringArray(
				R.array.dose_unit_array);
		SharedPreferences sharedPref = getActivity().getSharedPreferences("AddPrescriptionActivity",
				Context.MODE_PRIVATE);
		edit_medicine_name.setText(sharedPref.getString("medicineName", ""));
		edit_medicine_dose.setText(sharedPref.getString("medicineDose", ""));
		edit_medicine_reason.setText(sharedPref.getString("reason", ""));
		edit_medicine_doctor.setText(sharedPref.getString("doctor", ""));
		if (sharedPref.getString("doseUnit", "").equals(dose_unit_array[0])) {
			dose_selector.setSelection(0);
		} else if (sharedPref.getString("doseUnit", "").equals(
				dose_unit_array[1])) {
			dose_selector.setSelection(1);
		} else if (sharedPref.getString("doseUnit", "").equals(
				dose_unit_array[2])) {
			dose_selector.setSelection(2);
		} else if (sharedPref.getString("doseUnit", "").equals(
				dose_unit_array[3])) {
			dose_selector.setSelection(3);
		} else if (sharedPref.getString("doseUnit", "").equals(
				dose_unit_array[4])) {
			dose_selector.setSelection(4);
		} else if (sharedPref.getString("doseUnit", "").equals(
				dose_unit_array[5])) {
			dose_selector.setSelection(5);
		}
		Bitmap bitmap = Conveter.convertStringToBitmap(sharedPref.getString(
				"image", ""));
		edit_imageview.setImageBitmap(bitmap);
	}

	private void importViews() {
		FragmentActivity currentActivity = getActivity();
		btn_set_medicine_image = (Button) currentActivity
				.findViewById(R.id.btn_set_medicine_image);
		btn_set_medicine_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setMedicineImage();
			}
		});
		dose_selector = (Spinner) currentActivity
				.findViewById(R.id.spinner_dose_selector);
		edit_imageview = (ImageView) currentActivity
				.findViewById(R.id.edit_imageview);
		edit_medicine_name = (EditText) currentActivity
				.findViewById(R.id.edit_medicine_name);
		edit_medicine_dose = (EditText) currentActivity
				.findViewById(R.id.edit_medicine_dose);
		edit_medicine_reason = (EditText) currentActivity
				.findViewById(R.id.edit_medicine_reason);
		edit_medicine_doctor = (EditText) currentActivity
				.findViewById(R.id.edit_medicine_doctor);
	}

	private void initSpinner() {
		ArrayAdapter<CharSequence> doseAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.dose_unit_array,
						R.layout.spinner_view);
		doseAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dose_selector.setAdapter(doseAdapter);
	}

	private void setOnTouchListener() {
		edit_medicine_name.setOnTouchListener(foucsHandler);
		edit_medicine_dose.setOnTouchListener(foucsHandler);
		edit_medicine_reason.setOnTouchListener(foucsHandler);
		edit_medicine_doctor.setOnTouchListener(foucsHandler);
	}

	private OnTouchListener foucsHandler = new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			// TODO Auto-generated method stub
			arg0.requestFocusFromTouch();
			return false;
		}
	};

	private void setMedicineImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Set Medicine Image");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent cameraIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, TAKE_PICTURE_CODE);
				} else if (items[item].equals("Choose from Library")) {
					Intent libraryIntent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(libraryIntent, SELECT_PICTURE_CODE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	private Bitmap resizeBitmap(Bitmap bitmap) {
		final int w = 125;
		final int h = 125;

		float factorH = h / (float) bitmap.getHeight();
		float factorW = w / (float) bitmap.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap,
				(int) (bitmap.getWidth() * factorToUse),
				(int) (bitmap.getHeight() * factorToUse), false);
		return resizedBitmap;
	}
}