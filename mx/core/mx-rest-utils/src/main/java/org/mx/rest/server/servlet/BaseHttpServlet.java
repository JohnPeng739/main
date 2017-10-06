package org.mx.rest.server.servlet;

import javax.servlet.http.HttpServlet;

/**
 * Created by john on 2017/10/6.
 */
public class BaseHttpServlet extends HttpServlet {
    private String pathSpec = "servlet";

    public BaseHttpServlet() {
        super();
    }

    protected void setPathSpec(String pathSpec) {
        this.pathSpec = pathSpec;
    }

    public String getPathSpec() {
        return this.pathSpec;
    }
}
