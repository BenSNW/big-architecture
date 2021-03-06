package hx.sboot.jpa;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BenSNW
 */
@SpringBootApplication
public class JpaBookmarkApplication {

	private static final Logger logger = LoggerFactory.getLogger(JpaBookmarkApplication.class);
	
    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(JpaBookmarkApplication.class, args);
//    	ApplicationContext ctx = SpringApplication.run(BookmarkApplication.class, "--debug");
        logger.debug("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//        	System.out.println(beanName);
//        	System.out.println(beanName + ": " + ctx.getBean(beanName).getClass().getName());
//        }
        
        Arrays.asList(beanNames).stream().sorted().forEach(System.out::println);
//        Arrays.asList(beanNames).stream().sorted().forEach(
//    		beanName -> {
//    			System.out.println(beanName + ": " + ctx.getBean(beanName).getClass().getName());
//    		}
//		);
        
//        WebApplicationContextUtils
        
    }
    
    @Bean
    CommandLineRunner testCommandLineRunner() {
    	return args -> {
    		Arrays.asList(args).forEach(System.out::println);
    	};
    }
    
	public Function<Integer, String> intToStringFunction() {
		return new Function<Integer, String>() {
			@Override
			public String apply(Integer integer) {
				return "" + integer;
			}
		};
	}
	
	public Function<Integer, String> intToStringLambda() {
		return it -> {  return "" + it; };
	}
	
	public Function<Integer, String> intToStringCoolLambda() {
		return it -> "" + it;
	}

    @Bean
    CommandLineRunner init(BookmarkRepository bookmarkRepository) {
    	/**
    	 * just think of lambda as in favor of anonymous class to avoid the unclear syntax
    	 * so the following lambda is equivalent to:  	  
		        new CommandLineRunner() {		        	   
					@Override
					public void run(String... args) throws Exception {
						bookmarkRepository.deleteAll();
			            Arrays.asList("mstine", "jlong").forEach( n ->
			                    bookmarkRepository.save( new Bookmark( n,
			                            "http://some-other-host" + n + ".com/",
			                            "A description for " + n + "'s link", n)));
					}
				}
		 * Keep this in mind, not difficult to get to know that the parameter args
		 * will be passed from command line, namely the program parameters
    	 */
    	return args -> {
            
    		bookmarkRepository.deleteAll();
            Arrays.asList("mstine", "jslong")
            	  .forEach( userId -> bookmarkRepository.save( 
            			  new Bookmark( userId,
            					  "http://some-other-host" + userId + ".com/",
            					  "A description for " + userId + "'s link", userId)));
            
            bookmarkRepository.findAll().forEach(System.out::println);
            
            Bookmark bookmark = bookmarkRepository.findOne(1);
            bookmarkRepository.save(bookmark);
            
            bookmark.setHref(null);
            
            bookmarkRepository.findAll().forEach(System.out::println);
        };
        
    }
}

@RestController
@RequestMapping("/{userId}/bookmarks")
class BookmarkRestController {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @RequestMapping(method = RequestMethod.GET)
    Collection<Bookmark> getBookmarks(@PathVariable String userId) {
        return this.bookmarkRepository.findByUserId(userId);
    }

    @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
    Bookmark getBookmark(@PathVariable String userId,
                         @PathVariable Integer bookmarkId) {
        return bookmarkRepository.findByUserIdAndId(userId, bookmarkId).get();
    }

    @RequestMapping(method = RequestMethod.POST)
    Bookmark createBookmark(@PathVariable String userId,
                            @RequestBody Bookmark bookmark) {

        Bookmark bookmarkInstance = new Bookmark(
                userId,
                bookmark.getHref(),
                bookmark.getDescription(),
                bookmark.getLabel());

        return this.bookmarkRepository.save(bookmarkInstance);
    }

}


interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {

	Optional<Bookmark> findByUserIdAndId(String userId, Integer id);

    List<Bookmark> findByUserId(String userId);
    
    @Query
    int updateByAttr();
    
}

@Entity
class Bookmark {

	@Id
	@GeneratedValue
	private Integer id;
	private String userId;
	private String href;
	private String description;
	private String label;

	public Bookmark() { }

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

    public Integer getId() {
        return id;
    }

    public String getHref() {
        return href;
    }

    public String getDescription() {
        return description;
    }
    
    public void setId(Integer id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "Bookmark [id=" + id + ", userId=" + userId + ", href=" + href
				+ ", description=" + description + ", label=" + label + "]";
	}

}