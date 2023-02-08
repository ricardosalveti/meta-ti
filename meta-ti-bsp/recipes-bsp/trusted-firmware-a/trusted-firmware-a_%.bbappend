PV:k3 = "2.7"
SRCREV_tfa:k3 = "1309c6c805190bd376c0561597653f3f8ecd0f58"
SRC_URI:k3 = "git://git.trustedfirmware.org/TF-A/trusted-firmware-a.git;protocol=https;name=tfa;branch=master"
COMPATIBLE_MACHINE:k3 = "k3"
TFA_BUILD_TARGET:k3 = "all"
TFA_INSTALL_TARGET:k3 = "bl31"
TFA_SPD:k3 = "opteed"

# Use default package TI SECDEV is one is not provided
DEPENDS:append:k3 = "${@ '' if d.getVar('TI_SECURE_DEV_PKG_K3') else ' ti-k3-secdev-native' }"

# Set a default value for TI_K3_SECDEV_INSTALL_DIR
export TI_K3_SECDEV_INSTALL_DIR = "${STAGING_DIR_NATIVE}${datadir}/ti/ti-k3-secdev"
include recipes-ti/includes/ti-paths.inc
TI_SECURE_DEV_PKG:k3 = "${@ d.getVar('TI_SECURE_DEV_PKG_K3') or d.getVar('TI_K3_SECDEV_INSTALL_DIR') }"

EXTRA_OEMAKE:append:k3 = "${@ ' K3_USART=' + d.getVar('TFA_K3_USART') if d.getVar('TFA_K3_USART') else ''}"
EXTRA_OEMAKE:append:k3 = "${@ ' K3_PM_SYSTEM_SUSPEND=' + d.getVar('TFA_K3_SYSTEM_SUSPEND') if d.getVar('TFA_K3_SYSTEM_SUSPEND') else ''}"

# Signing procedure for K3 devices
do_compile:append:k3() {
	export TI_SECURE_DEV_PKG=${TI_SECURE_DEV_PKG}
	mv ${BUILD_DIR}/bl31.bin ${BUILD_DIR}/bl31.bin.unsigned
	${TI_SECURE_DEV_PKG}/scripts/secure-binary-image.sh ${BUILD_DIR}/bl31.bin.unsigned ${BUILD_DIR}/bl31.bin
}
