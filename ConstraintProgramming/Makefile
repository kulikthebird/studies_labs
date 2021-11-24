CPX_PATH  = /opt/ibm/ILOG/CPLEX_Studio128

CPX_INCDIR  = /opt/ibm/ILOG/CPLEX_Studio128/cpoptimizer/include
CPX_INCCON  = /opt/ibm/ILOG/CPLEX_Studio128/concert/include
CPX_FLAGS = -DIL_STD -DILOUSEMT -D_REENTRANT -DILM_REENTRANT -O

CC=clang++ -Wno-switch -Wno-deprecated-register -Werror=return-type --std=c++11
CCFLAGS := -I./Inc -I$(CPX_INCDIR) -I$(CPX_INCCON) $(CPX_FLAGS)
CPX_LDFLAGS = -L$(CPX_PATH)/cpoptimizer/lib/x86-64_linux/static_pic -lcp -L$(CPX_PATH)/cplex/lib/x86-64_linux/static_pic -lcplex -L$(CPX_PATH)/concert/lib/x86-64_linux/static_pic -lconcert  -lpthread -lm -ldl 



compile: Bin/StoneProblem.o Bin/PuzzleProblem.o Bin/DominoProblem.o Bin/CelticProblem.o Bin/GriddlersProblem.o Bin/CubeProblem.o
	$(CC) $(CCFLAGS) Src/Main.cpp $^ -o Bin/solve1 $(CPX_LDFLAGS)

Bin/%.o: Src/%.cpp
	$(CC) $(CCFLAGS) -c -o $@ $<

run:
	Bin/./solve1

clean:
	rm Bin/*.o
