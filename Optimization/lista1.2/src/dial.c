/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <uinterface.h>
#include <list.h>

#define INFINITE  0xFFFFFFFF

void dial(Graph *V, int s, int t);

int main(int argc, char **argv)
{
    generateResult(argc, argv, &dial);
    return 0;
}

int pivot = 0;
Node* selectMinFromBucket(DoubleList *buckets, int C)
{
    int i;
    for(i=0; i<C+1; i++)
        if(buckets[(pivot+i)%(C+1)].listBegin != NULL)
        {
            Node* nd = (Node*) popDoubleList(&buckets[(pivot+i)%(C+1)]);
            pivot = (pivot+i)%(C+1);
            return nd;
        }
    return NULL;
}

void dial(Graph *V, int s, int t)
{
    const int C = V->maxArcValue;
    int i;
    pivot = 0;
    DoubleList *list = malloc(sizeof(DoubleList)*(C+1));
    memset(list, 0, sizeof(DoubleList)*(C+1));

    for(i=0; i<V->nodesAmount; i++)
    {
        Node* actualNode = &V->nodeList[i];
        actualNode->prev = NULL;
        if(i+1 == s)    actualNode->value = 0;
        else            actualNode->value = INFINITE;
    }
    V->nodeList[s-1].position.listPosition = pushDoubleList(&V->nodeList[s-1], &list[0]);

    ListNode *v;
    Node *u;
    while((u = selectMinFromBucket(list, C)) != NULL)
    {
        if(t != -1)
            if(u->id == t)
                break;
        for(v = u->neighboursList.listBegin; v; v = v->nextNode)
        {
            Neighbour *actualNeighbour = (Neighbour*)v->value;
            unsigned int neighbourIndex = actualNeighbour->neighbour;
            unsigned int newVal = u->value + actualNeighbour->value;
            if(V->nodeList[neighbourIndex-1].value > newVal)
            {
                Node *neighbour = &V->nodeList[neighbourIndex-1];
                if(V->nodeList[neighbourIndex-1].position.listPosition != NULL)
                    deleteFromDoubleList((ListNodeD*)neighbour->position.listPosition, &list[neighbour->value%(C+1)]);
                neighbour->value = newVal;
                neighbour->position.listPosition = pushDoubleList(neighbour, &list[newVal%(C+1)]);
                neighbour->prev = u;
            }
        }
    }
    for(i=0; i<C+1; i++)
        releaseDoubleList(&list[i]);
     free(list);
}
