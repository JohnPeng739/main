package org.mx.spring;

import org.springframework.stereotype.Component;

@Component("testBean")
public class TestBean {
    public String hello() {
        return "Hello, world.";
    }
}
