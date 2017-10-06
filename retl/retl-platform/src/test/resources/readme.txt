***************************************************************************
**
**  实时数据ETL 计算拓扑 提交命令后程序
**
***************************************************************************

提交步骤：
1） 将配置程序上传到nimbus服务器中：/opt/storm/upload
2） 在nimbus服务器中运行以下命令来提交拓扑：
    /opt/storm/bin/storm jar /opt/storm/upload/retlp.jar com.ds.retl.ETLTopologyBuilder /opt/storm/upload/storm-sample.json
其中：
    /opt/storm/upload/retlp.jar         为RETL平台运行代码
    /opt/storm/upload/storm-sample.json 为本次提交的拓扑配置程序