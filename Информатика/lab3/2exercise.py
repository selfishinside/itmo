import re

pattern = r'\d{2}:\d{2}:\d{2}|\d{2}:\d{2}|\d:\d{2}:\d{2}|\d:\d{2}'

def tes(string):
    print(re.sub(pattern, "(TBD)", string,))

def test(string):
    data = string.split()
    a = "(TBD)"
    for i in range (len(data)):
        if ":" in data[i]:
            data[i] = a
        print(data[i], end=' ')
    print("")


test("Уважаемые студенты! В эту субботу в 15:00 планируется доп. занятие на 2 часа. То есть в 17:00:01 оно уже точно кончится.")
test("Долгота дня: 10 ч 20 мин Восход — 7:34 Заход — 17:54")
test("12:23 21:14:59 7:31:40 4:20")
test("Ровно в 2 и не позже 14:10")
test("вт > ис ((")

