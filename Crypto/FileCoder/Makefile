CC=clang++
CC_FLAGS := -IInc/ --std=c++11 -W -p -lssl -lcrypto

compile: Bin/Encryptor.o Bin/FileCipher.o
	$(CC) $(CC_FLAGS) Src/Main.cpp $^ -o Bin/encrypt

Bin/%.o: Src/%.cpp
	$(CC) $(CC_FLAGS) -c -o $@ $<

clean:
	rm Bin/*.o Bin/encrypt
