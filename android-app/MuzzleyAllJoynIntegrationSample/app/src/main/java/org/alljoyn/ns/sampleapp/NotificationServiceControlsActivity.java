/******************************************************************************
 * Copyright (c) 2013, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

package org.alljoyn.ns.sampleapp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.alljoyn.ns.NotificationServiceException;
import org.alljoyn.ns.NotificationText;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.muzzley.alljoynintegration.DownloadImageTask;

public class NotificationServiceControlsActivity extends Activity implements OnClickListener {

    private static final String TAG = "ioe" + NotificationServiceControlsActivity.class.getSimpleName();
	
	/*
	 * Spinners
	 */
	private Spinner messageTypeSpinner;
	private Spinner languageSpinnerConsumer;
	private Spinner languageSpinnerProducer;
	
	/*
	 * Consumer and producer checkboxes
	 */
	private CheckBox consumerChbId;
	private CheckBox producerChbId;
	
	/*
	 * Notification message text boxes
	 */
	private EditText msg1Id;
	private EditText msg2Id;
	
		
	/**
	 * Reference to ttl text edit
	 */
	private EditText ttlEditText;
	
	/**
	 * Application name
	 */
	private EditText appName;
	
	/*
	 * Notification receiver list view and adapter
	 */
	private ListView lstv1Id;
	private static List<Map<String, String>> notificationItemsList = new LinkedList<Map<String,String>>();
	private ListAdapter listAdapter;
	
	/*
	 * Notification list view item keys.
	 */
	private static final String MSG_TYPE 	        = "MSG_TYPE";
	private static final String TEXT                = "TEXT";
	private static final String RICH_ICON_URL       = "RICH_ICON_URL";
	private static final String RICH_AUDIO_URL      = "RICH_AUDIO_URL";
	private static final String RICH_ICON_OBJ_PATH  = "RICH_ICON_OBJ_PATH";
	private static final String RICH_AUDIO_OBJ_PATH = "RICH_AUDIO_OBJ_PATH";
	
	/**
	 * Reference to this Application object
	 */
	private IoeNotificationApplication myApp;
	
	/**
	 * Stores this Activity UI cache
	 */
	private static final Map<String, Object> actCache = new HashMap<String, Object>();
	
	/**
	 * Reference to serviceControlButton
	 */
	private Button shutdownButton;
	
	/**
	 * Reference to send button
	 */
	private Button sendButton;
	
	/**
	 * Reference to delete button
	 */
	private Button deleteButton;

	/**
	 * Reference to icon rich content check box
	 */
	private CheckBox iconRichCheck;
	
	/**
	 * Reference to audio rich content check box
	 */
	private CheckBox audioRichCheck;
	
	/**
	 * Reference to icon ObjPath rich content check box
	 */
	private CheckBox iconObjPathRichCheck;
	
	/**
	 * Reference to audio ObjPath rich content check box
	 */
	private CheckBox audioObjPathRichCheck;
	
	/**
	 * Reference to CPS object path check box
	 */
	private CheckBox cpsObjPath;
	
	/**
	 * Producer related Layouts
	 */
	private int [] producer_lo_arr = {R.id.lo_prod_msg1, R.id.lo_prod_msg2, R.id.lo_prod_msgtype, R.id.lo_prod_colors, R.id.lo_prod_sendDelete};

	/*************************************/
	
	/**
	 * Called when the activity is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"Activity has been created");
		setContentView(R.layout.activity_main);
		myApp = (IoeNotificationApplication)getApplicationContext(); //Object to my application

		ImageView gotoGatewayActivity = (ImageView) this.findViewById(R.id.open_home_gateway_notification);
		// final NotificationServiceControlsActivity self = this;
		gotoGatewayActivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
                //startActivity(new Intent(self, NotificationGatewayActivity.class));
            }
        });

	}//onCreate

	/**
	 * Called on started activity
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG,"Starting application");
		myApp.setSampleAppActivity(this);
		setUI();
		//myApp.onActivityLoaded();
	}//onStart
	
	/**
	 * Called when the Activity is resumed
	 */
	@Override
	protected void onResume() {
		super.onResume();
		myApp.setBackground(false);
		Log.d(TAG, "Resume - app is in fg");
	}//onResume
	
	/**
	 * Called when the activity is going to be paused, i.e BG
	 */
	@Override
	protected void onPause() {
		super.onPause();
		myApp.setBackground(true);	
		Log.d(TAG,"Pause - app is in bg, storing state");
		
		actCache.put("PROD_CHK_BTN", producerChbId.isChecked());
		actCache.put("CONS_CHK_BTN", consumerChbId.isChecked());
		actCache.put("SRVC_CNTRL_BTN", shutdownButton.isEnabled());
		actCache.put("APP_NAME", appName.getText().toString());
	}//onPause
	
	
	/**
	 * Build options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}//onCreateOptionsMenu

	/**
	 * Initialize UI
	 */
	private void setUI() {
		Log.d(TAG,"Setting UI");
		setSpinners();
		
		//set application name reference
		appName = (EditText)findViewById(R.id.app_Name);
		
		//set ttl edit text
		ttlEditText = (EditText)findViewById(R.id.et_ttl);
		
		//set message text boxes
		msg1Id = (EditText)findViewById(R.id.msg_1);
		msg2Id = (EditText)findViewById(R.id.msg_2);
		prepareMsgTextBox(msg1Id);
		prepareMsgTextBox(msg2Id);
		
		//set Button(s)
		shutdownButton = (Button) findViewById(R.id.b_shutdown);
		shutdownButton.setOnClickListener(this);
		
		sendButton   = (Button) findViewById(R.id.b_send);
		deleteButton = (Button) findViewById(R.id.b_delete);
		sendButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		
		//set the Producer&&Consumer checkboxes id's
		consumerChbId = (CheckBox) findViewById(R.id.chb_consumer);
		producerChbId = (CheckBox) findViewById(R.id.chb_producer);
		consumerChbId.setOnClickListener(this);
		producerChbId.setOnClickListener(this);
		
		//set the rich content of icon & audio checkbox id's
		audioRichCheck        = (CheckBox) findViewById(R.id.chb_audio);
		iconRichCheck         = (CheckBox) findViewById(R.id.chb_icon);
		audioObjPathRichCheck = (CheckBox) findViewById(R.id.chb_audio_obj_path);
		iconObjPathRichCheck  = (CheckBox) findViewById(R.id.chb_icon_obj_path);
		audioRichCheck.setOnClickListener(this);
		iconRichCheck.setOnClickListener(this);
		audioObjPathRichCheck.setOnClickListener(this);
		iconObjPathRichCheck.setOnClickListener(this);
		
		//set the CPS obj path checkbox id's
		cpsObjPath = (CheckBox) findViewById(R.id.chb_cpsPath);
		cpsObjPath.setOnClickListener(this);
		
		//set the lstv_1 ListView id's
		lstv1Id = (ListView) findViewById(R.id.lstv_1);
		lstv1Id.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		
		String[] from = {MSG_TYPE, TEXT, RICH_ICON_URL, RICH_AUDIO_URL, RICH_ICON_OBJ_PATH, RICH_AUDIO_OBJ_PATH};
		int[] to      = {R.id.tv_nitem_msgtype, R.id.tv_nitem_text, R.id.tv_nitem_rich_icon, R.id.tv_nitem_rich_audio, R.id.tv_nitem_rich_icon_ObjPath, R.id.tv_nitem_rich_audio_ObjPath};
		
		listAdapter = new SimpleAdapter(this, notificationItemsList, R.layout.notification_item, from, to);
		lstv1Id.setAdapter(listAdapter);
		lstv1Id.setSelection(lstv1Id.getCount() - 1);
	
		shutdownButton.setEnabled(false);
		setProducerLayout(false);
		setConsumerLayout(false);
		
		//Check persistence
		Log.d(TAG, "Check UI persistense");
		Boolean prodChkBoxState = (Boolean)actCache.get("PROD_CHK_BTN");
		Boolean consChkBoxState = (Boolean)actCache.get("CONS_CHK_BTN");
		Boolean shutdownBtn     = (Boolean)actCache.get("SRVC_CNTRL_BTN");
		String  appNameStr      = (String)actCache.get("APP_NAME");
		
		//application name
		if ( appNameStr != null ) {
			appName.setText(appNameStr);
		}
		//shutdown button
		if ( shutdownBtn  != null ) {
			shutdownButton.setEnabled(shutdownBtn );
		}
		//producer checkbox
		if ( prodChkBoxState != null ) {
			producerChbId.setChecked(prodChkBoxState);
			setProducerLayout(prodChkBoxState); 
		}
		//consumer checkbox
		if ( consChkBoxState != null ) {
			consumerChbId.setChecked(consChkBoxState);
			setConsumerLayout(consChkBoxState); 
		}
		//setConsProdChbEnabled(false);
	}//setUI
	
	/**
	 * Show Notification
	 * @param messageType
	 * @param text
	 */
	public synchronized void showNotification(String messageType, List <NotificationText> text, String richIconUrl, String richAudioUrl, String richIconObjPath, String richAudioObjPath) {
		LangEnum usrPrefLang    = LangEnum.valueOf(languageSpinnerConsumer.getSelectedItem().toString());
		String  usrPrefLangStr  = usrPrefLang.INT_NAME;
		
		//Received default message in english
		String recvDefltMsg  = "";
		String msgToShow     = "";
		
		// Search notification in the preferred language, if not found - use English
		for (NotificationText ntObj : text ) {
			Log.d(TAG, "Received message Lang: '" + ntObj.getLanguage() + "', text: '" + ntObj.getText() + "'");
			if ( ntObj.getLanguage().startsWith(usrPrefLangStr) ) {
					msgToShow = ntObj.getText();
					break;
			}
			else if ( ntObj.getLanguage().startsWith(LangEnum.English.INT_NAME) ) { // The default English language must be sent  
				recvDefltMsg = ntObj.getText();
			}
		}//for :: languages

		// Not found notification in the desired language
		if ( msgToShow.length() == 0 ) {
			Log.d(TAG, "Not found message in the desired language: " + usrPrefLang.toString() + ", show english msg: " + recvDefltMsg);
			msgToShow = recvDefltMsg;
		}
		else {
			Log.d(TAG, "Found message in the desired language: '" + usrPrefLang.toString() + "', message: '" + msgToShow + "'");
		}
		
		final Map<String, String> newNotification = new HashMap<String, String>();
		newNotification.put(MSG_TYPE, messageType);
		newNotification.put(TEXT, msgToShow);
		
		if (richIconUrl == null) {
			newNotification.put(RICH_ICON_URL, "");
		}
		else {
			newNotification.put(RICH_ICON_URL, richIconUrl);
		}
		if (richAudioUrl == null) {
			newNotification.put(RICH_AUDIO_URL, "");
		}
		else {
			newNotification.put(RICH_AUDIO_URL, richAudioUrl);
		}
		if (richIconObjPath == null) {
			newNotification.put(RICH_ICON_OBJ_PATH, "");
		}
		else {
			newNotification.put(RICH_ICON_OBJ_PATH, richIconObjPath);
		}
		if (richAudioObjPath == null) {
			newNotification.put(RICH_AUDIO_OBJ_PATH, "");
		}
		else {
			newNotification.put(RICH_AUDIO_OBJ_PATH, richAudioObjPath);
		}
		
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notificationItemsList.add(newNotification);
				((BaseAdapter)listAdapter).notifyDataSetChanged();
				lstv1Id.setSelection(lstv1Id.getCount() - 1);
			}
		});
	}//viewNewNotification
	
	/**
	 * Create a Spinner reference for each spinner (using prepareSpinner method)
	 */
	private void setSpinners() {
		String[] langsStr       = LangEnum.stringValues();
		languageSpinnerConsumer = prepareSpinner(R.id.language_spinnerConsumer, langsStr);
		languageSpinnerProducer = prepareSpinner(R.id.language_spinnerProducer, langsStr);
		messageTypeSpinner  	= prepareSpinner(R.id.msgtype_spinner, MessageTypeEnum.stringValues());
	}//setSpinners
	
	/**
	 * Prepare a spinner/adapter
	 * @param spinnerId resource id of the spinner object
	 * @param itemsArr
	 * @return
	 */
	private Spinner prepareSpinner(int spinnerId, String[] itemsArr) {
		Spinner s = (Spinner) findViewById(spinnerId);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = new  ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, itemsArr);
		//Specify the layout to use when the list of choices appears
		s.setAdapter(adapter);
		return s;
	}//prepareSpinner

	/**
	 * Adds handling of IME virtual keyboard 
	 */
	private void prepareMsgTextBox(EditText msgTextObj) {
		
		msgTextObj.setImeOptions(EditorInfo.IME_ACTION_DONE);
		msgTextObj.setImeActionLabel(getString(R.string.send), EditorInfo.IME_ACTION_DONE);
		msgTextObj.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE != actionId) {
					return false;
				}
				grabSendControlArea();
				
				View currentFocusView = getCurrentFocus();
				if ( currentFocusView != null ) {
					//Hide Keyboard
					InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(currentFocusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				
				return true;
			}//onEditorAction
		});
		
	}//prepareMsgTextBox
	
	/**
	 * OnClick event handler 	 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {				
		switch (view.getId()) {
			case R.id.b_shutdown: {
				onShutdownClicked(true);
				break;
			}//service Control
			case R.id.b_send: {
				grabSendControlArea();
				break;
			}
			case R.id.b_delete: {
				grabDeleteControlArea();
				break;
			}
			case R.id.chb_consumer: {
				onCheckboxClicked(view);
				break;
			}
			case R.id.chb_producer: {
				onCheckboxClicked(view);
				break;
			}
			case R.id.chb_icon: {
				break;
			}
			case R.id.chb_audio: {
				Log.d(TAG, "Rich Audio was clicked");
				break;
			}
			case R.id.chb_icon_obj_path: {
				break;
			}
			case R.id.chb_audio_obj_path: {
				Log.d(TAG, "Rich Audio obj path was clicked");
				break;
			}
		}//switch
	}//onClick

	/**
	 * The event handler will be invoked when the checked state of the radio buttons
	 * will be changed
	 */
	public void onCheckboxClicked(View buttonView) {
		CheckBox chkBoxBtn = (CheckBox)buttonView;
		String appNameStr = appName.getText().toString();
		//for testers who don't want the app to filter notifications on the app name
		if ( appNameStr.length() == 0 ) {
			appNameStr = IoeNotificationApplication.DEFAULT_APP_NAME;
			Log.d(TAG, "No app name passed in setting to: '" + appNameStr + "'");
		}
		myApp.setAppName(appNameStr);
		
		switch ( buttonView.getId() ) {
			case R.id.chb_producer: {
				Log.d(TAG, "Producer checkbox changed, isChecked: " + chkBoxBtn.isChecked());
				if ( chkBoxBtn.isChecked() ) {         // Start producer service
					setProducerLayout(true);
					myApp.startSender();
				}
				else {					   // Stop producer service
					setProducerLayout(false);
					iconRichCheck.setChecked(false);
					audioRichCheck.setChecked(false);
					iconObjPathRichCheck.setChecked(false);
					audioObjPathRichCheck.setChecked(false);
					cpsObjPath.setChecked(false);
					myApp.stopSender();
				}
				break;
			}//producer
			case R.id.chb_consumer: {
				Log.d(TAG, "Consumer checkbox changed, isChecked: " + chkBoxBtn.isChecked());
				if ( chkBoxBtn.isChecked() ) {         // Start Consumer service
					setConsumerLayout(true);
					myApp.startReceiver();
				}
				else {
					setConsumerLayout(false);
					notificationItemsList.clear();
					((BaseAdapter)listAdapter).notifyDataSetChanged();
					myApp.stopReceiver();
				}
				break;
			}//consumer
		}//switch
		
	    //Enable Shutdown button - if at least one of the check boxes checked and shutdown is disabled
		if ( !shutdownButton.isEnabled() && chkBoxBtn.isChecked() ) {
			Log.d(TAG, "Enabling shutdown button");
			shutdownButton.setEnabled(true);
		}
	}//onCheckedChanged
		
	/**
	 * Notify app about shutdown pressed and initialize UI area
	 */
	public void onShutdownClicked(boolean callNSShutdown) {
  		//clear all input/selections:
		//1. unchecked Producer/Consumer checkbox
		setConsProdChbChecked(false);
		 
		//2. clear msg_1,msg_2
		msg1Id.setText("");
		msg2Id.setText("");
		
		//3. clear application name
		appName.setText("");
		
		//4. clear the notificationItemsList
		notificationItemsList.clear();
		((BaseAdapter)listAdapter).notifyDataSetChanged();

		//display producer layout's
		setProducerLayout(false);
		setConsumerLayout(false);
		
		//Uncheck check boxes
		iconRichCheck.setChecked(false);
		audioRichCheck.setChecked(false);
		iconObjPathRichCheck.setChecked(false);
		audioObjPathRichCheck.setChecked(false);
		cpsObjPath.setChecked(false);
		
		shutdownButton.setEnabled(false);

		//clean act persistence
		actCache.clear();
		
		if ( callNSShutdown ) {
			//deligate shutdown to my app
			myApp.shutdown();
		}
	}//shutdownToStart	
	   
	/**
	 * set Producer Layout visible/invisible according to isVisable parameter
	 * @param isVisible true - producer layout should be visible.
	 */
	private void setProducerLayout(boolean isVisible) {
		/**
		 * by default - ProducerLayout is INVISIBLE
		 */
		int producerVisiblity = View.INVISIBLE;
		
		if(isVisible){
			producerVisiblity = View.VISIBLE;
		}
		
		for( int i = 0; i < producer_lo_arr.length ; i++) {
			findViewById(producer_lo_arr[i]).setVisibility(producerVisiblity);
		}		
	}//setProducerLayout
	
	/**
	 * Set Consumer Layout visible/invisible according to isVisable parameter
	 * @param isVisible
	 */
	private void setConsumerLayout(boolean isVisible) {
		View layout = findViewById(R.id.lo_consumer);
		if(isVisible) {
			Log.d(TAG, "Set Consumer layout to be VISIBLE");
			layout.setVisibility(View.VISIBLE);
		}
		else {
			Log.d(TAG, "Set Consumer layout to be INVISIBLE");
			layout.setVisibility(View.INVISIBLE);
		}		
	}//setConsumerLayout
	
	/**
	 * set  consumer/producer CheckBox checked/unchecked
	 * @param isChecked  if true, consumer/producer CheckBox are checked
	 */
	private void setConsProdChbChecked(boolean isChecked) {
		Log.d(TAG,"setConsProdChbChecked "+ "isChecked " + isChecked);
		consumerChbId.setChecked(isChecked);
		producerChbId.setChecked(isChecked);
	}//setConsProdChbChecked

	/**
	 * Grab send control area.
	 * Prepare data structure to send Notification
	 */
	private void grabSendControlArea() {
		String msg1 = msg1Id.getText().toString(); // content of message1
		String msg2 = msg2Id.getText().toString(); // content of message2
		
		//Message of language2
		LangEnum msg2Lang = LangEnum.valueOf(languageSpinnerProducer.getSelectedItem().toString());
		
		//At least one of the messages should be sent in English
		if ( msg1.length() == 0 && ( msg2.length() == 0 || msg2Lang != LangEnum.English) ) {
			myApp.showToast("At least one of the messages should be sent in English");
			myApp.vibrate();
			return;
		}
		
		String msgType  = MessageTypeEnum.valueOf(messageTypeSpinner.getSelectedItem().toString()).INT_NAME;

		//construct text to be sent
		List<NotificationText> text = new LinkedList<NotificationText>();
		
		try {
			if ( msg1.length() != 0 ) {                                        // msg1 not empty
				text.add(new NotificationText(LangEnum.English.INT_NAME, msg1));
			}
			if ( msg2.length() != 0 ) {								         // msg2 not empty
				text.add(new NotificationText(msg2Lang.INT_NAME, msg2));
			}
		}
		catch (NotificationServiceException nse) {
			Log.d(TAG, "Failed create NotificationText, Error: " + nse.getMessage());
		}
		
		//construct customArgs
		Map<String, String> customArgs = new HashMap<String, String>();
		customArgs.put("color", "red");
		
		//get ttl value
		String ttlStr = ttlEditText.getText().toString();
		int ttl;
		try {
			ttl = Integer.parseInt(ttlStr);
		}
		catch (NumberFormatException nfe) {
			myApp.showToast("TTL should be a number !!!");
			myApp.vibrate();
			return;
		}
		
		boolean isIcon           = false;
		boolean isAudio          = false;
		boolean isIconObjPath    = false;
		boolean isAudioObjPath   = false;
		boolean isCps            = false;
		
		//Check richContent
		if ( audioRichCheck.isChecked() ) {
			Log.d(TAG, "RichContent selected to be sent - AUDIO");
			isAudio    = true;
		}
		if ( iconRichCheck.isChecked() ) {
			Log.d(TAG, "RichContent selected to be sent - ICON");
			isIcon = true;
		}
		if ( audioObjPathRichCheck.isChecked() ) {
			Log.d(TAG, "RichObjPathContent selected to be sent - AUDIO");
			isAudioObjPath    = true;
		}
		if ( iconObjPathRichCheck.isChecked() ) {
			Log.d(TAG, "RichObjPathContent selected to be sent - ICON");
			isIconObjPath = true;
		}
		
		if ( cpsObjPath.isChecked() ){
			Log.d(TAG, "CPS object path selected to be sent");
			isCps = true;
		}
		
		//====  SEND A MESSAGE  ====//
		myApp.send(msgType, text, customArgs, ttl, isIcon, isAudio, isIconObjPath, isAudioObjPath, isCps);
		
		//clean text areas
		msg1Id.setText(""); // content of message1
		msg2Id.setText("");
		
		//clean ttl area
		ttlEditText.setText(R.string.ttl_dflt);
		
		//clear rich checkboxs
		iconRichCheck.setChecked(false);
		audioRichCheck.setChecked(false);
		iconObjPathRichCheck.setChecked(false);
		audioObjPathRichCheck.setChecked(false);
		
		//clear cps check box
		cpsObjPath.setChecked(false);
		
	}//grabSendControl	
	
	/**
	 * Grab delete control area
	 */
	private void grabDeleteControlArea() {
		String msgType = MessageTypeEnum.valueOf(messageTypeSpinner.getSelectedItem().toString()).INT_NAME;
		myApp.delete(msgType);
	}//grabDeleteControlArea

}//MainActivity
