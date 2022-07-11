package com.meide.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CheckValue extends StringUtils {
	public static synchronized Date strToDateXSSS(String datex){
		Date currentData = null;
		try{
			currentData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(datex);
		}catch (Exception e){
			e.printStackTrace();
		}
		return currentData;
	}
	public static synchronized String curDateXSSS(){
		String currentData = null;
		try{
			currentData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		}catch (Exception e){
			e.printStackTrace();
		}
		return currentData;
	}
	public static synchronized String getStringByObject(Object value){
		String result = "";
		try{
			//logger.info(value+";"+value.length());
			if(value==null||value.toString().equals(""))
				result = "";
			else
				result = value.toString();
		}catch(Exception e){
			result = "";
		}
		return result;
	}
	public static synchronized Integer getIntegerByObject(Object value){
		Integer result = 0;
		try{
			//logger.info(value+";"+value.length());
			if(value==null||value.toString().equals(""))
				result =0;
			else
				result = Integer.valueOf(value.toString());
		}catch(Exception e){
			result = 0;
		}
		return result;
	}
	public static synchronized Long getLongByObject(Object value){
		Long result = 0L;
		try{
			//logger.info(value+";"+value.length());
			if(value==null||value.toString().equals(""))
				result =0L;
			else
				result = Long.valueOf(value.toString());
		}catch(Exception e){
			result = 0L;
		}
		return result;
	}
	public static synchronized String checkString(String value){
		String result = "0";
		try{
			value = value.trim();
			//logger.info(value+";"+value.length());
			if(value==null||value.equals(""))
				value = "0";
			result = value;
		}catch(Exception e){
			result = "0";
			//logger.error(e.getMessage(),e);
		}
		return result;
	}
	public static synchronized float strtofloat(String value){
		float result = 0;
		try{
			value = value.trim();
			//logger.info(value+";"+value.length());
			if(value==null||value.equals(""))
				value = "0";
			result = Float.valueOf(value);
		}catch(Exception e){
			result = 0;
		}
		return result;
	}
	public static synchronized Integer strtofloattoInt(String value){
		Integer result = 0;
		try{
			value = value.trim();
			//logger.info(value+";"+value.length());
			if(value==null||value.equals(""))
				value = "0";
			result = Float.valueOf(value).intValue();
		}catch(Exception e){
			result = 0;
		}
		return result;
	}
	public static synchronized String substr(String value,int num){
		String result = "";
		try{
			value = value.trim();
			//logger.info(value+";"+value.length());
			if(value==null||value.equals(""))
				value = "0";
			if(value.length()>10)
				value=value.substring(0, num);
			result = value;
		}catch(Exception e){
			
		}
		return result;
	}
}
