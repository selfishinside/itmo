import re

pattern = r'\b\d+\b'

def test(string) :
    def replace_numbers(match):
        num = int(match.group(0))
        return str (4 * num ** 2 - 7)
    print(re.sub(pattern, replace_numbers, string))

test("20+22=42")
test("вчера в 12 я")
test("1,2,3,4,5,6,7,8 319493")
test("54 ананаса и 123 яблока((")
test("из 17 слогов, составляющих один столбец иероглифов. Особыми разделительными словами – кирэдзи – текст хайку делится на части из 5, 7 и сн")