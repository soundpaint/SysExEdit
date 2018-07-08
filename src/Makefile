# Makefile for SysExEdit
#
# Copyright (C) 1998, 2018 Jürgen Reuter
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

include defs.mak

#
# Packages
#

SRC_SYSEXEDIT=$(SRC_DIR)/org/soundpaint/sysexedit
PACKAGE_ROOT=org.soundpaint.sysexedit
SUB_PACKAGES = model gui devices midi parser
PACKAGES = $(PACKAGE_ROOT) $(patsubst %,$(PACKAGE_ROOT).%,$(SUB_PACKAGES))

#
# Source Files
#

JAVA=\
	$(wildcard $(SRC_SYSEXEDIT)/*.java) \
	$(wildcard $(patsubst %,$(SRC_SYSEXEDIT)/%/*.java,$(SUB_PACKAGES)))

CLASS=$(patsubst $(SRC_DIR)/%.java,$(BUILD_DIR)/%.class,$(JAVA))

all: classes doc jar

TUTORIAL_SRC=$(wildcard $(SRC_DIR)/doc/tutorial/*.html)
TUTORIAL_OBJ=$(patsubst $(SRC_DIR)/%,$(BUILD_DIR)/%,$(TUTORIAL_SRC))
LICENSE=$(BUILD_DIR)/LICENSE.html
DEMO=$(BUILD_DIR)/Demo.html
ICONS=$(patsubst $(SRC_DIR)/%,$(BUILD_DIR)/%,$(wildcard $(SRC_DIR)/icons/*.gif)) \
	$(BUILD_DIR)/icons.properties
DEVICES=\
  $(patsubst $(SRC_SYSEXEDIT)/devices/%,$(BUILD_DIR)/devices/%,$(wildcard $(SRC_SYSEXEDIT)/devices/*.xml)) \
  $(patsubst $(SRC_SYSEXEDIT)/devices/%,$(BUILD_DIR)/devices/%,$(wildcard $(SRC_SYSEXEDIT)/devices/*.xsd))
JAR_FILE=$(JAR_DIR)/SysExEdit.jar

classes: $(BUILD_DIR) $(CLASS) $(ICONS) $(DEVICES) $(LICENSE) $(DEMO)

$(BUILD_DIR):
	mkdir -p $@

$(BUILD_DIR)/LICENSE.html: $(SRC_DIR)/LICENSE.html
	cp -fp $< $@

$(BUILD_DIR)/Demo.html: $(SRC_DIR)/Demo.html
	cp -fp $< $@

$(BUILD_DIR)/icons:
	mkdir -p $@

$(BUILD_DIR)/icons/%: $(SRC_DIR)/icons/% | $(BUILD_DIR)/icons
	cp -fp $< $@

$(BUILD_DIR)/icons.properties: $(SRC_DIR)/icons.properties
	cp -fp $< $@

icons:
	cd icons; make all

$(BUILD_DIR)/devices:
	mkdir -p $@

$(BUILD_DIR)/devices/%: $(SRC_SYSEXEDIT)/devices/% | $(BUILD_DIR)/devices
	cp -fp $< $@

$(BUILD_DIR)/doc:
	mkdir -p $@

$(BUILD_DIR)/doc/api: $(BUILD_DIR)/doc
	mkdir -p $@

$(BUILD_DIR)/doc/tutorial: $(BUILD_DIR)/doc
	mkdir -p $@

$(BUILD_DIR)/doc/tutorial/%: $(SRC_DIR)/doc/tutorial/% | $(BUILD_DIR)/doc/tutorial
	cp -fp $< $@

$(BUILD_DIR)/doc/api/images: $(BUILD_DIR)/doc/api
	mkdir -p $@

$(BUILD_DIR)/doc/api/images/%: $(SRC_DIR)/doc/api/images/%
	cp -fp $< $@

$(BUILD_DIR)/doc/api/packages.html: \
	$(SRC_SYSEXEDIT) $(patsubst %,$(SRC_SYSEXEDIT)/%,$(SUB_PACKAGES)) \
	| classes
	javadoc -d $(BUILD_DIR)/doc/api -classpath $(CLASSPATH) \
		$(DOC_OTHER) $(PACKAGES)

doc: $(TUTORIAL_OBJ) $(BUILD_DIR)/doc/api/packages.html

jar: doc $(JAR_FILE)

$(JAR_DIR):
	mkdir -p $@

$(JAR_FILE): $(JAR_DIR) $(CLASS)
	cd $(BUILD_DIR); \
	jar -0cvfm $(JAR_DIR)/SysExEdit.jar $(SRC_DIR)/META-INF/MANIFEST.MF .

depend:

tags:
	etags *.java

docclean:
	- rm -rf $(BUILD_DIR)/doc

objclean:
	- rm -rf $(BUILD_DIR)
	- cd icons ; make clean

jarclean:
	- rm -rf $(JAR_DIR)

bkpclean:
	- find $(SRC_DIR) -name \*~ -exec /bin/rm -f {} \; -print

clean: objclean jarclean

distclean: clean bkpclean

tarball: distclean
	@cd .. ; \
	TGZ_DATE=`date +%Y-%m-%d_%H-%M-%S` ; \
	PROJECT_NAME=SysExEdit ; \
	PROJECT_PATH=`basename \`pwd\`` ; \
	TGZ_PREFIX=$$PROJECT_NAME\_$$TGZ_DATE ; cd .. ; \
	tar cvf ./$$TGZ_PREFIX.tar.bz2 \
		--exclude=.git \
		--exclude=untracked_files \
		--transform=s/$$PROJECT_PATH/$$TGZ_PREFIX/ \
		--bzip2 $$PROJECT_PATH

.SUFFIXES: $(SUFFIXES) .java .class

$(BUILD_DIR)%class: $(SRC_DIR)%java
	javac -Xlint:all -d $(BUILD_DIR) -classpath $(CLASSPATH) $<

#  Local Variables:
#    coding:utf-8
#    mode:Makefile
#  End:
