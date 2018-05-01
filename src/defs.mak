# defs.mak for SysExEdit makefile
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

# the project home directory
SYSEXEDIT_HOME = $(PWD)/..

# the java runtime environment (JRE) home directory
JRE_HOME = $(PWD)/../../JRE

# java source files root dircetory
SRC_ROOT = $(SYSEXEDIT_HOME)/src

# compiled class files root directory
# (run 'make classes' to generate)
CLS_ROOT = $(SYSEXEDIT_HOME)/classes

# binary target directory; contains archive file with the compiled classes
# (run 'make jar' to generate)
JAR_DIR = $(SYSEXEDIT_HOME)/jar

# other (external) packages to be included when running javadoc
DOC_OTHER =

# java classpath environment variable
CLASSPATH = $(CLS_ROOT):$(SRC_ROOT)

#  Local Variables:
#    coding:utf-8
#    mode:Makefile
#  End:
