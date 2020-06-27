package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName="book",type="doc",shards=2)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookElastic {
	@Id
	public int bookid;
	public String title;
	public String author;
	public String description;
}
