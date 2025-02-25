DESCRIPTION = "Userspace libraries for PowerVR SGX chipset on TI SoCs"
HOMEPAGE = "https://git.ti.com/graphics/omap5-sgx-ddk-um-linux"
LICENSE = "TI-TSPA"
LIC_FILES_CHKSUM = "file://TI-Linux-Graphics-DDK-UM-Manifest.doc;md5=b17390502bc89535c86cfbbae961a2a8"

inherit features_check

REQUIRED_MACHINE_FEATURES = "gpu"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "ti33x|ti43x|omap-a15|am65xx"

PR = "r38"

BRANCH = "ti-img-sgx/kirkstone/${PV}"

SRC_URI = " \
    git://git.ti.com/git/graphics/omap5-sgx-ddk-um-linux.git;protocol=https;branch=${BRANCH} \
    file://pvrsrvkm.rules \
"
SRCREV = "905809029b877fea42e91b9738825a6294ff1775"

TARGET_PRODUCT:ti33x = "ti335x"
TARGET_PRODUCT:ti43x = "ti437x"
TARGET_PRODUCT:omap-a15 = "ti572x"
TARGET_PRODUCT:am65xx = "ti654x"

INITSCRIPT_NAME = "rc.pvr"
INITSCRIPT_PARAMS = "defaults 8"

inherit update-rc.d

PACKAGECONFIG ??= "udev"
PACKAGECONFIG[udev] = ",,,udev"

PROVIDES += "virtual/egl virtual/libgles1 virtual/libgles2 virtual/libgbm"

DEPENDS += "libdrm udev wayland wayland-protocols libffi expat"
DEPENDS:append:libc-musl = " gcompat"
RDEPENDS:${PN} += "libdrm libdrm-omap udev wayland wayland-protocols libffi expat"

RPROVIDES:${PN} = "libegl libgles1 libgles2 libgbm"
RPROVIDES:${PN}-dev = "libegl-dev libgles1-dev libgles2-dev libgbm-dev"
RPROVIDES:${PN}-dbg = "libegl-dbg libgles1-dbg libgles2-dbg libgbm-dbg"

RREPLACES:${PN} = "libegl libgles1 libgles2 libgbm"
RREPLACES:${PN}-dev = "libegl-dev libgles1-dev libgles2-dev libgbm-dev"
RREPLACES:${PN}-dbg = "libegl-dbg libgles1-dbg libgles2-dbg libgbm-dbg"

RCONFLICTS:${PN} = "libegl libgles1 libgles2 libgbm"
RCONFLICTS:${PN}-dev = "libegl-dev libgles1-dev libgles2-dev libgbm-dev"
RCONFLICTS:${PN}-dbg = "libegl-dbg libgles1-dbg libgles2-dbg libgbm-dbg"

# The actual SONAME is libGLESv2.so.2, so need to explicitly specify RPROVIDES for .so.1 here
RPROVIDES:${PN} += "libGLESv2.so.1"

RRECOMMENDS:${PN} += "ti-sgx-ddk-km"

S = "${WORKDIR}/git"

do_install () {
    oe_runmake install DESTDIR=${D} TARGET_PRODUCT=${TARGET_PRODUCT}
    ln -sf libGLESv2.so.2 ${D}${libdir}/libGLESv2.so.1

    without_sysvinit=${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'false', 'true', d)}
    with_udev=${@bb.utils.contains('PACKAGECONFIG', 'udev', 'true', 'false', d)}

    # Delete initscript if it is not needed or would conflict with the udev rules
    if $without_sysvinit || $with_udev; then
        rm -rf ${D}${sysconfdir}/init.d
        rmdir --ignore-fail-on-non-empty ${D}${sysconfdir}
    fi

    if $with_udev; then
        install -m644 -D ${WORKDIR}/pvrsrvkm.rules \
            ${D}${nonarch_base_libdir}/udev/rules.d/80-pvrsrvkm.rules
    fi

    chown -R root:root ${D}
}

FILES:${PN} =  "${bindir}/*"
FILES:${PN} += " ${libdir}/*"
FILES:${PN} +=  "${includedir}/*"
FILES:${PN} +=  "${sysconfdir}/*"
FILES:${PN} +=  "${datadir}/*"
FILES:${PN} += "${nonarch_base_libdir}/udev/rules.d"

INSANE_SKIP:${PN} += "dev-so ldflags useless-rpaths"
INSANE_SKIP:${PN} += "already-stripped dev-deps"

CLEANBROKEN = "1"
