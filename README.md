

# Building

    ./gradlew clean shadowjar
    
    
# Usage

     java -jar build/libs/jms-client-all.jar  -url tcp://localhost:61611  -channel broker -message foo -topic bar