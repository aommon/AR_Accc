package com.example.ar_acc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.R.array;
import android.R.string;
import android.location.Location;
import android.os.Bundle;

import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.ma;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.DataApi;


public class MainActivity extends Activity implements SensorEventListener, SurfaceHolder.Callback  , AutoFocusCallback {

	//camera
	Camera mCamera;
    SurfaceView mPreview;
    //---------Acc
    TextView textX, textY, textZ, textAlert,textDistance,textLat1,textLong1,textLat2,textLong2,textangle,textHeading,a3x,a3y;
    SensorManager sensorManager,compassSensor;
    Sensor accelerometer;
    //compass
    float degree,con_degree,azimuthInDegress;
    Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;

    
    //GPS
    LocationClient mLocationClient;
    double lat,lng,d_latitude,d_longitude;
    
    //Database
    SQLiteDatabase mDb;  
    Database mHelper;  
    Cursor mCursor_near,mCurcor;
    
    //nearby
    PointF a[] = new PointF[4];
    ArrayList<String> arr_list = new ArrayList<String>();
    ArrayList<String> all = new ArrayList<String>();
    
    //listview
    Button btn_show, clear_array;
    
    //gridview
    GridView gridView;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

      //database
        mHelper = new Database(this);
        mDb = mHelper.getWritableDatabase();  
        //mCurcor = mDb.rawQuery("SELECT * FROM " + Database.TABLE_NAME, null);
        
        
        //camera
        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // click autofocus
        mPreview.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCamera.autoFocus(MainActivity.this);
            }
        });
        
      //---------Acc
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        //compass
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);
        textAlert = (TextView) findViewById(R.id.textAlert);
        textDistance = (TextView) findViewById(R.id.text_distance);
        textangle = (TextView) findViewById(R.id.text_azimuth);
        textLat1 = (TextView) findViewById(R.id.textLat1);
        textLong1 = (TextView) findViewById(R.id.textLong1);
        textLat2 = (TextView) findViewById(R.id.textLat2);
        textLong2 = (TextView) findViewById(R.id.textLong2);
        textHeading = (TextView) findViewById(R.id.textHeading);        
        a3x = (TextView)findViewById(R.id.textView1);
        a3y = (TextView)findViewById(R.id.textView2);
        
        gridView = (GridView) findViewById(R.id.grid);
        
    	//GPS
        boolean result = isServicesAvailable();        
        if(result) {
            // ในเครื่องมี Google Play Services
        	mLocationClient = new LocationClient(this, mCallback, mListener);
        } else {
            // ในเครื่องไม่มี Google Play Services ติดตั้งอยู่
        	finish();
        }      
       
        //Haversine
        d_latitude = 13.7295883; //dorm
        d_longitude = 100.7750316;
        
        //button_listview
        btn_show = (Button)findViewById(R.id.btn_showplace); 
        clear_array = (Button)findViewById(R.id.btn_clearArray);
    }
    
    public void onResume() {
        Log.d("System","onResume");
        super.onResume();
        mCamera = Camera.open();
      //---------Acc
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(accelListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //compass
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);   
    }
    
    protected void onStart(){
    	super.onStart();
    	//GPS
    	mLocationClient.connect();

    }
    
    public void onPause() {
        Log.d("System","onPause");
        super.onPause();
        mCamera.release();
      //---------Acc
        sensorManager.unregisterListener(this);
    }
    
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
        mLocationClient.disconnect();
    }
    
    private void verymain(){
    	
    	if(azimuthInDegress > con_degree+2 || azimuthInDegress < con_degree-2){
    		 con_degree = azimuthInDegress;
    		 float left = nearby.degree_left(azimuthInDegress);
    	        float right = nearby.degree_right(azimuthInDegress);
    	        
    	        textHeading.setText("Heading: " + Float.toString(azimuthInDegress) + " degrees");
    	        textX.setText("right : "+ Float.toString(right));
    	        textY.setText("left : "+ Float.toString(left));
    		 
    		//harver
		   	 double distance = Harversine.haversine(lat, lng, d_latitude, d_longitude);
		//   	 textDistance.setText("distance : " + (String.format("%.8f", distance)));
		   	 
		   	 //azimuth
		   	 double angle = Azimuth.calAngle(lat, lng, d_latitude, d_longitude);
		   	 //textangle.setText("Angle : " + (String.format("%.8f", angle))); 
		   	
		/*   textLat1.setText("Latitude_S : " + (String.format("%.8f", lat)));
		   	 textLong1.setText("Longitude_S : " + (String.format("%.8f", lng)));
		     textLat2.setText("Latitude_D : " + (String.format("%.8f", d_latitude)));
		     textLong2.setText("Longitude_D : " + (String.format("%.8f", d_longitude)));   *
		*/   
    	
	    	//nearby
		   	 a = nearby.nearbyLaLong(lat, lng, con_degree);
		   	 mCursor_near = mDb.rawQuery("SELECT  " +  Database.COL_PLACE  + "," + Database.COL_LATITUDE + "," + 
	        		Database.COL_LONGITUDE  + " FROM " + Database.TABLE_NAME + " WHERE " +
	        		Database.COL_LATITUDE + " > " + a[2].x + " AND " +
	        		Database.COL_LATITUDE + " < " + a[0].x + " AND " +
	        		Database.COL_LONGITUDE + " < " + a[1].y + " AND " +
	        		Database.COL_LONGITUDE + " >= " + a[3].y + " ORDER BY " + Database.COL_LATITUDE + " , " + 
	        		Database.COL_LONGITUDE , null);    
	        
		   	 mCursor_near.moveToFirst();
	
		        textLat1.setText("a0x : " + (String.format("%.8f", a[0].x)));
		    	textLong1.setText("aoy : " + (String.format("%.8f", a[0].y)));
		        textLat2.setText("a1x : " + (String.format("%.8f", a[1].x)));
		        textLong2.setText("a1y : " + (String.format("%.8f", a[1].y)));
		        textDistance.setText("a2x : " + (String.format("%.8f", a[2].x)));
		        textangle.setText("a2y : " + (String.format("%.8f", a[2].y))); 		        
		        a3x.setText("a3x : " + (String.format("%.8f", a[2].x)));
		        a3y.setText("a3y : " + (String.format("%.8f", a[2].y)));
		
		        if(mCursor_near.moveToFirst()){
		        	arr_list.clear();
		        	all.clear();
		        	do{
			        	double pl_distance = Harversine.haversine(lat, lng, mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LATITUDE)), 
			        							mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LONGITUDE)));
			        	
			        	double pl_azimuth = Azimuth.initial(lat, lng, mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LATITUDE)), 
								mCursor_near.getDouble(mCursor_near.getColumnIndex(Database.COL_LONGITUDE)));
			        	
			        	if(pl_azimuth > nearby.degree_right(con_degree) && pl_azimuth < nearby.degree_left(con_degree)){
				        //if(pl_azimuth > 100 && pl_azimuth < 150){

			        		arr_list.add("ชื่อ : " + mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_PLACE)) + " " +
				    				//mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_LATITUDE)) + "," +
				    				//mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_LONGITUDE)) + " aaa" +
				    				//pl_distance + "bbb " + 
				    				pl_azimuth); 
				        	//Log.e("ri", ""+mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_PLACE)));
				        	//Log.e("le", ""+nearby.degree_left(con_degree));
			        	}
			        	
			        	all.add("ชื่อ : " + mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_PLACE)) + " " +
			    				//mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_LATITUDE)) + "," +
			    				//mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_LONGITUDE)) + " aaa" +
			    				//pl_distance + "bbb " + 
			    				pl_azimuth); 

			        	
			        	//Log.e("all_pl", "" + mCursor_near.getString(mCursor_near.getColumnIndex(Database.COL_PLACE)));
			        	
		        	} while (mCursor_near.moveToNext());
		        }
	        

	        ArrayAdapter<String> adapter_place = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr_list);
	        gridView.setAdapter(adapter_place);
	        
	        clear_array.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(getApplicationContext(),ListViewClass.class);
					i.putExtra("Place", arr_list);
					startActivity(i);
				}
			});
	        
	        btn_show.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
	
					Intent i = new Intent(getApplicationContext(),ListViewClass.class);
					i.putExtra("Place", all);
					startActivity(i);
					
				}
			}); 
    	} else {
    		//Log.e("not changed", degree + "aom" + con_degree);
    	}
    	
    	
    	
        
        
        
    }
   
    
    //public void onAccuracyChanged(Sensor sensor, int acc) { }
    
    @Override
	public void onSensorChanged(SensorEvent event) {

 
		//compass
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values;
           
        }
          if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
          if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
              float orientation[] = new float[3];
              SensorManager.getOrientation(R, orientation);
              degree = orientation[0]; // orientation contains: azimut, pitch and roll
              //Log.e("azi", "" + degree);
              azimuthInDegress = (float)Math.toDegrees(degree);
              if (azimuthInDegress < 0.0f) {
                  azimuthInDegress += 360.0f;
              }
            }
          }
    	
          //Log.e("azi", "" + azimuthInDegress);
          
        
        verymain();
	}
    
    //GPS
    private ConnectionCallbacks mCallback = new ConnectionCallbacks() {
        public void onConnected(Bundle bundle) {
            // เชื่อมต่อกับ Google Play Services ได้
        	Toast.makeText(MainActivity.this, "Services connected", Toast.LENGTH_SHORT).show();

            LocationRequest mRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000).setFastestInterval(1000);

            mLocationClient.requestLocationUpdates(mRequest, locationListener);
        }

        public void onDisconnected() {
            // หยุดเชื่อมต่อกับ Google Play Services
        	Toast.makeText(MainActivity.this, "Services disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnConnectionFailedListener mListener = new OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult result) {
            // เมื่อเปิดปัญหาเชื่อมต่อกับ Google Play Services ไม่ได้
        	Toast.makeText(MainActivity.this, "Services connection failed", Toast.LENGTH_SHORT).show();
        }
    };
    private boolean isServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        	LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());
        	lat = location.getLatitude();
        	lng = location.getLongitude();
        	
        	verymain();
         
		}       
    };
    
    
    

//camera
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();

        int preview_index = ImageMaxSize.maxSize(previewSize);
        int picture_index = ImageMaxSize.maxSize(pictureSize);
        
        
        params.setPictureSize(previewSize.get(preview_index).width, previewSize.get(preview_index).height);
        params.setPreviewSize(previewSize.get(picture_index).width,previewSize.get(picture_index).height);
               
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d("CameraSystem","surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) { }
   
    
    public void onAutoFocus(boolean success, Camera camera) {
        Log.d("CameraSystem","onAutoFocus");
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
