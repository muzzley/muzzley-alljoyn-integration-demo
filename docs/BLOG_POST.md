# Extending AllJoyn communication to the cloud

We're big fans of the AllJoyn technology and have joined the [AllSeen Alliance](http://allseenalliance.org/) as a community member.

**About AllJoyn communication**

The AllJoyn Framework facilitates the communication between nearby devices and provides services, including onboarding and notifications, out of the box. The communication is usually performed over ad hoc proximity networks, such as Wi-Fi.

**About Muzzley communication**

Muzzley follows a cloud-centric philosophy. Devices which are paired and communicate through Muzzley, usually do so through the cloud using the IP protocol. This allows to very quickly and easily pair different Internet-connected devices with a hassle-free network setup, if any.

**Integrating AllJoyn devices with Muzzley**

To demonstrate how Muzzley and AllJoyn fit together, we've made a demo project showing how AllJoyn Notifications can be transported through the Muzzley cloud to your smartphone so that you don't miss anything when you leave the house.

If you can't contain your excitement any longer, the full source code of this demo is available at [GitHub](https://github.com/muzzley/muzzley-alljoyn-integration-demo).

We've forked AllJoyn's Notification UI Android sample and integrated it with Muzzley. The base sample can be downloaded at the [AllJoyn SDK Downloads Page](https://www.alljoyn.org/docs-and-downloads) and is included in the `AllJoyn Notification Service Framework SDK - 1.0.1` zip.

Our use case consists of an Android tablet hanging on your wall (next to the family dog picture) where all notifications are received. We've conveniently named it, _drum roll_, the "Home Notification Gateway". _Ba dum tss_!

Here's how the main screen looks like:

![](https://raw.github.com/muzzley/muzzley-alljoyn-integration-demo/master/docs/imgs/MuzzleyAllJoynHomeNotificationGateway.png)

The Home Notification Gateway is an AllJoyn Notification consumer and, at the same time, it is Muzzley-enabled. So, if you want access to your appliances' notifications outside the house, you simply pair your smartphone using the Muzzley App for [Android](https://play.google.com/store/apps/details?id=com.muzzley) or [iPhone](https://itunes.apple.com/us/app/muzzley/id604133373) with the Gateway and all notifications will be forwarded to you, no matter where you are. You can also control which devices' notifications are forwarded to you directly from the smartphone interface.

The Android Gateway was integrated with Muzzley through the [Java Lib](http://www.muzzley.com/documentation/libraries/java.html).

The smartphone's interface is custom-developed in HTML, JavaScript and CSS. Have a look at [Muzzley's WebView widget documentation](http://www.muzzley.com/documentation/widgets/webview.html). The following image is a screenshot of the interface:

![](https://raw.github.com/muzzley/muzzley-alljoyn-integration-demo/master/docs/imgs/MuzzleyNotificationWebview.png)

If you want to try it out or have a more technical look, follow these links:

* Full source code of this demo: https://github.com/muzzley/muzzley-alljoyn-integration-demo
* Muzzley WebView Widget docs: http://www.muzzley.com/documentation/widgets/webview.html
* Muzzley Java Lib docs: http://www.muzzley.com/documentation/libraries/java.html