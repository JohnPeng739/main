package org.mx.comps.rbac;

import org.mx.comps.rbac.rest.config.RbacRestHibernateConfig;
import org.mx.comps.rbac.rest.config.RbacRestMongodbConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RBACRestApplication {
    public static void main(String[] args) {
        if (args.length == 2 && "-type".equalsIgnoreCase(args[0])) {
            if ("jpa".equalsIgnoreCase(args[1])) {
                new AnnotationConfigApplicationContext(RbacRestHibernateConfig.class);
                return;
            } else if ("mongodb".equalsIgnoreCase(args[1])) {
                new AnnotationConfigApplicationContext(RbacRestMongodbConfig.class);
                return;
            }
        }
        System.out.println("java -cp <classpath> org.mx.comps.rbac.RBACRestApplication -type <jpa | mongodb>");
    }
}
