package project.records;

import project.constants.OperandType;

public record InstructionDefinition(
        String opcode,
        OperandType operandType
) {}