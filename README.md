# Thoth
Scalable, peer to peer area of interest management for simulations.

## Build Status
![Build Status](https://github.com/hellblazer/prime-mover/actions/workflows/maven.yml/badge.svg)
___

Licensed under AGPL Version 3

Uses Maven 3.83+, and Java 20+ to build:

	mvn clean install

## Maven Artifacts
Currently, Thoth is in active development and does not publish to maven central.  Rather, periodic snapshots (and releases when they happen)
will be uploaded to the [repo-hell]() repository.  If you would like to use Thoth maven artifacts, you'll need to add the following repository
declarations to your pom.xml  The maven coordinates for individual artifacts are found below.
    
    <repositories>
        <repository>
            <id>hell-repo</id>
            <url>https://raw.githubusercontent.com/Hellblazer/repo-hell/main/mvn-artifact</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

    <pluginRepositories>
        <pluginRepository>
            <id>plugin-hell-repo</id>
            <url>https://raw.githubusercontent.com/Hellblazer/repo-hell/main/mvn-artifact</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>
 
### Thoth library

     <dependency>
         <groupId>com.hellblazer</groupId>
         <artifactId>thoth</artifactId>
         <version>0.1.0-SNAPSHOT</version>
     </dependency>
