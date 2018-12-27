package org.mx.service.test.graphql;

import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PersonManager {
    private Map<String, Person> persons = new ConcurrentHashMap<>();

    public Person savePerson(PersonInfoInput personInfoInput) {
        if (personInfoInput == null || StringUtils.isBlank(personInfoInput.getName())) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (StringUtils.isBlank(personInfoInput.getId())) {
            personInfoInput.setId(DigestUtils.uuid());
        }
        Person check = persons.get(personInfoInput.getId());
        if (check == null) {
            check = new Person();
        }
        List<Person> friends = new ArrayList<>();
        List<String> friendIds = personInfoInput.getFriendIds();
        if (friendIds != null && !friendIds.isEmpty()) {
            friendIds.forEach(id -> {
                if (persons.containsKey(id)) {
                    friends.add(persons.get(id));
                } else {
                    throw new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
                    );
                }
            });
        }
        check.setId(personInfoInput.getId());
        check.setAddress(personInfoInput.getAddress());
        check.setAge(personInfoInput.getAge());
        check.setGender(personInfoInput.getGender());
        check.setMarried(personInfoInput.isMarried());
        check.setName(personInfoInput.getName());
        check.setWeight(personInfoInput.getWeight());
        check.setFriends(friends);
        persons.put(check.getId(), check);
        return check;
    }

    public Person getPerson(String id) {
        return persons.get(id);
    }

    public List<Person> getPersons() {
        return new ArrayList<>(persons.values());
    }

    public List<Person> findPersonByGender(final Person.Gender gender) {
        return persons.values().stream()
                .filter(person -> person.getGender() == gender || gender == null)
                .collect(Collectors.toList());
    }
}
