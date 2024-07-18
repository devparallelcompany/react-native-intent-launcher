# react-native-intent-launcher  
[![version](https://img.shields.io/npm/v/react-native-intent-launcher.svg)](https://www.npmjs.com/package/react-native-intent-launcher) [![downloads](https://img.shields.io/npm/dm/react-native-intent-launcher.svg?style=flat)](https://www.npmjs.com/package/react-native-intent-launcher)
[![downloads](https://img.shields.io/npm/dt/react-native-intent-launcher.svg?style=flat)](https://www.npmjs.com/package/react-native-intent-launcher)  

call native function `startActivity` in react-native

## Description
You can call native function `startActivity` in react-native to do something with `Intent` which can only be solved with android native code
and make a phone call immediately

## Installation
```
yarn add https://github.com/devparallelcompany/react-native-intent-launcher.git   
```

## Make A Phone Call
```javascript
const makeAPhoneCall = async () => {
  const granted = await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.CALL_PHONE,
    {
      title: "Call Phone Permission",
      message: "This app needs access to make phone calls.",
      buttonNeutral: "Ask Me Later",
      buttonNegative: "Cancel",
      buttonPositive: "OK"
    }
  );

  IntentLauncher.makePhoneCall("123456789");
}
```

## Legacy Usage
```javascript
import IntentLauncher, { IntentConstant } from 'react-native-intent-launcher'
...
IntentLauncher.startActivity({
	action: 'android.settings.APPLICATION_DETAILS_SETTINGS',
	data: 'package:com.example'
})

// check if app is installed by package name
IntentLauncher.isAppInstalled('wtf.swell')
  .then((result) => {
    console.log('isAppInstalled yes');
  })
  .catch((error) => console.warn('isAppInstalled: no', error));

// open another app by package name
IntentLauncher.startAppByPackageName('wtf.swell')
  .then((result) => {
    console.log('startAppByPackageName started');
  })
  .catch((error) => console.warn('startAppByPackageName: could not open', error));
...
``` 

## Properties
* `action` String
* `data` String
* `category` String
* `flags` String
* `extra` Object
* `packageName` String
* `className` String
* `flags` Number

In the `IntentConstant`, we provide some constants for these properties, you can look up document provided by google to find out property we didn't support currently.

## License
*MIT*


