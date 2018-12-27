package org.mx.service.test.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.mx.StringUtils;
import org.mx.service.rest.graphql.GraphQLAnnotationExecution;
import org.mx.service.rest.graphql.GraphQLFieldAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryGraphQLExecution implements GraphQLAnnotationExecution {
    private PersonManager personManager;

    @Autowired
    public QueryGraphQLExecution(PersonManager personManager) {
        super();
        this.personManager = personManager;
    }

    @GraphQLFieldAnnotation("person")
    public Person person(DataFetchingEnvironment environment) {
        String id = environment.getArgument("id");
        return personManager.getPerson(id);
    }

    @GraphQLFieldAnnotation("persons")
    public List<Person> persons(DataFetchingEnvironment environment) {
        Person.Gender gender = null;
        String genderStr = environment.getArgument("gender");
        if (!StringUtils.isBlank(genderStr)) {
            gender = Person.Gender.valueOf(genderStr);
        }
        return personManager.findPersonByGender(gender);
    }

    @Override
    public String getTypeName() {
        return "Query";
    }
}
