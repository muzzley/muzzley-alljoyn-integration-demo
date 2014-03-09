var snapper = new Snap({
  element: document.getElementById('content'),
  disable: 'right',
  maxPosition: $("#content").width() - 30
});

document.getElementById('menu-toggle').addEventListener('click', function () {
  if( snapper.state().state=="left" ){
      snapper.close();
  } else {
      snapper.open('left');
  }
});

// ---------------------------------------

function hideAllViews(exceptElement) {
  $('.content-view').hide();
  exceptElement.show();
  snapper.close();
}

$("#menu-settings").click(function () {
  hideAllViews($('#settings-wrapper'));
});

$("#menu-main").click(function () {
  hideAllViews($('#notification-wrapper'));
});

// ---------------------------------------

var notifications = [
  {
     "appId":"3100c03b-8606-4cd2-9b38-33c27fa33301",
     "appName":"DISPLAY_ALL",
     "deviceId":"4564545455454",
     "deviceName":"Thermostat",
     "messageId":"5000",
     "messageType":"INFO",
     "senderBusname":":IGZw9r7X.2",
     "text":[
        {
           "text":"Temperature set to 23ºC",
           "lang":"en"
        }
     ],
     "version":1
  },
];

var ractive = new Ractive({
  el: '#notification-wrapper',
  template: '#notificationtemplate',
  noIntro: true, // disable transitions during initial render
  data: {
    notifications: notifications,
    normalizeMessageType: function (type) {
      // Known AllJoyn notification types (from the demo):
      //   EMERGENCY, WARNING, INFO
      // Bootstrap types:
      //   bg-primary, bg-success, bg-info, bg-warning, bg-danger
      var t = '';
      type = type.toLowerCase();
      switch (type) {
        case 'emergency':
        case 'danger':
        case 'error':
          t = 'danger';
          break;
        case 'warning':
          t = 'warning';
          break;
        case 'info':
          t = 'success';
          break;
        default:
          t = 'primary';
      }
      return t.toLowerCase();
    }
  }
});

var filters = [
  // { appName: 'App 1', appId: '123'},
  // { appName: 'App 2', appId: '234'}
];

var ractiveFilters = new Ractive({
  el: '#settings-wrapper',
  template: '#filtertemplate',
  noIntro: true,
  data: {
    filters: filters
  }
});

var onSwitchChange = function (e, data) {
  var $element = $(data.el);
  var appName = $element.data('appname');
  var value = data.value;

  var action = (value) ? 'remove' : 'add';
  
  muzzley.send('alljoynFilter', {
    action: action,
    name: appName
  });
};
$('.toggle-checkbox').bootstrapSwitch();
$('.toggle-checkbox').on('switchChange', onSwitchChange);

var addFilter = function (appName, appId) {
  var exists = false;
  for (var i = 0; i < filters.length; i++) {
    if (filters[i].appName === appName) {
      exists = true;
      break;
    }
  }

  if (!exists) {
    filters.push({ appName: appName, appId: appId });
    $('.toggle-checkbox').bootstrapSwitch();
    $('.toggle-checkbox').off('switchChange', onSwitchChange);
    $('.toggle-checkbox').on('switchChange', onSwitchChange);
  }
};

var addNotification = function (notification) {
  notifications.unshift(notification);
  if (notifications.length > 15) { // Working around an issue  (Ractive.js problem?)
    notifications.splice(15, 10);
  }

  addFilter(notification.appName, notification.appId);
};

muzzley.on('alljoynNotification', addNotification);