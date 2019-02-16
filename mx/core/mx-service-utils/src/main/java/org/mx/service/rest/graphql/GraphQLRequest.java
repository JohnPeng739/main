package org.mx.service.rest.graphql;

/**
 * 描述： 来自于GraphQL的RESTful请求对象定义
 *
 * @author john peng
 * Date time 2018/11/25 11:07 AM
 */
public class GraphQLRequest {
    private String name, param, result;

    public GraphQLRequest(String name, String param, String result) {
        super();
        this.name = name;
        this.param = param;
        this.result = result;
    }

    /**
     * 获取请求名
     *
     * @return 请求名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置请求名
     *
     * @param name 请求名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取请求参数
     *
     * @return 请求参数
     */
    public String getParam() {
        return param;
    }

    /**
     * 设置请求参数
     *
     * @param param 请求参数
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * 获取请求结果
     *
     * @return 请求结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置请求结果
     *
     * @param result 请求结果
     */
    public void setResult(String result) {
        this.result = result;
    }
}
