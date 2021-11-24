/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <uinterface.h>
#include <heap.h>
#include <list.h>

#define INFINITE  0xFFFFFFFF

void dijkstra(Graph *V, int s, int t);


int main(int argc, char **argv)
{
    generateResult(argc, argv, &dijkstra);
    return 0;
}

Element createNewElement(Node* nd)
{
    Element result;
    result.e = nd;
    result.value = &nd->value;
    result.index = &nd->position.index;
    return result;
}

void dijkstra(Graph *V, int s, int t)
{
    int i;
    Heap heap;
    initHeap(V->edgesAmount, &heap);
    for(i=0; i<V->nodesAmount; i++)
    {
        Node* actualNode = &V->nodeList[i];
        V->nodeList[i].prev = NULL;
        V->nodeList[i].position.index = -1;
        actualNode->value = INFINITE;
    }
    V->nodeList[s-1].value = 0;
    push(createNewElement(&V->nodeList[s-1]), &heap);
    Element u;
    ListNode *v;
    while((u = pop(&heap)).e != NULL)
    {
        Node *actualNode = ((Node*)u.e);
        if(t != -1)
            if(actualNode->id == t)
                break;
        for(v = actualNode->neighboursList.listBegin; v; v = v->nextNode)
        {
            Neighbour *actualNeighbour = (Neighbour*)v->value;
            int neighbourIndex = actualNeighbour->neighbour;
            int newVal = actualNode->value + actualNeighbour->value;
            if(V->nodeList[neighbourIndex-1].value > newVal)
            {
                Node *neighbour = &V->nodeList[neighbourIndex-1];
                neighbour->value = newVal;
                if(neighbour->position.index == -1)
                    push(createNewElement(neighbour), &heap);
                else
                    increaseKey(neighbour->position.index, newVal, &heap);
                neighbour->prev = u.e;
            }
        }
    }
    releaseHeap(&heap);
}
