CC=gcc
CFLAGS=-Iinclude -Wall -lm

INCLUDE = include
BUILDDIR = obj
SRCDIR = src
TARGET = bin
RESULTS = results
TEST = test

_DEPS = uinterface.h heap.h radixheapstruct.h list.h
DEPS = $(patsubst %,$(INCLUDE)/%,$(_DEPS))
_ZAD1 = dijkstra.o heap.o list.o uinterface.o
_ZAD2 = dial.o list.o uinterface.o
_ZAD3 = radixheap.o radixheapstruct.o list.o uinterface.o
ZAD1 = $(patsubst %,$(BUILDDIR)/%,$(_ZAD1))
ZAD2 = $(patsubst %,$(BUILDDIR)/%,$(_ZAD2))
ZAD3 = $(patsubst %,$(BUILDDIR)/%,$(_ZAD3))

compile: dijkstra dial radixheap

$(BUILDDIR)/%.o: $(SRCDIR)/%.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

dijkstra: $(ZAD1)
	$(CC) -o $(TARGET)/$@ $^ $(CFLAGS)

dial: $(ZAD2)
	$(CC) -o $(TARGET)/$@ $^ $(CFLAGS)

radixheap: $(ZAD3)
	$(CC) -o $(TARGET)/$@ $^ $(CFLAGS)

.PHONY: test

test:
	# functionality tests
	./$(TARGET)/dijkstra -d $(TEST)/test.gr -p2p $(TEST)/test.p2p -op2p $(RESULTS)/test_dijkstra.txt
	./$(TARGET)/dial -d $(TEST)/test.gr -p2p $(TEST)/test.p2p -op2p $(RESULTS)/test_dial.txt
	./$(TARGET)/radixheap -d $(TEST)/test.gr -p2p $(TEST)/test.p2p -op2p $(RESULTS)/test_radixheap.txt
	# [WARNING] TODO: Add files with graphes to test.
	# functionality tests
	#./$(TARGET)/dijkstra -d $(TEST)/USA-road-d.NY.gr -p2p $(TEST)/points.p2p -op2p $(RESULTS)/result_functionality_dijkstra.txt
	#./$(TARGET)/dial -d $(TEST)/USA-road-d.NY.gr -p2p $(TEST)/points.p2p -op2p $(RESULTS)/result_functionality_dial.txt
	#./$(TARGET)/radixheap -d $(TEST)/USA-road-d.NY.gr -p2p $(TEST)/points.p2p -op2p $(RESULTS)/result_functionality_radixheap.txt
	#
	# performance tests
	# New York
	#./$(TARGET)/dijkstra -d $(TEST)/USA-road-d.NY.gr -ss $(TEST)/more_sources.ss -oss $(RESULTS)/result_performance_dijkstra_NY.txt
	#./$(TARGET)/dial -d $(TEST)/USA-road-d.NY.gr -ss $(TEST)/more_sources.ss -oss $(RESULTS)/result_performance_dial_NY.txt
	#./$(TARGET)/radixheap -d $(TEST)/USA-road-d.NY.gr -ss $(TEST)/more_sources.ss -oss $(RESULTS)/result_performance_radixheap_NY.txt
	# Central USA
	#./$(TARGET)/dijkstra -d $(TEST)/USA-road-d.CTR.gr -ss $(TEST)/few_sources.ss -oss $(RESULTS)/result_performance_dijkstra_CTR.txt
	#./$(TARGET)/dial -d $(TEST)/USA-road-d.CTR.gr -ss $(TEST)/few_sources.ss -oss $(RESULTS)/result_performance_dial_CTR.txt
	#./$(TARGET)/radixheap -d $(TEST)/USA-road-d.CTR.gr -ss $(TEST)/few_sources.ss -oss $(RESULTS)/result_performance_radixheap_CTR.txt
	# West USA
	#./$(TARGET)/dijkstra -d $(TEST)/USA-road-d.W.gr -ss $(TEST)/more_sources.ss -oss $(RESULTS)/result_performance_dijkstra_W.txt
	#./$(TARGET)/dial -d $(TEST)/USA-road-d.W.gr -ss $(TEST)/more_sources.ss -oss $(RESULTS)/result_performance_dial_W.txt
	#./$(TARGET)/radixheap -d $(TEST)/USA-road-d.W.gr -ss $(TEST)/more_sources.ss -oss $(RESULTS)/result_performance_radixheap_W.txt

clean:
	rm $(BUILDDIR)/*.o
