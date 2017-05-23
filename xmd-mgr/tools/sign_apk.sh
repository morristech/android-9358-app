#!/bin/sh

. ./variables

jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -storepass $PASSWORD -keypass $PASSWORD -keystore $KEYSTORE $TO_SIGN_APK $ALIAS 
