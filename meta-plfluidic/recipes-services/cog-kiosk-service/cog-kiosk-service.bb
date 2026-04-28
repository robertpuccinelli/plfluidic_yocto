SUMMARY = "Cog Kiosk Mode Startup Service"
DESCRIPTION = "Launches the Cog browser in kiosk mode on boot for microfluidic telemetry."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# 1. Inherit the systemd class to handle the unit registration
inherit systemd

# 2. Point to your service file in the 'files' subdirectory
SRC_URI = "file://cog-kiosk.service"

S = "${WORKDIR}"

# 3. Tell systemd which service to enable
SYSTEMD_SERVICE:${PN} = "cog-kiosk.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    # Install the service file into the standard systemd directory
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/cog-kiosk.service ${D}${systemd_system_unitdir}/cog-kiosk.service
}

# 4. Ensure the file is packaged correctly in the final image
FILES:${PN} += "${systemd_system_unitdir}/cog-kiosk.service"

# 5. Dependency check: Cog must be installed for this service to be useful
RDEPENDS:${PN} += "cog"