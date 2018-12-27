package org.mx.service.test.graphql;

public class GraphQLRequest {
    private  String name, param, result;

    public GraphQLRequest(String name, String param, String result) {
        super();
        this.name = name;
        this.param = param;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public String getParam() {
        return param;
    }

    public String getResult() {
        return result;
    }
}
