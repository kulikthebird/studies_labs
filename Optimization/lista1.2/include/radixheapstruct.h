/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#ifndef KULIK_RADIXHEAP
#define KULIK_RADIXHEAP

#include <list.h>

struct Element
{
    void * e;
    unsigned int *value;
    unsigned int listNumber;
    ListNodeD **listPosition;
};
typedef struct Element  Element;

struct RadixHeap
{
    struct DoubleList *list;
    unsigned int size;
    unsigned int lastDeleted;
};
typedef struct RadixHeap  RadixHeap;

void initRadixHeap(int size, RadixHeap *heap);
void releaseRadixHeap(RadixHeap *heap);
void* pushRadixHeap(Element *ver, RadixHeap *heap);
void increaseKeyRadixHeap(ListNodeD *listPosition, int newKey, RadixHeap *heap);
Element popRadixHeap(RadixHeap *heap);

#endif
