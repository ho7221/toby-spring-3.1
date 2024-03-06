package toby.tutorial.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import toby.tutorial.user.domain.Level;
import toby.tutorial.user.domain.User;
import toby.tutorial.user.exception.DuplicateUserIdException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/applicationContext_test.xml")
public class UserDaoJdbcTest {
    @Autowired
    private UserDao dao;


    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp(){
        this.user1 = new User("test1","test1","test1", Level.BASIC, 1, 0, "test1@gmail.com");
        this.user2 = new User("test2","test2","test2", Level.SILVER, 55, 10, "test2@gmail.com");
        this.user3 = new User("test3","test3","test3", Level.GOLD, 100, 40, "test3@gmail.com");
    }

    @Test
    public void addAndGet(){
        dao.deleteAll();
        assertEquals(dao.getCount(),0);

        dao.add(user1);
        assertEquals(dao.getCount(),1);

        User testUser = dao.get(user1.getId());
        assertEquals(testUser.getName(),user1.getName());
        assertEquals(testUser.getPassword(),user1.getPassword());
    }

    @Test
    public void count(){
        dao.deleteAll();
        assertEquals(dao.getCount(),0);

        dao.add(user1);
        assertEquals(dao.getCount(),1);

        dao.add(user2);
        assertEquals(dao.getCount(),2);

        dao.add(user3);
        assertEquals(dao.getCount(),3);
    }

    @Test
    public void getUserFailure(){
        dao.deleteAll();
        assertEquals(dao.getCount(),0);

        assertThrows(EmptyResultDataAccessException.class, ()-> dao.get("unknown_id"));
    }

    @Test
    public void getAllTest(){

        List<User> users;
        dao.deleteAll();
        users = dao.getAll();
        assertEquals(users.size(),0);


        dao.deleteAll();
        dao.add(user1);
        users = dao.getAll();
        assertEquals(users.size(), 1);
        checkSameUser(user1, users.get(0));

        dao.add(user2);
        users = dao.getAll();
        assertEquals(users.size(), 2);
        checkSameUser(user1, users.get(0));
        checkSameUser(user2, users.get(1));

        dao.add(user3);
        users = dao.getAll();
        assertEquals(users.size(), 3);
        checkSameUser(user1, users.get(0));
        checkSameUser(user2, users.get(1));
        checkSameUser(user3, users.get(2));
    }

    @Test
    public void addSameId() throws DuplicateUserIdException {
        dao.deleteAll();
        dao.add(user1);
        assertThrows(DuplicateUserIdException.class,()-> dao.add(user1));
    }


    @Test
    public void update(){
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);
        user1.setName("tester");
        dao.update(user1);

        checkSameUser(user1, dao.get(user1.getId()));
        checkSameUser(user2, dao.get(user2.getId()));
    }

    private void checkSameUser(User user1, User user2){
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(),user2.getPassword());
        assertEquals(user1.getLevel(), user2.getLevel());
        assertEquals(user1.getLogin(), user2.getLogin());
        assertEquals(user1.getRecommend(), user2.getRecommend());
        assertEquals(user1.getEmail(), user2.getEmail());
    }
}