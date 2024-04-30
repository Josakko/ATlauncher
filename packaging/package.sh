#!/bin/bash

cd ..

VERPATH="./src/main/resources/version"
version="$1"
if [[ "$version" != "" ]]; then
    echo "$version" > "$VERPATH"
    echo "Setting version: $version"

else
    version="$(cat "$VERPATH")"
    echo "Using version: $version"

fi 

jarfile="ATLauncher-$version.jar"
exefile="ATLauncher-$version.exe"

./gradlew build -x test

cd ./packaging
mkdir bin
cp "../dist/$jarfile" "./bin/ATLauncher.jar"
cp "../dist/$exefile" "./bin/ATLauncher.exe"


cd linux 
./build.sh

cd ../windows-setup
./build.sh
