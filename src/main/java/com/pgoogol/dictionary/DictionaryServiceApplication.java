package com.pgoogol.dictionary;

import com.pgoogol.elasticsearch.data.config.ElkConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.pgoogol.elasticsearch.data",
		"com.pgoogol.dictionary"
})
@Import({
	ElkConfig.class
})
public class DictionaryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DictionaryServiceApplication.class, args);
	}

}
