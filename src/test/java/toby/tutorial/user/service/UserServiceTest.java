package toby.tutorial.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import toby.tutorial.user.dao.MockUserDao;
import toby.tutorial.user.dao.UserDao;
import toby.tutorial.user.domain.Level;
import toby.tutorial.user.domain.User;
import toby.tutorial.user.exception.TestUserServiceException;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static toby.tutorial.user.service.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static toby.tutorial.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/applicationContext_test.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    DataSource dataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    UserService testUserService;
    @Autowired
    MailSender mailSender;
    @Autowired
    ApplicationContext context;

    List<User> userList;

    @BeforeEach
    public void setUp(){
        userList = Arrays.asList(
                new User("u1","u1","u1",Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0, "ho7221@gmail.com"),
                new User("u2","u2","u2",Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0, "ho7221@gmail.com"),
                new User("u3","u3","u3",Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "ho7221@gmail.com"),
                new User("u4", "u4","u4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "ho7221@gmail.com"),
                new User("u5","u5","u5",Level.GOLD, 100, Integer.MAX_VALUE, "ho7221@gmail.com")
        );
    }

    @Test
    public void register(){
        userService.deleteAll();
        User test = userList.get(0);
        test.setLevel(null);
        userService.add(test);

        checkLevel(userService.get(test.getId()),Level.BASIC);
    }

    @Test
    @DirtiesContext
    public void upgradeLevelTest(){
        MockUserDao mockUserDao = new MockUserDao(this.userList);
        MockMailSender mockMailSender = new MockMailSender();
        UserServiceImpl mockUserService = new UserServiceImpl();

        mockUserService.setUserDao(mockUserDao);
        mockUserService.setMailSender(mockMailSender);

        mockUserService.upgradeLevels();

        // check upgrade function
        List<User> upgraded = mockUserDao.getUpdated();
        assertEquals(upgraded.size(), 2);
        checkUserAndLevel(upgraded.get(0),"u2",Level.SILVER);
        checkUserAndLevel(upgraded.get(1),"u4",Level.GOLD);

        // check mail function
        List<String> request = mockMailSender.getRequests();
        assertEquals(request.size(),2);
        assertEquals(request.get(0),userList.get(1).getEmail());
        assertEquals(request.get(1),userList.get(3).getEmail());
    }

    @Test
    public void upgradeAllOrNothing() throws Exception{
        userService.deleteAll();
        for(User user: userList){
            userService.add(user);
        }
        assertThrows(TestUserServiceException.class, () -> testUserService.upgradeLevels());

        checkLevelUpgraded(userList.get(1),false);
    }

    @Test
    public void mockUpgradeLevels(){
        UserServiceImpl mockUserService = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.userList);
        MailSender mockMailSender = mock(MailSender.class);

        mockUserService.setUserDao(mockUserDao);
        mockUserService.setMailSender(mockMailSender);

        mockUserService.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(userList.get(1));
        assertEquals(userList.get(1).getLevel(),Level.SILVER);
        verify(mockUserDao).update(userList.get(3));
        assertEquals(userList.get(3).getLevel(),Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertEquals(mailMessages.size(),2);
        assertEquals(mailMessages.get(0).getTo()[0],userList.get(1).getEmail());
        assertEquals(mailMessages.get(1).getTo()[0],userList.get(3).getEmail());
    }

    @Test
    public void advisorAutoProxyCreator(){
        assertInstanceOf(java.lang.reflect.Proxy.class, testUserService);
    }

    @Test
    public void readOnlyTransactionTest(){
        assertThrows(TransientDataAccessResourceException.class, () -> testUserService.getAll());
    }

    @Test
    @Transactional
    public void transactionSync(){
        userDao.deleteAll();
        userService.add(userList.get(0));
        userService.add(userList.get(1));
    }

    private void checkLevel(User user, Level expected){
        assertEquals(userDao.get(user.getId()).getLevel(), expected);
    }

    private void checkLevelUpgraded(User user, Boolean upgraded){
        if(upgraded){
            checkLevel(user, user.getLevel().getNext());
        }
        else{
            checkLevel(user, user.getLevel());
        }
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        assertEquals(updated.getId(),expectedId);
        assertEquals(updated.getLevel(),expectedLevel);
    }
}
