package org.mx.comps.rbac;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RBACRestApplication {
    public static void main(String[] args) {
        if (args.length == 2 && "-type".equalsIgnoreCase(args[0])) {
            if ("jpa".equalsIgnoreCase(args[1])) {
                new AnnotationConfigApplicationContext("org.mx.comps.rbac.jpa.config",
                        "org.mx.comps.rbac.rest.config");
                return;
            } else if ("mongodb".equalsIgnoreCase(args[1])) {
                new AnnotationConfigApplicationContext("org.mx.comps.rbac.mongodb.config",
                        "org.mx.comps.rbac.rest.config");
                return;
            }
        }
        System.out.println("java -cp <classpath> org.mx.comps.rbac.RBACRestApplication -type <jpa | mongodb>");
    }
}
