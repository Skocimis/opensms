# TextFlow Open SMS with Add-on for receiving SMS
An open-source solution to send SMS from your own devices using REST API. 

*Note: You can only receive SMS if you set this app as your default messaging app*

This option just requires a dedicated spare phone, with a sim card, and allows you to send as much SMS as you can, which could even be free if you opted in for some mobile carriers' unlimited SMS options, but be warned, some SMS carriers may limit your ability to send SMS this way, which could result even in your subscription being canceled. 

You will also need a computer to host your SMS server, which can also be done on your local network. If you do not want to hassle with setting up your own server, we have a [solution at just a fraction of our normal SMS price](https://textflow.me/smsgateway). 

If you follow this guide, you will be able to get a webhook to your server, every time you receive an SMS, along with just sending SMS. If you just want to send SMS with easier setup, take a look at [this branch](https://github.com/Skocimis/opensms/tree/master). 

## How it works

The main idea in this solution is to use android phone's native ability to send SMS programmatically. We create the listener on the android phone, that listens for the Firebase notifications that we send from our server, and when we receive a notification, we send the SMS corresponding to the data passed in that notification. Since our SMS server can now send the SMS, it can open an endpoint that will let us send SMS from any of the phones that are connected to it.

Our SMS server also handles the logic of multiple senders, for each sim card you can create a sender for every country. We also support load-balancing, if there are multiple senders that send to the same country, the server will chose the one with the least SMS sent today. The sim-cards are identified by the names that you give them, so make sure they are unique. You can also mark senders as worldwide (if there are no senders for a specific country, server will use a worldwide sender) or turn their safety on (limit SMS rate to 10 SMS / min). If you want to change some parameters, you can take a look at the code, or contact us at support@textflow.me if you need some help. 

![image](https://github.com/Skocimis/opensms/assets/24946127/15849bdf-9082-46bf-9778-5f6be3f4f465)

## Step 1: Preparation

Before you get started, make sure to install all of these:

* Node.js and npm <br> Recommended version for Node.js is v14.18.1
* Android Studio<br>Once you install Android Studio, open it, it will run the setup wizard for installing the Android SDK, which we are going to need for the latter steps. Choose the standard installation option, or install Android SDK if you are using a custom option. Take note of the SDK installation directory, as you will need it. If you chose the standard option, it should be installed under `C:\Users\YourUsername\AppData\Local\Android\Sdk` on Windows. Create an environment variable called `ANDROID_HOME`, pointing to that directory. Make sure to replace _YourUsername_ with your actual Windows user account name.

After installing those dependencies, make sure to install expo-cli, and eas-cli, as we will also need them:
```bash
npm install -g expo-cli
npm install -g eas-cli
```

Also, you should enable USB debugging under development settings, for the device that you are going to test the application on. The procedure is different for every device, but is not complex. You should be able to do it if you google "DEVICE_NAME developer settings", and after enabling developer settings, look for the USB debugging option there. 

## Step 2: Cloning our github repository

If you are familiar with Git, you can use it to clone our [github repository](https://github.com/Skocimis/opensms). 
If not, you can just [download our code](https://github.com/Skocimis/opensms/archive/refs/heads/master.zip). Once you have download it, just unpack the folder anywhere on your computer, and rename it from `opensms-ReceiveSMSAddOn` to `opensms`. 
Either way, you will have a folder called `opensms`, and within it there will be two folders, `server` and `app`. Open your terminal (or cmd in Windows) in each of these two folders and run the following command:
```bash
npm install
```
This will install all the required dependencies for our project.

## Step 3: Updating the constants in code

Determine the address that you would like to host your server on. Also, create a password that will be used to authenticate between the mobile app that you run on your phone and your server. It can be any string, but make it unique, so the others will not be able to use your server to send SMS for them. 
In this example, I will be running the server locally, on address `http://192.168.1.40`, and my password will be `unique-password-49`. 
Once you have determined your server address and password, you will need to make the following changes to the code:
1. In `opensms/app/App.js`, replace SERVER_ADDRESS and YOUR_SECRET with your address and password, if running on local network use address like this: `http://192.168.1.40`
2. In `opensms/server/app.js`, do the same thing for YOUR_SECRET, in line 4
3. In `opensms/app/android/app/src/main/java/com/textflow/smssender/MyFirebaseMessagingService.java`, replace YOUR_SECRET in line 37 with your password
4. In `opensms/app/android/app/src/main/java/com/textflow/smssender/SendSmsWorker.java`, replace YOUR_WEBHOOK_ADDRESS with the address of your webhook and WEBHOOK_SECRET with your webhook secret. You will then receive a request similar to a request from [Receiving SMS replies](https://docs.textflow.me/receiving-sms-replies), to your webhook, with your secret in the body, along with the sender phone number and the message text every time you receive an SMS. 

## Step 4: Registering a Firebase account

The changes that you need to make in the code are now complete, and now it is time to set up our Firebase app, which will allow us to send notifications to our connected phones, to let them know when to send SMS. 
Head out to [Firebase console](https://console.firebase.google.com/), and click on Create a project. Give your project a name and accept their terms and conditions. The Google analytics part is optional, so you can leave it unchecked.
Once you have created your project, the following page will pop up:

![image](https://github.com/Skocimis/opensms/assets/24946127/955f94a3-c5a3-4f6a-8f73-ec9d702ff6cf)

Click on the android icon. It will take you to the form. The only required field is the _Android package name_, put `com.textflow.smssender` in there, click the Register app button. Download the `google-services.json` file, and put it in `opensms/app/android/app` folder.
Now finish the app creation by clicking the button Next two times, and then go to _Continue to console_.  
Now go to the project settings (you can see it when you click the gear in the upper left), and under the Service accounts tab, click on _Generate new private key_. It will download a file. Move that file to `opensms/server`, and rename it to `admin.json`. 

## Step 5: Testing the app

After you have done the steps above, it is the time to test if all is working. 
Firstly, you need to open the opensms/server folder, and under it, run the command
```bash
node app
```
That will start your own server, that will store the data about your connected sender phones, and is able to send Firebase notifications to them to send SMS. It should not have any output or throw any errors. 
Secondly, you have to connect your android device to your computer (the device should have enabled USB debugging in Developer options, and when you connect it, you should choose that you want to transfer files). When you have done that, navigate to the folder `opensms/app`, and run the following command:
```bash
npx expo run:android
```
Your android phone that you use for testing may prompt you with something, accept it. This process may take several minutes, depending on the hardware capabilities of your devices. 

To test receiving SMS, you have to make your app a system default messaging app. If you are not sure how to do this, google *default sms app YOUR_PHONE_MODEL*. You also need to make sure your webhook is running, reachable and open for your SMS sending device. 

If everything goes well, you should be able to see the screen that looks like this

![photo_5891140446896373807_y](https://github.com/Skocimis/opensms/assets/24946127/0ba50311-0132-4d66-9043-6cff66d29ae3)

There, you can create senders for each SIM card. When adding a sender, just choose where it would send SMS to. If you wanted to send to multiple countries from one SIM card, just create multiple senders for it. 
Finally, you can test sending SMS. To send an SMS, send the following request to your server:

<details>
 <summary><code>POST</code> <code><b>SERVER_ADDRESS/send-sms</b></code> <code>(Use your own SMS server for sending a message)</code></summary>

##### Header

> | name         |  type     | data type               | description                                                           |
> |--------------|-----------|-------------------------|-----------------------------------------------------------------------|
> | Content-Type |  required | String                  | Should be `application/json`                                          |

##### Body

> | name         |  type     | data type               | description                                                           |
> |--------------|-----------|-------------------------|-----------------------------------------------------------------------|
> | secret       |  required | String                  | The password that you have created in step 3                          |
> | phone_number |  required | String                  | Recipient phone number, with a country prefix. E.g. `+11234567890`    |
> | text         |  required | String                  | Message body                                                          |

</details>

If everything is working well, the message should be sent, and you will be able to see it in your SMS app. 

## Step 6: Building the app

If you want to deploy your app to your device, without being connected to it, you will need to create an APK file for installing the app on the device. This can be done by building the app, which will in turn create the installable APK file. 
To do that, run the following command in the `opensms/app` folder:
```bash
npx eas build -p android --profile production
```
You will be prompted to log in to your Expo account. [Create an account](https://expo.dev/signup), and use the credentials that you have created to log in. After that, answer to all questions yes (Y). Now you have to wait for the build to complete on the Expo remote servers, and when it is done, you will get a link with the APK file that you can download and install on your android device. 
Also, the sending may work even if the screen is off and app is in the background on closed on some devices, but that behavior is chaotic and we recommend you to keep the phone screen on at all time, focused on the app and charging, as it may save you some possible inconvenience. Do not run it in the background without prior testing. 

## Getting help

If you need help or have any suggestions or bugs, don't hesitate to reach out to us at `support@textflow.me`.
