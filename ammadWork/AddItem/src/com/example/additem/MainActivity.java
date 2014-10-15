package com.example.additem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;


public class MainActivity extends Activity {

	
	// Request Code for speech Activity
	private static final int REQUEST_CODE = 1234;

	private static final int CAMERA_REQUEST = 1888;
	private static final int GALLERY_REQUEST = 1889;

	ActionMode mMode;

	// Adapter for AutoCompletes
	ArrayAdapter<String> adapter;
	private ImageAdapter imgAdapter;
	// Custom GridView
	ExpandableHeightGridView imgGridView;

	// Dialog for the list of speech result
	Dialog match_text_dialog;
	ArrayList<String> matches_text;

	MyDB db;

	Spinner uom;

	AutoCompleteTextView sku, title, type, category, brand;
	EditText description, tempEditText;

	
	

	ListView textlist;

	
	String[] uom_arr = { "Kg", "gm", "Ltr", "Dozen", "Oz" };

	String skuStr, titleStr, typeStr, desStr, uomStr, catStr, brandStr;

	String encodedImage = "";
	//string to store title in edit case
	String tempTitle = "";

	ProgressDialog mAuthProgressDialog;
	
	ImageButton scanner;

	public class MyAsyncTask extends AsyncTask<Void, Void, String> {

		// ProgressDialog pd;
		Context context;

		int which;

		public MyAsyncTask(Context context, int w) {
			this.context = context;
			which = w;
			if(which!=0 && which!=1)
				showProgressDialog("wait");
			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

		}

		@Override
		protected String doInBackground(Void... params) {

			String result1 = "";
			String immmgg = "aa";
			
			//Case to query titles
			if(which==0)
			{
				String encodedURL = null;
				try {
					encodedURL = URLEncoder.encode(titleStr, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String url = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/get_mob_shortdesc.php?item="
						+encodedURL;
				String result = sendPostRequest(url);

				Log.i("RESULT", result);
				
				JSONArray arr = null;
				try {
					arr = new JSONArray(result);
					 Log.d("My App", arr.toString());
					 ArrayList<String> stringArray = new ArrayList<String>();
					 for (int i = 0; i < arr.length(); i++) {
					     stringArray.add(arr.getString(i));
					 }
					 
					
					 
					 //Log.d("My App", stringArray[4]);
					 SelectCategoryActivity.titleArray= stringArray;
					 
					 
					 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}
			
			
			
			// Edit Case
			else if (which == 6) {

				String encodedURL = null;
				try {
					encodedURL = URLEncoder.encode(skuStr, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String url1 = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/get_mob_barcode_new.php?barcode="
						+ encodedURL;
				
				Log.i("URL", url1);
				
				result1 = sendPostRequest(url1);
				Log.i("RESULT", result1);

				try {
					JSONObject obj = new JSONObject(result1);
					titleStr = obj.getString("shortdescription");
					desStr = obj.getString("itemdescription");
					catStr = obj.getString("category_name");
					brandStr = obj.getString("brandname");
					uomStr = obj.getString("uom_desc");
					return "";
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.toString();
				}
				

			}
			 // New Entry Case
			else if (which == 5) {
				Cursor cursor = null;
				cursor = db.select("items", null, null);

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				JSONArray array = new JSONArray();
				if (cursor != null && cursor.moveToFirst()) {
					int i = 0;
					Log.i("CUR", cursor.getString(1));
					do {
						JSONObject object = new JSONObject();

						try {
							// object.put("id", cursor.getString(0));
							object.put("sku", cursor.getString(1));
							// Log.i("TITLE",cursor.getString(1));
							object.put("title", cursor.getString(2));
							object.put("description", cursor.getString(3));
							// object.put("type", cursor.getString(4));
							object.put("uom", cursor.getString(5));
							object.put("category", cursor.getString(6));
							object.put("brand", cursor.getString(7));
							

							array.put(object);

						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						nameValuePairs.add(new BasicNameValuePair("image_file",
								""));
						i++;
					} while (cursor.moveToNext());

					Log.i("JSON", array.toString());
					
					postData(immmgg);

					
					String encodedURL = null;
					try {
						encodedURL = URLEncoder.encode(array.toString(),
								"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String url1 = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/item_save_mobile.php?jasondata="
							+ encodedURL;
					Log.i("URL", url1);
					result1 = sendPostRequest(url1);

					JSONArray arr = null;
					try {
						arr = new JSONArray(result1);
						Log.d("My App", arr.toString());
						ArrayList<String> stringArray = new ArrayList<String>();
						for (int j = 0; j < arr.length(); j++) {
							stringArray.add(arr.getString(j));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					 // Deleting from local Database
					 
					db.delete("items", null, null);
					db.delete("images", null, null);
				

					Log.i("RESULT", result1);
					return result1;

				} else {
					return "No record found in Local DB.";
				}
			}

			return null;

		}

		@Override
		protected void onPostExecute(String str) {
			// TODO Auto-generated method stub
			if(which==0)//setting adapter with loaded titles
			{
				
				setAdapter(title, SelectCategoryActivity.titleArray);
			}
			
			else if(which==5){//Informing about the barcodes that are uploaded
			
				JSONArray arr = null;
				try {
					arr = new JSONArray(str);
					Log.d("My App", arr.toString());
					ArrayList<String> stringArray = new ArrayList<String>();
					for (int j = 0; j < arr.length(); j++) {
						stringArray.add(arr.getString(j));
						Toast.makeText(context,
								"Item : " + arr.getString(j) + " is uploaded.",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			} 
			}else if(which==6) {// populating the views with the response data

				if (!titleStr.equals("null")){
					title.setText(titleStr);
					tempTitle=titleStr;
					SelectCategoryActivity.titleArray.add(titleStr);
					titleStr=tempTitle.substring(0, 0);
					new MyAsyncTask(MainActivity.this, 0);
					titleStr=tempTitle;
				}
				else{
					title.setText("");
					tempTitle="";
				}
				if (!desStr.equals("null"))
					description.setText(desStr);
				else
					description.setText("");
				if (!catStr.equals("null"))
					category.setText(catStr);
				else
					category.setText("");
				if (!brandStr.equals("null"))
					brand.setText(brandStr);
				else
					brand.setText("");

				for (int i = 0; i < uom_arr.length; i++) {
					if (uom_arr[i].equalsIgnoreCase(uomStr)) {
						uom.setSelection(i);
						break;
					}
				}
			}
			if(which!=0)
				mAuthProgressDialog.dismiss();
		}	

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		scanner=(ImageButton) findViewById(R.id.btnScanner);
		
		scanner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(MainActivity.this,CaptureActivity.class);
				 intent.setAction("com.google.zxing.client.android.SCAN");
				 // this stops saving ur barcode in barcode scanner app's history
				 intent.putExtra("SAVE_HISTORY", false);
				 startActivityForResult(intent, 0);
//				 IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
//			      integrator.initiateScan();
			}
		});
		
		
//		new MyAsyncTask(this,1).execute();
		db = new MyDB(this);

		imgGridView = (ExpandableHeightGridView) findViewById(R.id.imagesGridView);
		imgGridView.setExpanded(true);
		imgAdapter = new ImageAdapter(this);

		imgGridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (mMode == null) {
					mMode = startActionMode(new MyActionMode());

				}
				ImageView image2 = (ImageView) arg1
						.findViewById(R.id.itemImage2);
				imgAdapter.selectedImages.set(arg2,
						!imgAdapter.selectedImages.get(arg2));
				showToast("selected");
				if (imgAdapter.selectedImages.get(arg2)) {
					image2.setBackgroundColor(Color.parseColor("#6633b5e5"));
				} else {
					image2.setBackgroundColor(Color.parseColor("#00000000"));
				}

			}
		});



	
		description = (EditText) findViewById(R.id.description);

		sku = (AutoCompleteTextView) findViewById(R.id.sku);
		setAdapter(sku, SelectCategoryActivity.barcodeArray);
		
		

		title = (AutoCompleteTextView) findViewById(R.id.title);
		setAdapter(title, new ArrayList<String>());
		
		title.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.toString().length()==1){//query using the first character
				
					titleStr=title.getText().toString();
					new MyAsyncTask(MainActivity.this,0).execute();
				}

			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

		type = (AutoCompleteTextView) findViewById(R.id.type);

		uom = (Spinner) findViewById(R.id.uom);
		category = (AutoCompleteTextView) findViewById(R.id.category);
		setAdapter(category, SelectCategoryActivity.categoryArray);

		category.setText(getIntent().getExtras().getString("category"));

		brand = (AutoCompleteTextView) findViewById(R.id.brand);
		setAdapter(brand, SelectCategoryActivity.brandArray);

		brand.setText(getIntent().getExtras().getString("brand"));

		uom.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, uom_arr));

		sku.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (!arg1) {
					
					skuStr = sku.getText().toString();

					
					// Checking whether barcodes exists
					if (SelectCategoryActivity.barcodeArray != null)
						for (int i = 0; i < SelectCategoryActivity.barcodeArray
								.size(); i++) {
							if (skuStr
									.equals(SelectCategoryActivity.barcodeArray
											.get(i))) {

								
								// Barcode exists then show Dialog
								showErrorDialog("Error", "item already exists",
										0);
								break;
							}
						}
				}
			}
		});

		

		title.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				

				if (!hasFocus) {
					titleStr = title.getText().toString();
					//Checking whether title exists already
					if (!titleStr.equals(tempTitle)) {
						for (int i = 0; i < SelectCategoryActivity.titleArray
								.size(); i++) {
							if (titleStr
									.equals(SelectCategoryActivity.titleArray
											.get(i))) {
								//Display error dialog
								showErrorDialog("Error",
										"Please change the title.", 1);
								title.setText("");
								break;
							}
						}
					}
				}
			}
		});

		

	}

	/**
	 * Set Adapters to Views
	 */
	private void setAdapter(AutoCompleteTextView view, ArrayList<String> arr) {
		adapter = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_list_item_1, arr);
		view.setAdapter(adapter);
		view.setThreshold(-1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Gallery case
		if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK
				&& data != null) {

			try {
				Uri pickedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(pickedImage,
						filePath, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePath[0]);

				String imagePath = cursor.getString(columnIndex);
				cursor.close();

				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

				imgAdapter.addBitmap(bitmap);
				imgGridView.setAdapter(imgAdapter);

				Toast.makeText(this, "Image added.", Toast.LENGTH_LONG).show();
			} catch (Error e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}

		}
		//Camera Case
		else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			// img1.setImageBitmap(bitmap);

			// bitmaps.add(bitmap);
			imgAdapter.addBitmap(bitmap);
			imgGridView.setAdapter(imgAdapter);

			Toast.makeText(getBaseContext(), "Image added.", Toast.LENGTH_LONG)
					.show();
		}
		//Barcode sccaner case
		else
		{
			String contents = data.getStringExtra("SCAN_RESULT");
			showErrorDialog("Info", contents, 1);
			sku.setText(contents);
 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case R.id.actionReset:
			
			sku.setText("");
			title.setText("");
			description.setText("");
			category.setText("");
			brand.setText("");

			uom.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, uom_arr));
			
			imgAdapter = new ImageAdapter(MainActivity.this);
			imgGridView.setAdapter(imgAdapter);
			
			sku.requestFocus();

			break;
		case R.id.actionImage:
			showCameraDialog();
			break;
		case R.id.actionAdd:
			skuStr = sku.getText().toString();
			titleStr = title.getText().toString();
			typeStr = type.getText().toString();
			desStr = description.getText().toString();

			uomStr = uom.getSelectedItem().toString();
			catStr = category.getText().toString();
			brandStr = brand.getText().toString();

			if (skuStr.equals("") || titleStr.equals("")) {
				showToast("Please Fill in Require data.");
			} else {

				ContentValues values = new ContentValues();

				values.put("sku", skuStr);
				values.put("title", titleStr);
				values.put("description", desStr);
				values.put("type", typeStr);
				values.put("uom", uomStr);
				values.put("category", catStr);
				values.put("brand", brandStr);
				
				long iid = db.insert("items", values);

				for (int i = 0; i < imgAdapter.encodedImages.size(); i++) {
					ContentValues values2 = new ContentValues();

					values2.put("sku", skuStr);
					values2.put("image", titleStr);
					db.insert("images", values2);
				}
				// getContentResolver().insert(null, values);

				if (isConnected())
					new MyAsyncTask(MainActivity.this, 5).execute();
				else {
					showErrorDialog("Info", "Record is Added to Local DB", 1);
					Toast.makeText(getBaseContext(),
							"Record is Added to Local DB", Toast.LENGTH_LONG)
							.show();
				}

				SelectCategoryActivity.barcodeArray.add(skuStr);
				setAdapter(sku, SelectCategoryActivity.barcodeArray);

				sku.setText("");
				title.setText("");
				description.setText("");
				category.setText("");
				brand.setText("");

				uom.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, uom_arr));

				imgAdapter = new ImageAdapter(MainActivity.this);
				imgGridView.setAdapter(imgAdapter);
				sku.requestFocus();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showCameraDialog() {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Select an option");

		dialog.setSingleChoiceItems(new String[] { "Gallery", "Camera" }, -1,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(i, GALLERY_REQUEST);
						} else {
							Intent cameraIntent = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(cameraIntent, CAMERA_REQUEST);

						}
						dialog.dismiss();
					}
				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});
		AlertDialog alert = dialog.create();

		alert.setCanceledOnTouchOutside(true);
		alert.show();

	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = cm.getActiveNetworkInfo();
		if (net != null && net.isAvailable() && net.isConnected()) {
			return true;
		} else {
			return false;
		}
	}


	


	String sendPostRequest(String uri) {
		StringBuilder sb = new StringBuilder();
		try {
			String line = "";
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse response = new DefaultHttpClient().execute(httpPost);
			if (response != null) {
				InputStream source = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(source));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			}
		} catch (Exception exp) {

			// ErrorHandler.ExceptionMessage=exp.getMessage();
			return exp.getMessage();
		}
		return sb.toString();
	}

	private void showErrorDialog(String _title, String message, int which) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this)
				.setTitle(_title).setMessage(message)
				.setPositiveButton(android.R.string.ok, null)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false);

		if (which == 0) {
			alert.setNegativeButton("Cancle",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							sku.setText("");
						}
					});
			alert.setPositiveButton("Edit",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new MyAsyncTask(getBaseContext(), 6).execute();
						}
					});
		}

		alert.show();
	}

	private void showProgressDialog(String msg) {
		mAuthProgressDialog = new ProgressDialog(this);
		mAuthProgressDialog.setTitle("Loading");
		mAuthProgressDialog.setMessage(msg);
		mAuthProgressDialog.setCancelable(false);
		mAuthProgressDialog.show();
	}

	public void postData(String str) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/item_save_file.php");
		StringBuilder sb = new StringBuilder();
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("image_file", "12345"));
			nameValuePairs.add(new BasicNameValuePair("image_file", str));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			String line = "";
			if (response != null) {
				InputStream source = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(source));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			}

			Log.i("RESPONMSE", sb.toString());

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	@SuppressLint("NewApi")
	private final class MyActionMode implements ActionMode.Callback {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Used to put dark icons on light action bar
			// boolean isLight = SampleList.THEME ==
			// R.style.Theme_Sherlock_Light;

			menu.add("Delete").setIcon(android.R.drawable.ic_menu_delete)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Toast.makeText(MainActivity.this, "Got click: " + item,
					Toast.LENGTH_SHORT).show();
			if (item.getTitle().equals("Delete")) {
				new DeleteAsync(imgAdapter.selectedImages).execute();
			}
			// mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			for (int i = 0; i < imgAdapter.selectedImages.size(); i++)
				imgAdapter.selectedImages.set(i, false);
			imgGridView.setAdapter(imgAdapter);
			// selectedImages.clear();
			Toast.makeText(getBaseContext(), "destroy action mode.",
					Toast.LENGTH_LONG).show();
		}
	}

	private class DeleteAsync extends AsyncTask<Void, Void, Void> {
		ArrayList<Boolean> arr;

		public DeleteAsync(ArrayList<Boolean> arr) {
			// TODO Auto-generated constructor stub
			this.arr = arr;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			ArrayList<Boolean> tempB = new ArrayList<Boolean>();
			ArrayList<String> tempStr = new ArrayList<String>();
			ArrayList<Bitmap> tempBitmap = new ArrayList<Bitmap>();

			for (int i = 0; i < arr.size(); i++) {
				if (!imgAdapter.selectedImages.get(i)) {
					tempStr.add(imgAdapter.encodedImages.get(i));
					tempBitmap.add(imgAdapter.data.get(i));
					tempB.add(false);

				}
			}

			imgAdapter.encodedImages.clear();
			imgAdapter.encodedImages = tempStr;

			imgAdapter.data.clear();
			imgAdapter.data = tempBitmap;

			imgAdapter.selectedImages = tempB;

			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// super.onPostExecute(result);

			imgGridView.setAdapter(imgAdapter);
			mMode.finish();

			showToast("EI : " + imgAdapter.encodedImages.size() + "Data : "
					+ imgAdapter.data.size() + " SI: "
					+ imgAdapter.selectedImages.size());
		}

	}

}
