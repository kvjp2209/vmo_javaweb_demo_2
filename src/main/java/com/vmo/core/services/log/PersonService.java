package com.vmo.core.services.log;

import com.vmo.core.models.database.entities.EStatus;
import com.vmo.core.models.database.entities.Person;
import com.vmo.core.models.requests.PersonRequestDto;
import com.vmo.core.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;

    @Autowired
    CacheManager cacheManager;

    private RedisTemplate template;

    public static final String HASH_KEY = "person";

    public List<Person> getAllPerson() {
        return personRepository.findAll();
    }

    @Cacheable("person")
    public Person getByIdPerson(Integer id) {
        if (personRepository.findById(id).isPresent()) {
            simulateSlowService();
            Person person = personRepository.findById(id).get();
            return person;
        }
        return null;
    }

    // Don't do this at home
    private void simulateSlowService() {
        try {
            long time = 3000L;
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }


    @Transactional
    public Person createPerson(PersonRequestDto personRequestDto) {
        Person person = new Person(personRequestDto.getName(),
                personRequestDto.getEmail(),
                personRequestDto.getAge(),
                personRequestDto.getDob(),
                personRequestDto.getPhone(),
                personRequestDto.getGender(),
                personRequestDto.getIdCard(),
                personRequestDto.getStatus());
        personRepository.save(person);
//        template.opsForHash().put(HASH_KEY, person.getId(), person);
        return person;
    }

    @Transactional
    public Person createPersonJdbc(PersonRequestDto personRequestDto) {
        Person person = new Person(personRequestDto.getName(),
                personRequestDto.getEmail(),
                personRequestDto.getAge(),
                personRequestDto.getDob(),
                personRequestDto.getPhone(),
                personRequestDto.getGender(),
                personRequestDto.getIdCard(),
                personRequestDto.getStatus());

        personRepository.insertPersons(
                personRequestDto.getName(),
                personRequestDto.getEmail(),
                personRequestDto.getAge(),
                personRequestDto.getDob(),
                personRequestDto.getPhone(),
                personRequestDto.getGender(),
                personRequestDto.getIdCard(),
                personRequestDto.getStatus()
        );
        return person;
    }

    public void deletePerson(Person person) {
        person.setStatus(EStatus.DISABLE.getId());
        personRepository.save(person);
    }

    @CachePut(value = "person")
    @Transactional
    public Person updatePerson(PersonRequestDto personRequestDto, Person person) {
        if (!person.getIdCard().equalsIgnoreCase(personRequestDto.getIdCard()) == true) {
            if (checkDuplicateIdCard(personRequestDto.getIdCard()) == false) {
                return null;
            }
            person.update(personRequestDto);
            personRepository.save(person);
            return person;
        }
        person.update(personRequestDto);
        personRepository.save(person);
        return person;
    }

    public Boolean checkDuplicateIdCard(String idCard) {
        if (personRepository.findByIdCard(idCard) != null) {
            return false;
        }
        return true;
    }

//    public Boolean checkDuplicateEmail(String email) {
//
//    }

    public List<Person> getAllByEmailAndStatus(String email, int status) {
        return personRepository.findAllByEmailAndStatus(email, status);
    }

    public List<Person> getAllByStatus(int status, Pageable pageable) {
        return personRepository.findAllByStatus(status, pageable);
    }

    public int countPersonsByEmailAndStatus(String email, int status) {
        return personRepository.countPersonsWithEmailAndStatus(email, status);
    }

    @CacheEvict("person")
    public void clearCacheById(int id) {
    }

    @CacheEvict(value = "user", allEntries = true)
    public void clearCache() {
    }
}
