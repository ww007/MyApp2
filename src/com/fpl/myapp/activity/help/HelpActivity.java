package com.fpl.myapp.activity.help;

import com.fpl.myapp2.R;

import android.app.Activity;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class HelpActivity extends Activity {

	private TextView tvTitle;
	private ImageButton ibQuit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("°ïÖú");
		ibQuit = (ImageButton) findViewById(R.id.ib_top_quit);
		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
