SUMMARY = "The Suricata Engine is an Open Source Next Generation Intrusion Detection and Prevention Engine"

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=1fbd81241fe252ec0f5658a521ab7dd8"

SRC_URI += "file://no_libhtp_build.patch \
            file://no_pcap_pkgconf_check.patch \
            file://volatiles.03_suricata \
            file://suricata.yaml \
        "

inherit autotools-brokensep pkgconfig

CFLAGS += "-D_DEFAULT_SOURCE"

CACHED_CONFIGUREVARS = "ac_cv_header_htp_htp_h=yes ac_cv_lib_htp_htp_conn_create=yes "

EXTRA_OECONF += " --disable-debug \
    --enable-non-bundled-htp \
    --disable-gccmarch-native \
    "

PACKAGECONFIG ??= "htp jansson file pcre yaml pcap cap-ng net nfnetlink"
PACKAGECONFIG[htp] = "--with-libhtp-includes=${STAGING_INCDIR} --with-libhtp-libraries=${STAGING_LIBDIR}, ,libhtp,"
PACKAGECONFIG[pcre] = "--with-libpcre-includes=${STAGING_INCDIR} --with-libpcre-libraries=${STAGING_LIBDIR}, ,libpcre ," 
PACKAGECONFIG[yaml] = "--with-libyaml-includes=${STAGING_INCDIR} --with-libyaml-libraries=${STAGING_LIBDIR}, ,libyaml ,"
PACKAGECONFIG[pcap] = "--with-libpcap-includes=${STAGING_INCDIR} --with-libpcap-libraries=${STAGING_LIBDIR}, ,libpcap ," 
PACKAGECONFIG[cap-ng] = "--with-libcap_ng-includes=${STAGING_INCDIR} --with-libcap_ng-libraries=${STAGING_LIBDIR}, ,libcap-ng , "
PACKAGECONFIG[net] = "--with-libnet-includes=${STAGING_INCDIR} --with-libnet-libraries=${STAGING_LIBDIR}, , libnet," 
PACKAGECONFIG[nfnetlink] = "--with-libnfnetlink-includes=${STAGING_INCDIR} --with-libnfnetlink-libraries=${STAGING_LIBDIR}, ,libnfnetlink ,"

PACKAGECONFIG[jansson] = "--with-libjansson-includes=${STAGING_INCDIR} --with-libjansson-libraries=${STAGING_LIBDIR},,jansson, jansson"
PACKAGECONFIG[file] = ",,file, file"
PACKAGECONFIG[nss] = "--with-libnss-includes=${STAGING_INCDIR} --with-libnss-libraries=${STAGING_LIBDIR}, , libnss,"
PACKAGECONFIG[nspr] = "--with-libnspr-includes=${STAGING_INCDIR} --with-libnspr-libraries=${STAGING_LIBDIR}, , libnspr,"

export logdir = "${localstatedir}/log"

do_install_append () {
    install -d ${D}${sysconfdir}/suricata
    install -d ${D}${sysconfdir}/suricata ${D}${sysconfdir}/default/volatiles
    install -m 644 classification.config ${D}${sysconfdir}/suricata
    install -m 644 reference.config ${D}${sysconfdir}/suricata
    install -m 644 ${WORKDIR}/suricata.yaml ${D}${sysconfdir}/suricata
    install -m 0644 ${WORKDIR}/volatiles.03_suricata  ${D}${sysconfdir}/default/volatiles/volatiles.03_suricata
}

pkg_postinst_${PN} () {
if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ] ; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
    ${bindir}/suricata -c ${sysconfdir}/suricata.yaml -i eth0 
}

FILES_${PN} += "${sysconfdir}/suricata ${logdir}/suricata"
FILES_${PN}-dev += "/usr/lib/python2.7/site-packages"

RDEPENDS_${PN} += " python"
