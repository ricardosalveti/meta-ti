require conf/machine/include/ti-soc.inc
SOC_FAMILY:append = ":ti43x"

DEFAULTTUNE ?= "armv7athf-neon"
require conf/machine/include/arm/armv7a/tune-cortexa9.inc

# Increase this everytime you change something in the kernel
MACHINE_KERNEL_PR = "r3"

# Default providers, may need to override for specific machines
PREFERRED_PROVIDER_virtual/kernel ?= "linux-ti-staging"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-ti-staging"
PREFERRED_PROVIDER_u-boot ?= "u-boot-ti-staging"
PREFERRED_PROVIDER_virtual/egl ?= "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles1 ?= "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgles2 ?= "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgbm ?= "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/gpudriver ?= "ti-sgx-ddk-km"

KERNEL_IMAGETYPE = "zImage"

KERNEL_DEVICETREE = " \
    am437x-gp-evm.dtb \
    ${@oe.utils.conditional('PREFERRED_PROVIDER_virtual/kernel', 'linux-ti-mainline', '', 'am437x-gp-evm-hdmi.dtb', d)} \
    am437x-sk-evm.dtb am437x-idk-evm.dtb \
    am43x-epos-evm.dtb \
    ${@oe.utils.conditional('PREFERRED_PROVIDER_virtual/kernel', 'linux-ti-mainline', '', 'am43x-epos-evm-hdmi.dtb', d)} \
"

KERNEL_DEVICETREE += "${@oe.utils.conditional('ENABLE_TI_UIO_DEVICES', '1', 'am437x-idk-pru-excl-uio.dtb', '', d)}"

UBOOT_ARCH = "arm"
UBOOT_MACHINE = "am43xx_evm_config"

UBOOT_ENTRYPOINT = "0x80008000"
UBOOT_LOADADDRESS = "0x80008000"

# Generate an extlinux.conf file
UBOOT_EXTLINUX = "1"
UBOOT_EXTLINUX_ROOT = "root=PARTUUID=${uuid}"
UBOOT_EXTLINUX_BOOT_FILES = " \
    extlinux.conf;extlinux/extlinux.conf \
    ${KERNEL_IMAGETYPE} \
    ${KERNEL_DEVICETREE} \
"

SPL_BINARY = "MLO"
UBOOT_SUFFIX = "img"

# Use the expected value of the ubifs filesystem's volume name in the kernel
# and u-boot.
UBI_VOLNAME = "rootfs"

EXTRA_IMAGEDEPENDS += "u-boot"

# List common SoC features, may need to add touchscreen for specific machines
MACHINE_FEATURES = "kernel26 apm usbgadget usbhost vfat ext2 screen alsa ethernet gpu"

IMAGE_FSTYPES += "tar.xz wic.xz wic.bmap"
WKS_FILE ?= "sdimage-2part.wks"
IMAGE_BOOT_FILES ?= "${SPL_BINARY} u-boot.${UBOOT_SUFFIX} ${UBOOT_EXTLINUX_BOOT_FILES}"
do_image_wic[depends] += "virtual/bootloader:do_deploy"
