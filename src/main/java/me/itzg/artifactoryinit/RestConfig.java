package me.itzg.artifactoryinit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig {

  private final ArtifactoryProperties properties;

  @Autowired
  public RestConfig(ArtifactoryProperties properties) {
    this.properties = properties;
  }

}
