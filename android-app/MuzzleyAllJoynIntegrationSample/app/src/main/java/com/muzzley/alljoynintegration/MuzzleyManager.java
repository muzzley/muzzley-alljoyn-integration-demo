package com.muzzley.alljoynintegration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.alljoyn.ns.Notification;
import org.alljoyn.ns.NotificationText;
import org.alljoyn.ns.sampleapp.IoeNotificationApplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.muzzley.lib.Activity;
import com.muzzley.lib.Muzzley;
import com.muzzley.lib.Participant;
import com.muzzley.lib.commons.Action;
import com.muzzley.lib.commons.Callback;
import com.muzzley.lib.commons.Content;
import com.muzzley.lib.commons.Response;

public class MuzzleyManager {
	public static interface Listener {
		public void onActivityCreated(Activity activity);
	}

	private Listener listener;
	
	public void setListener(Listener listener) {
		this.listener = listener;
		
		if (muzActivity != null && listener != null) {
			listener.onActivityCreated(muzActivity);
		}
	}
	
	
	private static final Gson gson;
	private static final List<String> deviceNames;
	
	private static final String APP_TOKEN = "caaf5ced77d90463";
	private static final String WEBVIEW_UUID = "318ef097-088a-4a3b-a913-42ed03e9b084";
	
	static {
		gson = new GsonBuilder()
			.registerTypeAdapter(Notification.class, new NotificationSerializer())
			.registerTypeAdapter(NotificationText.class, new NotificationTextSerializer())
			.create();
		deviceNames = new ArrayList<String>();
		deviceNames.add("Thermostat");
		deviceNames.add("Fridge");
		deviceNames.add("Coffee Maker");
		
	}
	
	public static class NotificationTextSerializer implements JsonSerializer<NotificationText> {
		@Override
	    public JsonElement serialize(final NotificationText notificationText, final Type type, final JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty("text", notificationText.getText());
			obj.addProperty("lang", notificationText.getLanguage());
			return obj;
	    }
	}
	
	public static class NotificationSerializer implements JsonSerializer<Notification> {
		@Override
	    public JsonElement serialize(final Notification notification, final Type type, final JsonSerializationContext context) {
	        JsonObject obj = new JsonObject();
		    obj.addProperty("appId", notification.getAppId().toString());
		    obj.addProperty("appName", notification.getAppName());
		    obj.addProperty("deviceId", notification.getDeviceId());
		    obj.addProperty("deviceName", notification.getDeviceName());
		    obj.addProperty("messageId", "" + notification.getMessageId());
		    obj.addProperty("messageType", notification.getMessageType().toString());
		    obj.addProperty("responseObjectPath", notification.getResponseObjectPath());
		    obj.addProperty("senderBusname", notification.getSenderBusName());
		    obj.add("text", context.serialize(notification.getText()));
		    obj.addProperty("version", notification.getVersion());		    	    
	       return obj;
	    }
	}

	private static final String TAG = "AllJoynMuzzleyActivity";
	
	private Context context;
	private List<String> filteredApps = new ArrayList<String>();
	
	private List<Participant> participants = new ArrayList<Participant>();
	private Activity muzActivity;
	private final Handler handler;
	
	public MuzzleyManager(Context context) {
		this.context = context;
		this.handler = new Handler(Looper.getMainLooper());
		Log.i(TAG, "Initializing Muzzley");
	}
		
	public void forwardNotification(final Notification notification) {
		JsonElement je = gson.toJsonTree(notification);
		
		Log.i(TAG, "Notification: " + gson.toJson(notification));

        final IoeNotificationApplication ioeApp = (IoeNotificationApplication) this.context.getApplicationContext();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ioeApp.getNotificationAdapter().insert(notification, 0);
            }
        });


        if (!filteredApps.isEmpty() && filteredApps.contains(notification.getAppName().toLowerCase())) {
			// The notification was sent from an app that belongs to the ignore list
			Log.i(TAG, "App that sent notification is being filtered. Aborting.");
			return;
		}

		Content c = new Content("alljoynNotification", je);
		for (Participant participant : participants) {
			participant.send(c);
		}
	}
	
	public void connect() {
		
		// Participant Joined Event Handler
		final Action<Participant> participantHandler = new Action<Participant>() {
			@Override
			public void invoke(Participant participant) {
				Log.i(TAG, "Muzzley Participant Joined: " + participant.name);

				participants.add(participant);
				
				// Load the AllJoyn Notification WebView Widget on all joining Muzzley participants
				JsonObject params = new JsonObject();
				params.addProperty("uuid", WEBVIEW_UUID);
				params.addProperty("orientation", "portrait");

				participant.changeWidget(
					"webview",
					params,
					new Action<Response>() {
						@Override
						public void invoke(Response response) {
							// TODO Auto-generated method stub
						}
					},
					new Action<Exception>() {
						@Override
						public void invoke(Exception ex) {
							// TODO Auto-generated method stub
						}
					}
				);
				
				participant.onSignal.add(new Callback<Content>() {
					@Override
					public void invoke(Content content) {
						Log.i(TAG, "Muzzley Signaling Message received: " + content.a);
						if ("alljoynFilter".equals(content.a)) {
							// content.d: { action: "add", name: "something" }
							// content.d: { action: "remove", name: "something" }
							
							if (!content.d.isJsonObject()) {
								Log.e(TAG, "Invalid alljoynFilter signaling message");
								return;
							}

							JsonObject obj = content.d.getAsJsonObject();
							JsonElement actionElement = obj.get("action");
							JsonElement nameElement = obj.get("name");
							if (!actionElement.isJsonPrimitive() || !nameElement.isJsonPrimitive()) {
								Log.e(TAG, "Invalid alljoynFilter signaling message");
								return;
							}
							
							String action = actionElement.getAsString();
							String name = nameElement.getAsString();
							
							Log.i(TAG, "ACTION: " + action);
							Log.i(TAG, "NAME: " + name);
							
							if ("add".equals(action)) {
								filteredApps.add(name.toLowerCase());
							}
							
							if ("remove".equals(action)) {
								filteredApps.remove(name.toLowerCase());
							}
						}
					}

					@Override
					public void invoke(Content content, Action<Response> response) {
						
						response.invoke(new Response(true));
					}
					
					@Override
					public void invoke(Exception ex) {
						// TODO Auto-generated method stub
					}
				});
			}
		};
		
		Log.i(TAG, "Initializing Muzzley2");
		
		Muzzley.connectApp(
			APP_TOKEN,
			new Action<Activity>() {
				@Override
				public void invoke(Activity activity) {
					// Muzzley App Connected
					participants.clear();
					
					activity.onQuit.add(new Action<Void>() {
						@Override
						public void invoke(Void arg0) {
							muzActivity = null;
						}
					});
					
					Log.i(TAG, "Initializing Muzzley3a");
					if (listener != null) {
						listener.onActivityCreated(activity);						
					}
					
					Log.i(TAG, "Initializing Muzzley4a");
					activity.onParticipant.add(participantHandler);
					
					muzActivity = activity;

				}
			},
			new Action<Exception>() {
				@Override
				public void invoke(Exception ex) {
					Log.i(TAG, "Initializing Muzzley Exception: " + ex.getMessage());					
				}
			}
		);
	}
	

}
