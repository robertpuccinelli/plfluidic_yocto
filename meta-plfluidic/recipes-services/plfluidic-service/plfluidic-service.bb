SUMMARY = "Pre-packaged virtual environment for microfluidic control"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit python3native
inherit features_check
inherit systemd

DEPENDS += "python3-native python3-pip-native"
DEPENDS += "git-native"
RDEPENDS:${PN} += "python3-modules"
RDEPENDS:${PN} += "python3-ctypes"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = " \
    git://github.com/robertpuccinelli/plfluidics.git;protocol=https;branch=master \
    file://plfluidic.sh \
    file://plfluidic.service \
"

SRCREV = "906f6112715606b987a88cf1bc81a58ce2351a59"

S = "${WORKDIR}"

# An unclean install that requires a download
do_install[network] = "1"

# Prevent Yocto from trying to "fix" or strip the vendor binary for libft4222
INSANE_SKIP:${PN} += "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_install() {
    # 1. Install the module directly into the system site-packages
    # --root ${D} tells pip to install relative to the image staging area
    # --prefix /usr ensures the internal paths point to the final Pi location

    local target_path="${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages"
    install -d ${target_path}

    python3 -m pip install \
            --target ${target_path} \
            --no-cache-dir \
            --platform manylinux2014_aarch64 \
            --only-binary=:all: \
            --upgrade \
            --force-reinstall \
            ${S}/git
    
    # 2. Install your launcher script
    install -d ${D}${bindir}
    install -m 0755 ${S}/plfluidic.sh ${D}${bindir}/plfluidic.sh

    # 3. Install the systemd unit file
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/plfluidic.service ${D}${systemd_system_unitdir}/plfluidic.service

    # Scrub the host-specific build paths from pip metadata
    find ${D}${libdir}/python${PYTHON_BASEVERSION}/site-packages -name "direct_url.json" -exec rm -f {} +
    rm -rf ${target_path}/bin
}

# 4. Ensure the system files are included in the package
FILES:${PN} += " \
    ${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
    ${bindir}/plfluidic.sh \
    ${systemd_system_unitdir}/plfluidic.service \
    ${libdir}/libftd2xx.so* \
"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "plfluidic.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
