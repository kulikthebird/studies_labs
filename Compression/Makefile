CC=clang++
CC_FLAGS := -IInc/ -IGenerated/ -Wno-switch -Wno-deprecated-register -Werror=return-type --std=c++11

compile: Bin/Huffman.o Bin/Arithmetic.o Bin/ICompress.o Bin/BitStream.o Bin/PartialSum.o Bin/Lz77.o Bin/Lzw.o Bin/ExtendedString.o
	$(CC) $(CC_FLAGS) Src/Main.cpp $^ -o Bin/compress

Bin/%.o: Src/%.cpp
	$(CC) $(CC_FLAGS) -c -o $@ $<

clean:
	rm Bin/*.o
