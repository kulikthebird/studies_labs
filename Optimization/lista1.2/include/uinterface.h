/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#ifndef KULIK_UINTERFACE
#define KULIK_UINTERFACE

#include <list.h>

struct Node
{
    struct Node *prev;
    struct List neighboursList;
    unsigned int value;
    unsigned int id;
    union
    {
        unsigned int index;
        ListNodeD *listPosition;
    }position;
};
typedef struct Node  Node;

struct Neighbour
{
    unsigned int value;
    unsigned int neighbour;
};
typedef struct Neighbour  Neighbour;

struct Graph
{
    struct Node *nodeList;
    unsigned int nodesAmount;
    unsigned int edgesAmount;
    unsigned int maxArcValue;
    unsigned int minArcValue;
};
typedef struct Graph  Graph;

struct Sources
{
    unsigned int *sources;
    unsigned int size;
};
typedef struct Sources  Sources;

struct Point
{
    unsigned int source;
    unsigned int destination;
    unsigned int distance;
};
typedef struct Point  Point;

struct Points
{
    struct Point *points;
    unsigned int size;
};
typedef struct Points  Points;

int createGraphFromFile(FILE *file, Graph *out);
int readSourcesFile(FILE *file, Sources *s);
int readPointsFile(FILE *file, Points *p);
int createSourcesFileResult(FILE *file, Graph *g, Sources *s, const char* graphFile, const char* sourceFile, double time);
int createPointsFileResult(FILE *file, Graph *g, Points *p, const char* graphFile, const char* pointsFile);

int generateResult(int argc, char **argv, void (*algorithm)(Graph*,int,int));

#endif
