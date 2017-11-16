#!/bin/bash

read -p "Enter the machine's name: " machineName
read -p "Enter the machine's IP: " machineIp
read -p "Enter the machine's netmask: " netmask
read -p "Enter the machine's gateway: " gateway
read -p "Enter the machine's DNS: " dns

#sed -i "s/localhost.localdomain/${machineName}/g" /etc/hostname
mv /etc/hostname /etc/hostname.bak
echo "${machineName}" > /etc/hostname
cp /etc/sysconfig/network-scripts/ifcfg-enp0s3 /etc/sysconfig/network-scripts/ifcfg-enp0s3.bak
sed -i "s/IPADDR=\"192.168.2.111\"/IPADDR=\"${machineIp}\"/g" /etc/sysconfig/network-scripts/ifcfg-enp0s3
sed -i "s/NETMASK=\"255.255.255.0\"/NETMASK=\"${netmask}\"/g" /etc/sysconfig/network-scripts/ifcfg-enp0s3
sed -i "s/GATEWAY=\"192.168.0.1\"/GATEWAY=\"${gateway}\"/g" /etc/sysconfig/network-scripts/ifcfg-enp0s3
sed -i "s/DNS1=\"192.168.0.6\"/DSN1=\"${dns}\"/g" /etc/sysconfig/network-scripts/ifcfg-enp0s3
#sed -i "s/nameserver 192.168.0.6/nameserver {dns}" /etc/resolv.conf
mv /etc/resolv.conf /etc/resolv.conf.bak
echo "nameserver ${dns}" > /etc/resolv.conf

cd /opt/retl

mv bin/retl.service /usr/lib/systemd/system
systemctl enable retl
ln -s /opt/retl/conf/web-app.conf /etc/nginx/conf.d/web-app.conf

systemctl start retl
systemctl reload nginx
systemctl restart retl

echo "System will restart..."
init 6