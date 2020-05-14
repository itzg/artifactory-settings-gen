A small, CLI-style Spring Boot application that sets up a Maven settings.xml file for Artifactory access.

`ApplicationProperties` declares properties, prefixed with `artifactory.`, that specify how to access Artifactory and a project name referenced in the generated `settings.xml`:
- `artifactory.base-url` : the base URL of Artifactory without the `/artifactory` path
- `artifactory.project` : not actually an Artifactory parameter, but rather an identifier used in the generated `settings.xml`
- `artifactory.username`
- `artifactory.password`

It is recommended to place the first two in a file `application.yml` in the current directory:
```yaml
artifactory:
  baseUrl: http://...
  project: yours
```

The username and password should be passed on the command-line to keep them as secure as possible. After bulding the application jar with:

```
./mwvnw package
```

You can run it as:

```
java -jar target/artifactory-settings-gen-*.jar --artifactory.username=... --artifactory.password=...
```

The entrypoint and main part of the application is in `ArtifactoryInitApplication` where it
- contacts artifactory to grab the encrypted password for the given user
- populates that and the project name into the mustache template `settings-xml.mustache`, in `src/main/resources/templates`
- writes the file `settings.xml` in the directory `$HOME/.m2` or outputs the content if that file already exists