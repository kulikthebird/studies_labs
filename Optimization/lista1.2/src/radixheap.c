/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <uinterface.h>
#include <radixheapstruct.h>
#include <list.h>

#define INFINITE  0xFFFFFFFF

void radix(Graph *V, int s, int t);

int main(int argc, char **argv)
{
    generateResult(argc, argv, &radix);
    return 0;
}

Element* createNewElement(Node* nd)
{
    Element *result = malloc(sizeof(Element));
    result->e = nd;
    result->value = &nd->value;
    result->listPosition = &nd->position.listPosition;
    result->listNumber = 0;
    return result;
}

void radix(Graph *V, int s, int t)
{
    int i;
    RadixHeap heap;
    initRadixHeap(33, &heap);
    for(i=0; i<V->nodesAmount; i++)
    {
        Node* actualNode = &V->nodeList[i];
        actualNode->prev = NULL;
        actualNode->position.listPosition = NULL;
        if(i+1 == s)    actualNode->value = 0;
        else            actualNode->value = INFINITE;
    }
    V->nodeList[s-1].position.listPosition = pushRadixHeap(createNewElement(&V->nodeList[s-1]), &heap);
    Element u;
    ListNode *v;
    while((u = popRadixHeap(&heap)).e != NULL)
    {
        Node *actualNode = ((Node*)u.e);
        if(t != -1)
            if(actualNode->id == t)
                break;
        for(v = actualNode->neighboursList.listBegin; v; v = v->nextNode)
        {
            Neighbour *actualNeighbour = (Neighbour*)v->value;
            unsigned int neighbourIndex = actualNeighbour->neighbour;
            unsigned int newVal = actualNode->value + actualNeighbour->value;
            if(V->nodeList[neighbourIndex-1].value > newVal)
            {
                Node *neighbour = &V->nodeList[neighbourIndex-1];
                neighbour->value = newVal;
                if(neighbour->position.listPosition == NULL)
                    neighbour->position.listPosition = pushRadixHeap(createNewElement(neighbour), &heap);
                else
                    increaseKeyRadixHeap((ListNodeD*)neighbour->position.listPosition, newVal, &heap);
                neighbour->prev = (Node*)u.e;
            }
        }
    }
    releaseRadixHeap(&heap);
}
