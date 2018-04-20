package org.mx.test.repository;

import org.mx.test.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface UserRepository extends Repository<UserEntity, String> {
    public List<UserEntity> getByCode(String code);

    @Query("select u from User  u where u.email like %?1%")
    public List<UserEntity> getLikeEmail(String email);
}
