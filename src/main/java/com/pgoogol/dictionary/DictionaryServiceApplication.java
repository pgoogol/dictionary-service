package com.pgoogol.dictionary;

import com.pgoogol.elasticsearch.data.config.ElkConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.pgoogol.elasticsearch.data.repository",
		"com.pgoogol.dictionary"
})
@Import({
	ElkConfig.class
})
public class DictionaryServiceApplication {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		SpringApplication.run(DictionaryServiceApplication.class, args);
	}

}
