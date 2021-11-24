/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#ifndef KULIK_HEAP
#define KULIK_HEAP

struct Element
{
    void * e;
    unsigned int *value;
    unsigned int *index;
};
typedef struct Element  Element;

struct Heap
{
    struct Element* list1;
    int value;
    int vertex;
    int size;
    int last;
};
typedef struct Heap  Heap;

void initHeap(int size, Heap *heap);
void releaseHeap(Heap *heap);
void push(Element ver, Heap *heap);
void increaseKey(int i, int newKey, Heap *heap);
Element pop(Heap *heap);

#endif
