import re


def readInput(filepath):
    ciphertextList = []
    with open(filepath, 'r') as f:
        readText = f.read()
        readText = readText.replace("\n", "")
        splitByBlocks = re.split("ciphertext #\d+:", readText)
        splitByBlocks = splitByBlocks[1:]
        ciphertextList = map(lambda x: map(lambda y: int(y, 2), x.split(" ")), splitByBlocks)
    f.closed
    return ciphertextList

def brute(cipherList):
    kList = []
    for text in range(200):
        currentRank = 0
        currentBestK = 0
        for k in range(256):
            tempRank = 0
            for column in range(len(cipherList)):
                dec = (cipherList[column][text] ^ k) if text < len(cipherList[column]) else -1
                if dec in [ord(x) for x in list('aeyuio')]:
                    tempRank += 120
                elif (dec >= ord('a') and dec <= ord('z')):
                    tempRank += 110
                elif (dec >= ord('A') and dec <= ord('Z')):
                    tempRank += 40
                elif dec == ord(' '):
                    tempRank += 80
            if tempRank > currentRank:
                currentRank = tempRank
                currentBestK = k
        kList += [currentBestK]
    return kList

cipherList = readInput('input.txt')
kList = brute(cipherList)
# for a in range(len(cipherList)):
#     for b in range(a+1, len(cipherList)):
#         xored = [pair[0] ^ pair[1] for pair in zip(cipherList[a], cipherList[b])]


print ''.join([str(unichr(x ^ k)) for x,k in zip(cipherList[-1], kList)])

