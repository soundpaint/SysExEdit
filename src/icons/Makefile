# Makefile for SysExEdit icons
#
# Copyright (C) 1998  Juergen Reuter
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

#
# giftrans is available at
# ftp://ftp.rz.uni-karlsruhe.de/pub/net/www/tools/giftrans/giftrans.c
#
GIFTRANS = /usr/local/bin/giftrans

all: index.html

clean:
	- rm -f index.html

bkpclean:
	- rm -f *~ *.bak

veryclean: clean bkpclean

trans:
	for i in int*.gif ; do ( echo making transparent: $$i -\> $$i.trans ; \
		$(GIFTRANS) -t \#b1b1b1 $$i > $$i.trans ; \
		echo rm $$i ; rm $$i ; \
		echo mv $$i.trans $$i ; mv $$i.trans $$i ) ; done

index.html: *.gif
	./createindex.sh
