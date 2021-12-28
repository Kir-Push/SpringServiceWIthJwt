package com.mesh.testtask;

import com.mesh.testtask.dao.UserRepository;
import com.mesh.testtask.domain.entity.BaseResponse;
import com.mesh.testtask.domain.entity.Phone;
import com.mesh.testtask.domain.entity.User;
import com.mesh.testtask.domain.entity.UserDTO;
import com.mesh.testtask.domain.entity.UserRequest;
import com.mesh.testtask.domain.filter.FilterCriteria;
import com.mesh.testtask.domain.filter.UserFilter;
import com.mesh.testtask.domain.filter.UserSpecification;
import com.mesh.testtask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
class TestTaskApplicationTests {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserService userService;

    private User userJohn;
    private User userTom;


    @Test
    public void testRepoFilter() {
        userJohn = new User();
        userJohn.setName("John");
        userJohn.setEmail("john@doe.com");
        Phone phone = new Phone();
        phone.setUser(userJohn);
        phone.setValue("124");
        userJohn.setPhones(Set.of(phone));
        short b = 22;
        userJohn.setAge(b);
        repository.save(userJohn);

        userTom = new User();
        userTom.setName("Tom");
        userTom.setEmail("tom@doe.com");
        b = 26;
        userTom.setAge(b);
        phone = new Phone();
        phone.setUser(userTom);
        phone.setValue("1234567890");
        userTom.setPhones(Set.of(phone));
        repository.save(userTom);

        UserSpecification spec1 =
                new UserSpecification(new FilterCriteria("name", "like", "John"));
        UserSpecification spec2 =
                new UserSpecification(new FilterCriteria("email", "like", "john@doe.com"));
        UserSpecification spec3 =
                new UserSpecification(new FilterCriteria("phone", "like", "124"));
        UserSpecification spec4 =
                new UserSpecification(new FilterCriteria("age", ">", "22"));
        UserSpecification spec5 =
                new UserSpecification(new FilterCriteria("age", "<", "22"));

        List<User> results = repository.findAll(Specification.where(spec3).and(spec1).and(spec2).and(spec4).and(spec5));
        assertEquals(1, results.size());
        assertEquals("john@doe.com", results.get(0).getEmail());
    }

    @Test
    public void testServiceFindAll() {
        userJohn = new User();
        userJohn.setName("John");
        userJohn.setEmail("john@doe.com");
        Phone phone = new Phone();
        phone.setUser(userJohn);
        phone.setValue("124");
        userJohn.setPhones(Set.of(phone));
        short b = 22;
        userJohn.setAge(b);
        repository.save(userJohn);

        userTom = new User();
        userTom.setName("Tom");
        userTom.setEmail("tom@doe.com");
        b = 26;
        userTom.setAge(b);
        phone = new Phone();
        phone.setUser(userTom);
        phone.setValue("1234567890");
        userTom.setPhones(Set.of(phone));
        repository.save(userTom);

        UserFilter userFilter = new UserFilter();
        userFilter.setName("Tom");
        userFilter.setAge((short) 26);

        List<UserDTO> results = userService.getUserList(userFilter);
        assertEquals(1, results.size());
        assertEquals("tom@doe.com", results.get(0).getEmail());
    }

    @Test
    public void testServiceCRUDUser() throws InterruptedException {
        UserRequest johnRequest = new UserRequest();
        johnRequest.setName("John");
        johnRequest.setEmail("john@doe.com");
        johnRequest.setPhone(List.of("124"));
        johnRequest.setPassword("password");
        short b = 22;
        johnRequest.setAge(b);
        johnRequest.setAmount(BigDecimal.valueOf(125000));
        userService.createUser(johnRequest);

        UserRequest tomRequest = new UserRequest();
        tomRequest.setName("Tom");
        tomRequest.setEmail("tom@doe.com");
        tomRequest.setPassword("pass");
        b = 26;
        tomRequest.setAge(b);
        tomRequest.setPhone(List.of("1234567890"));
        tomRequest.setAmount(BigDecimal.valueOf(25000));
        userService.createUser(tomRequest);

        List<User> results = userService.getAllUsers();
        assertEquals(2, results.size());
        assertEquals("john@doe.com", results.get(0).getEmail());

        tomRequest.setEmail("john2@doe.com");
        tomRequest.setId(results.get(1).getId());
        BaseResponse baseResponse = userService.editUser(tomRequest);
        System.out.println(baseResponse.message);
        assertEquals(true, baseResponse.success);
        assertEquals(2, results.size());
        assertEquals("john2@doe.com", results.get(1).getEmail());
        assertEquals("Tom", results.get(1).getName());

        tomRequest.setEmail("john@doe.com");
        baseResponse = userService.editUser(tomRequest);
        assertEquals(false, baseResponse.success);
        results = userService.getAllUsers();
        assertEquals(2, results.size());
        assertEquals("john@doe.com", results.get(0).getEmail());
        assertEquals("john2@doe.com", results.get(1).getEmail());
        assertEquals("Tom", results.get(1).getName());

        BaseResponse response = userService.deleteUser(tomRequest);
        assertEquals(true, response.success);
        results = userService.getAllUsers();
        assertEquals(1, results.size());
        assertEquals("john@doe.com", results.get(0).getEmail());
        assertEquals("John", results.get(0).getName());
    }

}
