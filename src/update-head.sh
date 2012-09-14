#!/bin/bash

SRC_TARGET=anthavio-dao/src/main/java/com/anthavio/gdao

if [ ! -d $SRC_TARGET ]; then
	mkdir -p $SRC_TARGET
else
	rm -rf $SRC_TARGET
fi

#echo "Checking out svn trunk"
#svn checkout http://hibernate-generic-dao.googlecode.com/svn/trunk/ generic-dao-readonly
echo "Stripping svn directories"
#svn export http://hibernate-generic-dao.googlecode.com/svn/trunk/ generic-dao-exported

echo "Copying dao sources"
cp -r generic-dao-exported/dao/src/main/java/com/googlecode/genericdao $SRC_TARGET
echo "Copying search sources"
cp -r generic-dao-exported/search/src/main/java/com/googlecode/genericdao/* $SRC_TARGET
cp -r generic-dao-exported/search-hibernate/src/main/java/com/googlecode/genericdao/* $SRC_TARGET
cp -r generic-dao-exported/search-jpa-hibernate/src/main/java/com/googlecode/genericdao/* $SRC_TARGET


echo "Renaming packages etc..."

OLD="com.googlecode.genericdao"
NEW="com.anthavio.gdao"

find $SRC_TARGET -type f -name "*.java" -exec sed -i s/$OLD/$NEW/g {} \;
find $SRC_TARGET -type f -print0 | xargs -0 sed -i 's/$OLD/$NEW/g'

OLD="implements GenericDAO<T, ID>"
NEW="\/\*implements GenericDAO<T, ID>\*\/"

find $SRC_TARGET -type f -name "GenericDAOImpl.java" -exec sed -i "s/$OLD/$NEW/g" {} \;
