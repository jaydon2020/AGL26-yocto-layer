SUMMARY = "Network Info Plus Example - AGL WiFi Test"
HOMEPAGE = "https://github.com/jaydon2020/agl-test-wifi"
LICENSE = "CLOSED"

SECTION = "graphics"

# Source points to the monorepo
SRC_URI = "git://github.com/jaydon2020/agl-test-wifi.git;protocol=https;branch=testing"
SRCREV = "${AUTOREV}"

# Set S to the specific example directory so the flutter-app class finds pubspec.yaml
S = "${WORKDIR}/git"

inherit flutter-app agl-app

# network_info_plus on Linux uses D-Bus to talk to the network manager
DEPENDS += " \
    glib-2.0 \
    dbus \
    pkgconfig-native \
"

RDEPENDS:${PN} += " \
    glib-2.0 \
    dbus \
"

# The app name defined in the example's pubspec.yaml
PUBSPEC_APPNAME = "agl_test_wifi"
PUBSPEC_IGNORE_LOCKFILE = "1"

FLUTTER_APPLICATION_INSTALL_PREFIX = "/usr/share/flutter"

# AGL Specifics
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "AGL WiFi Demo"
AGL_APP_ID = "agl_test_wifi"

# Optional: If the example requires files from the parent directory 
# (like the actual plugin source), ensure the build system can see the root.
# The flutter-app class usually handles this if S is set correctly.