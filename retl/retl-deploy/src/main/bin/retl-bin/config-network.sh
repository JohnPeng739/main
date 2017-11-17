#!/bin/bash
@echo off
clear
echo "-------------------------------------------"
echo "Configure the network:"
echo -n "Enter the machine's name: "
read machineName
echo -n "Enter the machine's ip: "
read machineIp
echo -n "Enter the network mask: "
read networkMask
echo -n "Enter the gateway: "
read gateway
echo "-------------------------------------------"
echo "Configure the static hosts:"

while(true)
do
  echo -n "How many servers you want configure? [number]"
  read num
  if [ $num -gt 0 ]; then
    break
  fi
done
for ((i=1; i<=$num; ++i))
do
  echo -n "Enter server $i static DNS: [192.168.2.121 storm1]"
  read hosts[$i-1]
done

echo "********************************************"
echo "**"
echo "** machine's name:  $machineName"
echo "** machine's ip:    $machineIp"
echo "** network mask:    $networkMask"
echo "** gateway:         $gateway"
echo "**"
echo "** There has $num servers:"
for ((i=1; i<=$num; ++i))
do
  echo "** server $i: ${hosts[i-1]}"
done
echo "********************************************"
echo "##"
echo -n "## All the information are correct?[yes/NO] "
read confirm

if [ "$confirm " == "yes " ]; then
  echo ""
  echo "Modify the system's files......"
  mv /etc/hostname /etc/hostname.bak
  echo "${machineName}" > /etc/hostname

  net=`cat /proc/net/dev | sed -n '3p' | awk '{print substr($1, 0, index($1, ":") - 1)}'`
  if [ "$net " == "lo " ]; then
    echo "There is no network interface, please check the network."
    exit 0
  fi
  enp0s3=/etc/sysconfig/network-scripts/ifcfg-${net}
  if [ -f ${emp0s3} ]; then
    cp ${enp0s3} ${enp0s3}.bak
  fi
  cat > ${enp0s3} << EOF
DEVICE=${net}
NAME="${net}"
BOOTPROTO=static
ONBOOT=yes
IPADDR=${machineIp}
NETMASK=${networkMask}
GATEWAY=${gateway}
TYPE=Ethernet
USERCTL=no
IPV6INIT=0
PEERDNS=yes
EOF

  mv /etc/hosts /etc/hosts.bak
  for ((i=1;i<=$num;++i))
  do
    echo "${hosts[i-1]}" >> /etc/hosts
  done
  echo "Modify the systems's files successfully."

  echo "******************************************"
  echo "**"
  echo -n "** The system need restart, do it now?[yes/NO] "
  read confirm
  if [ "$confirm " == "yes " ]; then
    init 6
  else
    echo "## You can restart the system late."
  fi
fi
