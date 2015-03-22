package org.thezero.bitzero;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jooik.tabbeddialog.fragments.FragmentDialog;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.thezero.bitzero.adapters.CardAdapter;
import org.thezero.bitzero.address.Address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	
	public static Map<String, String> addr = new HashMap<>();
    public static AlertDialog idia;
    static MainActivity ma;
    public static FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private static SwipeRefreshLayout refreshLayout;
    private static CardAdapter cardAdapter;
    private List<Address> address = new ArrayList<>();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ma=this;
		setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutParams);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        cardAdapter = new CardAdapter(this, address);
        mRecyclerView.setAdapter(cardAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(int newState) {
            }

            public void onScrolled(int dx, int dy) {
                refreshLayout.setEnabled(layoutParams.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

		// Load address from storage
		loadArray(addr);
		
		// Check if there are address
		if(addr.size()<1){
			// BitZero is not a wallet
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage(R.string.dialog_msg_bitzero)
				.setTitle(R.string.dialog_title_bitzero)
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   dialog.dismiss();
	               }
	           }).create().show();
			
		} else {
			
			// Let's do some background stuff
			Refresh();
		}

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsertDialog();
            }
        });

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	LayoutInflater inflater = this.getLayoutInflater();
    	final View v = inflater.inflate(R.layout.dialog_address, null);
    	v.findViewById(R.id.imageButtonQr).setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			idia.dismiss();
    			IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
    			integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    		}
    	});
    	builder.setView(v)
    		.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
    		@Override
               public void onClick(DialogInterface dialog, int id) {
            	   	String name_dialog = ((EditText)v.findViewById(R.id.dialog_name)).getText().toString();
            	   	String address_dialog = ((EditText)v.findViewById(R.id.dialog_address)).getText().toString();
            	   	if(!address_dialog.isEmpty()){
            	   
            	   		Toast.makeText(getApplicationContext(), address_dialog, Toast.LENGTH_LONG).show();

            	   		addr.put(address_dialog,name_dialog);
            	   		saveArray(addr);

            	   		Refresh();
            	   		
            	   	}else{
            	   		showDialog(R.string.empty,getString(R.string.empty_dialog));
            	   	}
                	((EditText)idia.findViewById(R.id.dialog_name)).setText("");
                	((EditText)idia.findViewById(R.id.dialog_address)).setText("");
        	   		dialog.dismiss();
                    fab.show();
               }
           })
           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
            	   dialog.dismiss();
                   fab.show();
               }
           });
    	idia = builder.create();
	}
	
	@Override
	protected void onPause() {
        super.onPause();
        saveArray(addr);
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	saveArray(addr);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (result != null) {
			String contents = result.getContents();
			if (contents != null) { 
				String[] qr = QrParse(contents);
				if(qr[1].isEmpty()){
					showDialog(R.string.result_failed, getString(R.string.result_failed_why));
				}else{
					showInsertDialog(qr[0],qr[1]);
				}
			} else {
				showDialog(R.string.result_failed, getString(R.string.result_failed_why));
			}
		}
        if(!fab.isVisible()){
            fab.show();
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_about:
                FragmentManager fm = getSupportFragmentManager();
                FragmentDialog overlay = new FragmentDialog();
                overlay.show(fm, "FragmentDialog");
				return true;
		}
		return false;
	}
    
	public boolean saveArray(Map<String,String> a){
	    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor mEdit1 = sp.edit();
	    mEdit1.putInt("Address_size", a.size()); /* sKey is an array */ 
	    
	    int i=0;

	    for (Map.Entry<String, String> entry : a.entrySet()) {
	    	mEdit1.remove("Address_" + i);
	    	mEdit1.putString("Address_" + i, entry.getKey());
	    	mEdit1.remove("Name_" + i);
	    	mEdit1.putString("Name_" + i, entry.getValue()); 
			i++;
		}
	    
	    return mEdit1.commit();     
	}
	
	public void loadArray(Map<String,String> a){  
	    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
	    a.clear();
	    int sizea = sp.getInt("Address_size", 0);

	    for(int i=0;i<sizea;i++) 
	    {
	        a.put(sp.getString("Address_" + i, null),sp.getString("Name_" + i, null));
	    }
	}

    private static Integer count;
    private static Integer before;

    private class Fetch extends AsyncTask<String,Void,Void> {
    	private Address coinAddr;
    	
		protected Void doInBackground(String... param) {
			String val=Address.parseValuta(param[1]);
			if(isInternetAvailable(MainActivity.this)){
				aparse(param[0],param[1],val);
			}else{
				coinAddr = new Address(val,param[0],param[1]);
				view();
			}
			return null;
		}
		
		protected void aparse(final String l, String a,String val) {	
			try {
				if(val.equals(Address.Val[1][0])){
					String read = request("http://blockchain.info/address/" + a + "?format=json");
					JSONObject jsono;
					try {
						jsono = new JSONObject(read);
						coinAddr = new Address(Address.Val[1][0],l,a,jsono.getInt("n_tx"),jsono.getDouble("final_balance"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else if(val.equals(Address.Val[1][1])){
                    double balance = Double.valueOf(request("http://explorer.litecoin.net/chain/Litecoin/q/addressbalance/"+a));
                    coinAddr = new Address(Address.Val[1][1],l,a,-1,balance);
				}else if(val.equals(Address.Val[1][2])){
					double balance = Double.valueOf(request("http://dogechain.info/chain/Dogecoin/q/addressbalance/"+a));
					coinAddr = new Address(Address.Val[1][2],l,a,-1,balance);
				}else if(val.equals(Address.Val[1][3])){
					double balance = Double.valueOf(request("http://darkgamex.ch:2751/chain/Zetacoin/q/addressbalance/"+a));
					coinAddr = new Address(Address.Val[1][3],l,a,-1,balance);
				}
				view();
			} catch (Exception ignored){
				
			}
			
		}
    
	    protected void view() {
            cardAdapter.add(coinAddr, cardAdapter.getItemCount());
            mRecyclerView.scrollToPosition(cardAdapter.getItemCount());
            count=count+1;
		}

        protected void onPreExecute() {
            before=addr.size();
        }
		
		protected void onPostExecute(Void result) {
            if(count>=before){
                refreshLayout.setRefreshing(false);
            }
        }
	}
    
    private void showDialog(int title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
    
    private void showInsertDialog() {
    	idia.show();
        fab.hide();
    }
    
    private void showInsertDialog(String n, String a) {
    	if(idia != null) {
    		try {
    		    ((EditText)idia.findViewById(R.id.dialog_name)).setText(n);
    		    ((EditText)idia.findViewById(R.id.dialog_address)).setText(a);
    		    idia.show();
                fab.hide();
    		} catch (Exception ignored) {
  
    		}
    	}
    }
    
    public static void Refresh() {
        if(cardAdapter.getItemCount()>=1){
            cardAdapter.removeAll();
        }
        count=0;
    	for (Map.Entry<String, String> entry : addr.entrySet()) {
			(ma.new Fetch()).execute(entry.getValue(),entry.getKey());
		}
    }
    
    public String request(String URL) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(MainActivity.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			runOnUiThread(new Runnable() {
			    public void run() {
			    	AlertDialog.Builder dbuilder = new AlertDialog.Builder(MainActivity.this);
					dbuilder.setMessage(R.string.dialog_msg_uhost)
					.setTitle(R.string.dialog_title_uhost)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}).create().show();
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
    }
    
    public static boolean isInternetAvailable(Context context){
        NetworkInfo info = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null){
             return false;
        } else{
            if(info.isConnected()){
                return true;
            } else{
        		HttpClient client = new DefaultHttpClient();
        		HttpGet httpGet = new HttpGet("http://google.com/");
        		try {
        			HttpResponse response = client.execute(httpGet);
        			StatusLine statusLine = response.getStatusLine();
        			int statusCode = statusLine.getStatusCode();
                    return statusCode == 200;
        		} catch (ConnectException e) {
        			return false;
        		} catch (Exception e) {
        			return false;
        		}
                
            }
        }
    }
	
	public static void encodeBarcode(CharSequence type, CharSequence data) {
    	IntentIntegrator integrator = new IntentIntegrator(MainActivity.ma);
	    integrator.shareText(data, type);
    }
    
    public String[] QrParse(String qrtext) {
    	String[] r = {"",""};
    	if(qrtext.split(":").length>1){
    		String b=qrtext.split(":")[1];
	    	String addr_id = b.substring(0,34);
	    	String label = "Qr";
	    	if(b.length() > 34){
	    		String param = qrtext.split(":")[1].substring(35);
		    	String[] p = param.split("=");
		    	
		    	Map<String,String> amap = new HashMap<>();
		    	for(int i=0;i<p.length;i++){
		    		if(i%2==0){
		    			p[i]=p[i].replace("?","");
		    			amap.put(p[i], p[i+1]);
		    		}
		    	}
		    	try{
			    	if (!(amap.get("label").isEmpty())){
			    		label=amap.get("label");
			    	}
		    	} catch(Exception e){
		    		e.printStackTrace();
		    	}
	    	}
            return new String[]{label,addr_id};
    	}else{
    		if(qrtext.length()==34){
    			for(int i=0;i<Address.Val[1].length;i++){
    				if(qrtext.startsWith(Address.Val[2][i])){
                        return new String[]{"",qrtext};
    				}
    			}
    		}
    	}
    	return r;
    }
}
