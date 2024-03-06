package toby.tutorial.user.service;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import toby.tutorial.user.dao.UserDao;
import toby.tutorial.user.exception.SqlRetrievalFailureException;
import toby.tutorial.user.sqlservice.jaxb.SqlType;
import toby.tutorial.user.sqlservice.jaxb.Sqlmap;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService{
    private Map<String, String> sqlMap = new HashMap<String, String>();
    private String sqlMapFile;

    public void setSqlMapFile(String sqlMapFile) {
        this.sqlMapFile = sqlMapFile;
    }

    @PostConstruct
    public void loadSql(){
        String contextPath = Sqlmap.class.getPackage().getName();
        try{
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream is = UserDao.class.getResourceAsStream(this.sqlMapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

            for(SqlType sql: sqlmap.getSql()){
                sqlMap.put(sql.getKey(),sql.getValue());
            }
        }catch(JAXBException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if(sql == null) throw new SqlRetrievalFailureException("No sql for key "+key);
        else return sql;
    }
}
