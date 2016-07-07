TARGET=RubyMode.jar

SRC_ROOT_PATH=src
SRCDIR=$(SRC_ROOT_PATH)/processing/mode/ruby
OUTPUT_PATH=classes

DIST_PATH=~/Documents/Processing/modes/RubyMode/mode/

rwildcard=$(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2) $(filter $(subst *,%,$2),$d))
SRCS=$(call rwildcard,$(SRCDIR),*.java)

PROCESSING_PATH=/Applications/Processing.app
PROCESSING_CORE_JAR=$(PROCESSING_PATH)/Contents/Java/core.jar
PROCESSING_APP_JAR=$(PROCESSING_PATH)/Contents/Java/pde.jar

all:	$(DIST_PATH)/$(TARGET)

$(DIST_PATH)/$(TARGET):	$(TARGET)
	cp $(TARGET) $(DIST_PATH)

$(TARGET):	$(SRCS) $(OUTPUT_PATH)
	javac -d $(OUTPUT_PATH) -sourcepath $(SRC_ROOT_PATH) -cp src:$(PROCESSING_CORE_JAR):$(PROCESSING_APP_JAR) src/processing/mode/ruby/RubyMode.java
	jar -cvf $@ -C $(OUTPUT_PATH) .

 $(OUTPUT_PATH):
	mkdir -p  $(OUTPUT_PATH)

clean:
	rm -rf $(OUTPUT_PATH) $(TARGET)

RubyMode.zip:	$(TARGET)
	mkdir RubyMode && \
	cp -r others/* RubyMode/ && \
	cp $(TARGET) RubyMode/mode/ && \
	zip -r $@ RubyMode && \
	rm -rf RubyMode
