if "%1"=="" (
    echo "No arguments provided. Running Maven and Java without additional arguments."
    mvn clean verify
    java -jar target/application.jar
) else (
@REM     Run Maven and then run the Java application with the remaining arguments
    mvn clean verify
    java -jar target/application.jar "%1%"
)