SUMMARY = "BlueZ Native Communications Library"
DESCRIPTION = "Native C/C++ library for BlueZ D-Bus communication via sdbus-c++, \
providing zero-copy characteristic notifications to Dart applications"

HOMEPAGE = "https://github.com/jwinarske/bluez_native_comms"
LICENSE = "CLOSED"

SECTION = "libs"

SRC_URI = " \
    git://github.com/jwinarske/bluez_native_comms.git;protocol=https;branch=main;name=bluez \
    git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master;destsuffix=git/native/third_party/sdbus-cpp;name=sdbus \
"

SRCREV_FORMAT = "bluez_sdbus"
SRCREV_bluez = "${AUTOREV}"
SRCREV_sdbus = "28b78822cfc5fbec4bd9906168493e9985f586ed"

S = "${WORKDIR}/git"

# The CMakeLists.txt is in the native/ subdirectory
OECMAKE_SOURCEPATH = "${S}/native"

inherit cmake pkgconfig

# Required dependencies for BlueZ D-Bus communication
DEPENDS += " \
    glib-2.0 \
    dbus \
    bluez5 \
"

# Only package the shared library
FILES:${PN} = "${libdir}/libbluez_nc.so*"
FILES:${PN}-dev = "${includedir}/ ${libdir}/cmake/"

PROVIDES = "libbluez_nc"
