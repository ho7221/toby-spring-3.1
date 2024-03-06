package toby.tutorial.user.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import toby.tutorial.user.domain.Level;
import toby.tutorial.user.domain.User;
import toby.tutorial.user.exception.DuplicateUserIdException;
import toby.tutorial.user.service.SqlService;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.VARCHAR;

public class UserDaoJdbc implements UserDao{
    private JdbcTemplate jdbcTemplate;
    private SqlService sqlService;
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    private final RowMapper<User> userRowMapper =
            (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setLevel(Level.valueOf(rs.getInt("level")));
                user.setLogin(rs.getInt("login"));
                user.setRecommend(rs.getInt("recommend"));
                user.setEmail(rs.getString("email"));
                return user;
            };

    public void add(final User user) throws DuplicateUserIdException{
        try {
            jdbcTemplate.update(this.sqlService.getSql("add"),
                    user.getId(),
                    user.getName(),
                    user.getPassword(),
                    user.getLevel().intValue(),
                    user.getLogin(),
                    user.getRecommend(),
                    user.getEmail()
            );
        }
        catch(DataAccessException e){
            if(e instanceof DuplicateKeyException){
                throw new DuplicateUserIdException("User with id "+user.getId()+" already exists", e);
            }else{
                throw new RuntimeException(e);
            }
        }
    }

    public User get(String id)  {
        return jdbcTemplate.queryForObject(this.sqlService.getSql("get"),
                new Object[]{id},
                new int[]{VARCHAR},
                userRowMapper
        );
    }

    public void update(User user){
        jdbcTemplate.update(this.sqlService.getSql("update"),
                user.getName(),
                user.getPassword(),
                user.getLevel().intValue(),
                user.getLogin(),
                user.getRecommend(),
                user.getEmail(),
                user.getId()
        );
    }

    public void deleteAll()  {
        jdbcTemplate.update(this.sqlService.getSql("deleteAll"));
    }

    public int getCount()  {
        return Objects.requireNonNullElse(
                jdbcTemplate.query(
                    con -> con.prepareStatement(this.sqlService.getSql("getCount"))
                    ,
                    rs -> {
                        rs.next();
                        return rs.getInt(1);
                    })
                ,0
        );
    }

    public List<User> getAll()  {
        return jdbcTemplate.query(this.sqlService.getSql("getAll"),
                userRowMapper
                );
    }
}

