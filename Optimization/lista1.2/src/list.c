/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <list.h>
#include <stdio.h>
#include <stdlib.h>

// Single linked list

void initList(List *list)
{
    list->listBegin = NULL;
}

void releaseList(List *list)
{
    ListNode *nd;
    for(nd = list->listBegin; nd; nd = nd->nextNode)
        free(nd);
}

void* popList(List *list)
{
    ListNode *nd = list->listBegin;
    void *val = nd->value;
    list->listBegin = list->listBegin->nextNode;
    if(nd->nextNode == NULL)
        list->listBegin = NULL;
    free(nd);
    return val;
}

void pushList(void *value, List *list)
{
    ListNode *node = malloc(sizeof(ListNode));
    node->nextNode = list->listBegin;
    node->value = value;
    list->listBegin = node;
}


// Double linked list

void initDoubleList(DoubleList *list)
{
    list->listBegin = NULL;
    list->listEnd = NULL;
}

void releaseDoubleList(DoubleList *list)
{
    ListNodeD *nd;
    for(nd = list->listBegin; nd; nd = nd->nextNode)
        free(nd);
}

void* popDoubleList(DoubleList *list)
{
    ListNodeD *nd = list->listBegin;
    if(nd == NULL)
        return NULL;
    void *val = nd->value;
    list->listBegin = nd->nextNode;
    if(list->listBegin != NULL)
        list->listBegin->prevNode = NULL;
    else
        list->listEnd = NULL;
    free(nd);
    return val;
}

void* pushDoubleList(void *value, DoubleList *list)
{
    ListNodeD *node = malloc(sizeof(ListNodeD));
    node -> value = value;
    node -> prevNode = NULL;
    node -> nextNode = list->listBegin;
    if(list->listBegin != NULL)
        list->listBegin->prevNode = node;
    else
        list->listEnd = node;
    list->listBegin = node;
    return node;
}

void deleteFromDoubleList(ListNodeD *nd, DoubleList *list)
{
    if(list->listBegin == nd)
        list->listBegin = nd->nextNode;
    if(list->listEnd == nd)
        list->listEnd = nd->prevNode;
    if(nd->nextNode != NULL)
        nd->nextNode->prevNode = nd->prevNode;
    if(nd->prevNode != NULL)
        nd->prevNode->nextNode = nd->nextNode;
    free(nd);
}

void moveToAnotherDoubleList(ListNodeD *nd, DoubleList *listIn, DoubleList *listOut)
{
    if(listIn->listBegin == nd)
        listIn->listBegin = nd->nextNode;
    if(listIn->listEnd == nd)
        listIn->listEnd = nd->prevNode;
    if(nd->nextNode != NULL)
        nd->nextNode->prevNode = nd->prevNode;
    if(nd->prevNode != NULL)
        nd->prevNode->nextNode = nd->nextNode;
    nd -> prevNode = NULL;
    nd -> nextNode = listOut->listBegin;
    if(listOut->listBegin != NULL)
        listOut->listBegin->prevNode = nd;
    else
        listOut->listEnd = nd;
    listOut->listBegin = nd;
}
