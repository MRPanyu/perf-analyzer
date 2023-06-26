package perfanalyzer.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PerfAnalyzerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerfAnalyzerWebApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("perf")
	public PerfAnalyzerProperties perfAnalyzerProperties() {
		return new PerfAnalyzerProperties();
	}

}
