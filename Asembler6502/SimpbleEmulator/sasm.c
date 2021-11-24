#include <stdlib.h>
#include <stdio.h>

char*   instruction[] = {"ins", "load", "store", "add", "subt", "input", "output",
                            "halt", "skipcond", "jump", "clear", "addi", "jumpi", "loadi", "hex"};
int     last_instr = 0;
FILE*   source;
FILE*   obx;

char* src;
char* dst;

char out[2] = {0, 0};
int line    = 1;
int pc      = 0x100;

char program[8192];
int prog_count = 0;

char*  labels[1024];
int    labels_addr[1024];
int    lab_count = 0;

struct __label
{
    int pc;
    int type;
}label[8192];
int meta_addr_count = 0;

int openFileR(char*);
int openFileW(char*);
int transtaleSource();
int printHelp();

/******************************************************/
/******************************************************/

int main(int argc, char *argv[])
{
    dst = "out.obx";
    if(argc < 2)
    {
        printHelp();
        return 0;
    }
    openFileR(argv[1]);
    src = argv[1];
    translateSource();
    fclose(source);
    fclose(obx);
    return 0;
}

/******************************************************/
/******************************************************/
//
// Definitions
//
//

void error(int type)
{
    printf("Error in line %d:  ", line);
    switch(type)
    {
        case 0:
            printf("Argument expected. \n");
        break;
        case 1:
            printf("Instruction expected. \n");
        break;
        case 2:
            printf("Wrong label. \n");
        break;
        case 3:
            printf("Argument is too big (expected maximum 12-bit argrument). \n");
        break;
    }
}

int openFileR(char* file)
{
    source = fopen(file, "r");
    if(source == NULL)
        return 1;
    return 0;
}

int openFileW(char* file)
{
    obx = fopen(file, "w");
    if(obx == NULL)
        return 1;
    return 0;
}
/******************************************************/
/******************************************************/

int addLabel(char* label)
{
    char* temp = malloc(sizeof(char)*(strlen(label) + 1));
    strcpy(temp, label);
    labels[lab_count] = temp;
    lab_count++;
    return 1;
}

int findLabels()
{
    char* ins = calloc(64, sizeof(char));
    int counter = 0;
    int byte;
    while(1)
    {
        byte = fgetc(source);
        if(byte == ':')
        {
            ins[counter] = 0;
            addLabel(ins);
            counter = 0;
        }
        else if(byte != ' ' && byte != '\n'&& byte != '\t' && byte != EOF)
        {
            ins[counter] = (char) byte;
            counter++;
        }
        else if(byte == EOF)
                break;
        else
        {
            counter = 0;
        }
    }
}

/******************************************************/
/******************************************************/

int addStandardHeader()
{
    char str[] = {0xFF, 0xFF, 0x00, 0x01, 0x00, 0x00};
    fwrite(str, sizeof(char), 6, obx);
    return 0;
}

int newInstruction(char* ins)
{
    out[0] = 0;
    out[1] = 0;
    int i;
    last_instr = -1;
    for(i=0; i<15; i++)
    {
        if(strcmp(ins, instruction[i]) == 0)
        {
            last_instr = i;
            break;
        }
    }
    if(last_instr == -1)
        return 3;

    out[1] |= (char) (last_instr*16);
    if((last_instr >= 5 && last_instr <= 7) || last_instr == 10)
    {
        pc++;
        return 1;
    }
    return 0;
}

int newArgument(char* arg)
{
    int i;
    int out_arg = -1;

    for(i=0; i<lab_count; i++)
    {
        if(strcmp(arg, labels[i]) == 0)
        {
            label[meta_addr_count].type = i;
            label[meta_addr_count].pc = pc;
            out_arg = 0;
            meta_addr_count++;
            break;
        }
    }
    if(out_arg == -1)
    {
        if((out[1] & 0xF0) == 0xE0)
        {
            out[0] = 0;
            out[1] = 0;
        }
        else if(out_arg > 0xFFF)
            return 3;
        int len = strlen(arg);
        out_arg = 0;
        for(i=0; i<len; i++)
        {
            out_arg *= 16;
            if(arg[i] >= 'A' && arg[i] <= 'F')
            {
                out_arg += arg[i] - 'A' + 10;
            }
            else if(arg[i] >= '0' && arg[i] <= '9')
            {
                out_arg += arg[i] - '0';
            }
            else return 3;
        }
    }
    out[0] = (char)(out_arg & 0xFF);
    out[1] |= (char)((out_arg & 0xFF00) >> 8);

    pc += 1;

    return 1;
}

int addLabelPC(char* arg)
{
    int i;
    for(i=0; i<lab_count; i++)
    {
        if(strcmp(arg, labels[i]) == 0)
        {
            labels_addr[i] = pc;
            break;
        }
    }
    return 1;
}

void putOn(char byte)
{
    program[prog_count] = byte;
    prog_count++;
}

int translateLabelsToPC()
{
    int i;
    int new_addr, x;
    for(i=0; i<meta_addr_count; i++)
    {
        new_addr = labels_addr[label[i].type];
        x = (label[i].pc - 0x100) << 1;
        printf(" < %d ; %X > ", x, new_addr);
        program[x] = (unsigned char)(new_addr & 0xFF);
        program[x+1] |= (unsigned char)((new_addr & 0xF00) >> 8);
    }
}

int writeFile()
{
    printf("Creating file: \"%s\"\n", dst);
    openFileW(dst);
    addStandardHeader();
    fwrite(program, sizeof(char), prog_count, obx);
    return 0;
}

int translateSource()
{
    printf("Translating file \"%s\" \n", src, dst);
    findLabels();
    rewind(source);
    char* ins = calloc(64, sizeof(char));
    int counter = 0;
    int byte;
    char isInstr = 1;
    while(1)
    {
        byte = fgetc(source);

        if(byte == ':' && isInstr == 1)
        {
            ins[counter] = 0;
            isInstr = addLabelPC(ins);
            if(isInstr == 3)
            {
                error(2);
                return 0xff;
            }
            counter = 0;
        }
        else if(byte != ' ' && byte != '\n' && byte != '\t' && byte != EOF)
        {
            ins[counter] = (char) byte;
            counter++;
        }
        else if(counter != 0)
        {
            ins[counter] = 0;
            if(isInstr == 1)
            {
                if(pc != 0x100)
                {
                    putOn(out[0]);
                    putOn(out[1]);
                }
                isInstr = newInstruction(ins);
                if(isInstr == 3)
                {
                    error(1);
                    return 0xff;
                }
            }
            else
            {
                isInstr = newArgument(ins);
                if(isInstr > 1)
                {
                    if(isInstr == 3)
                        error(0);
                    else if(isInstr == 4)
                        error(4);
                    return 0xff;
                }
            }
            if(byte == EOF)
                break;
            counter = 0;
        }
        if(byte == '\n')
            line++;
    }
    putOn(out[0]);
    putOn(out[1]);

    translateLabelsToPC();
    writeFile();
}

int printHelp()
{
    printf("TODO: Make an instruction! \n");
    return 0;
}
