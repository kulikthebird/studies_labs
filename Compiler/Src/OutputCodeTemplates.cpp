#include <OutputCodeTemplates.hpp>


OutputCodeVector OutputCodeTemplates::createConstant(u64 val, u32 result)
{
    OutputCodeVector resultInstr;
    resultInstr.push_back({OutCode::RESET, result});
    u64 new_val = 0;
    u32 i = 0;
    for(;val != 0;i++)
    {
        new_val <<= 1;
        new_val |= val & 0x1;
        val >>= 1;
    }
    for(; i>=0 ; i--)
    {
        if((new_val & 0x1) == 1)
            resultInstr.push_back({OutCode::INC, result});
        new_val >>= 1;
        if(i > 1)
            resultInstr.push_back({OutCode::SHL, result});
    }
    return resultInstr;
}

OutputCodeVector OutputCodeTemplates::buildIfStmtOutputCode(ExpressionType conditionType, u32 ELSE, u32 temporary, u32 in1, u32 in2)
{
    u32 IF_P1, IF_P2;
    variableManager->createLabels({IF_P1, IF_P2});
    switch(conditionType)
    {
    case ExpressionType::EQ:
        return
        {
            {OutCode::COPY, temporary, in1},
            {OutCode::SUB, temporary, in2},
            {OutCode::JZERO, temporary, IF_P1},
            {OutCode::JUMP, ELSE},
            {OutCode::LABEL, IF_P1},
            {OutCode::COPY, temporary, in2},
            {OutCode::SUB, temporary, in1},
            {OutCode::JZERO, temporary, IF_P2},
            {OutCode::JUMP, ELSE},
            {OutCode::LABEL, IF_P2}
        };
    case ExpressionType::LT:
        return
        {
            {OutCode::COPY, temporary, in1},
            {OutCode::SUB, temporary, in2},
            {OutCode::JZERO, temporary, IF_P1},
            {OutCode::JUMP, ELSE},
            {OutCode::LABEL, IF_P1},
            {OutCode::COPY, temporary, in2},
            {OutCode::SUB, temporary, in1},
            {OutCode::JZERO, temporary, ELSE},
        };
    case ExpressionType::GT:
        return
        {
            {OutCode::COPY, temporary, in2},
            {OutCode::SUB, temporary, in1},
            {OutCode::JZERO, temporary, IF_P1},
            {OutCode::JUMP, ELSE},
            {OutCode::LABEL, IF_P1},
            {OutCode::COPY, temporary, in1},
            {OutCode::SUB, temporary, in2},
            {OutCode::JZERO, temporary, ELSE},
        };
    case ExpressionType::LE:
        return
        {
            {OutCode::COPY, temporary, in1},
            {OutCode::SUB, temporary, in2},
            {OutCode::JZERO, temporary, IF_P1},
            {OutCode::JUMP, ELSE},
            {OutCode::LABEL, IF_P1},
        };
    case ExpressionType::GE:
        return
        {
            {OutCode::COPY, temporary, in2},
            {OutCode::SUB, temporary, in1},
            {OutCode::JZERO, temporary, IF_P1},
            {OutCode::JUMP, ELSE},
            {OutCode::LABEL, IF_P1},
        };
    case ExpressionType::NE:
        return
        {
            {OutCode::COPY, temporary, in1},
            {OutCode::SUB, temporary, in2},
            {OutCode::JZERO, temporary, IF_P1},
            {OutCode::JUMP, IF_P2},
            {OutCode::LABEL, IF_P1},
            {OutCode::COPY, temporary, in2},
            {OutCode::SUB, temporary, in1},
            {OutCode::JZERO, temporary, ELSE},
            {OutCode::LABEL, IF_P2},
        };
    }
    return {{}};
}

OutputCodeVector OutputCodeTemplates::addOutputCode(u32 result, u32 in1)
{
    return {{OutCode::ADD, result, in1}};
}

OutputCodeVector OutputCodeTemplates::subOutputCode(u32 result, u32 in1)
{
    return {{OutCode::SUB, result, in1}};
}

OutputCodeVector OutputCodeTemplates::mulOutputCode(u32 result, u32 in1, u32 in2)
{
    u32 OPTION2, OPT1_P1, OPT1_P2, OPT2_P1, OPT2_P2, END;
    variableManager->createLabels({OPTION2, OPT1_P1, OPT1_P2, OPT2_P1, OPT2_P2, END});
    return
    {
        {OutCode::JZERO, in1, END},
        {OutCode::JZERO, in2, END},
        {OutCode::COPY, result, in1},
        {OutCode::SUB, result, in2},
        {OutCode::JZERO, result, OPTION2},
        {OutCode::RESET, result},
        {OutCode::JODD, in2, OPT1_P1},
        {OutCode::JUMP, OPT1_P2},
        {OutCode::LABEL, OPT1_P1},
        {OutCode::ADD, result, in1},
        {OutCode::LABEL, OPT1_P2},
        {OutCode::SHR, in2},
        {OutCode::JZERO, in2, END},
        {OutCode::SHL, in1},
        {OutCode::JODD, in2, OPT1_P1},
        {OutCode::JUMP, OPT1_P2},
        {OutCode::LABEL, OPTION2},
        {OutCode::JODD, in1, OPT2_P1},
        {OutCode::JUMP, OPT2_P2},
        {OutCode::LABEL, OPT2_P1},
        {OutCode::ADD, result, in2},
        {OutCode::LABEL, OPT2_P2},
        {OutCode::SHR, in1},
        {OutCode::JZERO, in1, END},
        {OutCode::SHL, in2},
        {OutCode::JODD, in1, OPT2_P1},
        {OutCode::JUMP, OPT2_P2},
        {OutCode::LABEL, END}
    };
}

OutputCodeVector OutputCodeTemplates::divOutputCode(u32 result, u32 in1, u32 in2, u32 multiple, u32 next_multiple, u32 temporary)
{
    u32 reminder = in1;
    u32 WHILE1, WHILE1_CONDITION1, WHILE2, WHILE2_CONDITION1, WHILE_IF1, WHILE2_IF1, WHILE2_ENDIF1, END;
    variableManager->createLabels({WHILE1, WHILE1_CONDITION1, WHILE2, WHILE2_CONDITION1, WHILE_IF1, WHILE2_IF1, WHILE2_ENDIF1, END});
    return
    {
        {OutCode::RESET, result},
        {OutCode::JZERO, in1, END},
        {OutCode::JZERO, in2, END},
        {OutCode::COPY, next_multiple, in2},
        {OutCode::LABEL, WHILE1},
        {OutCode::COPY, multiple, next_multiple},
        {OutCode::SHL, next_multiple},
        {OutCode::COPY, temporary, next_multiple},
        {OutCode::SUB, temporary, reminder},
        {OutCode::JZERO, temporary, WHILE1_CONDITION1},
        {OutCode::JUMP, WHILE2},
        {OutCode::LABEL, WHILE1_CONDITION1},
        {OutCode::COPY, temporary, next_multiple},
        {OutCode::SUB, temporary, multiple},
        {OutCode::JZERO, temporary, WHILE2},
        {OutCode::JUMP, WHILE1},
        {OutCode::LABEL, WHILE2},
        {OutCode::COPY, temporary, in2},
        {OutCode::SUB, temporary, multiple},
        {OutCode::JZERO, temporary, WHILE2_CONDITION1},
        {OutCode::JUMP, END},
        {OutCode::LABEL, WHILE2_CONDITION1},
        {OutCode::SHL, result},
        {OutCode::COPY, temporary, multiple},
        {OutCode::SUB, temporary, reminder},
        {OutCode::JZERO, temporary, WHILE2_IF1},
        {OutCode::JUMP, WHILE2_ENDIF1},
        {OutCode::LABEL, WHILE2_IF1},
        {OutCode::SUB, reminder, multiple},
        {OutCode::INC, result},
        {OutCode::LABEL, WHILE2_ENDIF1},
        {OutCode::SHR, multiple},
        {OutCode::JUMP, WHILE2},
        {OutCode::LABEL, END}
    };
}

OutputCodeVector OutputCodeTemplates::modOutputCode(u32 in1_result, u32 in2, u32 multiple, u32 next_multiple, u32 temporary)
{
    u32 reminder = in1_result;
    u32 WHILE1, WHILE1_BODY, WHILE2, WHILE2_BODY, WHILE2_IF1, WHILE2_ENDIF1, PRE_END, END;
    variableManager->createLabels({WHILE1, WHILE1_BODY, WHILE2, WHILE2_BODY, WHILE2_IF1, WHILE2_ENDIF1, PRE_END, END});
    return
    {
        {OutCode::JZERO, in1_result, END},
        {OutCode::JZERO, in2, PRE_END},
        {OutCode::COPY, next_multiple, in2},
        {OutCode::LABEL, WHILE1},
        {OutCode::COPY, multiple, next_multiple},
        {OutCode::SHL, next_multiple},
        {OutCode::COPY, temporary, next_multiple},
        {OutCode::SUB, temporary, reminder},
        {OutCode::JZERO, temporary, WHILE1_BODY},
        {OutCode::JUMP, WHILE2},
        {OutCode::LABEL, WHILE1_BODY},
        {OutCode::COPY, temporary, next_multiple},
        {OutCode::SUB, temporary, multiple},
        {OutCode::JZERO, temporary, WHILE2},
        {OutCode::JUMP, WHILE1},
        {OutCode::LABEL, WHILE2},
        {OutCode::COPY, temporary, in2},
        {OutCode::SUB, temporary, multiple},
        {OutCode::JZERO, temporary, WHILE2_BODY},
        {OutCode::JUMP, END},
        {OutCode::LABEL, WHILE2_BODY},
        {OutCode::COPY, temporary, multiple},
        {OutCode::SUB, temporary, reminder},
        {OutCode::JZERO, temporary, WHILE2_IF1},
        {OutCode::JUMP, WHILE2_ENDIF1},
        {OutCode::LABEL, WHILE2_IF1},
        {OutCode::SUB, reminder, multiple},
        {OutCode::LABEL, WHILE2_ENDIF1},
        {OutCode::SHR, multiple},
        {OutCode::JUMP, WHILE2},
        {OutCode::LABEL, PRE_END},
        {OutCode::RESET, in1_result},
        {OutCode::LABEL, END}
    };
}
