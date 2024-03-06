package toby.tutorial.user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import toby.tutorial.user.dao.UserDao;
import toby.tutorial.user.domain.Level;
import toby.tutorial.user.domain.User;

import java.util.List;

public class UserServiceImpl implements UserService{
    UserDao userDao;
    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void add(User user){
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    public User get(String id){
        return userDao.get(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void deleteAll() {
        userDao.deleteAll();
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void upgradeLevels(){
        List<User> userList = userDao.getAll();
        for (User user : userList) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
                userDao.update(user);
                sendUpgradeMail(user);
            }
        }
    }

    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public Boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        return switch (currentLevel) {
            case BASIC -> (user.getLogin() >= MIN_LOGIN_COUNT_FOR_SILVER);
            case SILVER -> (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD -> false;
        };
    }

    protected void upgradeLevel(User user) {
        Level curLevel = user.getLevel();
        Level nextLevel = curLevel.getNext();
        if(nextLevel == null){
            throw new IllegalStateException(curLevel + "is not upgradable.");
        }
        else{
            user.setLevel(nextLevel);
        }
    }

    private void sendUpgradeMail(User user){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("ho7221@korea.ac.kr");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Upgrade Notification");
        mailMessage.setText("You have been upgraded to " + user.getLevel() + " .");

        mailSender.send(mailMessage);
    }
}
