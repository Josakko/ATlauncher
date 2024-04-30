#!/bin/bash

# 1 - install the jar into /usr/share/atlauncher-temp
# 2 - cp the jar to specific home dir of the user
# 3 - for pcacgaging in this script with the version assemble the filename from dists and copy it to some local dir here 
# under ./packages from which packagers could use it for packaging
#
#
cd ..

VERPATH="./src/main/resources/version"
version="$1"
if [[ "$version" != "" ]]; then
    echo "$version" > "$VERPATH"

else
    version="$(cat "$VERPATH")"

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
