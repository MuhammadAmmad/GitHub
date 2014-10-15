package com.example.additem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDB{  

private SQLiteHelper dbHelper;  

private SQLiteDatabase database;  

public MyDB(Context context){  
    dbHelper = new SQLiteHelper(context);  
    database = dbHelper.getWritableDatabase();  
}


public long insert(String tableName, ContentValues values){  
  
   return database.insert(tableName, null, values);  
}    

     public Cursor select(String tableName,String where,String[] args) {
        
       Cursor mCursor = database.query(true, tableName,null,where, args, null, null, null, null);  
       
       if (mCursor != null) {  
        mCursor.moveToFirst();  

     }  
     return mCursor; 
}
     
     public Cursor select(String tableName,String where,String[] args,String orderBy) {
         
         Cursor mCursor = database.query(true, tableName,null,where, args, null, null, orderBy, null);  
         
         if (mCursor != null) {  
          mCursor.moveToFirst();  

       }  
       return mCursor; 
  }
     
     
     public long delete(String tableName,String where,String[] args)
     {
    	return database.delete(tableName,where, args); 
     }
     
     
     public long update(String tableName, ContentValues values, String where,String [] args)
     {
    	 return database.update(tableName, values, where, args );
     }
     
     public void close()
     {
    	 database.close();
     }
     
     public void upgradeTables()
     {
    	 dbHelper.onUpgrade(database, 2, 2);
     }
}