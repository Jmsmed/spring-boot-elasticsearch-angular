package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.elastic.repository.BookElasticRepository;
import com.example.model.Book;
import com.example.model.BookElastic;
import com.example.repository.BookRepository;


@EnableElasticsearchRepositories("com.example.elastic.repository")
@EnableJpaRepositories("com.example.repository")
@EntityScan(basePackages = "com.example.model")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@SpringBootApplication
public class SpringBootElasticsearchExampleProject1Application {

	
	@Autowired
	private BookRepository repo3;
	@Autowired
	private BookElasticRepository repo4;
	
	Runtime rt = Runtime.getRuntime();
	Boolean synchronised = true;
	
//	Create a new Book
	@PostMapping("/saveBook")
	public String createBook(@RequestBody Book book) throws IOException {
		repo3.save(book);
		if(synchronised==true) {
			synchronised = false;
			Update();
			System.out.println(synchronised);
			}
		return book.title+" A été bien ajouter";
	}
	
//	Get the list of books
//	@GetMapping("/findAll")
//	public List<BookElastic> getBookList(){
//		Iterable <BookElastic> iterable =repo4.findAll();
//		List <BookElastic> result = new ArrayList<BookElastic>();
//		for (BookElastic bk : iterable) {
//			result.add(bk);
//		}
//		return result;
//	}
	
	@GetMapping("/findAll")
	public <T> List<T> getBookList(){
		System.out.println(synchronised);
		if (synchronised == true) {
		Iterable <T> iterable =(Iterable<T>) repo4.findAll(); //elasticsearch
		List <T> result =  new ArrayList<T>();
		for (T bk : iterable) {
			result.add(bk);
		}
		return result;
	}
		else {
			System.out.println("Should be here");
			return (List<T>) repo3.findAll(); //secondary
		}
	}
//	Get a Single Book
	@RequestMapping(value = "/findById/{bookid}", method = RequestMethod.GET)
	public BookElastic findById(@PathVariable int bookid){
		return repo4.findBybookid(bookid);
	}
//	
	@Transactional
	@DeleteMapping("deleteBook/{bookid}")
	public String deleteBook(@PathVariable int bookid) throws IOException {
		repo3.deleteBybookid(bookid);
		if(synchronised==true) {
			synchronised = false;
			Update();
			System.out.println(synchronised);
			}
		return "Le livre à été bien supprimer " ;
		
	}
	
//	@GetMapping("/search/{name}")
//	public List<BookElastic> findbyName(@PathVariable String name) {
//		String search = ".*"+name+".*";
//		SearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(QueryBuilders.regexpQuery("name", search)).build();
//		List<BookElastic> books = template.queryForList(searchQuery, BookElastic.class);
//		return books;
//	}
//  Update a book 
	@RequestMapping
	@PostMapping("/update/{bookid}")
	public Book updateBook(@PathVariable int bookid,
             @RequestBody Book bookDetails) {

		Book book = repo3.findById(bookid).orElseThrow(() -> new ResourceNotFoundException("Book", "bookid", bookid));

			book.setTitle(bookDetails.getTitle());
			book.setDescription(bookDetails.getDescription());
			book.setAuthor(bookDetails.getAuthor());

			Book updatedBook = repo3.save(book);
				return updatedBook;
	}

	
	public void Update() throws IOException {
		Process proc1 = rt.exec("cmd /c curl -XDELETE http://localhost:9200/book");
		Process proc2 = rt.exec("cmd /c  C:\\logstash-7.8.0\\bin\\logstash.bat -f C:\\logstash-7.8.0\\logstash.conf");
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootElasticsearchExampleProject1Application.class, args);
	}
	

	@Scheduled(fixedDelayString="PT5M")
	void switchState() throws InterruptedException,IOException{
		if (synchronised ==false) {
		Update();
		}
		synchronised = true;
	}
@Configuration
@EnableScheduling
@ConditionalOnProperty(name="scheduling.enable",matchIfMissing=true)
class SchedulingConnfiguration {
	
}

}
