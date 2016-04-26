package com.hx.wang.mybatis;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

// https://objectpartners.com/2011/04/05/using-mybatis-annotations-with-spring-3-0-and-maven/

@SpringBootApplication
// @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.hx.wang.mybatis")
public class MybatisUserApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory
			.getLogger(MybatisUserApplication.class);

	@Autowired
	private UserMapper userMapper;

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
	}

}

@Mapper
interface UserMapper {

	@Insert("insert into users(name, email) values(#{name},#{email})")
	@SelectKey(statement = "call identity()", keyProperty = "id", before = false, resultType = Integer.class)
	void insertUser(User user);

	@Select("select id, name, email from users WHERE id=#{id}")
	User findUserById(@Param("id") Integer id);

	@Select("select id, name, email from users")
	List<User> findAllUsers();
}

// @Entity
@Embeddable
class User implements Serializable {

	private static final long serialVersionUID = 1282663618302750900L;

	private Integer id;
	private String name;
	private String email;
	private static int ID = 0;

	public User() { }

	public User(String name, String email) {
		this.id = ++ID;
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
