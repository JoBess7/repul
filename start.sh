if [ $# -eq 0 ]; then
    echo "No arguments provided. Running Maven and Java without additional arguments."
    mvn clean verify
    java -jar target/application.jar
else
    # Run Maven and then run the Java application with the remaining arguments
    mvn clean verify
    java -jar target/application.jar "$@"
fi
