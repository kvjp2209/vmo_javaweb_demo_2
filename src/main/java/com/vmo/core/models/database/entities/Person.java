package com.vmo.core.models.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmo.core.models.requests.PersonRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    private String name;

    private String email;

    private int age;

    private Timestamp dob;

    private String phone;

    private int gender;


    private String idCard;

    private int status;

    public Person(String name, String email, int age, Timestamp dob, String phone, int gender, String idCard, int status) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.dob = dob;
        this.phone = phone;
        this.gender = gender;
        this.idCard = idCard;
        this.status = status;
    }

    public void update(PersonRequestDto personRequestDto) {
        this.name = personRequestDto.getName();
        this.email = personRequestDto.getEmail();
        this.age = personRequestDto.getAge();
        this.dob = personRequestDto.getDob();
        this.phone = personRequestDto.getPhone();
        this.gender = personRequestDto.getGender();
        this.idCard = personRequestDto.getIdCard();
        this.status = personRequestDto.getStatus();
    }
}
