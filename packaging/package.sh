#!/bin/bash

if [[ -d ./src/ ]]; then
    :

elif [[ -d ./linux/ && -d ./aur/ ]]; then
    cd ..

else
    echo "[!] Working directory must be in the root direcotry or in then packaging directory!"
    exit
fi

VERPATH="./src/main/resources/version"
version="$1"

if [[ "$version" == "clean" ]]; then
    echo "[!] Cleaning the "

    echo "[+] Removing output folders"
    sudo rm -rf ./packaging/linux/out

    rm -rf ./packaging/bin
    rm -rf ./packaging/windows-setup/Output

    echo "[+] Finished cleaning packaging direcotry"
    exit
fi

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
