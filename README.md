# CallMeInTwoWeeks
This is the source code for Chiral Software's internal call tracking system. It has been in use for years and has been kept updated.
This project is very useful on its own, and also gives good examples of Spring Boot, Spring Security, JPA,
and implementing click-to-dial with Asterisk.

It will work with any SQL database, including H2 for development, Postgres, MySQL and any other which has JDBC
and JPA support.

# Future plans

Use Spring native to build a standalone
binary application. Currently this is not working due to
some issues with Thymeleaf and Spring native. These will be fixed
as Spring native becomes more mature. 

Add full text search using Lucene.

Fix CSS and other quirks.

# Update 2022

This has been moved to Spring Boot 3, which means Spring Security 
and Spring Framework 6, plus a move from `javax.servlet` to
`jakarta.servlet`. This has been tested with Tomcat 10.

It now uses the Spring Security remember-me feature.

# Update 2021

Moved into Spring Boot.

It now compiles to Spring's excellent
executable WAR format. You can deploy the WAR like any other WAR, or you can execute it directly as an executable file.
I have also included a systemd unit file plus a sample properties file.

It works well behind a reverse proxy such as Nginx. 
