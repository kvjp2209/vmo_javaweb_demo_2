package com.vmo.core.repositories;

import com.vmo.core.models.database.entities.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {


    Person findByIdCard(String idCard);
    List<Person> findAllByEmailAndStatus(String email, int status);

    List<Person> findAllByStatus(int status, Pageable pageable);

    @Query(nativeQuery = true, value = "Select count(*) From PERSON p " +
            "where p.email = :email and p.status = :status")
    int countPersonsWithEmailAndStatus(String email, int status);


    @Modifying
    @Query(value = "insert into person(name, email, age, dob, phone, gender, id_card, status) values " +
            "(:name, :email, :age, :dob, :phone, :gender, :idCard, :status)", nativeQuery = true)
    void insertPersons(String name, String email, int age, Timestamp dob, String phone, int gender, String idCard, int status);

}
