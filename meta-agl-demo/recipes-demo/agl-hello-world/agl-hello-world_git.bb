SUMMARY = "AGL Flutter Hello World Application"
HOMEPAGE = "https://github.com/jaydon2020/agl-hello-world"
LICENSE = "CLOSED"
SECTION = "graphics"
PV = "1.0+git${SRCREV}"
SRC_URI = "git://github.com/jaydon2020/agl-hello-world.git;protocol=https;branch=agl"
SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"

inherit flutter-app agl-app

DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-bad \
    protobuf-native \
    grpc-native \
    grpc \
    systemd \
    rtl-sdr \
    libusb-compat \
    flutter-engine \
    flutter-sdk-native \
"

RDEPENDS:${PN} += " \
    gstreamer1.0-pipewire \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
"

PUBSPEC_APPNAME = "hello_world"
PUBSPEC_IGNORE_LOCKFILE = "1"
FLUTTER_APPLICATION_INSTALL_PREFIX = "/usr/share/flutter"
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "AGL Hello World Demo"
AGL_APP_ID = "hello_world"
