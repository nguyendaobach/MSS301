package mss.mindmap.mindmapservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MindmapserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MindmapserviceApplication.class, args);
	}
}
