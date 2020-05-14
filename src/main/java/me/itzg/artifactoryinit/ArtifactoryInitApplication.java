package me.itzg.artifactoryinit;

import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Slf4j
public class ArtifactoryInitApplication implements ApplicationRunner {

  private final RestTemplate restTemplate;
  private final ArtifactoryProperties properties;
  private final Compiler mustacheCompiler;
  private final TemplateLoader templateLoader;

  public static void main(String[] args) {
    SpringApplication.run(ArtifactoryInitApplication.class, args);
  }

  @Autowired
  public ArtifactoryInitApplication(ArtifactoryProperties properties,
                                    RestTemplateBuilder restTemplateBuilder,
                                    Compiler mustacheCompiler,
                                    TemplateLoader templateLoader) {
    this.properties = properties;
    this.mustacheCompiler = mustacheCompiler;
    this.templateLoader = templateLoader;
    restTemplate = restTemplateBuilder
        .rootUri(properties.getBaseUrl())
        .basicAuthentication(properties.getUsername(), properties.getPassword())
        .build();
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    final ResponseEntity<String> resp = restTemplate.getForEntity(
        "/artifactory/api/security/encryptedPassword",
        String.class
    );

    if (!resp.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException(
          String.format("Failed to get encryptedPassword: %s %s",
              resp.getStatusCode(), resp.getBody()));
    }

    final String passwordEncrypted = resp.getBody();
    log.debug("Got {}", passwordEncrypted);

    try (Reader templateReader = templateLoader.getTemplate("settings-xml")) {
      final Template template = mustacheCompiler.compile(templateReader);
      final String result = template.execute(new SettingsXmlContext(
          properties.getUsername(),
          passwordEncrypted,
          properties.getBaseUrl(),
          properties.getProject()
      ));

      writeSettingsFile(result);
    }

  }

  private void writeSettingsFile(String result) {

    final Path m2Path = Paths.get(System.getProperty("user.home"), ".m2");
    final Path settingsXmlPath = m2Path.resolve("settings.xml");
    try {
      Files.createDirectories(m2Path);

      if (!Files.exists(settingsXmlPath)) {
        Files.writeString(settingsXmlPath, result, StandardOpenOption.CREATE_NEW);
        System.out.printf(
            "%nSuccessfully created %s%n",
            settingsXmlPath
        );
      } else {
        System.out.printf(
            "%n%s already exists, so merge the following in with that.%n%n%s%n",
            settingsXmlPath, result
        );
      }
    } catch (IOException e) {
      System.out.printf(
          "%nFailed to write settings file. Here is the settings.xml content to place in %s%n%n%s%n",
          settingsXmlPath, result);
    }
  }
}
