package com.example.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.model.Book;

public interface BookRepository extends CrudRepository<Book,Integer> {

	Book save(Optional<Book> book);

	void deleteBybookid(int bookid);





}
