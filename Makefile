NAME=RubyMode
JAR_NAME=$(NAME).jar
TARGET=dist/RubyMode/$(JAR_NAME)
ZIP_NAME=$(NAME).zip

DIST_DIR=dist
DIST_ZIP=$(DIST_DIR)/$(ZIP_NAME)

SRC_ROOT_PATH=src
SRC_DIR=$(SRC_ROOT_PATH)/processing/mode/ruby
OUTPUT_PATH=classes

rwildcard=$(foreach d,$(wildcard $1*),$(call rwildcard,$d/,$2) $(filter $(subst *,%,$2),$d))
SRCS=$(call rwildcard,$(SRC_DIR),*.java)

P5_PATH=/Applications/Processing.app
P5_CORE_JAR=$(P5_PATH)/Contents/Java/core.jar
P5_APP_JAR=$(P5_PATH)/Contents/Java/pde.jar

P5_RBMODE_PATH=~/Documents/Processing/modes/RubyMode
P5_RBMODE_JAR_PATH=$(P5_RBMODE_PATH)/mode/$(JAR_NAME)

all:	$(P5_RBMODE_JAR_PATH)

zip:	$(DIST_ZIP)

$(P5_RBMODE_JAR_PATH):	$(TARGET)
	cp $(TARGET) $@

$(TARGET):	$(SRCS) $(OUTPUT_PATH)
	javac -d $(OUTPUT_PATH) -sourcepath $(SRC_ROOT_PATH) \
	  -cp src:$(P5_CORE_JAR):$(P5_APP_JAR) src/processing/mode/ruby/RubyMode.java
	jar -cvf $@ -C $(OUTPUT_PATH) .

 $(OUTPUT_PATH):
	mkdir -p  $(OUTPUT_PATH)

clean:
	rm -rf $(OUTPUT_PATH) $(TARGET)

$(DIST_ZIP):	$(TARGET)
	cd dist && \
	rm -f $(ZIP_NAME) && \
	zip -r $(ZIP_NAME) RubyMode -x '*.DS_Store'
