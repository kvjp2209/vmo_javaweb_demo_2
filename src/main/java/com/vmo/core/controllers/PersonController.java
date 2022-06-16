package com.vmo.core.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vmo.core.models.database.entities.EStatus;
import com.vmo.core.models.database.entities.Person;
import com.vmo.core.models.requests.PersonRequestDto;
import com.vmo.core.services.log.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @Autowired
    CacheManager cacheManager;

    @PostMapping("")
    public ResponseEntity<Object> createPerson(@RequestBody PersonRequestDto personRequestDto) {
        try {
            if (personService.getAllByEmailAndStatus(personRequestDto.getEmail(), EStatus.ENABLE.getId()).size() != 0) {
                return new ResponseEntity<>("person existed!!", HttpStatus.CONFLICT);
            }
            Person person = personService.createPerson(personRequestDto);
            return new ResponseEntity<>("Success!!!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("false!!!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPerson() {
        try {
            List<Person> persons = personService.getAllPerson();
            return new ResponseEntity<>(persons, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable int id) {
        try {
            Person person = personService.getByIdPerson(id);
            if (person == null) {
                return new ResponseEntity<>("not found!!!", HttpStatus.NOT_FOUND);
            }
            personService.deletePerson(person);
            return new ResponseEntity<>("success!!!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updatePerson(@PathVariable int id, @RequestBody PersonRequestDto personRequestDto) {
        try {
            Person person = personService.getByIdPerson(id);
            if (person == null) {
                return new ResponseEntity<>("not found by this id!!!", HttpStatus.NOT_FOUND);
            }
            Person result = personService.updatePerson(personRequestDto, person);
            if (result == null) {
                return new ResponseEntity<>("id card existed!!!", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("update success!!!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/check")
    public ResponseEntity<Object> countPersonByEmailAndStatus(@RequestParam(value = "email") String email, @RequestParam(value = "status") int status) {
        try {
            int count = personService.countPersonsByEmailAndStatus(email, status);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all/status")
    public ResponseEntity<Object> getAllByStatus(@RequestParam(value = "status") int status,
                                                 @RequestParam(value = "page") int page,
                                                 @RequestParam(value = "size") int size) {
        try {
            if (page <= 0) {
                Pageable pageable = Pageable.unpaged();
                List<Person> persons = personService.getAllByStatus(status, pageable);
                return new ResponseEntity<>(persons, HttpStatus.OK);
            }
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").descending());
            List<Person> persons = personService.getAllByStatus(status, pageable);
            return new ResponseEntity<>(persons, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/test")
    public ResponseEntity<Object> testExisted(@RequestBody String persons) {
        Type personListType = new TypeToken<ArrayList<Person>>() {
        }.getType();

        List<Person> personList = new Gson().fromJson(persons, personListType);
        return new ResponseEntity<>(personList, HttpStatus.OK);
    }

    @PostMapping("/jdbc")
    public ResponseEntity<Object> createPersonJdbc(@RequestBody PersonRequestDto personRequestDto) {
        try {
            if (personService.getAllByEmailAndStatus(personRequestDto.getEmail(), EStatus.ENABLE.getId()).size() != 0) {
                return new ResponseEntity<>("person existed!!", HttpStatus.CONFLICT);
            }
            Person person = personService.createPersonJdbc(personRequestDto);
            return new ResponseEntity<>("Success!!!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("false!!!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Cacheable(value = "person", key = "#id")
    public Person getPersonById(@PathVariable("id") int id) {
        try {
            Person person = personService.getByIdPerson(id);
            if (person == null) {
                return null;
            }
            System.out.println(cacheManager.getCacheNames());
            System.out.println(cacheManager.getCache("person"));
//            System.out.println(cacheManager.getCache("loda_list"));
            return person;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/cache/{id}")
    public ResponseEntity<Object> deleteCache(@PathVariable("id") int id){
        try {
            personService.clearCacheById(id);
            return new ResponseEntity<>("success!!!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cache/all")
    public ResponseEntity<Object> deleteCacheAll() {
        try {
            personService.clearCache();
            return new ResponseEntity<>("success!!!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}