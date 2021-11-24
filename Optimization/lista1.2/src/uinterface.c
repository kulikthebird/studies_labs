/**
 * author: Tomasz Kulik
 * subject: Algorytmy optymalizacji dyskretnej
 * Politechnika Wroclawska, 2016 rok
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <uinterface.h>
#include <time.h>

void resetGraph(Graph *g)
{
    int i;
    for(i=0; i<g->nodesAmount; i++)
    {
        g->nodeList[i].position.index = 0;
        g->nodeList[i].position.listPosition = NULL;
        g->nodeList[i].prev = NULL;
        g->nodeList[i].value = 0;
    }
}

int createGraphFromFile(FILE *file, Graph *out)
{
    int i;
    char buff[32];
    char *comment = NULL;
    size_t commentSize=0;
    int data1=0, data2=0;
    int is_setup = 0;
    int counter = 0;
    out->maxArcValue = 0;
    out->minArcValue = 0xFFFFFFFF;
    while(1)
    {
        if(fscanf(file, "%s", buff) == EOF)
            return 1;
        if(strcmp(buff, "c") == 0)
            getline(&comment, &commentSize, file);
        else if(strcmp(buff, "p") == 0)
        {
            if(is_setup == 1)
            {
                printf("Error occured while reading input file!\n");
                return 1;
            }
            if(fscanf(file, "%s %d %d", buff, &data1, &data2) == EOF)
                return 1;
            out->nodesAmount = data1;
            out->edgesAmount = data2;
            is_setup = 1;
            out->nodeList = malloc(sizeof(Node) * out->nodesAmount);
            memset(out->nodeList, 0, out->nodesAmount * sizeof(Node));
            for(i=0; i<out->nodesAmount; i++)
                out->nodeList[i].id = i+1;
        }
        else if(strcmp(buff, "a") == 0)
        {
            if(is_setup == 0)
            {
                printf("There is a problem with input data file.\n");
                return 1;
            }
            counter++;
            int from, to;
            if(fscanf(file, "%d %d %d", &from, &to, &data1) == EOF)
                return 1;
            Neighbour *nb = malloc(sizeof(Neighbour));
            nb->value = data1;
            if(out->maxArcValue < data1)
                out->maxArcValue = data1;
            if(out->minArcValue > data1)
                out->minArcValue  = data1;
            nb->neighbour = to;
            pushList(nb, &(out->nodeList[from-1].neighboursList));
            if(counter == out->edgesAmount)
                break;
        }
    }
    return 0;
}

int readSourcesFile(FILE *file, Sources *s)
{
    char buff1[16];
    char buff2[16];
    char buff3[16];
    char *comment = NULL;
    size_t commentSize=0;
    int data1=0;
    int is_setup = 0;
    int counter = 0;
    while(1)
    {
        if(fscanf(file, "%s", buff1) == EOF)
            return 1;
        if(strcmp(buff1, "c") == 0)
            getline(&comment, &commentSize, file);
        else if(strcmp(buff1, "p") == 0)
        {
            if(is_setup == 1)
            {
                printf("Error occured while reading input file!\n");
                return 1;
            }
            if(fscanf(file, "%s %s %s %d", buff1, buff2, buff3, &data1) == EOF)
                return 1;
            is_setup = 1;
            s->sources = malloc(sizeof(unsigned int) * data1);
            s->size = data1;
        }
        else if(strcmp(buff1, "s") == 0)
        {
            if(is_setup == 0)
            {
                printf("There is a problem with input data file.\n");
                return 1;
            }
            if(fscanf(file, "%d", &data1) == EOF)
                return 1;
            s->sources[counter] = data1;
            counter++;
            if(counter == s->size)
                break;
        }
    }
    return 0;
}

int readPointsFile(FILE *file, Points *p)
{
    char buff1[16];
    char buff2[16];
    char buff3[16];
    char *comment = NULL;
    size_t commentSize=0;
    int data1=0, data2=0;
    int is_setup = 0;
    int counter = 0;
    while(1)
    {
        if(fscanf(file, "%s", buff1) == EOF)
            return 1;
        if(strcmp(buff1, "c") == 0)
            getline(&comment, &commentSize, file);
        else if(strcmp(buff1, "p") == 0)
        {
            if(is_setup == 1)
            {
                printf("Error occured while reading input file!\n");
                return 1;
            }
            if(fscanf(file, "%s %s %s %d", buff1, buff2, buff3, &data1) == EOF)
                return 1;
            is_setup = 1;
            p->points = malloc(sizeof(Point) * data1);
            p->size = data1;
        }
        else if(strcmp(buff1, "q") == 0)
        {
            if(is_setup == 0)
            {
                printf("There is a problem with input data file.\n");
                return 1;
            }
            if(fscanf(file, "%d %d", &data1, &data2) == EOF)
                return 1;
            p->points[counter].source = data1;
            p->points[counter].destination = data2;
            p->points[counter].distance = 0;
            counter++;
            if(counter == p->size)
                break;
        }
    }
    return 0;
}

int createSourcesFileResult(FILE *file, Graph *g, Sources *s, const char* graphFile, const char* sourceFile, double time)
{
    fprintf(file, "p res sp ss %d\n", s->size);
    fprintf(file, "f %s %s\n", graphFile, sourceFile);
    fprintf(file, "g %d %d %d %d\n", g->nodesAmount, g->edgesAmount, g->minArcValue, g->maxArcValue);
    fprintf(file, "t %f", time);
    return 0;
}

int createPointsFileResult(FILE *file, Graph *g, Points *p, const char* graphFile, const char* pointsFile)
{
    fprintf(file, "p res sp p2p %d\n", p->size);
    fprintf(file, "f %s %s\n", graphFile, pointsFile);
    fprintf(file, "g %d %d %d %d\n", g->nodesAmount, g->edgesAmount, g->minArcValue, g->maxArcValue);
    int i;
    for(i=0; i<p->size; i++)
        fprintf(file, "d %d %d %d\n", p->points[i].source, p->points[i].destination, p->points[i].distance);
    return 0;
}

int optionSourcesList(Graph *g, void (*algorithm)(Graph*,int,int), const char *inputFile, const char *resultFile, const char *graphFile)
{
    FILE *file;
    Sources s;
    int i;
    if((file = fopen(inputFile, "r")) == NULL)
    {
        printf("There is no such file: %s\n", inputFile);
        return 1;
    }
    if(readSourcesFile(file, &s) != 0)
    {
        printf("Error occured while reading sources\n");
        return 1;
    }
    fclose(file);
    clock_t sum = 0;
    for(i=0; i<s.size; i++)
    {
        clock_t t = clock();
        algorithm(g, s.sources[i], -1);
        sum += clock() - t;
        resetGraph(g);
    }
    double avgTime = ((double)sum)/(CLOCKS_PER_SEC*s.size);
    if((file = fopen(resultFile, "w")) == NULL)
    {
        printf("Error occured while creating result file: %s\n", resultFile);
        return 1;
    }
    createSourcesFileResult(file, g, &s, graphFile, inputFile, avgTime);
    fclose(file);
    return 0;
}

int optionPointsList(Graph *g, void (*algorithm)(Graph*,int,int), const char *inputFile, const char *resultFile, const char *graphFile)
{
    Points p;
    int i;
    FILE *file;
    if((file = fopen(inputFile, "r")) == NULL)
    {
        printf("There is no such file: %s\n", inputFile);
        return 1;
    }
    if(readPointsFile(file, &p) != 0)
    {
        printf("Error occured while reading sources\n");
        return 1;
    }
    fclose(file);
    for(i=0; i<p.size; i++)
    {
        algorithm(g, p.points[i].source, p.points[i].destination);
        p.points[i].distance = g->nodeList[p.points[i].destination - 1].value;
        resetGraph(g);
    }
    if((file = fopen(resultFile, "w")) == NULL)
    {
        printf("Error occured while creating result file: %s\n", resultFile);
        return 1;
    }
    createPointsFileResult(file, g, &p, graphFile, inputFile);
    fclose(file);
    return 0;
}

int createGraph(Graph *g, const char *graphFile)
{
    FILE *file;
    if((file = fopen(graphFile, "r")) == NULL)
        return 1;
    if(createGraphFromFile(file, g) != 0)
    {
        printf("There was an error while creating graph structure from file\n");
        return 1;
    }
    return 0;
}

int generateResult(int argc, char **argv, void (*algorithm)(Graph*,int,int))
{
    int i;
    const char *graphFile;
    const char *resultFile;
    const char *inputFile;
    int option = 0;
    for(i=0; i<argc; i++)
    {
        if(strcmp(argv[i], "-d") == 0)
        {
            graphFile = argv[i+1];
            i++; continue;
        }
        else if(strcmp(argv[i], "-ss") == 0)
        {
            option = 1;
            inputFile = argv[i+1];
            i++; continue;
        }
        else if(strcmp(argv[i], "-p2p") == 0)
        {
            option = 2;
            inputFile = argv[i+1];
            i++; continue;
        }
        else if(strcmp(argv[i], "-oss") == 0)
        {
            resultFile = argv[i+1];
            i++; continue;
        }
        else if(strcmp(argv[i], "-op2p") == 0)
        {
            resultFile = argv[i+1];
            i++; continue;
        }
    }
    Graph g;
    if(createGraph(&g, graphFile)!=0)
    {
        printf("Error while reading graph file\n");
        return 1;
    }
    switch(option)
    {
        case 1:
            if(optionSourcesList(&g, algorithm, inputFile, resultFile, graphFile) != 0)
            {
                printf("Error while reading sources file or creating result file\n");
                return 1;
            }
        break;
        case 2:
            if(optionPointsList(&g, algorithm, inputFile, resultFile, graphFile) != 0)
            {
                printf("Error while reading points file or creating result file\n");
                return 1;
            }
        break;
        default:
            printf("Wrong options used\n");
            return 1;
    }
    return 0;
}
