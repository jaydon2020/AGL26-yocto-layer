SUMMARY = "AGL Flutter Hello World Application"
HOMEPAGE = "https://github.com/jaydon2020/AGL-2026-Flutter-Quiz"
LICENSE = "CLOSED"
SECTION = "graphics"
PV = "1.0+git${SRCREV}"
SRC_URI = "git://github.com/jaydon2020/AGL-2026-Flutter-Quiz.git;protocol=https;branch=main"
SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"

inherit flutter-app agl-app
# Dependencies needed for D-Bus and Audioplayers Linux
DEPENDS += " \
    glib-2.0 \
    dbus \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
"

RDEPENDS:${PN} += " \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
"

PUBSPEC_APPNAME = "gsoc26_flutter_quiz"
PUBSPEC_IGNORE_LOCKFILE = "1"
FLUTTER_APPLICATION_INSTALL_PREFIX = "/usr/share/flutter"
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "AGL GSoC 2026 Flutter Quiz"
AGL_APP_ID = "gsoc26_flutter_quiz"
