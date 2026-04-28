SUMMARY = "Tailscale Automatic Provisioning Service"
SECTION = "connectivity"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# 0. Check if auth key is defined, skip if not
python () {
    auth_key = d.getVar('TS_AUTH_KEY')
    
    # Check if the key is missing OR explicitly set to "NONE"
    if not auth_key or auth_key == "NONE":
        raise bb.parse.SkipRecipe("TS_AUTH_KEY is set to NONE or is empty. Skipping tailscale-auth.")
}

# 1. Inherit the systemd class
inherit features_check
inherit systemd
REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = " \
    file://tailscale-auth.sh \
    file://tailscale-auth.service \
"

S = "${WORKDIR}"

# 2. Tell the systemd class which service file to enable
SYSTEMD_SERVICE:${PN} = "tailscale-auth.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    # Install the shell script to /usr/bin
    install -d ${D}${bindir}
    install -m 0755 ${S}/tailscale-auth.sh ${D}${bindir}/tailscale-auth.sh

    # Inject your secret key from local.conf into the script
    sed -i "s|@TS_AUTH_KEY@|${TS_AUTH_KEY}|g" ${D}${bindir}/tailscale-auth.sh

    # Install the systemd unit file to the standard location
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/tailscale-auth.service ${D}${systemd_system_unitdir}/tailscale-auth.service

    # Define a DNS server to facilitate connections with tailscale
    install -d ${D}${sysconfdir}
    echo "nameserver 8.8.8.8" > ${D}${sysconfdir}/resolv.conf
}

# 3. Ensure the files are packaged correctly
FILES:${PN} += " \
    ${systemd_system_unitdir}/tailscale-auth.service \
    ${bindir}/tailscale-auth.sh \
    ${sysconfdir}/resolv.conf \
"

# 4. Make sure systemd is actually available in the image
REQUIRED_DISTRO_FEATURES = "systemd"

do_install:append() {
}