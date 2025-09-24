import re

pattern = r'=-{/'
def test(string):
    print(len(re.findall(pattern,string)))


test("=-{/=+")
test("")
test("=-{/]]1/ /=-{/")
test("=-{/=-{/=-{/=-{/=-{/")
test("=-{/  /       =-{/")