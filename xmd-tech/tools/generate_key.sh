#!/bin/sh

#. ./variables
. ./xmd_variables

keytool -v -genkey -alias $ALIAS -keyalg RSA -keysize 2048 -validity 20000 -keystore $KEYSTORE -storepass $PASSWORD -keypass $PASSWORD
