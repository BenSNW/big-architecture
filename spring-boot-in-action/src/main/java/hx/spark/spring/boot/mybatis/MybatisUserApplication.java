package hx.spark.spring.boot.mybatis;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

// http://sivalabs.in/2016/03/springboot-working-with-mybatis/
// https://objectpartners.com/2011/04/05/using-mybatis-annotations-with-spring-3-0-and-maven/

@SpringBootApplication
@MapperScan("com.hx.wang.mybatis")
public class MybatisUserApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(MybatisUserApplication.class);

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private SqlSession sqlSession;

	public static void main(String... args) {
		logger.debug("Starting JpaMybatisApplication...");
		ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.bannerMode(Banner.Mode.OFF)
				.sources(MybatisUserApplication.class)
				.web(false)
				.build()
				.run("--info");
		
		if (context != null)
			logger.info("JpaMybatisApplication Started");
		else
			logger.warn("JpaMybatisApplication context not started");
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(String... args) throws Exception {
		User user = new User("Siva", "sivaer@gmail.com");
        userMapper.insertUser(user);
        logger.info(userMapper.findUserById(user.getId()).toString());
        logger.info(userMapper.findAllUsers().toString());
        
        logger.info(sqlSession.getClass().toString());
        logger.info(sqlSession.selectList("findUserByName", "Siva").toString());
        
        
        Map<String, Object> condition = new HashMap<>(4);
        condition.put("name", "Siva");
        condition.put("email", "sivaer@gmail.com");
        String conString = condition.entrySet().stream()
        		.map(entry -> entry.getKey() + "=" + entry.getValue())
        		.collect(Collectors.joining(" and "));
        logger.info(conString);
//        logger.info(userMapper.findUsers(conString).toString());
	}

}

interface MybatisCrudTemplate<T> {
	
	T findById(int id);
	
	T findOne(T conditions);
	
	List<T> findAll(T conditions);
	
	int insert(T entity);
	
	int update(T conditions, T newValues);
}

@Mapper
interface UserMapper {

	@Insert("insert into users (name, email) values(#{name}, #{email})")
	@SelectKey(statement = "call identity()", keyProperty = "id", before = false, resultType = Integer.class)
	int insertUser(User user);

	@Select("select id, name, email from users where id=#{id}")
	User findUserById(@Param("id") Integer id);
	
	@Select("select id, name, email from users where name=#{name}")
	List<User> findUserByName(@Param("name") String name);

	@Select("select id, name, email from users")
	List<User> findAllUsers();
	
	// doesn't work
//	@Select("select * from users where #{condition}")
//	List<User> findUsers(@Param("condition") String condition);
	
}

// http://stackoverflow.com/questions/4381290/hibernate-exception-org-hibernate-annotationexception-no-identifier-specified
// @Entity if this annotation is used, there must be a @Id annotation to indicate the primary key
// @Embeddable : don't want your entity to be persisted in a separate table, but rather be a part of other entities
// Here just leave it a simple pojo; simply a data transfer object to hold some data from the hibernate entity
class User implements Serializable {

	private static final long serialVersionUID = 1282663618302750900L;

	private Integer id;
	private String name;
	private String email;

	public User() { }

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return String.format("Id: %s name: %s email: %s", id, name, email);
	}

}
