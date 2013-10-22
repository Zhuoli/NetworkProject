JAR_PKG = project1.jar

ENTRY_POINT = project2.WebCrawler 

RES_DIR = yes

SOURCE_FILES = \
	project2/StateStruct.java \
	project2/ContentParse.java \
	project2/ClientConnection.java \
	project2/WebCrawler.java	

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

