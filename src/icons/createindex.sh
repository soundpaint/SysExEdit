#!/bin/sh
# createindex.sh creates index.html, an index of all icons
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

cat > index.html <<EOF
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN">
<HTML>

  <HEAD>
    <TITLE>SysExEdit: Icons for General-Purpose Use</TITLE>
  </HEAD>

  <BODY>
    <H2><CENTER>SysExEdit: Icons for General-Purpose Use</CENTER></H2>

EOF
for i in *.gif *.xbm ; do ( cat >> index.html <<EOF
    <A HREF = "$i">
      <IMG SRC = "$i" ALT = "$i" BORDER = 0></A>
    &nbsp;$i<BR>

EOF
) ; done
cat >> index.html <<EOF
  </BODY>

</HTML>
EOF

#  Local Variables:
#    coding:utf-8
#    mode:sh
#  End:
