package com.muzzley.alljoynintegration;

import java.util.List;

import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.ns.sampleapp.NotificationServiceControlsActivity;
import org.alljoyn.ns.sampleapp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAdapter extends ArrayAdapter<Notification> {
	
	private static final String TAG = "ioe" + NotificationAdapter.class.getSimpleName();
	
	public NotificationAdapter(Context context, int resource) {
		super(context, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG,"Notification Adapter getView() called.");
		
		LayoutInflater inflater = (LayoutInflater) this.getContext()
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.activity_notification_item, parent, false);
	    
	    Notification notification = getItem(position);
	    List<NotificationText> notificationTextList = notification.getText();
	    
	    TextView timeAgo = (TextView) rowView.findViewById(R.id.alljoyn_notification_time_ago);
	    TextView deviceName = (TextView) rowView.findViewById(R.id.alljoyn_notification_device_name);
	    TextView text = (TextView) rowView.findViewById(R.id.alljoyn_notification_text);

        ImageView icon = (ImageView) rowView.findViewById(R.id.alljoyn_notification_icon);

        if ("emergency".equalsIgnoreCase(notification.getMessageType().name())) {
            icon.setImageResource(R.drawable.icon_warning);
        } else {
            icon.setImageResource(R.drawable.icon_message);
        }
	
	    deviceName.setText(notification.getDeviceName());
	    // timeAgo.setText(String.valueOf(notification.getMessageId()));
	    if (notificationTextList != null && notificationTextList.size() > 0) {
	    	text.setText(notification.getText().get(0).getText());
	    }
	    return rowView;
	}
}