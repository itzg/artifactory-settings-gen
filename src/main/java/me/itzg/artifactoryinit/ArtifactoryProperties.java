package me.itzg.artifactoryinit;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("artifactory")
@Component
@Data
public class ArtifactoryProperties {
  @NotBlank
  String baseUrl;

  @NotBlank
  String project;

  @NotBlank
  String username;

  @NotBlank
  String password;
}
