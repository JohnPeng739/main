package org.mx.service.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.service.client.rest.RestClientInvoke;
import org.mx.service.rest.graphql.GraphQLRequest;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.RestfulServerFactory;
import org.mx.service.test.graphql.Person;
import org.mx.service.test.graphql.PersonManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Collections;

import static org.junit.Assert.*;

public class TestGraphQL {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(org.mx.service.test.config.TestGraphQLConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        AbstractServerFactory factory = context.getBean(RestfulServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);
        PersonManager personManager = context.getBean(PersonManager.class);
        assertNotNull(personManager);

        try {
            assertTrue(personManager.getPersons().isEmpty());
            RestClientInvoke invoke = new RestClientInvoke();
            String mutationUrl = "http://localhost:9999/rest/v1/graphql/mutation";
            String queryUrl = "http://localhost:9999/rest/v1/graphql/query";
            GraphQLRequest request = new GraphQLRequest("savePerson",
                    "input: {name: \"josh\", address: \"address of josh.\", age: 20, weight: 123.45, married: true}",
                    "id name address age weight married gender friends{id name}");
            DataVO<JSONObject> dataVO = invoke.post(mutationUrl, request, DataVO.class);
            assertNotNull(dataVO);
            assertEquals(1, personManager.getPersons().size());
            JSONObject json = dataVO.getData().getJSONObject("savePerson");
            assertNotNull(json);
            assertFalse(StringUtils.isBlank(json.getString("id")));
            assertEquals("josh", json.getString("name"));
            assertEquals("address of josh.", json.getString("address"));
            assertEquals(20, json.getIntValue("age"));
            assertEquals(123.45, json.getDoubleValue("weight"), 0.00001);
            assertTrue(json.getBooleanValue("married"));
            assertEquals(Person.Gender.MALE.name(), json.getString("gender"));
            JSONArray friends = json.getJSONArray("friends");
            assertTrue(friends == null || friends.isEmpty());

            String id = json.getString("id");
            request = new GraphQLRequest("person", "id: \"" + id + "\"", "id name");
            dataVO = invoke.post(queryUrl, request, DataVO.class);
            assertNotNull(dataVO);
            json = dataVO.getData().getJSONObject("person");
            assertNotNull(json);
            assertEquals(id, json.getString("id"));
            assertEquals("josh", json.getString("name"));
            assertNull(json.getString("address"));
            assertNull(json.getInteger("age"));
            assertNull(json.getDouble("weight"));
            assertNull(json.getBoolean("married"));
            assertNull(json.getString("gender"));

            request = new GraphQLRequest("savePerson",
                    "input: {name: \"john\", address: \"address of john.\", age: 20, weight: 123.45, married: true, friendIds: [\"" + id + "\"]}",
                    "id name address age weight married gender friends{id name}");
            dataVO = invoke.post(mutationUrl, request, DataVO.class);
            assertNotNull(dataVO);
            assertEquals(2, personManager.getPersons().size());
            json = dataVO.getData().getJSONObject("savePerson");
            assertNotNull(json);
            assertFalse(StringUtils.isBlank(json.getString("id")));
            friends = json.getJSONArray("friends");
            assertTrue(friends != null && friends.size() == 1);
            json = friends.getJSONObject(0);
            assertEquals(id, json.getString("id"));
            assertEquals("josh", json.getString("name"));
            assertNull(json.getString("address"));
            assertNull(json.getInteger("age"));
            assertNull(json.getDouble("weight"));
            assertNull(json.getBoolean("married"));
            assertNull(json.getString("gender"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void test2() {
        AbstractServerFactory factory = context.getBean(RestfulServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);
        PersonManager personManager = context.getBean(PersonManager.class);
        assertNotNull(personManager);

        try {
            assertTrue(personManager.getPersons().isEmpty());
            RestClientInvoke invoke = new RestClientInvoke();
            String mutationUrl = "http://localhost:9999/rest/v2/graphql/mutation";
            String queryUrl = "http://localhost:9999/rest/v2/graphql/query";
            GraphQLRequest request = new GraphQLRequest("savePerson",
                    "input: {name: \"josh\", address: \"address of josh.\", age: 20, weight: 123.45, married: true}",
                    "id name address age weight married gender friends{id name}");
            DataVO<JSONObject> dataVO = invoke.post(mutationUrl, Collections.singletonList(request), DataVO.class);
            assertNotNull(dataVO);
            assertEquals(1, personManager.getPersons().size());
            JSONObject json = dataVO.getData().getJSONObject("savePerson");
            assertNotNull(json);
            assertFalse(StringUtils.isBlank(json.getString("id")));
            assertEquals("josh", json.getString("name"));
            assertEquals("address of josh.", json.getString("address"));
            assertEquals(20, json.getIntValue("age"));
            assertEquals(123.45, json.getDoubleValue("weight"), 0.00001);
            assertTrue(json.getBooleanValue("married"));
            assertEquals(Person.Gender.MALE.name(), json.getString("gender"));
            JSONArray friends = json.getJSONArray("friends");
            assertTrue(friends == null || friends.isEmpty());

            String id = json.getString("id");
            request = new GraphQLRequest("person", "id: \"" + id + "\"", "id name");
            dataVO = invoke.post(queryUrl, Collections.singletonList(request), DataVO.class);
            assertNotNull(dataVO);
            json = dataVO.getData().getJSONObject("person");
            assertNotNull(json);
            assertEquals(id, json.getString("id"));
            assertEquals("josh", json.getString("name"));
            assertNull(json.getString("address"));
            assertNull(json.getInteger("age"));
            assertNull(json.getDouble("weight"));
            assertNull(json.getBoolean("married"));
            assertNull(json.getString("gender"));

            request = new GraphQLRequest("savePerson",
                    "input: {name: \"john\", address: \"address of john.\", age: 20, weight: 123.45, married: true, friendIds: [\"" + id + "\"]}",
                    "id name address age weight married gender friends{id name}");
            dataVO = invoke.post(mutationUrl, Collections.singletonList(request), DataVO.class);
            assertNotNull(dataVO);
            assertEquals(2, personManager.getPersons().size());
            json = dataVO.getData().getJSONObject("savePerson");
            assertNotNull(json);
            assertFalse(StringUtils.isBlank(json.getString("id")));
            friends = json.getJSONArray("friends");
            assertTrue(friends != null && friends.size() == 1);
            json = friends.getJSONObject(0);
            assertEquals(id, json.getString("id"));
            assertEquals("josh", json.getString("name"));
            assertNull(json.getString("address"));
            assertNull(json.getInteger("age"));
            assertNull(json.getDouble("weight"));
            assertNull(json.getBoolean("married"));
            assertNull(json.getString("gender"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void test3() {
        AbstractServerFactory factory = context.getBean(RestfulServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);
        PersonManager personManager = context.getBean(PersonManager.class);
        assertNotNull(personManager);

        try {
            assertTrue(personManager.getPersons().isEmpty());
            RestClientInvoke invoke = new RestClientInvoke();
            String mutationUrl = "http://localhost:9999/rest/v3/graphql/mutation";
            String queryUrl = "http://localhost:9999/rest/v3/graphql/query";
            GraphQLRequest request = new GraphQLRequest("savePerson",
                    "input: {name: \"josh\", address: \"address of josh.\", age: 20, weight: 123.45, married: true}",
                    "id name address age weight married gender friends{id name}");
            DataVO<JSONObject> dataVO = invoke.post(mutationUrl, request, DataVO.class);
            assertNotNull(dataVO);
            assertEquals(1, personManager.getPersons().size());
            JSONObject json = dataVO.getData().getJSONObject("savePerson");
            assertNotNull(json);
            assertFalse(StringUtils.isBlank(json.getString("id")));
            assertEquals("josh", json.getString("name"));
            assertEquals("address of josh.", json.getString("address"));
            assertEquals(20, json.getIntValue("age"));
            assertEquals(123.45, json.getDoubleValue("weight"), 0.00001);
            assertTrue(json.getBooleanValue("married"));
            assertEquals(Person.Gender.MALE.name(), json.getString("gender"));
            JSONArray friends = json.getJSONArray("friends");
            assertTrue(friends == null || friends.isEmpty());

            String id = json.getString("id");
            request = new GraphQLRequest("person", "id: \"" + id + "\"", "id name");
            dataVO = invoke.post(queryUrl, Collections.singletonList(request), DataVO.class);
            assertNotNull(dataVO);
            json = dataVO.getData().getJSONObject("person");
            assertNotNull(json);
            assertEquals(id, json.getString("id"));
            assertEquals("josh", json.getString("name"));
            assertNull(json.getString("address"));
            assertNull(json.getInteger("age"));
            assertNull(json.getDouble("weight"));
            assertNull(json.getBoolean("married"));
            assertNull(json.getString("gender"));

            request = new GraphQLRequest("savePerson",
                    "input: {name: \"john\", address: \"address of john.\", age: 20, weight: 123.45, married: true, friendIds: [\"" + id + "\"]}",
                    "id name address age weight married gender friends{id name}");
            dataVO = invoke.post(mutationUrl, request, DataVO.class);
            assertNotNull(dataVO);
            assertEquals(2, personManager.getPersons().size());
            json = dataVO.getData().getJSONObject("savePerson");
            assertNotNull(json);
            assertFalse(StringUtils.isBlank(json.getString("id")));
            friends = json.getJSONArray("friends");
            assertTrue(friends != null && friends.size() == 1);
            json = friends.getJSONObject(0);
            assertEquals(id, json.getString("id"));
            assertEquals("josh", json.getString("name"));
            assertNull(json.getString("address"));
            assertNull(json.getInteger("age"));
            assertNull(json.getDouble("weight"));
            assertNull(json.getBoolean("married"));
            assertNull(json.getString("gender"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
