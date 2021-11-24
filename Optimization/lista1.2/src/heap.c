/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <stdlib.h>
#include <stdio.h>
#include <heap.h>
#include <math.h>
#include <string.h>

void initHeap(int size, Heap *heap)
{
    heap -> last = 0;
    heap -> size = size;
    heap -> list1 = NULL;
    heap -> list1 = (Element*)malloc(sizeof(Element)*size);
}

void releaseHeap(Heap *heap)
{
    if(heap->list1 != NULL)
        free(heap->list1);
}

int pow2(int n)
{
    if(n == 0)
        return 1;
    else if(n == 1)
        return 2;
    if(n%2 == 0)
    {
        int x = pow2(n/2);
        return x*x;
    }
    else
    {
        int x = pow2(n/2);
        return x*x*2;
    }
}

void push(Element ver, Heap *heap)
{
    int i = heap->last, j = ceil((double)heap->last/2) - 1;
    Element temp;
    heap->last++;
    heap->list1[i] = ver;
    *ver.index = i;
    while(1)
    {
        if(i == 0)
            break;
        if(*heap->list1[i].value < *heap->list1[j].value)
        {
            temp = heap->list1[j];
            heap->list1[j] = heap->list1[i];
            heap->list1[i] = temp;
            *heap->list1[i].index = i;
            *heap->list1[j].index = j;
            i = j;
            j = ceil((double)i/2) - 1;
        }
        else break;
    }
}

Element pop(Heap *heap)
{
    int i = 0, j = 0, p;
    Element result = heap->list1[0] , temp;
    if(heap->last == 0)
    {
        result.e = NULL;
        return result;
    }

    heap->list1[0] = heap->list1[heap->last-1];
    memset(&heap->list1[heap->last-1], 0, sizeof(Element));
    heap->last--;
    for(p = 0; p<log2(heap->last)+1; p++)
    {
        j = i*2 + 1;
        if(j>=heap->last)
            break;

        if(j+1 < heap->last && *heap->list1[j].value > *heap->list1[j+1].value)
            j++;
        if(*heap->list1[i].value > *heap->list1[j].value)
        {
            temp = heap->list1[j];
            heap->list1[j] = heap->list1[i];
            heap->list1[i] = temp;
            *heap->list1[i].index = i;
            *heap->list1[j].index = j;
            i = j;
        }
        else break;
    }
    //printf("usuwam node: %d\n", ((Node*)result.e)->id);
    return result;
}

void increaseKey(int i, int newKey, Heap *heap)
{
    int j = ceil((double)i/2) - 1;
    //*heap->list1[i].value = newKey;
    Element temp;
    while(1)
    {
        if(i == 0)
            break;
        if(*heap->list1[i].value < *heap->list1[j].value)
        {
            temp = heap->list1[j];
            heap->list1[j] = heap->list1[i];
            heap->list1[i] = temp;
            *heap->list1[i].index = i;
            *heap->list1[j].index = j;
            i = j;
            j = ceil((double)i/2) - 1;
        }
        else break;
    }
}
