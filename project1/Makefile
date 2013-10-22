JAR_PKG = project1.jar

ENTRY_POINT = pack.Project1

RES_DIR = yes

SOURCE_FILES = \
	pack/Calculate.java \
	pack/ClientWithSSL.java \
	pack/ClientWithoutSSL.java \
	pack/Project1.java

JAVAC = javac
JFLAGS = -g

vpath %.class bin
vpath %.java src

Default: build

build: $(SOURCE_FILES:.java=.class)

%.class: %.java
	$(JAVAC) -cp bin -d bin $(JFLAGS) $<
rebuild: clean build

.PHONY: new clean run jar

new:
ifeq ($(RES_DIR),yes)
	mkdir -pv src bin res
else 
	mkdir -pv src bin
endif

clean:
	rm -frv bin/*
run:
	java -cp bin $(ENTRY_POINT)
jar:
ifeq ($(RES_DIR),yes)
	jar cvfe $(JAR_PKG) $(ENTRY_POINT) -C bin . res
else
	jar cvfe $(JAR_PKG) $(ENTRY_POINT) -C bin .
endif	

