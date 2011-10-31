#
# This file is part of
# MORe - Managing Ongoing Relationships
#
# Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
# 
# MORe - Managing Ongoing Relationships is free software: You can redistribute 
# it and/or modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the License,
# or (at your option) any later version.
#  
# MORe - Managing Ongoing Relationships is distributed in the hope that it
# will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
# of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# Center for Environmental Systems Research, Kassel
#
#
# Installs all packages required by R-Scripts in a single R statement
#
# NOTE:
# - R_HOME must be set correctly
# - The directory containing R.dll must be in your PATH
# - JRI library must be in the current directory or any directory listed in java.library.path.
#   Alternatively you can specify its path with -Djava.library.path= when starting the JVM.
#   When you use the latter, make sure you check java.library.path property first such
#   that you won't break your Java.
# 
# Additional Information
#  - JRI/rJava (see http://www.rforge.net/JRI/)
#
# Author: Sascha Holzhauer
###############################################################################

install.packages(c(
				"sna", 
				"igraph",
				"rJava"
		))