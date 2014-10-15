package com.example.additem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {

	 private static final String DATABASE_NAME = "ItemsDB.db";
	 
	 private static final String CREATE_ITEMS="CREATE TABLE IF NOT EXISTS items(id INTEGER PRIMARY KEY AUTOINCREMENT," +
	 							" sku VARCHAR,  title VARCHAR, description VARCHAR, type VARCHAR," +
	 							" uom VARCHAR, category VARCHAR, brand VARCHAR);";
	 
	 private static final String CREATE_IMAGES="CREATE TABLE IF NOT EXISTS images(id INTEGER PRIMARY KEY AUTOINCREMENT," +
				" sku VARCHAR,  image VARCHAR);";
	
     public SQLiteHelper(Context context) {
         super(context, DATABASE_NAME, null, 2);
     }

   
     @Override
     public void onCreate(SQLiteDatabase database) {
         
         database.execSQL(CREATE_ITEMS);
         database.execSQL(CREATE_IMAGES);
         
     }

     
     @Override
     public void onUpgrade(SQLiteDatabase database, int oldVersion,
             int newVersion) {
       
         database.execSQL("DROP TABLE IF EXISTS items;");
         database.execSQL("DROP TABLE IF EXISTS images;");
      
         
         onCreate(database);
     }

}
