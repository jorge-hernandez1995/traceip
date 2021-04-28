FROM java:8
VOLUME /tmp
ADD ./target/traceip-1.0.jar traceip-1.0.jar
RUN bash -c 'touch /traceip-1.0.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","traceip-1.0.jar"]