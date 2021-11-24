#include <stdlib.h>
#include <stdio.h>

int loadProgram(char*);
int emulate();
int printHelp();
void error(int);

unsigned char memory[8192];
int prog_counter = 0;

char*   instruction[] = {"ins", "load", "store", "add", "subt", "input", "output",
                            "halt", "skipcond", "jump", "clear", "addi", "jumpi", "loadi"};

int debug = 0;

enum Instruction_set
{
    INS, LOAD, STORE, ADD, SUBT, INPUT, OUTPUT, HALT, SKIPCOND, JUMP, CLEAR, ADDI, JUMPI, LOADI
} instr;

unsigned int ac, mbr, mar, pc, ir;
unsigned int InREG, OutREG;

FILE* obx;
char* file_path;

/******************************************************/
/******************************************************/

int main(int argc, char* argv[])
{
    int err;
    if(argc < 2)
    {
        printHelp();
        return 0;
    }
    else
    {
        file_path = argv[1];
        err = loadProgram(file_path);
        if(err == 0xFF)
        {
            error(0);
            return 1;
        }
        else if(err == 1)
        {
            error(1);
            return 1;
        }
        if(argc > 2)
        {
            if(strcmp(argv[2], "-d") == 0)
                debug = 1;
        }
        emulate();
    }
    return 0;
}

/******************************************************/
/******************************************************/

int printHelp()
{
    printf("TODO: make an instruction. \n");
    return 0;
}

void error(int type)
{
    if(type > 1)
        printf("Error in addres: %04X -> ", pc);
    switch(type)
    {
    case 0:
        printf("Could not load obx file: %s\n", file_path);
    break;
    case 1:
        printf("The file \"%s\" is not SASM obx file! \n", file_path);
    break;
    case 2:
        printf("Wrong instruction code! \n");
    break;
    case 3:
        printf("Wrong skipcond argument! \n");
    break;
    default:
        printf("Unknown error! \n");
    }
}

int loadProgram(char* path)
{
    obx = fopen(path, "r");
    int byte;
    if(obx == NULL)
        return 0xFF;
    unsigned char header[6];
    fread(header, sizeof(char), 6, obx);
    if(header[0] != (unsigned char)0xFF || header[1] != (unsigned char)0xFF)
        return 1;
    pc += (int)header[3];
    pc <<= 8;
    pc += (int)header[2];
    prog_counter = pc*2;

    while(1)
    {
        byte = fgetc(obx);
        if(byte == EOF)
            break;
        else
        {
            memory[prog_counter] = byte;
            prog_counter++;
        }
    }

    printf("File \"%s\" has been loaded. \n", file_path);
    return 0;
}

/******************************************************/
/******************************************************/
/** Emulator' part **/

int debugger(int instr, int arg)
{
    printf("AC:%04X IR:%04X MBR:%04X MAR:%04X InREG:%04X OutREG:%04X   PC: [%04X]: %9s %04X \n", ac, ir, mbr, mar, InREG, OutREG, pc, instruction[instr], arg);
    return 0;
}

int mem(int mar)
{
    return (memory[mar*2 + 1] << 8) + memory[mar*2];
}

void write2mem(unsigned int data)
{
    memory[mar*2 + 1] = data >> 8;
    memory[mar*2] = data & 0xFF;
}

int emulate()
{
    unsigned int arg = 0;
    while(1)
    {

        if(pc == 0x021)
        {
            printf("%c", OutREG);
            mar = 0x020;
            pc = mem(mar);
        }

        /** Fetch an instruction **/
        mar = pc;
        mbr = mem(mar);
        ir = mbr;

        instr = ir>>12;
        arg = ir & 0xFFF;

        if(debug == 1)
            debugger(instr, arg);

        pc++;

        /** Translate the instruction */

        switch(instr)
        {
            case INS:
                mbr = pc;
                mar = arg;
                write2mem(mbr);
                pc  = arg + 1;
            break;
            case LOAD:
                mar = arg;
                mbr = mem(mar);
                ac  = mbr;
            break;
            case STORE:
                mar = arg;
                mbr = ac;
                write2mem(mbr);
            break;
            case ADD:
                mar = arg;
                mbr = mem(mar);
                ac  = ac + mbr;
                ac &= 0xFFFF;
            break;
            case SUBT:
                mar = arg;
                mbr = mem(mar);
                ac  = ac - mbr;
                ac &= 0xFFFF;
            break;
            case INPUT:
                ac = InREG;
            break;
            case OUTPUT:
                OutREG = ac;
            break;
            case HALT:
                return 0;
            break;
            case SKIPCOND:
                if(arg == 0x000)
                {
                    if(ac < 0)
                        pc++;
                }
                else if(arg == 0x400)
                {
                    if(ac == 0)
                        pc++;
                }
                else if(arg == 0x800)
                {
                    if(ac > 0)
                        pc++;
                }
                else
                {
                    error(3);
                    return 1;
                }
            break;
            case JUMP:
                pc = arg;
            break;
            case CLEAR:
                ac = 0;
            break;
            case ADDI:
                mar = arg;
                mbr = mem(mar);
                mar = mbr;
                mbr = mem(mar);
                ac = ac + mbr;
            break;
            case JUMPI:
                mar = arg;
                mbr = mem(mar);
                pc = mbr;
            break;
            case LOADI:
                ac = arg;
            break;
            default:
                error(2);
                return 1;
            break;
        }
    }
    return 0;
}
