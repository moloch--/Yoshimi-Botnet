package com.redactedlabs.yoshimi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class Http {

	private static final String TAG = "Http";
	private static final String HEADER = "All glory to the hypnotoad";

	public static String GET(String uri, String uuid) {
		Log.d(TAG, "GET: " + uri);
		HttpResponse response = null;
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setHeader("Meme", URLEncoder.encode(HEADER));
			request.setHeader("Uuid", URLEncoder.encode(uuid));
			request.setURI(new URI(uri));
			response = client.execute(request);
		} catch (IOException error) {
			Log.e(TAG, "I/O Error: " + error.toString());
		} catch (URISyntaxException error) {
			Log.e(TAG, "Syntax Error: " + error.toString());
		} catch (Exception error) {
			Log.e(TAG, "Error: " + error.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException error) {
					Log.e(TAG, error.toString());
				}
			}
		}
		return httpResponseToString(response);
	}

	public static String POST(String uri, String uuid, List<NameValuePair> parameters) {
		Log.d(TAG, "POST: " + uri);
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(uri);
	    httppost.setHeader("Meme", HEADER);
	    httppost.setHeader("Uuid", uuid);
	    HttpResponse response = null;
	    try {
	        httppost.setEntity(new UrlEncodedFormEntity(parameters));
	        response = httpclient.execute(httppost);
	    } catch (ClientProtocolException error) {
	        Log.e(TAG, "Protocol Error: " + error.toString());
	    } catch (IOException error) {
	    	Log.e(TAG, "I/O Error: " + error.toString());
	    } catch (Exception error) {
	    	Log.e(TAG, "Error: " + error.toString());
	    }
	    return httpResponseToString(response);
	}
	
	private static String httpResponseToString(HttpResponse response) {
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer("");
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			String newLine = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				buffer.append(line + newLine);
			}
			in.close();
		} catch (IOException error) {
			Log.d(TAG, error.toString());
		}
		return buffer.toString();
	}
}