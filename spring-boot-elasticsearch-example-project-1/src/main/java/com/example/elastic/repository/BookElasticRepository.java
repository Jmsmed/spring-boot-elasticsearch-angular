package com.example.elastic.repository;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.example.model.BookElastic;



@Repository
public interface BookElasticRepository extends ElasticsearchRepository<BookElastic,Integer>{

	BookElastic findBybookid(int bookid);

	
}
