package org.mx.comps.rbac;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Before;
import org.mx.dal.config.DalMongodbConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.fail;

public class BaseTest {
    protected AnnotationConfigApplicationContext context;
    private MongodExecutable mongodExecutable;
    private MongodProcess mongod;

    @Before
    public void before() {
        try {
            IMongodConfig config = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                    .net(new Net("localhost", 27017, Network.localhostIsIPv6())).build();
            mongodExecutable = MongodStarter.getDefaultInstance().prepare(config);
            mongod = mongodExecutable.start();
            context = new AnnotationConfigApplicationContext(DalMongodbConfig.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
        if (mongod != null) {
            mongod.stop();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }
}
