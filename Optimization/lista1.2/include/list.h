/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#ifndef KULIK_LIST
#define KULIK_LIST

struct ListNode
{
    void *value;
    struct ListNode *nextNode;
};
typedef struct ListNode  ListNode;

struct List
{
    struct ListNode *listBegin;
    struct ListNode *listEnd;
};
typedef struct List  List;


struct ListNodeD
{
    void *value;
    struct ListNodeD *nextNode;
    struct ListNodeD *prevNode;
};
typedef struct ListNodeD  ListNodeD;

struct DoubleList
{
    struct ListNodeD *listBegin;
    struct ListNodeD *listEnd;
};
typedef struct DoubleList  DoubleList;


void initList(List *list);
void releaseList(List *list);
void* popList(List *list);
void pushList(void *value, List *list);

void initDoubleList(DoubleList *list);
void releaseDoubleList(DoubleList *list);
void* popDoubleList(DoubleList *list);
void* pushDoubleList(void *value, DoubleList *list);
void deleteFromDoubleList(ListNodeD *nd, DoubleList *list);
void moveToAnotherDoubleList(ListNodeD *nd, DoubleList *listIn, DoubleList *listOut);

#endif
