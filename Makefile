TARGET=RubyMode.jar

SRC_ROOT_PATH=src
SRCDIR=$(SRC_ROOT_PATH)/processing/mode/ruby
OUTPUT_PATH=classes

DIST_PATH=~/Documents/Processing/modes/RubyMode/mode/

SRCS=$(wildcard $(SRCDIR)/*.java)
CLASSES=$(subst $(SRC_ROOT_PATH)/,$(OUTPUT_PATH)/,$(SRCS:%.java=%.class))
PATH_CLASSES=$(subst $(OUTPUT_PATH)/,,$(CLASSES))

PROCESSING_PATH=/Applications/Processing.app
PROCESSING_CORE_JAR=$(PROCESSING_PATH)/Contents/Java/core.jar
PROCESSING_APP_JAR=$(PROCESSING_PATH)/Contents/Java/pde.jar

dist:	$(TARGET)
	cp $(TARGET) $(DIST_PATH)

$(TARGET):	$(CLASSES)
	jar -cvf $@ -C $(OUTPUT_PATH) .

clean:
	rm -rf $(CLASSES) $(TARGET)

$(OUTPUT_PATH)/%.class:	$(SRC_ROOT_PATH)/%.java
	javac -d $(OUTPUT_PATH) -cp src:$(PROCESSING_CORE_JAR):$(PROCESSING_APP_JAR) $^
