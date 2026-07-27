package com.mycompany.frag;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.GridView;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import android.util.Log;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.GregorianCalendar;
import android.graphics.Color;
import android.app.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.util.Random;

public class MainActivity extends Activity 
{
	static final String url =  "http://friended-correspond.000webhostapp.com/app/";
	static final String PACKAGE = "com.mycompany.frag";
	static final float VERSION  = 1.8f;
	static final long UP_TIME  = (15*24*60*60*1000);
	
	String loc = "AudioBook.apk";
	String temp;
	FrameLayout f1;
	FragmentManager fm;
	ProgressBar pb;
	MyFrag mf;
	MyFrag2 mf2;
	MyFrag3 mf3;
	boolean connected = false;
	ArrayList<String> list,item;
	ArrayList<audiofiles> file;
	ArrayList<audibook> data;
	ArrayAdapter<String> ad;
	ArrayAdapter<String> ad2;
	SharedPreferences sp;
//	MyAdapter ad;
//	DatabaseHelper db;
	Cache cache;
	private static long back_pressed;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		f1 = (FrameLayout) findViewById(R.id.mainFrameLayout);
		pb = (ProgressBar) findViewById(R.id.mainProgressBar);
		sp = getSharedPreferences("setting",0);
		cache = new Cache(this);
		//db = new DatabaseHelper(this);
		list = new ArrayList<String>();
		item = new ArrayList<String>();
		file = new ArrayList<audiofiles>();
		data = new ArrayList<audibook>();
		query();
		mf = new MyFrag();
		mf2 = new MyFrag2();
		
		fm = getFragmentManager();
		

	
		
		/*
		if(check()==true)
		{
			ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
			start();
		}
		else
		{
			show("Not Connected");
		}
		
		*/
    }
    
    @Override
	protected void onStart()
	{
		// TODO: Implement this method
		super.onStart();
		if(fm.findFragmentById(R.id.mainFrameLayout)==null)
		{
			fm.beginTransaction().add(R.id.mainFrameLayout,mf).commit();
		}
		
		if(sp.getInt("uid",0)==0)
		{
			SharedPreferences.Editor edit = sp.edit();
			Random r = new Random();
			edit.putInt("uid",r.nextInt(1000));
			edit.apply();
			edit.commit();
				
		}
		
		if(System.currentTimeMillis() > sp.getLong("pro_time",0))
		{
			SharedPreferences.Editor edit = sp.edit();
			edit.putBoolean("pro",false);
			edit.apply();
			edit.commit();
		}
		
		if(check()==true) 
		{
			if(sp.getLong("last",System.currentTimeMillis()+6*24*60*60*1000) < System.currentTimeMillis())
			{

				update();

			}
			//else
		//	{
				
		/*		Log.i("Time : ",sp.getLong("last",System.currentTimeMillis()+(6*60*1000))/(1*60*1000)- (System.currentTimeMillis()/(1*60*1000))+" min Remaining"); */
			//}
		}
	}


	@Override 
	public boolean onCreateOptionsMenu(Menu menu) { 
		//inflate the menu resource file in your activity 
		getMenuInflater().inflate(R.menu.menu, menu); 
		return true; 
	} 

	@Override 
	public boolean onOptionsItemSelected(MenuItem item) { 
		//code for item select event handling 
		switch(item.getItemId()){ 
		
		case R.id.main_item1 : Intent i2 = new Intent(MainActivity.this,DownloadActivity.class);startActivity(i2); break; 
			case R.id.main_item2 : help(); break;
			case R.id.main_item3 : update(); break;
			case R.id.main_item4 : about(); break;
			case R.id.main_item5 : pro();break;
			case R.id.main_item6 : cache.clear(cache.size()); break;
			case R.id.main_item7 : apps();break;


		} 

		return super.onOptionsItemSelected(item); 
	} 
	
	@Override
	public void onBackPressed()
	{
		if(back_pressed+1000 > System.currentTimeMillis())
			super.onBackPressed();
			else
			{
				fm.beginTransaction().replace(R.id.mainFrameLayout,mf).commit();
				show("Quickly Double Click  To Exit");
				back_pressed = System.currentTimeMillis();
			}
		
	}

	private void show(String p0)
	{
		Toast.makeText(getApplicationContext(),p0,Toast.LENGTH_SHORT).show();
		
	}

	private boolean check()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni!=null && ni.isConnectedOrConnecting())
	    return true;
		//else
		return false;
	}
	
public class MyFrag extends Fragment
	{
		GridView g1;
		TextView t1;
		
		//String[] item = {"Rishav","Aman","Soyam"};
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			
			View v = inflater.inflate(R.layout.myfrag,container,false);
			g1 = (GridView)v.findViewById(R.id.g1);
			t1 = (TextView)v.findViewById(R.id.myfragTextView);
			t1.setTextColor(Color.parseColor(sp.getString("theme","#FFAB00")));
			ad = new ArrayAdapter<String>(getActivity(),R.layout.view,R.id.viewTextView,list);
		//	ad = new MyAdapter(getActivity(),list);
				g1.setAdapter(ad);
				
			if(list.isEmpty()==true)
			{
				if(check()==true)
			{
				t1.setVisibility(View.GONE);
				g1.setVisibility(View.VISIBLE);
				//start();
				Fetch f = new Fetch();
							f.execute("task1",url+"Audiobook/"+"content.json");
						
			//	g1.setAdapter(ad);
			}
			else
			{
				g1.setVisibility(View.GONE);
				t1.setVisibility(View.VISIBLE);
				show("Oops! An Error Ocurred Try Again");
				//t1.setText("No Internet ! \n\n Click me  To\n\n Refresh");
			}
				/*
			if(cache.exists("content.json")==false)
			{
				//show("list empty true "+list.size());
				//list.isEmpty()==true
				
			if(check()==true)
			{
				t1.setVisibility(View.GONE);
				g1.setVisibility(View.VISIBLE);
				//start();
				Fetch f = new Fetch();
							f.execute("task1",url+"Audiobook/"+"content.json");
						
			//	g1.setAdapter(ad);
			}
			else
			{
				g1.setVisibility(View.GONE);
				t1.setVisibility(View.VISIBLE);
				show("Oops! An Error Ocurred Try Again");
				//t1.setText("No Internet ! \n\n Click me  To\n\n Refresh");
			}
			}
			else
			{
				t1.setVisibility(View.GONE);
				g1.setVisibility(View.VISIBLE);
				load("task1","content.json");
			//	g1.setAdapter(ad);
				
			}
			*/
			}
			else
			{
				t1.setVisibility(View.GONE);
				g1.setVisibility(View.VISIBLE);
				
			}
			
			t1.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View p1)
					{
						if(check()==true)
						{
							t1.setVisibility(View.GONE);
							g1.setVisibility(View.VISIBLE);
							//start();
							Fetch f = new Fetch();
							f.execute("task1",url+"Audiobook/"+"content.json");
						
						//	g1.setAdapter(ad);
						}
						else
						{
							g1.setVisibility(View.GONE);
							t1.setVisibility(View.VISIBLE);
							//t1.setText("No Internet ! \n\n Click me  To\n\n Refresh");
							show("Oops! An Error Ocurred Try Again");
						}
						
					}
					
				
			});
			g1.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						
						fm.beginTransaction().replace(R.id.mainFrameLayout,mf2).commit();
						Bundle b = new Bundle();
						b.putString("src",file.get(p3).src);
						mf2.setArguments(b);
					}
				});
			
			return v;
		}
		
		
	
}
 
	public class MyFrag2 extends Fragment
	{
		GridView g2;
		ProgressBar p2;
		String s,s1="",b="";
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.inflate(R.layout.myfrag2,container,false);
			s = getArguments().getString("src");
			g2 = (GridView)v.findViewById(R.id.g2);
			p2 = (ProgressBar)v.findViewById(R.id.myfrag2ProgressBar);
			ad2 =  new ArrayAdapter<String>(getActivity(),R.layout.view,R.id.viewTextView,item);
				g2.setAdapter(ad2);
		//	if(s.equals(temp))
	//		{
			//	if(item.isEmpty()==true)
			//	{
					//p2.setVisibility(View.VISIBLE);
					if(cache.exists(s)==false)
					{
						Fetch f = new Fetch();
						f.execute("task2",url+"Audiobook/"+s,s);
					}
					else
					{
						load("task2",s);
					}
					
		//		}
				/*
				if(item.isEmpty()==true)
				{
					//p2.setVisibility(View.VISIBLE);
					Fetch f = new Fetch();
					f.execute("task2",url+"Audiobook/"+s,s);
				//g2.setAdapter(ad2);
				}
				*/
		//		else
		//		{
				//p2.setVisibility(View.GONE);
				//g2.setAdapter(ad2);
			//	}
			/*	
					
			}
			else
			{
				temp = s;
				if(cache.exists(s)==false)
				{
					Fetch f = new Fetch();
					f.execute("task2",url+"Audiobook/"+s,s);
					p2.setVisibility(View.GONE);
				}
				else
				{
					load("task2",s);
				}
			//	p2.setVisibility(View.VISIBLE);
			//	Fetch f = new Fetch();
				//f.execute("task2",url+"Audiobook/"+s,s);
				//g2.setAdapter(ad2);
			//	p2.setVisibility(View.GONE);
			}
			/*
			runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						//String p = task(url+"Audiobook/"+s);
						//parse2(p);
						Fetch f = new Fetch();
						f.execute("task2",url+"Audiobook/"+s);
					
					}

				});
				*/
			//	p2.setVisibility(View.GONE);
			g2.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						
						String a=data.get(p3).name,c=data.get(p3).src;
						
						Intent intent=new Intent(MainActivity.this,ReadActivity.class);
					intent.putExtra("s2",a);
					intent.putExtra("s3",c);
					startActivity(intent);
											
					}
				});
			
			return v;
		}
		
		
	}
	public class MyFrag3 extends Fragment
	{
		TextView t3;
		String s;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.inflate(R.layout.myfrag3,container,false);
			t3 = (TextView)v.findViewById(R.id.myfrag3TextView);
			s = getArguments().getString("send");
			t3.setText(s);
			return v;
		}
		
	}
	
	private void query()
	{
		if(android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(p);
		}
	}
	
	private void pro()
	{
	
		String ok = "Subscribe";
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		LinearLayout l1 = new LinearLayout(this);
		l1.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(this);
		final EditText et = new EditText(this);
		if(sp.getBoolean("pro",false)==false)
		tv.setText("Get All Pro Content For 24 Days at Rs.7 Only.\n\n To Activate Mail Your App Id to rishavkumar0631@gmail.com We Will Send You Pro Code \n");
		else
		{
			tv.setText("You are Pro User Enjoy Pro Content For "+((sp.getLong("pro_time",0)/(1*24*60*60*1000))-(System.currentTimeMillis()/(1*24*60*60*1000))+" Days \n"));
		
		  ok = "Okay";
		  et.setVisibility(View.GONE);
		}
		tv.setTextSize(19);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(Color.parseColor("#FFFFFF"));
		tv.setBackgroundColor(Color.parseColor(sp.getString("theme","#FFAB00")));
		et.setHint("Enter Pro Code : ");
		l1.addView(tv);
		l1.addView(et);
		ad.setView(l1);
		ad.setPositiveButton(ok,new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					String u = String.valueOf(sp.getInt("uid",0)*8);
					if(et.getText().toString().equals(u))
					{
						SharedPreferences.Editor edit = sp.edit();
						edit.putBoolean("pro",true);
						edit.putLong("pro_time",System.currentTimeMillis()+(24*24*60*60*1000));
						Random r = new Random();
						edit.putInt("uid",r.nextInt(1000));
						edit.apply();
						edit.commit();
						show("Activated");
					}
					else
					{
						if(sp.getBoolean("pro",false)==false)
							show("Please Check Your Code");
					}
				}
			});
		AlertDialog alertdialog = ad.create();
		alertdialog.show();
	}
	
	private void apps()
	{
		Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("http://friended-correspond.000webhostapp.com"));
		startActivity(i);
	}
	
	private void d(final String c,final String d)
	{
		Thread t = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					String b = task(url+"Audiobook/"+d);
					cache.Cache(d,b);
					b(b,c,d);
				}


			});t.start();
		
	}
	
	private void load(String s1,String s2)
	{
		if(s1.equals("task1"))
		{
			String p = cache.getCache(s2);
			try
			{
				JSONArray j = new JSONArray(p);
				for(int i=0;i<j.length();i++)
				{
					JSONObject j2 = j.getJSONObject(i);
					String n = j2.getString("name");
					String s = j2.getString("src");
					file.add(new audiofiles(n,s));
					list.add(n);
					//Thread.sleep(200);
				}
			}
			catch (Exception e)
			{
				//cache.cacheError(" Load1Err() "+e.toString());
			}

			ad.notifyDataSetChanged();
		}
		else
		{
			item.clear();
			data.clear();
			String p = cache.getCache(s2);
			try
			{
				JSONArray j = new JSONArray(p);
				for(int i=0;i<j.length();i++)
				{
					JSONObject j2 = j.getJSONObject(i);
					String n = j2.getString("name");
					String s = j2.getString("src");
					data.add(new audibook(n,s));
					item.add(n);
					
				}
			}
			catch (Exception e)
			{
				//cache.cacheError(" Load2Err() "+e.toString());
			}

			ad2.notifyDataSetChanged();
		}
		
	}

	private void parse3(String a)
	{
		float ver = 1.0f;
		
		try
		{
			JSONArray j = new JSONArray(a);
			for(int i=0;i<j.length();i++)
			{
				JSONObject j2 = j.getJSONObject(i);
				String name = j2.getString("package");
				if(name.equals(PACKAGE))
				{
					ver = Float.parseFloat(j2.getString("version"));
					loc = j2.getString("src");
					break;
				}
			}
			SharedPreferences.Editor e = sp.edit();
			e.putLong("last",System.currentTimeMillis()+(UP_TIME));
			e.commit();
			if(ver > VERSION)
			{
				runOnUiThread(new Runnable()
				  {
					@Override
					public void run()
					{
						Go();
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable()
				 {
					@Override
					public void run()
					{
						show("You Are Using Latest Version");
					}
				});
			}
		
		}
		catch(Exception e)
		{
			Log.e("UpdateErr()",e.toString());
			//cache.cacheError("UpdateErr() - "+e.toString());
		}

	}
	
	private void Go()
	{
		Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url+loc));
		startActivity(Intent.createChooser(i,"New Version is Available"));
	}
	
	private void b(final String b,final String a,final String c)
	{
		//Log.i("story",b);
		runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Intent intent=new Intent(MainActivity.this,ReadActivity.class);
					intent.putExtra("s1",b);
					intent.putExtra("s2",a);
					intent.putExtra("s3",c);
					startActivity(intent);
				}});
				/*
		Intent intent=new Intent(MainActivity.this,ReadActivity.class);
		intent.putExtra("s1",b);
		intent.putExtra("s2",a);
		intent.putExtra("s3",c);
		startActivity(intent);*/
	}
	
	private String task(String u)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL(u).openConnection().getInputStream()));
			String t;
			while((t=br.readLine())!=null)
			{
				sb.append(t).append("\n");
			}
		
		}

		catch (Exception e)
		{
			Log.e("DownErr()",e.toString());
			//cache.cacheError("DownErr()  "+e.toString());
		}

		return sb.toString().trim();
	}
	
	private void update()
	{
		show("Checking For Update");
		Thread t = new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					String url2 = task(url+"updateApp.json");
					parse3(url2);
				}
				
			
		});t.start();
	}
	
	private void help()
	{
		String str = "1.  Read Story in Online and Offline Modes \n\n "+ 
		"2.  For Offline You have to Save It First\n\n "+
		"3.  No Any more Data Used in Saving \n\n "+
		"4.  To delete select Delete From Menu then Click on it \n\n "+
		"5.  After deleting Please Go back to Stop Deleting \n\n"+
		"5.  AutoSpeak or Speak Story by Clicking On It \n\n "+
		"6.  Feeling Slow Speed Don't worry It will load Faster on Second Time \n\n"+
		"7.  You can Choose different TextStyle,TextSizes Background Color And Control Speech Speed \n\n"+
		"8. To Activate Pro Go to About Menu Check App Id and Mail it to Devloper Email. We will Send You Pro Code. Enter Pro Code and Enjoy Pro Features \n\n"+
		"9.  Regulary Update Your For Best Performance \n";
		

	    
	mf3 = new MyFrag3();	fm.beginTransaction().replace(R.id.mainFrameLayout,mf3).commit();
		Bundle b = new Bundle();
		b.putString("send",str);
		mf3.setArguments(b);
	}
	
	private void about()
	{
		String str = " App Name  :  हिंदीबुक \n\n"+
		             " App Version  :  "+VERSION +"\n\n"+
		             " App Id : "+"u_0"+sp.getInt("uid",0)+"\n\n"+
		             " Email Us : rishavkumar0631@gmail.com "+"\n\n"+
		             " Devloped By  :  Rishav Singh \n\n"+
					 " Company  :  Creative Glaxies \n\n"+
					 
					 " Website : http://friended-correspond.000webhostapp.com\n\n"+
					 " What's New ? \n\n All Bugs Fixed \n Manually Clear Cache \n Improved UI \n More Articles Added \n Auto Speaking Feature Added \n";
			
					 long next = sp.getLong("last",System.currentTimeMillis()+(6*24*60*60*1000))/(1*60*60*1000)- (System.currentTimeMillis()/(1*60*60*1000));
					 		 
		String app = "\n\nDevloper Use Only : \n \n Max Cache  : "+Math.round(cache.maxSize/1024/1024)+" MB"+"\n Clear Cache : "+Math.round(cache.DELETE/1024)+" KB"+"\n Cache Size : "+Math.round(((double)cache.size()/1024))+" KB\n Next Update In :  "+next + " Hours\n\n";
		
		//String err = cache.sendError();
		//((sp.getLong("long",System.currentTimeMillis()+6*60*1000)-System.currentTimeMillis())/1*60*1000)+" Hrs.\n";
		
		
	mf3 = new MyFrag3();	fm.beginTransaction().replace(R.id.mainFrameLayout,mf3).commit();
		Bundle b = new Bundle();
		b.putString("send",str+app);
		mf3.setArguments(b);
	}
	

	class Fetch extends AsyncTask<String,String,Void>
	{
		String p = "";
		String q = "empty.txt";
		@Override
		protected Void doInBackground(String[] p1)
		{
			if(p1[0].equals("task1"))
			{
					list.clear();
					file.clear();
				p = task(p1[1]);
				try
				{
					JSONArray j = new JSONArray(p);
					for(int i=0;i<j.length();i++)
					{
						JSONObject j2 = j.getJSONObject(i);
						String n = j2.getString("name");
						String s = j2.getString("src");
						publishProgress("task1",n,s);
					
					}
				}
				catch (Exception e)
				{
					Log.e("Task1Err()",e.toString());
					//cache.cacheError("Task1Err()  "+e.toString());
				}

				
			}
			else
			{
				item.clear();
			    data.clear();
			    
			    q = p1[2];
				p = task(p1[1]);
				try
				{
					JSONArray j = new JSONArray(p);
					for(int i=0;i<j.length();i++)
					{
						JSONObject j2 = j.getJSONObject(i);
						String n = j2.getString("name");
						String s = j2.getString("src");
						publishProgress("task2",n,s);
							Thread.sleep(100);
					}
					
				}
				catch (Exception e)
				{
					Log.e("Task2Err()",e.toString());
					
					//cache.cacheError(" Task2Err() "+e.toString());
			
				//	cache.cacheError("Task2Err() - "+e.toString());
				}
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(String[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			pb.setVisibility(View.GONE);
			if(values[0].equals("task1"))
			{
				file.add(new audiofiles(values[1],values[2]));
				list.add(values[1]);
				ad.notifyDataSetChanged();
				//cache.Cache("content.json",p);
			}
			else
			{
				data.add(new audibook(values[1],values[2]));
				item.add(values[1]);
				ad2.notifyDataSetChanged();
				cache.Cache(q,p);
			}
			
		}

		@Override
		protected void onPreExecute()
		{
			// TODO: Implement this method
			super.onPreExecute();
		pb.setVisibility(View.VISIBLE);
		//show("started");
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			// TODO: Implement this method
			super.onPostExecute(result);
			//show("Loaded Successfully");
			pb.setVisibility(View.GONE);
			//fm.beginTransaction().replace(R.id.mainFrameLayout,mf).commit();
		
		}
		
		
	}

}
class audibook
{
	
	String name;
	String src;
	public audibook(String name,String src)
	{
		this.name=name;
		this.src=src;

	}

} 

class audiofiles
{

	String name;
	String src;
	public audiofiles(String name,String src)
	{
		this.name=name;
		this.src=src;

	}

} 

class MyAdapter extends ArrayAdapter<String>{

    Context context;
    ArrayList<String> obj;
	
    public MyAdapter(Context context,ArrayList<String> obj) {
        super(context, R.layout.view);
        this.context = context;
        this.obj = obj;
    }

 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.viewTextView);
		textView.setText(obj.get(position));
		return view;
    }

}

class Cache
{
	File dir;
	Context c;
	static final long maxSize = 2048*1024L;
	static final long DELETE = 16*1024L;
	
	public Cache(Context c)
	{
		this.c = c;
		dir = c.getExternalCacheDir();
		if(!dir.exists())
            dir.mkdirs();
	}
	
	public String cacheDir()
	{
		return dir.toString();
	}
	
	public boolean exists(String s)
	{
		File f = new File(dir,s);
		if(f.exists())
			return true;
			else
				return false;
	}
	
	public String getCache(String s)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir,s))));
			String t;
			while((t=br.readLine())!=null)
			{
				sb.append(t).append("\n");
			}
			br.close();
			return sb.toString();
		}
		
		catch (Exception e)
		{
			Log.i("GetCacheErr()",e.toString());
		}
		return "";
	}
	
	public void Cache(String s,String d)
	{
		try
		{
		//	File f = new File(dir.getPath()+"/"+s);
		//	f.createNewFile();
		if(size()>maxSize)
		{
			clear(DELETE);
		}
			FileOutputStream fo = new FileOutputStream(new File(dir,s));
			fo.write(d.getBytes());
			fo.close();
			return;
		}
		catch (Exception e)
		{
			Log.e("SavingCacheErr()",e.toString());
		}
		return;
		}
		
		public boolean isSaved(String s)
	{
		File f = new File(ReadActivity.path+"/"+s);
		if(f.exists())
			return true;
		else
			return false;
	}
	
	public String getSaved(String s)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(ReadActivity.path+"/"+s))));
			String t;
			while((t=br.readLine())!=null)
			{
				sb.append(t).append("\n");
			}
			br.close();
			return sb.toString();
		}

		catch (Exception e)
		{
			Log.i("GetSavedErr()",e.toString());
		}
		return "";
	}
	
	public long size(){
		long l = 0;
        File[] files = dir.listFiles();
        if(files==null)
            return 0;
            
        for(File f:files)
            l = l + f.length();

            return l;
    }
    
	public void clear(long del){
		
		long deleted = 0;
		
        File[] files=dir.listFiles();
        if(files==null)
        {
        	Toast.makeText(c,"No Cache Found",Toast.LENGTH_SHORT).show();
            return;
        }
            
        for(File f : files)
		{
			deleted = deleted + f.length();
			f.delete();
			
			if(deleted>=del)
			{
			Toast.makeText(c,Math.round(((double)del/1024))+" KB Cache Cleared",Toast.LENGTH_SHORT).show();
				break;
				}		
		}
      return;
    }
    
    public void cacheError(String error)
	{
		GregorianCalendar c = new GregorianCalendar();
					String d = c.get(5)+"/"+c.get(2)+"/"+(c.get(1)-2000)+" "+c.get(11)+":"+c.get(12);
					
		try { 
			FileWriter fileWriter = new FileWriter(dir+"/log.txt",true); 
			fileWriter.write(d+" "+error+"\n\n"); 
			fileWriter.flush(); 
			fileWriter.close(); 
			// toast("Permission Added");
			//replace = replace.replace("$ACTIVITY$", str.substring(0, str.indexOf(".java"))); 
		
		} catch (Exception e) 
		{ 
			Log.e("cacheLogErr()",e.toString()); 
		} 
		
	}
	
	public String sendError()
	{
			StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir+"/log.txt"))));
			String t;
			while((t=br.readLine())!=null)
			{
				sb.append(t).append("\n");
			}
			br.close();
			return sb.toString();
		}

		catch (Exception e)
		{
			Log.i("sendLogErr()",e.toString());
		}
		return "";
	}
	
}
