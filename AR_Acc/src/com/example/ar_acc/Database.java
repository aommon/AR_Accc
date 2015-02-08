package com.example.ar_acc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
	private static final String DB_NAME = "Kmitl";  
    private static final int DB_VERSION = 1;  
     
    public static final String TABLE_NAME = "Place";  
   
    public static final String COL_PLACE = "place_name";  
    public static final String COL_LATITUDE = "latitude";  
    public static final String COL_LONGITUDE = "longitude";  	    
     
    Context context;  
     
    public Database(Context ctx) {  
    	super(ctx,DB_NAME, null, DB_VERSION);
        context = ctx;  
    }  
   
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE " + TABLE_NAME 
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "   
                + COL_PLACE + " TEXT, " + COL_LATITUDE + " DOUBLE, " 
                + COL_LONGITUDE + " DOUBLE);");  
           
        try {  
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(
                    "Database_v1_ms.csv")));  
            String readLine = null;  
            readLine = br.readLine();  

            try {  
                while ((readLine = br.readLine()) != null) {  
                	//Log.i("Data Input", readLine);
                    String[] str = readLine.split(",");  
                    db.execSQL("INSERT INTO " + TABLE_NAME 
                        + " (" + COL_PLACE + ", " + COL_LATITUDE 
                        + ", " + COL_LONGITUDE  
                        + ") VALUES ('" + str[0] 
                        + "', '" + str[1] + "', '" 
                        + str[2] + "');");   
                    
                    
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
       
    public void onUpgrade(SQLiteDatabase db, int oldVersion
            , int newVersion) {  
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);  
        onCreate(db);  
    }  
}
