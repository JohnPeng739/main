package org.mx.service.server.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基础的Http Servlet定义
 *
 * @author : john.peng date : 2017/10/6
 * @see HttpServlet
 */
public class BaseHttpServlet extends HttpServlet {
    private String path = "/";

    /**
     * 默认的构造函数
     */
    public BaseHttpServlet() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param path 路径
     */
    public BaseHttpServlet(String path) {
        this();
        this.path = path;
    }

    /**
     * 获取Http Servlet对应的路径
     *
     * @return 路径
     */
    public String getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     * @see HttpServlet#service(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }
}
