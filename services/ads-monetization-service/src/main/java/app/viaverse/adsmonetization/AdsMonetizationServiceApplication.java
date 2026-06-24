package app.viaverse.adsmonetization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "app.viaverse")
public class AdsMonetizationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdsMonetizationServiceApplication.class, args);
    }
}
