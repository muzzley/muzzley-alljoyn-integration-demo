var muzzley = require('muzzley-client');

var myAppToken = 'caaf5ced77d90463';
var myWebViewUuid = '318ef097-088a-4a3b-a913-42ed03e9b084';

muzzley.on('error', function (err) {
  console.log('Err: ' + err);
});

var options = {
  token: myAppToken
};

var MSG_TYPES = ["INFO","SUCCESS","WARNING","ERROR", "EMERGENCY"];
function getRandomMessageType() {
  return MSG_TYPES[Math.floor(Math.random() * MSG_TYPES.length)];
}

function getNextMessageInterval() {
  var max = 10;
  var min = 3;
  return Math.floor(Math.random()*(max-min+1)+min) * 1000;
}

muzzley.connectApp(options, function(err, activity) {
  if (err) return console.log("err: " + err);

  // Usually you'll want to show this Activity's QR code image
  // or its id so that muzley users can join.
  // They are in the `activity.qrCodeUrl` and `activity.activityId`
  // properties respectively.
  console.log('Activity:');
  console.log(activity);

  activity.on('participantJoin', function(participant) {
    console.log(participant);

    participant.changeWidget({
      widget: 'webview',
      params: {
        uuid: myWebViewUuid, // Test Edu that fails
        orientation: 'portrait'
      }
    }, function(err) {
      var timer = null;
      if (err) return console.log("err: " + err );
      console.log('Activity: changeWidget was successful');

      participant.on('quit', function() {
        console.log('Participant ' + participant.name + ' quit');
        clearTimeout(timer);
      });

      participant.on('action', function(action) {
        // This would be called if we were working with a native widget
        console.log(action);
      });

      var msgId = 6000;
      var temperature = 240;

      var sendNotification = function () {

        var data = {
          "appId":"3100c03b-8606-4cd2-9b38-33c27fa33301",
          "appName":"DISPLAY_ALL",
          "deviceId":"4564545455454",
          "deviceName":"Thermostat",
          "messageId":++msgId,
          "messageType":getRandomMessageType(),
          "senderBusname":":IGZw9r7X.2",
          "text":[
            {
               "text":"Temperature set to " + (++temperature/10).toFixed(1) + "ºC",
               "lang":"en"
            }
          ],
          "version":1
        };

        // console.log('SENDING DATA: ', data);
        participant.sendSignal('alljoynNotification', data);
        timer = setTimeout(sendNotification, getNextMessageInterval());
      };

      sendNotification();
    });
  });
});

