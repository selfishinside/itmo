#!/bin/bash

#1
#создание файлов
mkdir lab0
mkdir lab0/bronzor4 lab0/bronzor4/azurill lab0/bronzor4/whismur lab0/bronzor4/bastiodon
mkdir lab0/spoink7 lab0/spoink7/shedinja lab0/spoink7/pelipper lab0/spoink7/grimer lab0/spoink7/ponyta lab0/spoink7/baltoy
mkdir lab0/typhlosion2 lab0/typhlosion2/cyndaquil lab0/typhlosion2/misdreavus

#заполняем файлы
echo -e 'Возможности  Overland=9 Surface=7\nJump=5 Power=7 Intelligence=4' >> lab0/conkeldurr0
echo -e 'Развитые способности\nHydration' >> lab0/mudkip8
echo -e 'weigth=100.3 height=35.0 atk=4\ndef=4' >> lab0/tentacool4
echo -e 'Способности  Trackle Harden Block Rock Throw Thunder Wave\nRock Blast Rest Spark Rock Slide Power Gem Sandstorm Discharge Earth\nPower Stone Edge Lock-On Zap Cannon' >> lab0/bronzor4/nosepass
echo -e 'Возможности  Overland=7\nSurface=7 Jump=1 Power=6 Intelligence=4 Aura=0' >> lab0/bronzor4/hariyama
echo -e 'Развитые\nспособности  Simple' >>  lab0/bronzor4/swoobat
echo -e 'Способности  Uproar Astonish Howl Bite Supersonic\nStomp Screech Roar Synchronise Rest Sleep Talk Hyper\nVoice' >> lab0/spoink7/loudred
echo -e 'Развитые способности  Friend Guard\nHealer' >> lab0/typhlosion2/chansey
echo -e 'satk=7 sdef=5 spd=6' >> lab0/typhlosion2/psyduck
echo -e 'Возможности Overland=9 se Surface=7\nJump=5 Power=7 Intelligence=4' >> lab0/conkeldurr0se


#2
#установка прав
chmod u=rx lab0/bronzor4
chmod g=rwx lab0/bronzor4
chmod o=wx lab0/bronzor4
chmod u-rwx lab0/bronzor4/nosepass
chmod g=r lab0/bronzor4/nosepass
chmod o=rw lab0/bronzor4/nosepass
chmod u=rx lab0/bronzor4/azurill
chmod g=rwx lab0/bronzor4/azurill
chmod o=rwx lab0/bronzor4/azurill
chmod 315 lab0/bronzor4/whismur
chmod 440 lab0/bronzor4/hariyama
chmod 400 lab0/bronzor4/swoobat
chmod u=rwx lab0/bronzor4/bastiodon
chmod g=wx lab0/bronzor4/bastiodon
chmod o=rw lab0/bronzor4/bastiodon
chmod 046 lab0/conkeldurr0
chmod u-rwx lab0/mudkip8
chmod g=r lab0/mudkip8
chmod o=rw lab0/mudkip8
chmod 771 lab0/spoink7
chmod 577 lab0/spoink7/shedinja
chmod 737 lab0/spoink7/pelipper
chmod 444 lab0/spoink7/loudred
chmod 753 lab0/spoink7/grimer
chmod 312 lab0/spoink7/ponyta
chmod 555 lab0/spoink7/baltoy
chmod u=r lab0/tentacool4
chmod g=r lab0/tentacool4
chmod o=rwx lab0/tentacool4
chmod u=wx lab0/typhlosion2
chmod g=rwx lab0/typhlosion2
chmod o=wx lab0/typhlosion2
chmod u=rx lab0/typhlosion2/cyndaquil
chmod g=rwx lab0/typhlosion2/cyndaquil
chmod o=wx lab0/typhlosion2/cyndaquil
chmod 660 lab0/typhlosion2/chansey
chmod 064 lab0/typhlosion2/psyduck
chmod 770 lab0/typhlosion2/misdreavus
chmod 777 lab0/conkeldurr0se


#3
#Скопировать часть дерева и создать ссылки внутри дерева согласно заданию при помощи команд cp и ln, а также комманды cat и перенаправления ввода-вывода.
chmod 777 lab0/bronzor4/whismur
chmod 777 lab0/bronzor4/nosepass
chmod 777 lab0/bronzor4
chmod 777 lab0/typhlosion2
cp -r lab0/bronzor4 lab0/spoink7/pelipper
ln -s lab0/typhlosion2 lab0/Copy_65
ln -s lab0/mudkip8 lab0/bronzor4/swoobatmudkip8
chmod 777 lab0/conkeldurr0
cat lab0/conkeldurr0 > lab0/bronzor4/swoobatconkeldurr
chmod 777 lab0/typhlosion2/psyduck
cat lab0/bronzor4/hariyama lab0/typhlosion2/psyduck > lab0/mudkip8_79
chmod 777 lab0/spoink7/baltoy
cp lab0/tentacool4 lab0/spoink7/baltoy
ln lab0/mudkip8 lab0/typhlosion2/chanseymudkip

chmod 573 lab0/bronzor4
chmod 315 lab0/bronzor4/whismur
chmod 746 lab0/bronzor4/nosepass
chmod 046 lab0/conkeldurr0
chmod 064 lab0/typhlosion2/psyduck
chmod 555 lab0/spoink7/baltoy
chmod 373 lab0/typhlosion2


#4
#))))))))
#chmod 573 lab0/typhlosion2
wc -c conkeldurr0 &> /tmp/conkeldurr0_or_errors
cd lab0
ls -ltr $(grep -rls "se" .) 
cd ../
cat -n **/b* 2>/tmp/$$ | sort -r
ls -t lab0/typhlosion2 2>&1 | sort -r -nk5
cat $(ls -1 -d "$PWD/lab0/bronzor4/"* ) 2>/tmp/$$ | sort -r
ls -lu lab0/bronzor4 2>/tmp/$$


#5
rm -f lab0/tentacool4
chmod -R a+rwx lab0/typhlosion2
rm -f lab0/typhlosion2/chansey
chmod 777 lab0/bronzor4
rm -f lab0/bronzor4/swoobatmudk*
rm -f lab0/typhlosion2/chanseymudk*
rm -rf lab0/typhlosion2
rmdir lab0/bronzor4/bastiodon


