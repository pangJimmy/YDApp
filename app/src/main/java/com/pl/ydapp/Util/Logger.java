package com.pl.ydapp.Util;

import android.util.Log;

public class Logger {

	public static boolean debug = true ; 
	
	public static void  e(String tag , String info){
		if(debug){
			Log.e(tag, info) ;
		}
	}
}
