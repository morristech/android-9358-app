#!/bin/sh

. ./variables

$ANDROID_HOME/build-tools/android-4.4W/zipalign -v 4 $TO_SIGN_APK $SIGNED_APK
