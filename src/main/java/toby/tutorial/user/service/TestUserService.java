package toby.tutorial.user.service;

import toby.tutorial.user.domain.User;
import toby.tutorial.user.exception.TestUserServiceException;

import java.util.List;

class TestUserService extends UserServiceImpl {
    private String id = "u4";

    protected void upgradeLevel(User user){
        if(user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

    public List<User> getAll() {
        for(User user: super.getAll()){
            super.update(user);
        }
        return null;
    }
}
