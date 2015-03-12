package org.thezero.bitzero;

import android.graphics.Color;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends ActionBarActivity {

	private String label, addr;
	private EditText etlabel, etaddr;
	private Button ok, close;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		label = this.getIntent().getStringExtra("label");
		addr = this.getIntent().getStringExtra("addr");
		etlabel = ((EditText)findViewById(R.id.etIp));
		etaddr = ((EditText)findViewById(R.id.etDomain));

		if(label.isEmpty() && addr.isEmpty()){
			Toast.makeText(getApplicationContext(), getString(R.string.empty), Toast.LENGTH_LONG).show();
			NavUtils.navigateUpFromSameTask(EditActivity.this);
		} else {
			etaddr.setText(addr);
			etlabel.setText(label);
		
			close = ((Button)findViewById(R.id.btnClose));
			ok = ((Button)findViewById(R.id.btnOk));
			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!etlabel.getText().toString().isEmpty() && !etaddr.getText().toString().isEmpty()){
						if(MainActivity.addr.containsKey(etaddr)){
							MainActivity.addr.remove(etaddr);
						}
						MainActivity.addr.put(etaddr.getText().toString(),etlabel.getText().toString());
						
						NavUtils.navigateUpFromSameTask(EditActivity.this);
					} else {
						Toast.makeText(getApplicationContext(), getString(R.string.empty), Toast.LENGTH_LONG).show();
					}
				}
			});
			
			close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					NavUtils.navigateUpFromSameTask(EditActivity.this);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				//NavUtils.shouldUpRecreateTask(this, new Intent(this,MainActivity.class));
		}
		return false;
	}
}
