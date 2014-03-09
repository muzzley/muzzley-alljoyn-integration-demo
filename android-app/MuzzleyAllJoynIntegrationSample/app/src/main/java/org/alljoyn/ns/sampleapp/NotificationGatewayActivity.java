package org.alljoyn.ns.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.muzzley.alljoynintegration.MuzzleyManager;
import com.muzzley.alljoynintegration.DownloadImageTask;

public class NotificationGatewayActivity extends Activity implements MuzzleyManager.Listener {

    private static final String TAG = "ioe" + NotificationGatewayActivity.class.getSimpleName();

    private IoeNotificationApplication myApp;

    private ImageView qrCodeImage;
    private TextView activityIdText;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        myApp = (IoeNotificationApplication)getApplicationContext(); //Object to my application

        setContentView(R.layout.activity_notification_gateway);

        qrCodeImage = (ImageView) this.findViewById(R.id.qr_code);
        activityIdText = (TextView) this.findViewById(R.id.activity_id);

		ListView notificationsListView = (ListView) findViewById(R.id.alljoyn_notifications_layout);
		notificationsListView.setAdapter(myApp.getNotificationAdapter());

        final NotificationGatewayActivity self = this;
        ImageView iconHome = (ImageView) this.findViewById(R.id.icon_home);
        iconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(self, NotificationServiceControlsActivity.class));
            }
        });
    }

    /**
     * Called when the Activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        myApp.setBackground(false);
        myApp.getMuzzleyManager().setListener(this);
    }//onResume

    /**
     * Called when the activity is going to be paused, i.e BG
     */
    @Override
    protected void onPause() {
        super.onPause();
        myApp.setBackground(true);
        myApp.getMuzzleyManager().setListener(null);
    }//onPause

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification_gateway, menu);
		return true;
	}

    @Override
    public void onActivityCreated(com.muzzley.lib.Activity activity) {
        setActivityInfo(activity.id, activity.qrCodeUrl);
    }

    public void setActivityInfo(final String activityId, final String urlSrc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityIdText.setText(activityId);
                new DownloadImageTask(qrCodeImage).execute(urlSrc);
            }
        });
    }

}
