SUMMARY = "Template for Connman WiFi connection"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

S = "${WORKDIR}"

FILES:${PN} += "${localstatedir}/lib/connman/*"

do_install() {
    # Create directory
    install -d ${D}${localstatedir}/lib/connman

    # WiFi credentials file
    cat <<EOF > ${D}${localstatedir}/lib/connman/wifi_credentials.config
[service_wifi]
Type = wifi
Name = @WIFI_SSID@
Passphrase = @WIFI_PASS@
IPv4 = dhcp
AutoConnect = true
Favorite = true
EOF

    sed -i "s|@WIFI_SSID@|${WIFI_SSID}|g" ${D}${localstatedir}/lib/connman/wifi_credentials.config
    sed -i "s|@WIFI_PASS@|${WIFI_PASS}|g" ${D}${localstatedir}/lib/connman/wifi_credentials.config

    # Default settings file
    cat <<EOF > ${D}${localstatedir}/lib/connman/settings
[global]
OfflineMode=false

[WiFi]
Enable=true
Tethering=false
EOF
}

pkg_postinst_ontarget:${PN}() {
    chmod 700 /var/lib/connman
    chmod 600 /var/lib/connman/wifi_credentials.config
    chmod 600 /var/lib/connman/settings
}