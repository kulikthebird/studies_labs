CC=clang++

CC_FLAGS := -IInc/ -IGenerated/ -Wno-switch -Wno-deprecated-register -Werror=return-type --std=c++11


compile: Bin/CompilerCore.o Bin/Tokens.o Bin/Parser.o Bin/InterCodeGenerator.o Bin/InterCodeOptimizer.o Bin/InterToOutputTranslator.o Bin/OutputCodeGenerator.o Bin/OutputCodeTemplates.o Bin/VariableManager.o
	$(CC) $(CC_FLAGS) Src/Main.cpp $^ -o Bin/myCompiler

Bin/Parser.o: Generated/Parser.cpp
	$(CC) $(CC_FLAGS) -c -o $@ $<

Bin/Tokens.o: Generated/Tokens.cpp
	$(CC) $(CC_FLAGS) -c -o $@ $<

Bin/%.o: Src/%.cpp
	$(CC) $(CC_FLAGS) -c -o $@ $<

Generated/Parser.%: Src/Grammar.y
	bison -d $< -o Generated/Parser.cpp

Generated/Tokens.%: Generated/Parser.cpp
	flex -o Generated/Tokens.cpp --header-file=Generated/Tokens.hpp Src/Tokens.l

clean:
	rm Bin/*.o Generated/*.cpp Generated/*.hpp

test:
	Bin/./myCompiler < Test/
	Test
