package wad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AliveApplication {

    @Autowired
    ProductionProfile profile;
    
    public static void main(String[] args) {
        SpringApplication.run(AliveApplication.class, args);
    }

    public static String githubUrl() {
        return "https://github.com/maggaou/wepa2017-AliveApplication";
    }

    public static String travisUrl() {
        return "https://travis-ci.org/maggaou/wepa2017-AliveApplication";
    }

    public static String herokuUrl() {
        return "https://stormy-hamlet-11885.herokuapp.com/";
    }
}
