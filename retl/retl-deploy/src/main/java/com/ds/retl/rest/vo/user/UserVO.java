package com.ds.retl.rest.vo.user;

import com.ds.retl.dal.entity.User;
import org.mx.StringUtils;
import org.mx.rest.vo.BaseDictVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/10/6.
 */
public class UserVO extends BaseDictVO {
    private String password;
    private List<String> tools, roles;
    private boolean online;

    public static List<UserVO> transform(List<User> users) {
        List<UserVO> list = new ArrayList<>();
        if (users != null && users.size() > 0) {
            users.forEach(user -> {
                UserVO userVO = new UserVO();
                UserVO.transform(user, userVO);
                list.add(userVO);
            });
        }
        return list;
    }

    public static void transform(User user, UserVO userVO) {
        if (user == null || userVO == null) {
            return;
        }
        BaseDictVO.transform(user, userVO);
        userVO.tools = new ArrayList<>();
        String favorite = user.getFavorite();
        if (!StringUtils.isBlank(favorite)) {
            String[] tools = favorite.split(",");
            if (tools != null && tools.length > 0) {
                userVO.tools.addAll(Arrays.asList(tools));
            }
        }
        userVO.roles = new ArrayList<>();
        String rolesString = user.getRoles();
        if (!StringUtils.isBlank(rolesString)) {
            String[] roles = rolesString.split(",");
            if (roles != null && roles.length > 0) {
                userVO.roles.addAll(Arrays.asList(roles));
            }
        }
        userVO.online = user.isOnline();
    }

    public static void transform(UserVO userVO, User user) {
        if (user == null || userVO == null) {
            return;
        }
        BaseDictVO.transform(userVO, user);
        user.setPassword(userVO.getPassword());
        List<String> tools = userVO.getTools();
        if (tools != null && tools.size() > 0) {
            user.setFavorite(StringUtils.merge(tools, ","));
        }
        List<String> roles = userVO.getRoles();
        if (roles != null && roles.size() > 0) {
            user.setRoles(StringUtils.merge(roles, ","));
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getTools() {
        return tools;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isOnline() {
        return online;
    }
}
