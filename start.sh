#!/bin/bash

APPID=ti.goosh
VERSION=2.0.10

#cp android/assets/* iphone/


#rm -rvf ~/Library/Application\ Support/Titanium/modules/android/$APPID/$VERSION/*

export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/"

cd android && rm -rf build/classes && rm -f ~/Library/Application\ Support/Titanium/modules/android/$APPID/$VERSION/lib/* && \
ti --platform android build --build-only --sdk 6.1.1.GA && \
unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/ && cd ..
#cd android;rm -rf build/classes;ant; unzip -uo  dist/$APPID-android-$VERSION.zip  -d  ~/Library/Application\ Support/Titanium/; cd ..


#zip -d ~/Library/Application\ Support/Titanium/modules/android/$APPID/$VERSION/spinmenu.jar org/appcelerator/titanium/gen/bindings.json;

#cp android/dist/$APPID-android-$VERSION.zip .
#cp iphone/$APPID-iphone-$VERSION.zip .
