FROM debian:11
LABEL maintainer="josakko@protonmail.com"

# add in all the files
ADD deb/control /work/atlauncher/DEBIAN/control
ADD deb/postrm /work/atlauncher/DEBIAN/postrm
ADD deb/atlauncher /work/atlauncher/usr/bin/atlauncher
ADD ../../bin/ATLauncher.jar /work/atlauncher/usr/share/atlauncher-inst/ATLauncher.jar
ADD _common/atlauncher.desktop /work/atlauncher/usr/share/applications/atlauncher.desktop
ADD _common/atlauncher.metainfo.xml /work/atlauncher/usr/share/metainfo/atlauncher.metainfo.xml
ADD _common/atlauncher.png /work/atlauncher/usr/share/pixmaps/atlauncher.png
ADD _common/atlauncher.svg /work/atlauncher/usr/share/icons/hicolor/scalable/apps/atlauncher.svg

# chmod to what is needed
RUN chmod -R 0755 /work

# set the workdir
WORKDIR /work
