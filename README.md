# Overall build instructions 
(assuming Maven 3+ is configured on the system)

## Solr index configuration (prerequisite)

[See the core README](./neon-black-core/README.md)

## neon-black-core

Configure, build, and install the Neon Black code library

1. Update the following properties in neon-black-core/src/main/resources/NB.properties
  - _polygon.directory_ : local directory that contains polygons, e.g. [Natural Earth](./resources/NaturalEarth/NaturalEarth.txt)
  - _resource.directory_ : local directory that contains the [required configuration and data files](./resources/)
  - _solr.url_ : Solr instance configured in previous section
2. `cd neon-black-core`
3. `mvn install` 

## neon-black-api

Build and run the Neon Black API module

1. `cd ../neon-black-api`
2. `mvn package`
3. `java -jar target/neonblack-api.jar`

Should now be able to GET/POST text to http://localhost:9000/extract?input=