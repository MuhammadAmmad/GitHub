package com.example.additem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageAdapter extends BaseAdapter {
    
    private Activity activity;
    public ArrayList<Bitmap> data;
    private static LayoutInflater inflater=null;
    public ArrayList<String> encodedImages;
    public ArrayList<Boolean> selectedImages;
 //   public boolean isSelecting;
    ImageView image;
    CheckBox chkbox;
    
    public ImageAdapter(Activity a) {
        activity = a;
        data=new ArrayList<Bitmap>();
        encodedImages=new ArrayList<String>();
        selectedImages=new ArrayList<Boolean>(); 
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
   
    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    @SuppressLint("NewApi") public void addBitmap(Bitmap bitmap)
    {
    		data.add(bitmap);
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
																	// bitmap
																	// object
			byte[] b = baos.toByteArray();

			encodedImages.add( Base64.encodeToString(b, Base64.DEFAULT));
			selectedImages.add(false);
    }
    
    @SuppressLint("NewApi") public void addEncodedImage(String str)
    {
    	byte[] b=Base64.decode(str, Base64.DEFAULT);
		data.add(BitmapFactory.decodeByteArray(b, 0, b.length));
		selectedImages.add(false);
    }
    
    public void removeItem(int pos)
    {
    	data.remove(pos);
    	encodedImages.remove(pos);
    	selectedImages.remove(pos);
    }
    
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        
        if(convertView==null)
            vi = inflater.inflate(R.layout.main_image_view_for_grid_view, null);

       // TextView text=(TextView)vi.findViewById(R.id.text);
        image=(ImageView)vi.findViewById(R.id.itemImage);
      //  text.setText("item "+position);
//        chkbox=(CheckBox) vi.findViewById(R.id.imageCheckBox);
        image.setImageBitmap(data.get(position));
        
        final ImageView image2=(ImageView)vi.findViewById(R.id.itemImage2);
        
//        image2.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				selectedImages.set(position, !selectedImages.get(position));
//				Toast.makeText(activity, "click", Toast.LENGTH_LONG).show();
//				 if(selectedImages.get(position))
//			        {
//			        	image2.setBackgroundColor(Color.parseColor("#66ff12ff"));
//			        }
//			        else
//			        {
//			        	image2.setBackgroundColor(Color.parseColor("#00000000"));
//			        }
//			}
//		});
        
       
//        image.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//			//	if(isSelecting){
//				//	chkbox.setVisibility(View.VISIBLE);
//					chkbox.setChecked(true);
//			//	}
//			//	Toast.makeText(activity, "click", Toast.LENGTH_LONG).show();
//			}
//		});
//        
//        image.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//			//	if(!isSelecting)
//			//	{
//					isSelecting=true;
//				//	chkbox.setVisibility(View.VISIBLE);
//					chkbox.setChecked(true);
//			//		Toast.makeText(activity, "long click", Toast.LENGTH_LONG).show();
//			//	}
//				return false;
//			}
//		});
//        
////        ImageButton delete=(ImageButton) vi.findViewById(R.id.delete_btn);
////        delete.setOnClickListener(new OnClickListener() {
////			
////			@Override
////			public void onClick(View v) {
////				// TODO Auto-generated method stub
////				data.remove(position);
////				notifyDataSetChanged();
////				notifyDataSetInvalidated();
//////				notify();
//////				notifyAll();
////			}
////		});
        
        
        return vi;
    }
    
    
   
}