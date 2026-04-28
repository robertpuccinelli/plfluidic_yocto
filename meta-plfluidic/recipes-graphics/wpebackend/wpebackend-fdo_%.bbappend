# Ensure the unversioned and default symlinks are created in /usr/lib
do_install:append() {
    install -d ${D}${libdir}
    ln -sf libWPEBackend-fdo-1.0.so.1 ${D}${libdir}/libWPEBackend-default.so
    ln -sf libWPEBackend-fdo-1.0.so.1 ${D}${libdir}/libWPEBackend-fdo-1.0.so
    ln -sf libWPEBackend-fdo-1.0.so.1 ${D}${libdir}/libWPEBackend-fdo.so
}

# Ensure these symlinks are actually included in the main package, not just -dev
FILES:${PN} += " \
    ${libdir}/libWPEBackend-default.so \
    ${libdir}/libWPEBackend-fdo-1.0.so \
    ${libdir}/libWPEBackend-fdo.so \
"