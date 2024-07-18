package com.poberwong.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import static android.Manifest.permission.CALL_PHONE;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class IntentLauncherModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final int REQUEST_CODE = 12;
    private static final String ATTR_ACTION = "action";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_CATEGORY = "category";
    private static final String TAG_EXTRA = "extra";
    private static final String ATTR_DATA = "data";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_CLASS_NAME = "className";
    private static final int REQUEST_CALL_PERMISSION = 1;

    private Promise promise;
    private ReactApplicationContext reactContext;

    public IntentLauncherModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "IntentLauncher";
    }

    @ReactMethod
    public void startActivity(ReadableMap params, final Promise promise) {
        this.promise = promise;
        Intent intent = new Intent();

        if (params.hasKey(ATTR_CLASS_NAME)) {
            ComponentName cn;
            if (params.hasKey(ATTR_PACKAGE_NAME)) {
                cn = new ComponentName(params.getString(ATTR_PACKAGE_NAME), params.getString(ATTR_CLASS_NAME));
            } else {
                cn = new ComponentName(getReactApplicationContext(), params.getString(ATTR_CLASS_NAME));
            }
            intent.setComponent(cn);
        }
        if (params.hasKey(ATTR_ACTION)) {
            intent.setAction(params.getString(ATTR_ACTION));
        }
        if (params.hasKey(ATTR_DATA) && params.hasKey(ATTR_TYPE)) {
            intent.setDataAndType(Uri.parse(params.getString(ATTR_DATA)), params.getString(ATTR_TYPE));
        } else {
            if (params.hasKey(ATTR_DATA)) {
                intent.setData(Uri.parse(params.getString(ATTR_DATA)));
            }
            if (params.hasKey(ATTR_TYPE)) {
                intent.setType(params.getString(ATTR_TYPE));
            }
        }
        if (params.hasKey(TAG_EXTRA)) {
            intent.putExtras(Arguments.toBundle(params.getMap(TAG_EXTRA)));
        }
        if (params.hasKey(ATTR_FLAGS)) {
            intent.addFlags(params.getInt(ATTR_FLAGS));
        }
        if (params.hasKey(ATTR_CATEGORY)) {
            intent.addCategory(params.getString(ATTR_CATEGORY));
        }
        getReactApplicationContext().startActivityForResult(intent, REQUEST_CODE, null);
    }

    @ReactMethod
    public void isAppInstalled(String packageName, final Promise promise) {
        try {
            this.reactContext.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            promise.reject("app not found");
            return;
        }
        promise.resolve(true);
    }

    @ReactMethod
    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(getCurrentActivity(), CALL_PHONE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getCurrentActivity(), new String[]{CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            getCurrentActivity().startActivity(callIntent);
        }
    }

    @ReactMethod
    public void startAppByPackageName(String packageName, final Promise promise) {
        if (packageName != null) {
            Intent launchIntent = this.reactContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                // Uncomment the line below if you want to start the app as well
                 getReactApplicationContext().startActivity(launchIntent);
                promise.resolve(true);
                return;
            } else {
                promise.reject("could not start app");
                return;
            }
        }
        promise.reject("package name missing");
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        WritableMap params = Arguments.createMap();
        if (intent != null) {
            params.putInt("resultCode", resultCode);

            Uri data = intent.getData();
            if (data != null) {
                params.putString("data", data.toString());
            }

            Bundle extras = intent.getExtras();
            if (extras != null) {
                params.putMap("extra", Arguments.fromBundle(extras));
            }
        }

        this.promise.resolve(params);
    }
}
