package hx.spring.boot.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class WebSecurityApplication {

private static final Logger logger = LoggerFactory.getLogger(WebSecurityApplication.class);
	
    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(WebSecurityApplication.class, args);
//    	ApplicationContext ctx = SpringApplication.run(BookmarkApplication.class, "--debug");
        logger.debug("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.asList(beanNames).stream().sorted().forEach(System.out::println);
        Arrays.asList(beanNames).stream().sorted().forEach(
    		beanName -> {
    			System.out.println(beanName + ": " + ctx.getBean(beanName).getClass().getName());
    		}
		);
                
    }
    
    @Bean
    CommandLineRunner init(BookmarkRepository bookmarkRepository) {   	
    	return args -> {           
    		bookmarkRepository.deleteAll();
            Arrays.asList("mstine", "jslong")
            	  .forEach( userId -> bookmarkRepository.save( 
            			  new Bookmark( userId,
            					  "http://some-other-host" + userId + ".com/",
            					  "A description for " + userId + "'s link", userId)));
        };
        
    }
}

@RestController
@RequestMapping("/{userId}/bookmarks")
class BookmarkRestController {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
	String welcome(@PathVariable String userId) {
        return "Welcome!";
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Bookmark> getBookmarks(@PathVariable String userId) {
        return this.bookmarkRepository.findByUserId(userId);
    }

    @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
    Bookmark getBookmark(@PathVariable String userId, @PathVariable Long bookmarkId) {
        return this.bookmarkRepository.findByUserIdAndId(userId, bookmarkId);
    }

    @RequestMapping(method = RequestMethod.POST)
    Bookmark createBookmark(@PathVariable String userId, @RequestBody Bookmark bookmark) {

        Bookmark bookmarkInstance = new Bookmark(
                userId,
                bookmark.getHref(),
                bookmark.getDescription(),
                bookmark.getLabel());
        
        return this.bookmarkRepository.save(bookmarkInstance);
    }

}


interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Bookmark findByUserIdAndId(String userId, Long id);

    List<Bookmark> findByUserId(String userId);
}

@Entity
class Bookmark {

    private String userId;

    @Id
    @GeneratedValue
    private Long id;

    private String href;

    private String description;

    private String label;
    
    public Bookmark() {}

    public Bookmark(String userId, String href, String description, String label) {
        this.userId = userId;
        this.href = href;
        this.description = description;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getUserId() {
        return userId;
    }

    public Long getId() {
        return id;
    }

    public String getHref() {
        return href;
    }

    public String getDescription() {
        return description;
    }

}

@Configuration
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {
  
	@Override
	// Override to configure how requests are secured by interceptors
	protected void configure(HttpSecurity http) throws Exception {
		
		// authenticate via HTTP Basic
//		http.authorizeRequests().anyRequest().authenticated()
//			.and().httpBasic().realmName("Bookmark Security Application");
		
		http.formLogin() // default login page if HTTP Basic Authentication is not satisfied
			.and().logout().clearAuthentication(true).logoutSuccessUrl("/nobody/bookmarks/welcome")
			.and().httpBasic().realmName("Bookmark Security Application")  
			.and().authorizeRequests().antMatchers("/nobody/**").permitAll()
			.and().authorizeRequests().anyRequest().authenticated();

		
//		http
//			.formLogin()
//				.loginPage("/login")
//			.and()
//				.logout()
//					.logoutSuccessUrl("/")
//			.and()
//				.rememberMe()
//					.tokenRepository(new InMemoryTokenRepositoryImpl())
//					.tokenValiditySeconds(2419200).key("secureKey")
//			.and()
//				.httpBasic()
//					.realmName("Bookmark Security Application")
//			.and()
//				.authorizeRequests()
//					.antMatchers("/").authenticated()
//					.antMatchers("/spitter/me").authenticated()
//					.antMatchers(HttpMethod.POST, "/spittles").authenticated()
//					.anyRequest().permitAll();
	}
	
	@Override
	// Override to configure Spring Security’s filter chain
	public void configure(WebSecurity webSecurity) throws Exception {
		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("user").password("user").roles("USER").and()
			.withUser("admin").password("admin").roles("USER", "ADMIN");
	}

}
