/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <radixheapstruct.h>
#include <list.h>
#include <math.h>

void initRadixHeap(int size, RadixHeap *heap)
{
    heap -> lastDeleted = 0;
    heap -> size = size;
    heap -> list = NULL;
    heap -> list = (DoubleList*)malloc(sizeof(DoubleList)*size);
    memset(heap->list, 0, sizeof(DoubleList)*size);
}


void releaseRadixHeap(RadixHeap *heap)
{
    if(heap->list != NULL)
    {
        int i;
        for(i=0; i< heap->size; i++)
            releaseDoubleList(&heap->list[i]);
        free(heap->list);
    }
}

int hibit(unsigned int n) {
    if(n == 0)
        return -1;
    n |= (n >>  1);
    n |= (n >>  2);
    n |= (n >>  4);
    n |= (n >>  8);
    n |= (n >> 16);
    return log2(n - (n >> 1));
}

unsigned int getBucketNumber(int value, RadixHeap *heap)
{
    return hibit(value ^ heap->lastDeleted) + 1;
}

void* pushRadixHeap(Element *el, RadixHeap *heap)
{
    int bucketNumber = getBucketNumber(*el->value, heap);
    el->listNumber = bucketNumber;
    return pushDoubleList(el, &heap->list[bucketNumber]);
}

Element popRadixHeap(RadixHeap *heap)
{
    int i, minimum;
    Element result;
    ListNodeD *listNd, *minNode;
    for(i=0; i<heap->size; i++)
        if(heap->list[i].listBegin != NULL)
            break;
    if(i >= heap->size)
    {
        result.e = NULL;
        return result;
    }
    minNode = heap->list[i].listBegin;
    minimum = *((Element*)minNode->value)->value;
    for(listNd = minNode->nextNode; listNd; listNd = listNd->nextNode)
    {
        unsigned int distance = *((Element*)listNd->value)->value;
        if(distance < minimum)
        {
            minimum = distance;
            minNode = listNd;
        }
    }
    result = *(Element*)minNode->value;
    free((Element*)minNode->value);
    deleteFromDoubleList(minNode, &heap->list[i]);
    heap->lastDeleted = *result.value;
    listNd = heap->list[i].listBegin;
    while(listNd)
    {
        Element *element = (Element*)listNd->value;
        int bucketNumber = getBucketNumber(*element->value, heap);
        ListNodeD *ptr = listNd;
        listNd = listNd->nextNode;
        moveToAnotherDoubleList(ptr, &heap->list[i], &heap->list[bucketNumber]);
        element->listNumber = bucketNumber;
    }
    return result;
}

void increaseKeyRadixHeap(ListNodeD *listPosition, int newKey, RadixHeap *heap)
{
    Element *element = (Element*)listPosition->value;
    int bucketNumber = getBucketNumber(newKey, heap);
    if(bucketNumber != element->listNumber)
    {
        moveToAnotherDoubleList(listPosition, &heap->list[element->listNumber], &heap->list[bucketNumber]);
        element->listNumber = bucketNumber;
    }
}
