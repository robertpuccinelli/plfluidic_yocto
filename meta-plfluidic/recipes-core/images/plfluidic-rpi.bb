# Build off of the Raspberry Pi image
require recipes-core/images/rpi-test-image.bb

# Add features to image
# iptables, kernel-module-tun are req'd for tailscale
# wpe, weston, cog are required for graphics and browser
IMAGE_INSTALL:append = " \
    iptables \
    connman \
    connman-client \
    connman-template \
    kernel-modules \
    kernel-module-tun \
    tailscale \
    tailscale-auth-service \
    libwpe \
    wpebackend-fdo \
    wpewebkit \
    weston \
    weston-init \
    cog \
    cog-kiosk-service \
    python3-core \
    python3-venv \
    plfluidic-service \
    libftd2xx \
    sudo \
    ttf-dejavu-sans \
    ttf-dejavu-sans-mono \
"

# Add connman as a service
SYSTEMD_SERVICE:config-connman = "connman.service"


# Conditional tailscale-auth recipe installation
python () {
    auth_key = d.getVar('TS_AUTH_KEY')
    if auth_key and auth_key != "NONE":
        d.appendVar('IMAGE_INSTALL', ' tailscale-auth-service')
    else:
        bb.warn("TS_AUTH_KEY is 'NONE'. Skipping tailscale-auth-service from this image build.")
}

IMAGE_FEATURES += "ssh-server-openssh allow-root-login"

ROOTFS_POSTPROCESS_COMMAND += "set_sudo_permissions; "

# Create a configuration file to allow the 'sudo' group to run commands
fakeroot python set_sudo_permissions() {
    import os
    
    # Define the path inside the target rootfs
    sudoers_dir = d.expand('${IMAGE_ROOTFS}${sysconfdir}/sudoers.d')
    sudo_file = os.path.join(sudoers_dir, '90_sudo_group')
    
    # Create directory if it doesn't exist
    if not os.path.exists(sudoers_dir):
        os.makedirs(sudoers_dir)
        
    # Write the permission line
    with open(sudo_file, 'w') as f:
        f.write("%sudo ALL=(ALL) ALL\n")
        
    # Set the strict permissions required by sudo (read-only for owner/group)
    os.chmod(sudo_file, 0o440)
}