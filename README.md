# General Information

Application responsible for providing unique licenses for various commercial projects

# Deployment

* clone repository ```git clone https://github.com/Siterizer/licence-code-generator```
* install java 21 (pref Adoptium Eclipse Temurin 21) & Maven & docker & docker-compose
* run command ```mvn clean package```
* run command ```docker build --tag licence-code-generator:latest .```
* run command ```docker-compose up -d```
* app can be accessed from http://localhost:8081/
* **Note for myself**: Using docker windows logs can accessed here: ```\\wsl.localhost\docker-desktop-data\version-pack-data\community\docker\volumes\generator_logs```

# Local Development
* clone repository ```git clone https://github.com/Siterizer/licence-code-generator```
* install java 21 (pref Adoptium Eclipse Temurin 21) & Maven & docker & docker-compose
* run command ```docker-compose -f compose-local.yml -p "local_postgres_container" up -d```
* run application with following profile: ```spring.profiles.active=local```
* remember to edit every configuration property that has 'placeholder' value with your own values
* app can be accessed from http://localhost:8080/