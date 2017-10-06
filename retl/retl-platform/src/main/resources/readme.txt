***************************************************************************
**
**  实时数据ETL 计算拓扑 提交命令后程序
**
***************************************************************************

提交步骤：
1） 将配置程序上传到nimbus服务器中：/opt/storm/retl/config
2） 在nimbus服务器中运行以下命令来提交拓扑：
    /opt/storm/bin/submitTopology.sh /opt/storm/retl/config/cjd-persist-2-jdbc.json
其中：
    /opt/storm/bin/submitTopology.sh                为RETL平台运行代码
    /opt/storm/retl/config/cjd-persist-2-jdbc.json  为本次提交的拓扑配置程序