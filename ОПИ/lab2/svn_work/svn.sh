#!/bin/bash

rm -rf svn_repo
rm -rf wc

svnadmin create svn_repo
REPO_URL="file:///Users/ruslankara-oglanov/Desktop/OPI/lab2/svn_work/svn_repo"
echo "$REPO_URL"

cd svn_repo
svn mkdir -m "project structure" "$REPO_URL/trunk" "$REPO_URL/branches"
cd ..

svn checkout "$REPO_URL/trunk"/ wc # 0-3-6-8-11-12-13-14
cd wc

#коммит 0
cp -r ../../commits/commit0/* .
svn add * --force
svn commit -m "Revision r0" --username=red
echo "✅ r0 создан пользователь red"
echo " "

svn copy "$REPO_URL/trunk" "$REPO_URL/branches/branch1" -m "Creating branch1" --username=blue # 1-2-7-9
svn switch "$REPO_URL/branches/branch1"

#коммит 1
cp -r ../../commits/commit1/* .
svn add * --force
svn commit -m "Revision r1" --username=blue
echo "✅ r1 создан пользователь blue"
echo " "

#коммит 2
cp -r ../../commits/commit2/* .
svn add * --force
svn commit -m "Revision r2" --username=blue
echo "✅ r2 создан пользователь blue"
echo " "

svn switch "$REPO_URL/trunk"

#коммит 3
cp -r ../../commits/commit3/* .
svn add * --force
svn commit -m "Revision r3" --username=red
echo "✅ r3 создан пользователь red"
echo " "

svn copy "$REPO_URL/trunk" "$REPO_URL/branches/branch2" -m "Creating branch2" --username=blue # 4-5-10
svn switch "$REPO_URL/branches/branch2"

#коммит 4
cp -r ../../commits/commit4/* .
svn add * --force
svn commit -m "Revision r4" --username=blue
echo "✅ r4 создан пользователь blue"
echo " "

#коммит 5
cp -r ../../commits/commit5/* .
svn add * --force
svn commit -m "Revision r5" --username=blue
echo "✅ r5 создан пользователь blue"
echo " "

svn switch "$REPO_URL/trunk"

#коммит 6
cp -r ../../commits/commit6/* .
svn add * --force
svn commit -m "Revision r6"  --username=red
echo "✅ r6 создан пользователь red"
echo " "

svn switch "$REPO_URL/branches/branch1"

#коммит 7
cp -r ../../commits/commit7/* .
svn add * --force
svn commit -m "Revision r7" --username=blue
echo "✅ r7 создан пользователь blue"
echo " "

svn switch "$REPO_URL/trunk"

#коммит 8
cp -r ../../commits/commit8/* .
svn add * --force
svn commit -m "Revision r8" --username=red
echo "✅ r8 создан пользователь red"
echo " "

svn switch "$REPO_URL/branches/branch1"

#коммит 9
cp -r ../../commits/commit9/* .
svn add * --force
svn commit -m "Revision r9" --username=blue
echo "✅ r9 создан пользователь blue"
echo " "

svn update
svn switch "$REPO_URL/branches/branch2"
svn merge "$REPO_URL/branches/branch1"
svn add * --force
echo "Слияние r9 и r5"


#коммит 10
cp -r ../../commits/commit10/* .
svn add * --force
svn commit -m "Revision r10" --username=blue
echo "✅ r10 создан пользователь blue"
echo " "

svn update
svn switch "$REPO_URL/trunk"
svn merge "$REPO_URL/branches/branch2" 
svn add * --force
echo "Слияние r10 и r8"

#коммит 11
cp -r ../../commits/commit11/* .
svn add * --force
svn commit -m "Revision r11" --username=red
echo "✅ 11 создан пользователь red"
echo " "

svn switch "$REPO_URL/branches/branch3"

#коммит 12
cp -r ../../commits/commit12/* .
svn add * --force
svn commit -m "Revision r12" --username=red
echo "✅ r12 создан пользователь red"
echo " "

#коммит 13
cp -r ../../commits/commit13/* .
svn add * --force
svn commit -m "Revision r13" --username=red
echo "✅ r13 создан пользователь red"
echo " "

#коммит 14
cp -r ../../commits/commit14/* .
svn add * --force
svn commit -m "Revision r14" --username=red
echo "✅ r14 создан пользователь red"
echo " "

svn update