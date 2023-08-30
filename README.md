# apm-maven

- group
- artifact
- version
- classifier
- extension

id:
- group:artifact


- org.jogamp.jogl:jogl-all-main

- org.jogamp:jogl-all:natives-linux-amd64


##

org.jogamp.jogl:jogl-all-main
-> dependencies


org.jogamp.jogl:jogl-all
- classifiers: [javadoc, sources]


## Cases

- group/artifact
  - fetch POM
  - figure out version(s)
  - figure out classifier(s)
      - javadoc
      - sources
      - ?
  - figure out extension(s)
    - pom
    - jar
    - checksums, signatures?




- group/artifact
- group
- artifact
- version
- extension (pom, jar)



