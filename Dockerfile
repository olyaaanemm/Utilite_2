FROM hseeberger/scala-sbt:graalvm-ce-21.1.0-java8_1.5.5_2.13.6
WORKDIR ~
COPY ["./Utilite_2/src/main/scala/per/university/Main.scala", "."]
COPY ["./Utilite_2/homework_first_english.txt", "."]
COPY ["./Utilite_2/src/main/scala/per/university/FileNew.scala", "."]
COPY ["./Utilite_2/src/main/scala/per/university/config.xml", "."]
RUN mkdir ./source
RUN PATH=./source
ENTRYPOINT ["scala", "-cp", ".", "Main.scala"]