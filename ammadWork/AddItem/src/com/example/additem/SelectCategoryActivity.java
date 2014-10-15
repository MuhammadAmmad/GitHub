package com.example.additem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.zxing.client.android.CaptureActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;



public class SelectCategoryActivity extends Activity {

	ListView listView;
	public static ArrayList<String> barcodeArray,categoryArray,brandArray,titleArray;
	

	Menu menu;
	ArrayAdapter<String> adapter;
	
	static ProgressDialog mAuthProgressDialog;
	
	
	public class MyAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {

		// ProgressDialog pd;
		Context context;
		
		int which;

		public MyAsyncTask(Context context,int w) {
			this.context = context;
			which=w;
			// pd= new ProgressDialog(this.context);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// pd.setMessage("amamd");
			// pd.show();

			// super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			
			String url="";
			switch(which)
			{
			case 0://query all barcodes
				url = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/get_mob_barcodeitem.php?barcode=";
				break;
			case 1://query all titles
			//	url = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/get_mob_shortdesc.php";
				break;
			case 2://query all categories
				url = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/get_mob_category.php?cat=";
				break;
			case 3://query all branda
				url = "http://mdev.esajee.com/live_locations/kohsar_live/iPOS/admin/accounts/get_mob_brand.php?brand=";
				break;
				default:
					url="";
					break;
				
			}
			

			String result = sendPostRequest(url,null);

			Log.i("RESULT", result);
			
			//Converting json response to ArrayList<Stirng>
			JSONArray arr = null;
			try {
				arr = new JSONArray(result);
				 Log.d("My App", arr.toString());
				 ArrayList<String> stringArray = new ArrayList<String>();
				 for (int i = 0; i < arr.length(); i++) {
				     stringArray.add(arr.getString(i));
				 }
				 
				
				 
				 //Log.d("My App", stringArray[4]);
				 return stringArray;
				 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
			

		   
			return null;

		}

		@Override
		protected void onPostExecute(ArrayList<String> str) {
			// TODO Auto-generated method stub
			
			switch(which){
			case 0: 
				barcodeArray=str;
				mAuthProgressDialog.dismiss();
			   break;
			case 1: 
			//	titleArray=str;
				   break;
			case 2: 
				categoryArray=str;
				
				startBrandActivity(categoryArray);
				
				mAuthProgressDialog.dismiss();
				   break;
			case 3: 
				brandArray=str;
				
				mAuthProgressDialog.dismiss();
				   break;
			}
		
		}

	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_brand);
		
		titleArray=new ArrayList<String>();
		
		listView=(ListView) findViewById(R.id.listViewCategory);
		
		if(isConnected()){//wheter connected to Internet
		
		showProgressDialog("Categories...",this);
		
		new MyAsyncTask(this,0).execute();
		
		new MyAsyncTask(this,2).execute();
		
		new MyAsyncTask(this,3).execute();
		
		//new MyAsyncTask(this,1).execute();
		}
		else//not connected close the app Otherwise it will crash
		{
			Toast.makeText(this, "connct to internet", Toast.LENGTH_LONG).show();
			finish();
		}
		
	
		
		
		
		

		
	}

	
	
	@SuppressLint("NewApi") public boolean onCreateOptionsMenu(Menu menu) {
		 
	    getMenuInflater().inflate(R.menu.select_category, menu);
	 
	    this.menu = menu;
	 
	
	 
	        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	 
	        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
	 
	        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
	 
	        search.setOnQueryTextListener(new OnQueryTextListener() { 
	 
	            @Override 
	            public boolean onQueryTextChange(String query) {
	 
	                loadData(query);
	 
	                return true; 
	 
	            }

				@Override
				public boolean onQueryTextSubmit(String arg0) {
					// TODO Auto-generated method stub
					return true;
				} 
	 
	        });
	 
	    
	 
	    return true;
	 
	}
	 
	
	@SuppressLint("NewApi") private void loadData(String query) {
		
		if(categoryArray!=null && query.length()>0)
		{
			final ArrayList<String> tempArr=new ArrayList<String>();
			for(int i=0;i<categoryArray.size();i++)
			{
				if(categoryArray.get(i).toLowerCase().startsWith(query.toLowerCase()))
				{
					tempArr.add(categoryArray.get(i));
				}
			}
			startBrandActivity(tempArr);
			
			
		}
		else if(query.length()==0)
		{
			 startBrandActivity(categoryArray);
		}
	 
	    }



	/**
	 * 
	 */
	private void startBrandActivity(final ArrayList<String> arr) {
		adapter = new ArrayAdapter<String>(
					getBaseContext(),
					android.R.layout.simple_list_item_1, arr);
		listView.setAdapter(adapter );
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(brandArray!=null){
					Intent intent=new Intent(getBaseContext(),SelectBrandActivity.class);
					intent.putExtra("category", arr.get(arg2));
					startActivity(intent);
				}
				else{
					showProgressDialog("Brands...",SelectCategoryActivity.this);
				}
				
				
			}
		});
	}
	 
	public static void showProgressDialog(String msg,Context context) {
		mAuthProgressDialog = new ProgressDialog(context);
		mAuthProgressDialog.setTitle("Loading");
		mAuthProgressDialog.setMessage(msg);
		mAuthProgressDialog.setCancelable(false);
		mAuthProgressDialog.show();
	}
	
	
	String sendPostRequest(String uri,ArrayList<NameValuePair> nameValuePairs) {
		StringBuilder sb = new StringBuilder();
		try {
			String line = "";
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			if(nameValuePairs!=null && nameValuePairs.size()>0)
			{
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			
			
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
	
	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = cm.getActiveNetworkInfo();
		if (net != null && net.isAvailable() && net.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
	
}
