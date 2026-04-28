SUMMARY = "FTDI D2XX Driver Library"
LICENSE = "CLOSED" 
LIC_FILES_CHKSUM = "file://ReadMe.txt;md5=474e6c0f6e62d03b2ff8704d0057a766"

SRC_URI = "https://ftdichip.com/wp-content/uploads/2022/07/libftd2xx-arm-v8-1.4.27.tgz"
SRC_URI[sha256sum] = "48e202ecceb4cbe9e77f5444ef887d6687393a74e33da7de5a67c2227c567970"

S = "${WORKDIR}/release"

# Prevent Yocto from trying to strip or modify the binary
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

do_install() {
    install -d ${D}${libdir}
    install -m 0755 ${S}/build/libftd2xx.so.${PV} ${D}${libdir}/libftd2xx.so
}

FILES:${PN} = "${libdir}/libftd2xx.so"
INSANE_SKIP:${PN} = "ldflags dev-elf" 
# Necessary for pre-compiled binaries
