#!/bin/bash

@echo off
clear
retl=/opt/retl

# Configure
dbnames=("ORACLE" "MYSQL" "H2")
dbdrivers=("oracle.jdbc.driver.OracleDriver" "com.mysql.jdbc.Driver" "org.h2.Driver")
dbdialects=("org.hibernate.dialect.Oracle10gDialect" "org.hibernate.dialect.MySQL55Dialect" "org.hibernate.dialect.H2Dialect")

echo "****************************************************************"
echo -n "Enter the HTTP RESTful service's port: [9999] "
read port

while (true)
do
  echo "---------------------------------------------------------------"
  echo "Supported database:"
  for ((i=1;i<=3;++i))
  do
    echo "    $i. ${dbnames[i-1]}"
  done
  echo -n "Please select a database:[1-3] "
  read database
  if [ $database -ge 1 -a $database -le 3 ]; then
    break
  fi
done
echo -n "Enter the database's url: [jdbc:oracle:thin:@192.168.7.190:7110:dsdb]"
read dburl
echo -n "Enter the database's user: "
read dbuser
echo -n "Enter the database's password: "
read dbpassword

echo "*****************************************************************"
echo "**"
echo "** The HTTP RESTful service's port: $port"
echo "**"
echo "** You selected database:  ${dbnames[database-1]}"
echo "** The database's url:     $dburl"
echo "** The database's user:    $dbuser"
echo "** The database's passwod: $dbpassword"
echo "**"
echo "##"
echo -n "## All the information are correct?[yes/NO] "
read confirm

if [ "$confirm " == "yes " ]; then
  echo ""
  echo "Modify the configuration file......"

  if [ -f ${retl}/conf/server.properties ]; then
    cp ${retl}/conf/server.properties ${retl}/conf/server.properties.bak
  fi
  sed -i 's/^restful.port/#restful.port/g' ${retl}/conf/server.properties
  sed -i "/^restful.service.classes/i\restful.port=${port}" ${retl}/conf/server.properties

  if [ -f ${retl}/conf/web-app.conf ]; then
    cp ${retl}/conf/web-app.conf ${retl}/conf/web-app.conf.bak
  fi
  sed -i "s/http:\/\/localhost:9999/http:\/\/localhost:${port}/g" ${retl}/conf/web-app.conf

  if [ -f ${retl}/conf/database.properties ]; then
    cp ${retl}/conf/database.properties ${retl}/conf/database.properties.bak
  fi
  sed -i 's/^db.driver/#db.driver/g' ${retl}/conf/database.properties
  sed -i 's/^db.url/#db.url/g' ${retl}/conf/database.properties
  sed -i 's/^db.user/#db.user/g' ${retl}/conf/database.properties
  sed -i 's/^db.password/#db.password/g' ${retl}/conf/database.properties
  sed -i "/^db.initialSize/i\db.driver=${dbdrivers[database-1]}" ${retl}/conf/database.properties
  sed -i "/^db.initialSize/i\db.url=$dburl" ${retl}/conf/database.properties
  sed -i "/^db.initialSize/i\db.user=$dbuser" ${retl}/conf/database.properties
  sed -i "/^db.initialSize/i\db.password=$dbpassword" ${retl}/conf/database.properties

  if [ -f ${retl}/conf/jpa.properties ]; then
    cp ${retl}/conf/jpa.properties ${retl}/conf/jpa.properties.bak
  fi
  sed -i 's/^jpa.database/#jpa.database/g' ${retl}/conf/jpa.properties
  sed -i 's/^jpa.databasePlatform/#jpa.databasePlatform/g' ${retl}/conf/jpa.properties
  sed -i "/^jpa.generateDDL/i\jpa.database=${dbnames[database-1]}" ${retl}/conf/jpa.properties
  sed -i "/^jpa.generateDDL/i\jpa.databasePlatform=${dbdialects[database-1]}" ${retl}/conf/jpa.properties
  sed -i 's/^jpa.generateDDL=false/jpa.generateDDL=true/g' ${retl}/conf/jpa.properties

  echo "Prepare the service...."
  cp ${retl}/bin/retl.service /usr/lib/systemd/system
  systemctl enable retl

  systemctl start retl
  systemctl reload nginx
  systemctl restart retl
  echo "Service start successfully."
fi