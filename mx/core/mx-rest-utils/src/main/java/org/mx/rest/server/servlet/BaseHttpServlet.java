package org.mx.rest.server.servlet;

import javax.servlet.http.HttpServlet;

/**
 * Created by john on 2017/10/6.
 */

/**
 * 基础的Http Servlet定义
 *
 * @author : john.peng date : 2017/10/6
 * @see HttpServlet
 */
public class BaseHttpServlet extends HttpServlet {
    private String pathSpec = "servlet";

    /**
     * 默认的构造函数
     */
    public BaseHttpServlet() {
        super();
    }

    /**
     * 获取Http Servlet对应的路径
     *
     * @return 路径
     */
    public String getPathSpec() {
        return this.pathSpec;
    }

    /**
     * 设置Http Servlet对应的路径
     *
     * @param pathSpec 路径
     */
    protected void setPathSpec(String pathSpec) {
        this.pathSpec = pathSpec;
    }
}
