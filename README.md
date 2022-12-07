# CallMeInTwoWeeks
This is the source code for Chiral Software's internal call tracking system. It's old, it's clunky, it's quirky, but it does work. 
Many things need to be fixed in this. It should switch to Bootstrap. It has many bugs and quirks. But even with the quirks, it's very effective and shows 
how to implement click-to-dial with Asterisk. Chiral has used this system to track many thousands of calls and win many contracts.

# Update 2022

This has been moved to Spring Boot 3, which means Spring Security 
and Spring Framework 6, plus a move from `javax.servlet` to
`jakarta.servlet`. This has been tested with Tomcat 10.

Future plans are to use Spring native to build a standalone
binary application.


# Update 2021

It now compiles to Spring's excellent
executable WAR format. You can deploy the WAR like any other WAR, or you can execute it directly as an executable file.
I have also included a systemd unit file plus a sample properties file.

It works well behind a reverse proxy such as Nginx. 

Plenty of quirks still remain, and yet it remains just as useful as ever. 
