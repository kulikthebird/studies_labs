import fractions

def lcg_rand(seed=10, a=1103515245, c=12345, m=2**31 - 1):
    s = seed
    while True:
        s = (s * a + c) % m
        yield s

def generate_rand_numbers(counter=40):
    random_numbers = []
    for i in lcg_rand():
        if counter <= 0:
            break
        random_numbers.append(i)
        counter -= 1
    return random_numbers

def determ_matrix(s):
    return s[0]*s[2] + s[1]*s[2] + s[1]*s[3] - s[0]* s[3] - s[1]**2 - s[2]**2

def count_m(rand, tries=1):
    old = determ_matrix(rand[0:4])
    for i in range(1, tries):
        old = abs(fractions.gcd(old, determ_matrix(rand[i:i+4])))
    return old

def rev_modulo(a, b):
    u = 1
    w = a
    x = 0
    z = b
    while w > 0:
        if w < z:
            u, x = x, u
            w, z = z, w
        q = w / z
        u = u - q * x
        w = w - q * z
    if z == 1:
        if x < 0:
            x += b
        return x
    else:
        raise "Error while reverting number in Z(modulo) group"

def break_generator():
    s = generate_rand_numbers(40)
    m = count_m(s, 3)
    a = (((s[1] - s[2] + m) % m)*rev_modulo(((s[0] - s[1] + m) % m), m)) % m
    c = (s[1] - (s[0]*a % m) + m) % m
    print a, c, m

break_generator()