SUMMARY = "Flutter BLE Scanner - bluez_native_comms example"
DESCRIPTION = "Flutter BLE scanner example app using bluez_native_comms native library"

HOMEPAGE = "https://github.com/jwinarske/bluez_native_comms"
LICENSE = "CLOSED"

SECTION = "graphics"

SRC_URI = " \
    git://github.com/jwinarske/bluez_native_comms.git;protocol=https;branch=main;name=bluez \
    git://github.com/Kistler-Group/sdbus-cpp.git;protocol=https;branch=master;destsuffix=git/native/third_party/sdbus-cpp;name=sdbus \
"

SRCREV_FORMAT = "bluez_sdbus"
SRCREV_bluez = "${AUTOREV}"
SRCREV_sdbus = "28b78822cfc5fbec4bd9906168493e9985f586ed"

# Point S directly at the flutter app so flutter-app class finds pubspec.yaml immediately
S = "${WORKDIR}/git/example/flutter_ble_scanner"

inherit flutter-app agl-app pkgconfig

DEPENDS += " \
    glib-2.0 \
    dbus \
    bluez5 \
    pkgconfig-native \
    cmake-native \
    ninja-native \
"

RDEPENDS:${PN} += " \
    bluez5 \
"

PUBSPEC_APPNAME = "flutter_ble_scanner"
PUBSPEC_IGNORE_LOCKFILE = "1"

FLUTTER_APPLICATION_INSTALL_PREFIX = "/usr/share/flutter"

AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "BLE Scanner"
AGL_APP_ID = "flutter_ble_scanner"

do_fix_pubspec() {
    # Copy the bluez_native_comms Dart package into the app directory
    # so pub get resolves it locally — avoids relative URI issues across directories
    mkdir -p ${S}/local_bluez_nc
    cp -rf ${WORKDIR}/git/lib     ${S}/local_bluez_nc/
    cp -f  ${WORKDIR}/git/pubspec.yaml ${S}/local_bluez_nc/

    # Point pubspec.yaml to the local copy instead of path: ../..
    sed -i "s|path: \.\./\.\.|path: ./local_bluez_nc|g" ${S}/pubspec.yaml

    rm -rf ${S}/.dart_tool
}

addtask do_fix_pubspec before do_archive_pub_cache after do_patch

do_compile_native() {
    # Build libbluez_nc.so from native/ in the repo root
    export CXX="${CXX} ${CXXFLAGS} ${LDFLAGS}"
    export CC="${CC} ${CFLAGS} ${LDFLAGS}"
    export PKG_CONFIG_PATH="${PKG_CONFIG_DIR}:${PKG_CONFIG_PATH}"

    cd ${WORKDIR}/git
    cmake -B build native/ -GNinja \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_SYSROOT=${STAGING_DIR_TARGET}
    cmake --build build --parallel ${@oe.utils.parallel_make_argument(d, '-j %d')}
}

addtask do_compile_native before do_compile after do_configure

do_install:append() {
    # Install libbluez_nc.so so it can be found at runtime
    install -d ${D}${libdir}
    install -m 0755 ${WORKDIR}/git/build/libbluez_nc.so ${D}${libdir}/

    # Wrapper script that sets BLUEZ_NC_LIB before launching the app
    install -d ${D}${bindir}
    cat > ${D}${bindir}/flutter-ble-scanner << 'EOF'
#!/bin/sh
export BLUEZ_NC_LIB=/usr/lib/libbluez_nc.so
exec /usr/share/flutter/flutter_ble_scanner/flutter_ble_scanner "$@"
EOF
    chmod 0755 ${D}${bindir}/flutter-ble-scanner
}

FILES_SOLIBSDEV = ""
FILES:${PN} += " \
    ${libdir}/libbluez_nc.so* \
    ${bindir}/flutter-ble-scanner \
"

INSANE_SKIP:${PN}-dbg += "buildpaths"
