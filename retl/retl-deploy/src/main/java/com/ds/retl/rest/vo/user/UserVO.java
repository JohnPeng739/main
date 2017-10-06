package com.ds.retl.rest.vo.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/10/6.
 */
public class UserVO {
    private String code, name;
    private List<String> tools, roles;

    public UserVO() {
        super();
        this.tools = new ArrayList<>();
        this.roles = new ArrayList<>();
    }

    public UserVO(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<String> getTools() {
        return tools;
    }

    public List<String> getRoles() {
        return roles;
    }
}
