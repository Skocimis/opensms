package com.textflow.smssender;

import android.os.Build;
import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.List;
import java.util.ArrayList;

import expo.modules.ReactActivityDelegateWrapper;

public class MainActivity extends ReactActivity {
  private static final int PERMISSIONS_REQUEST_CODE = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Set the theme to AppTheme BEFORE onCreate to support
    // coloring the background, status bar, and navigation bar.
    // This is required for expo-splash-screen.
    setTheme(R.style.AppTheme);
    super.onCreate(null);
    GoogleApiAvailability a = new GoogleApiAvailability();
    a.makeGooglePlayServicesAvailable(this);
    ArrayList<String> permissionsToRequest = new ArrayList<>();
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      permissionsToRequest.add(Manifest.permission.SEND_SMS);
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
      permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE);
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
      permissionsToRequest.add(Manifest.permission.CALL_PHONE);
    }
    if (!permissionsToRequest.isEmpty()) {
      ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
    }

  }

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "main";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util
   * class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and
   * Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new ReactActivityDelegateWrapper(this, BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
        new DefaultReactActivityDelegate(
            this,
            getMainComponentName(),
            // If you opted-in for the New Architecture, we enable the Fabric Renderer.
            DefaultNewArchitectureEntryPoint.getFabricEnabled(), // fabricEnabled
            // If you opted-in for the New Architecture, we enable Concurrent React (i.e.
            // React 18).
            DefaultNewArchitectureEntryPoint.getConcurrentReactEnabled() // concurrentRootEnabled
        ));
  }

  /**
   * Align the back button behavior with Android S
   * where moving root activities to background instead of finishing activities.
   * 
   * @see <a href=
   *      "https://developer.android.com/reference/android/app/Activity#onBackPressed()">onBackPressed</a>
   */
  @Override
  public void invokeDefaultOnBackPressed() {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
      if (!moveTaskToBack(false)) {
        // For non-root activities, use the default implementation to finish them.
        super.invokeDefaultOnBackPressed();
      }
      return;
    }

    // Use the default back button implementation on Android S
    // because it's doing more than {@link Activity#moveTaskToBack} in fact.
    super.invokeDefaultOnBackPressed();
  }
}
