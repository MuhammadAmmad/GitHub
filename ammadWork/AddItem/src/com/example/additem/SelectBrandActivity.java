package com.example.additem;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SelectBrandActivity extends Activity {

	ListView listView;
	
	String category;
	
	
	
	Menu menu;
	ArrayAdapter<String> adapter;
	ProgressDialog mAuthProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_brand);
		
		
	
		
		category=getIntent().getExtras().getString("category");
		
		listView=(ListView) findViewById(R.id.listViewCategory);
		
		startMainActivity(SelectCategoryActivity.brandArray);
	
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
		
		if(SelectCategoryActivity.brandArray!=null && query.length()>0)
		{
			final ArrayList<String> tempArr=new ArrayList<String>();
			for(int i=0;i<SelectCategoryActivity.brandArray.size();i++)
			{
				if(SelectCategoryActivity.brandArray.get(i).toLowerCase().startsWith(query.toLowerCase()))
				{
					tempArr.add(SelectCategoryActivity.brandArray.get(i));
				}
			}
			startMainActivity(tempArr);
	
			
			
		}
		else if(query.length()==0)
		{
			 startMainActivity(SelectCategoryActivity.brandArray);
		}
	 
	    }


	/**
	 * 
	 */
	private void startMainActivity(final ArrayList<String> arr) {
		adapter = new ArrayAdapter<String>(
					getBaseContext(),
					android.R.layout.simple_list_item_1, arr);
		listView.setAdapter(adapter );
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			//	if(SelectCategoryActivity.barcodeArray!=null){
					Intent intent=new Intent(getBaseContext(),MainActivity.class);
					intent.putExtra("brand", arr.get(arg2));
					intent.putExtra("category", category);
					startActivity(intent);
//				}
//				else
//				{
//					SelectCategoryActivity.showProgressDialog("SKUs...",SelectBrandActivity.this);
//				}
			}
		});
	}
	
	
}
