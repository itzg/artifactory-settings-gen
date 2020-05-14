package me.itzg.artifactoryinit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class SettingsXmlContext {
  String username;
  String passwordEncrypted;
  String baseUrl;
  String project;
}
