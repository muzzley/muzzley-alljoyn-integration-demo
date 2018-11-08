# Muzzley / AllJoyn Integration Demo

This project demonstrates how [Muzzley](http://www.muzzley.com) can be used to transport [AllJoyn Notifications](https://www.alljoyn.org/about/core-services/notification) ([interface spec](https://allseenalliance.org/docs-and-downloads/documentation/alljoyn-notification-service-framework-interface-specification)) to the Internet.

AllJoyn Notifications are usually only transported through local networks such as WiFi or Bluetooth. By integrating Muzzley into an Android application that is capable of receiving local AllJoyn Notifications, we transport them through the Muzzley cloud to any other place in the world (where there's Internet access, of course).

This is a proof of concept that shows how Muzzley can be used for bi-directional real-time communication.

## Muzzley / AllJoyn Notification Gateway

The first component of the integration consists of an Android application that acts as an AllJoyn Notification receiver and, at the same time, as a Muzzley-enabled application capable of emitting data to paired devices through the Internet.

The Android app is in the `android-app/` folder.

### AllJoyn Notification Demo

This demo builds on top of the AllJoyn Notication UI demo. The base demo can be downloaded at the [AllJoyn SDK Downloads Page](https://www.alljoyn.org/docs-and-downloads) and is included in the `AllJoyn Notification Service Framework SDK - 1.0.1` zip.

The app is capable of acting as a notification producer and consumer at the same time. That is, you can emit notifications and receive them in the same Android device.

We have changed the original screen to add a button (the Muzzley logo on the top right) that takes the user back to the "Muzzley Notification Gateway" view.

![](https://raw.github.com/muzzley/muzzley-alljoyn-integration-demo/master/docs/imgs/AllJoynNotificationUI.png)

### Muzzley's Home Notification Gateway

The main view of this sample app is what we like to call the "Home Notification Gatway". It's the main view of the app described in the previous section.

It shows how you can easily create an Android-powered AllJoyn Notifications gateway and, through the Muzzley integration, have a simple method of taking your notifications with you when you leave the house.

![](https://raw.github.com/muzzley/muzzley-alljoyn-integration-demo/master/docs/imgs/MuzzleyAllJoynHomeNotificationGateway.png)

On the left side, the received notifications are shown. On the right side, we can see the Muzzley connection information that allows us to pair our smartphone using the Muzzley app for [Android](https://play.google.com/store/apps/details?id=com.muzzley) or [iPhone](https://itunes.apple.com/us/app/muzzley/id604133373).

The application connects to Muzzley by integrating the [Muzzley Java Lib](http://www.muzzley.com/documentation/libraries/java.html). The code where we connect to Muzzley and forward the AllJoyn Notifications, is in the file [com / muzzley / alljoynintegration / MuzzleyManager.java](https://github.com/muzzley/muzzley-alljoyn-integration-demo/blob/master/android-app/MuzzleyAllJoynIntegrationSample/app/src/main/java/com/muzzley/alljoynintegration/MuzzleyManager.java).


## The Muzzley WebView

The second component of this demo is a [Muzzley WebView widget](http://www.muzzley.com/documentation/widgets/webview.html) that lists the notifications that were forwarded by the Android app (described above) through the Muzzley cloud. It also has a "Settings" screen where the user can enable or disable receiving notifications from specific AllJoyn applications. These settings get sent back to the AllJoyn-enabled application. This shows how we achieve bi-directional real-time communication.

![](https://raw.github.com/muzzley/muzzley-alljoyn-integration-demo/master/docs/imgs/MuzzleyNotificationWebview.png)

The WebView widget's source code is in the `widget-webview/` folder. Its three types of components (`CSS`, `HTML` and `JavaScript`) are in that same folder. If you want to extend the original WebView provided here, you can create your own at http://www.muzzley.com/widgets (registration required) and copy the contents of these files so you have a base to start with.
