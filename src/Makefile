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

PACKAGES=see see.model see.gui see.devices

#
# Source Files
#

JAVA=$(wildcard $(SRC_ROOT)/see/*.java) \
	$(wildcard $(SRC_ROOT)/see/model/*.java) \
	$(wildcard $(SRC_ROOT)/see/gui/*.java) \
	$(wildcard $(SRC_ROOT)/see/devices/*.java)

CLASS=$(patsubst $(SRC_ROOT)/%.java,$(CLS_ROOT)/%.class,$(JAVA))

all: classes doc jar

TUTORIAL_SRC=$(wildcard $(SRC_ROOT)/doc/tutorial/*.html)
TUTORIAL_OBJ=$(patsubst $(SRC_ROOT)/%,$(CLS_ROOT)/%,$(TUTORIAL_SRC))
LICENSE=$(CLS_ROOT)/LICENSE.html
DEMO=$(CLS_ROOT)/Demo.html
ICONS=$(patsubst $(SRC_ROOT)/%,$(CLS_ROOT)/%,$(wildcard $(SRC_ROOT)/icons/*.gif)) \
	$(CLS_ROOT)/icons.properties
JAR_FILE=$(JAR_DIR)/SysExEdit.jar

classes: $(CLS_ROOT) $(CLASS) $(ICONS) $(LICENSE) $(DEMO)

$(CLS_ROOT):
	mkdir -p $@

$(CLS_ROOT)/LICENSE.html: $(SRC_ROOT)/LICENSE.html
	cp -fp $< $@

$(CLS_ROOT)/Demo.html: $(SRC_ROOT)/Demo.html
	cp -fp $< $@

$(CLS_ROOT)/icons:
	mkdir -p $@

$(CLS_ROOT)/icons/%: $(SRC_ROOT)/icons/% | $(CLS_ROOT)/icons
	cp -fp $< $@

$(CLS_ROOT)/icons.properties: $(SRC_ROOT)/icons.properties
	cp -fp $< $@

icons:
	cd icons; make all

$(CLS_ROOT)/doc:
	mkdir -p $@

$(CLS_ROOT)/doc/api: $(CLS_ROOT)/doc
	mkdir -p $@

$(CLS_ROOT)/doc/tutorial: $(CLS_ROOT)/doc
	mkdir -p $@

$(CLS_ROOT)/doc/tutorial/%: $(SRC_ROOT)/doc/tutorial/% | $(CLS_ROOT)/doc/tutorial
	cp -fp $< $@

$(CLS_ROOT)/doc/api/images: $(CLS_ROOT)/doc/api
	mkdir -p $@

$(CLS_ROOT)/doc/api/images/%: $(SRC_ROOT)/doc/api/images/%
	cp -fp $< $@

$(CLS_ROOT)/doc/api/packages.html: $(SRC_ROOT)/see $(SRC_ROOT)/see/midi $(SRC_ROOT)/see/model $(SRC_ROOT)/see/gui $(SRC_ROOT)/see/devices | classes
	javadoc -d $(CLS_ROOT)/doc/api -classpath $(CLASSPATH) \
		$(DOC_OTHER) $(PACKAGES)

doc: $(TUTORIAL_OBJ) $(CLS_ROOT)/doc/api/packages.html

jar: doc $(JAR_FILE)

$(JAR_DIR):
	mkdir -p $@

$(JAR_FILE): $(JAR_DIR) $(CLASS)
	cd $(CLS_ROOT); \
	jar -0cvfm $(JAR_DIR)/SysExEdit.jar $(SRC_ROOT)/META-INF/MANIFEST.MF .

depend:

tags:
	etags *.java

clean:
	- rm -rf $(CLS_ROOT)
	- rm -rf $(JAR_DIR)
	- cd icons ; make clean

modelclean:
	- rm -f $(CLS_ROOT)/see/model/*.class

guiclean:
	- rm -f $(CLS_ROOT)/see/gui/*.class

devicesclean:
	- rm -f $(CLS_ROOT)/see/devices/*.class

seeclean: modelclean guiclean devicesclean

bkpclean:
	- find $(SRC_ROOT) -name \*~ -exec /bin/rm -f {} \; -print

docclean:
	- rm -rf $(CLS_ROOT)/doc

jarclean:
	- rm -rf $(JAR_DIR)

distclean: clean bkpclean docclean jarclean

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

$(CLS_ROOT)%class: $(SRC_ROOT)%java
	javac -Xlint:all -d $(CLS_ROOT) -classpath $(CLASSPATH) $<

#  Local Variables:
#    coding:utf-8
#    mode:Makefile
#  End:
