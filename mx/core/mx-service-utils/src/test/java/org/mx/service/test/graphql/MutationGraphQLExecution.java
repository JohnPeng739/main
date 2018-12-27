package org.mx.service.test.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.mx.service.rest.graphql.GraphQLAnnotationExecution;
import org.mx.service.rest.graphql.GraphQLFieldAnnotation;
import org.mx.service.rest.graphql.GraphQLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MutationGraphQLExecution implements GraphQLAnnotationExecution {
    private PersonManager personManager;

    @Autowired
    public MutationGraphQLExecution(PersonManager personManager) {
        super();
        this.personManager = personManager;
    }

    @GraphQLFieldAnnotation("savePerson")
    public Person savePerson(DataFetchingEnvironment environment) {
        Map<String, Object> input = environment.getArgument("input");
        PersonInfoInput personInfoInput = GraphQLUtils.parse(input, PersonInfoInput.class);
        return personManager.savePerson(personInfoInput);
    }

    @Override
    public String getTypeName() {
        return "Mutation";
    }
}
