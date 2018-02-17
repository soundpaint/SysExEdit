# Makefile for SysExEdit
#
# Copyright (C) 1998, 2018 Juergen Reuter
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

# $Source:$
# $Revision:$
# $Aliases:$
# $Author:$
# $Date:$
# $State:$

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

COPYING		= $(CLS_ROOT)/COPYING.html
DEMO		= $(CLS_ROOT)/Demo.html
ICONS		= $(CLS_ROOT)/icons $(CLS_ROOT)/icons.properties

classes:	$(CLS_ROOT) $(CLASS) $(ICONS) $(WIN_ICONS) $(WIN_RESOURCES) \
		$(COPYING) $(DEMO)

$(CLS_ROOT):
	mkdir $(CLS_ROOT)

$(CLS_ROOT)/COPYING.html:
	cd $(CLS_ROOT) ; \
		ln -s $(SRC_ROOT)/COPYING.html COPYING.html

$(CLS_ROOT)/Demo.html:
	cd $(CLS_ROOT) ; \
		ln -s $(SRC_ROOT)/Demo.html Demo.html

$(CLS_ROOT)/icons: icons
	cd $(CLS_ROOT) ; ln -s $(SRC_ROOT)/icons icons

$(CLS_ROOT)/icons.properties:
	cd $(CLS_ROOT) ; ln -s $(SRC_ROOT)/icons.properties icons.properties

icons:
	cd icons; make all

$(CLS_ROOT)/doc:
	mkdir $(CLS_ROOT)/doc

$(CLS_ROOT)/doc/tutorial: $(CLS_ROOT)/doc
	cd $(CLS_ROOT)/doc ; ln -s $(SRC_ROOT)/doc/tutorial tutorial

$(CLS_ROOT)/doc/api: $(CLS_ROOT)/doc
	mkdir $(CLS_ROOT)/doc/api

$(CLS_ROOT)/doc/api/images: $(CLS_ROOT)/doc/api
	cd $(CLS_ROOT)/doc/api ; ln -s $(SRC_ROOT)/doc/api/images images

$(CLS_ROOT)/doc/api/packages.html: classes $(CLS_ROOT)/doc/api/images
	javadoc -d $(CLS_ROOT)/doc/api -classpath $(CLASSPATH) \
		$(DOC_OTHER) $(PACKAGES)

doc: $(CLS_ROOT)/doc/tutorial $(CLS_ROOT)/doc/api/packages.html

lib: classes doc $(LIB_DIR)

$(LIB_DIR):
	mkdir -p $(LIB_DIR)
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
