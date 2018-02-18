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

PACKAGES	= see see.model see.gui see.devices

#
# Source Files
#

JAVA =	$(SRC_ROOT)/see/model/AddressRepresentation.java \
	$(SRC_ROOT)/see/model/Representation.java \
	$(SRC_ROOT)/see/model/EnumerationType.java \
	$(SRC_ROOT)/see/model/Range.java \
	$(SRC_ROOT)/see/model/MapChangeEvent.java \
	$(SRC_ROOT)/see/model/MapChangeListener.java \
	$(SRC_ROOT)/see/model/Contents.java \
	$(SRC_ROOT)/see/model/RangeContents.java \
	$(SRC_ROOT)/see/model/MapDef.java \
	$(SRC_ROOT)/see/model/MapNode.java \
	$(SRC_ROOT)/see/gui/SelectableLabel.java \
	$(SRC_ROOT)/see/gui/Utils.java \
	$(SRC_ROOT)/see/gui/ExceptionPanel.java \
	$(SRC_ROOT)/see/gui/History.java \
	$(SRC_ROOT)/see/gui/HtmlFactory.java \
	$(SRC_ROOT)/see/gui/HtmlPanel.java \
	$(SRC_ROOT)/see/gui/Map.java \
	$(SRC_ROOT)/see/gui/MessageFrame.java \
	$(SRC_ROOT)/see/gui/DialogDevID.java \
	$(SRC_ROOT)/see/gui/FramesManager.java \
	$(SRC_ROOT)/see/gui/EditorFrame.java \
	$(SRC_ROOT)/see/SysExEdit.java \
	$(SRC_ROOT)/see/devices/DB50XG.java

CLASS =	$(CLS_ROOT)/see/model/AddressRepresentation.class \
	$(CLS_ROOT)/see/model/Representation.class \
	$(CLS_ROOT)/see/model/EnumerationType.class \
	$(CLS_ROOT)/see/model/Range.class \
	$(CLS_ROOT)/see/model/MapChangeEvent.class \
	$(CLS_ROOT)/see/model/MapChangeListener.class \
	$(CLS_ROOT)/see/model/Contents.class \
	$(CLS_ROOT)/see/model/RangeContents.class \
	$(CLS_ROOT)/see/model/MapDef.class \
	$(CLS_ROOT)/see/model/MapNode.class \
	$(CLS_ROOT)/see/gui/SelectableLabel.class \
	$(CLS_ROOT)/see/gui/Utils.class \
	$(CLS_ROOT)/see/gui/ExceptionPanel.class \
	$(CLS_ROOT)/see/gui/History.class \
	$(CLS_ROOT)/see/gui/HtmlFactory.class \
	$(CLS_ROOT)/see/gui/HtmlPanel.class \
	$(CLS_ROOT)/see/gui/Map.class \
	$(CLS_ROOT)/see/gui/MessageFrame.class \
	$(CLS_ROOT)/see/gui/DialogDevID.class \
	$(CLS_ROOT)/see/gui/FramesManager.class \
	$(CLS_ROOT)/see/gui/EditorFrame.class \
	$(CLS_ROOT)/see/SysExEdit.class \
	$(CLS_ROOT)/see/devices/DB50XG.class

all: classes doc lib

LICENSE		= $(CLS_ROOT)/LICENSE.html
DEMO		= $(CLS_ROOT)/Demo.html
ICONS		= $(patsubst $(SRC_ROOT)/%,$(CLS_ROOT)/%,$(wildcard $(SRC_ROOT)/icons/*.gif)) \
		  $(CLS_ROOT)/icons.properties

classes:	$(CLS_ROOT) $(CLASS) $(ICONS) $(LICENSE) $(DEMO)

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
	echo ICONS=$(ICONS)
	cp -fp $< $@

icons:
	cd icons; make all

$(CLS_ROOT)/doc:
	mkdir -p $@

$(CLS_ROOT)/doc/api: $(CLS_ROOT)/doc
	mkdir -p $@

$(CLS_ROOT)/doc/tutorial: $(CLS_ROOT)/doc
	mkdir -p $@

$(CLS_ROOT)/doc/tutorial/%: $(SRC_ROOT)/doc/tutorial/%
	cp -fp $< $@

$(CLS_ROOT)/doc/api/images: $(CLS_ROOT)/doc/api
	mkdir -p $@

$(CLS_ROOT)/doc/api/images/%: $(SRC_ROOT)/doc/api/images/%
	cp -fp $< $@

$(CLS_ROOT)/doc/api/packages.html: $(SRC_ROOT)/see $(SRC_ROOT)/see/midi $(SRC_ROOT)/see/model $(SRC_ROOT)/see/gui $(SRC_ROOT)/see/devices | classes
	javadoc -d $(CLS_ROOT)/doc/api -classpath $(CLASSPATH) \
		$(DOC_OTHER) $(PACKAGES)

doc: $(CLS_ROOT)/doc/tutorial $(CLS_ROOT)/doc/api/packages.html

lib: classes doc $(LIB_DIR)

$(LIB_DIR):
	mkdir -p $@
	cd $(CLS_ROOT); jar -0cvf $(LIB_DIR)/classes.zip .

depend:

tags:
	etags *.java

clean:
	- rm -rf $(CLS_ROOT)
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

libclean:
	- rm -rf $(LIB_DIR)

veryclean: clean bkpclean docclean libclean

tgz:
	- cd .. ; rm -f src.tgz.bak.bak
	- cd .. ; mv -f src.tgz.bak src.tgz.bak.bak
	- cd .. ; mv -f src.tgz src.tgz.bak
	cd .. ; tar -cvf - src | gzip --best > src.tgz

.SUFFIXES: $(SUFFIXES) .java .class

$(CLS_ROOT)%class: $(SRC_ROOT)%java
	javac -Xlint:all -d $(CLS_ROOT) -classpath $(CLASSPATH) $<

#  Local Variables:
#    coding:utf-8
#    mode:Makefile
#  End:
