SUMMARY = "Network Info Plus Example - AGL WiFi Test"
HOMEPAGE = "https://github.com/jwinarske/flutter_reactive_ble"
LICENSE = "CLOSED"

SECTION = "graphics"

# Source points to the monorepo
SRC_URI = "git://github.com/jwinarske/flutter_reactive_ble.git;protocol=https;branch=master"
SRCREV = "${AUTOREV}"

# Set S to the specific example directory so the flutter-app class finds pubspec.yaml
S = "${WORKDIR}/git/example"

inherit flutter-app agl-app

# network_info_plus on Linux uses D-Bus to talk to the network manager
DEPENDS += " \
    glib-2.0 \
    dbus \
    sdbus-c++ \
    cmake-native \
    pkgconfig-native \
"

RDEPENDS:${PN} += " \
    glib-2.0 \
    dbus \
    sdbus-c++ \
"

# The app name defined in the example's pubspec.yaml
PUBSPEC_APPNAME = "flutter_reactive_ble_example"
PUBSPEC_IGNORE_LOCKFILE = "1"

FLUTTER_APPLICATION_INSTALL_PREFIX = "/usr/share/flutter"

# AGL Specifics
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "Bluetooth Demo"
AGL_APP_ID = "flutter_reactive_ble_example"

# Optional: If the example requires files from the parent directory 
# (like the actual plugin source), ensure the build system can see the root.
# The flutter-app class usually handles this if S is set correctly.

# =======================================================================
# THE BULLETPROOF FIX: Bundle packages and sync the lockfile
# =======================================================================
do_configure:prepend() {
    # 1. Copy the entire 'packages' directory into the example app's folder
    cp -rf ${WORKDIR}/git/packages ${S}/local_packages
    
    # 2. Update the example's pubspec.yaml to point to this new local folder
    sed -i 's|path: \.\./packages|path: local_packages|g' ${S}/pubspec.yaml
    
    # 3. Update the pubspec.lock so Flutter's --enforce-lockfile passes!
    # (Checking for both quoted and unquoted paths just to be safe)
    if [ -f ${S}/pubspec.lock ]; then
        sed -i 's|path: "\.\./packages|path: "local_packages|g' ${S}/pubspec.lock
        sed -i 's|path: \.\./packages|path: local_packages|g' ${S}/pubspec.lock
    fi
    
    # 4. Only delete the cached tool directory, keep the lockfile intact
    rm -rf ${S}/.dart_tool
}

do_compile_ffi() {
    # Strip hidden visibility so sdbus::Error typeinfo bridges correctly across the dynamic library boundary
    sed -i 's/-fvisibility=hidden//g' ${WORKDIR}/git/packages/reactive_ble_linux/linux/CMakeLists.txt
    sed -i '/VISIBILITY_PRESET/d' ${WORKDIR}/git/packages/reactive_ble_linux/linux/CMakeLists.txt
    sed -i '/VISIBILITY_INLINES_HIDDEN/d' ${WORKDIR}/git/packages/reactive_ble_linux/linux/CMakeLists.txt

    # Build the Dart FFI native library (libdart_bluez_ble.so) for the plugin
    export CXX="${CXX} ${CXXFLAGS} ${LDFLAGS}"
    export CC="${CC} ${CFLAGS} ${LDFLAGS}"
    export PKG_CONFIG_PATH="${PKG_CONFIG_DIR}:${PKG_CONFIG_PATH}"
    
    cd ${WORKDIR}/git/packages/reactive_ble_linux/linux
    cmake -B build -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_SYSROOT=${STAGING_DIR_TARGET}
    cmake --build build --parallel ${@oe.utils.parallel_make_argument(d, '-j %d')}
    cd ${S}
}

addtask do_compile_ffi before do_compile after do_configure


do_install:append() {
    # Install the built libdart_bluez_ble.so into the standard library directory so dlopen can find it globally
    install -d ${D}${libdir}
    install -m 0755 ${WORKDIR}/git/packages/reactive_ble_linux/linux/lib/libdart_bluez_ble.so* ${D}${libdir}/
}

# Ensure the un-versioned .so goes into the main package instead of the -dev package
FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/libdart_bluez_ble.so*"

# Suppress the harmless buildpaths QA warning for the debug package
INSANE_SKIP:${PN}-dbg += "buildpaths"