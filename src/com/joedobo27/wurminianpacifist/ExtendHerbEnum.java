package com.joedobo27.wurminianpacifist;


import javassist.*;
import javassist.bytecode.*;

import java.util.ArrayList;
import java.util.stream.IntStream;

class ExtendHerbEnum {
    private static ArrayList<ExtendHerbEnum.EnumData> toExtendEntries = new ArrayList<>();
    private static final String className = "com.wurmonline.server.behaviours.Herb";
    private static int valuesSizerIndex= -1; // the bytecode index which puts a size specifying value on the stack for anewarray.
    private static int indexANEWARRAY = -1;
    private static int populateVALUESIndex = -1; // the bytecode index where references to various enum instances are put in the $VALUES array.

    private static ClassPool classPool;
    private static CtClass enumCtClass;
    private static ConstPool enumConstPool;
    private static ClassFile enumClassFile;
    private static CodeAttribute initiatorCodeAttribute;
    private static CodeIterator initiatorCodeIterator;
    private static MethodInfo initiatorMethodInfo;


    ExtendHerbEnum(ClassPool classPool) throws NotFoundException{
        ExtendHerbEnum.classPool = classPool;
        ExtendHerbEnum.enumCtClass = ExtendHerbEnum.classPool.get(className);
        ExtendHerbEnum.enumClassFile = enumCtClass.getClassFile();
        ExtendHerbEnum.enumConstPool = enumClassFile.getConstPool();
        ExtendHerbEnum.initiatorMethodInfo = enumCtClass.getClassInitializer().getMethodInfo();
        ExtendHerbEnum.initiatorCodeAttribute = initiatorMethodInfo.getCodeAttribute();
        ExtendHerbEnum.initiatorCodeIterator = initiatorCodeAttribute.iterator();
    }

    /**
     * A method to create data structures and add record a reference for that object.
     *
     * @param fieldName String type.
     * @param tileType String type. A unique string for tileType, ie: TILE_GRASS. In WU this is a byte and is assigned by fetching
     *                 a field value from Tiles.Tile enum. To avoid initializing the tile class use a string which is the same
     *                 as the enum's field name.
     * @param growthStage String type. In WU this is a GrassData$GrowthStage enum instance. To avoid initializing the GrowthStage class
     *                    use a string which is the same as the enum's field name.
     * @param action int type. See behaviours.Actions for a listing. In WU this is called "category".
     * @param item int type. see item.ItemList for a listing.
     * @param material byte type. if zero, then ItemFactory.createItem() will look up the template.getMaterial().
     * @param chanceAt1Skill int type.
     * @param chanceAt100Skill int type.
     * @param difficultyAt1Skill int type.
     * @param difficultyAt100Skill int type.
     * @param modifiedByCategory String type. ie: NO_TREES. In WU this is a object from behaviours.ModifiedBy enum. To avoid
     *                           initializing the ModifiedBy class use a string which is the same as the enum's field name.
     * @param modifierValue int type. This is a bonus given if modifiedByCategory conditions is true.
     */
    void addExtendEntry(String fieldName, String tileType, String growthStage, short action, int item, byte material,
                        int chanceAt1Skill, int chanceAt100Skill, int difficultyAt1Skill, int difficultyAt100Skill, String modifiedByCategory,
                        int modifierValue){
        ExtendHerbEnum.EnumData enumData = new ExtendHerbEnum.EnumData(fieldName, tileType, growthStage, action, item, material, chanceAt1Skill,
                chanceAt100Skill, difficultyAt1Skill, difficultyAt100Skill, modifiedByCategory, modifierValue);
        toExtendEntries.add(enumData);
    }

    private class EnumData {
        String fieldName;
        String tileType;
        // This is normally a byte field. a object has to be initiated to access the byte id. Instead use string.
        String growthStage;
        // Using a class GrassData$GrowthStage reference will initialize the class.
        short action;
        //see behaviours.Actions. In Herb enum this is called "category".
        int item;
        // see item.ItemList. Aka, itemType.
        byte material;
        // if zero, then ItemFactory.createItem() will look up the template.getMaterial().
        // Further, I'm not sure why this here since every entry for Herb has a material of type 0.
        int chanceAt1Skill;
        int chanceAt100Skill;
        int difficultyAt1Skill;
        int difficultyAt100Skill;
        String modifiedByCategory;
        // Using a class ModifiedBy reference will initialize the class.
        int modifierValue;


        EnumData(String fieldName, String tileType, String growthStage, short action, int item, byte material,
                 int chanceAt1Skill, int chanceAt100Skill, int difficultyAt1Skill, int difficultyAt100Skill, String modifiedByCategory,
                 int modifierValue){
            this.fieldName = fieldName;
            this.tileType = tileType;
            this.growthStage = growthStage;
            this.action = action;
            this.item = item;
            this.material = material;
            this.chanceAt1Skill = chanceAt1Skill;
            this.chanceAt100Skill = chanceAt100Skill;
            this.difficultyAt1Skill = difficultyAt1Skill;
            this.difficultyAt100Skill = difficultyAt100Skill;
            this.modifiedByCategory = modifiedByCategory;
            this.modifierValue = modifierValue;
        }
    }

    /**
     * Intended to be used in WurmServerMod-initiate section and it's for bytecode changes. This adds field objects to the enum class.
     *
     * @throws CannotCompileException forwarded, Javassist stuff.
     */
    static void createFieldsInEnum() throws  CannotCompileException {
        if (toExtendEntries.size() == 0){
            throw new RuntimeException("Can not create fields without values in toExtendEntries arrayList.");
        }

        for (ExtendHerbEnum.EnumData enumData : toExtendEntries) {
            CtField field = new CtField(enumCtClass, enumData.fieldName, enumCtClass);
            field.setModifiers(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL | Modifier.ENUM);
            enumCtClass.addField(field);
        }
    }

    /**
     * Goes through the enum class's initiator to find index positions.
     *
     * @throws BadBytecode forwarded, Javassist stuff.
     */
    private static void initiatorParser() throws BadBytecode {
        CodeIterator codeIterator = initiatorCodeIterator;
        // Get the byte code instruction index for
        // 1) size value for ANEWARRAY,
        // 2) the VALUES array assignment or population.

        int constPoolValuesIndex = BytecodeTools.byteArrayToInt(BytecodeTools.findConstantPoolReference(enumConstPool,
                "// Field com/wurmonline/server/behaviours/Herb.$VALUES:[Lcom/wurmonline/server/behaviours/Herb;"), 2);
        codeIterator.begin();
        int lastIndex = 0;
        while (codeIterator.hasNext()){
            int instructionIndex = codeIterator.next();
            int opCode = codeIterator.byteAt(instructionIndex);
            switch (opCode){
                case Opcode.ANEWARRAY :
                    valuesSizerIndex = lastIndex;
                    indexANEWARRAY = instructionIndex;
                    break;
                case Opcode.PUTSTATIC :
                    int cpAddress = codeIterator.u16bitAt(instructionIndex+1);
                    if (cpAddress == constPoolValuesIndex){
                        populateVALUESIndex = instructionIndex;
                    }
                    break;
                default:
                    break;
            }
            lastIndex = instructionIndex;
        }
        if (valuesSizerIndex == -1 || indexANEWARRAY == -1 || populateVALUESIndex == -1)
            throw new RuntimeException(String.format("bytecode indexing failed: valuesSizerIndex= %d, indexANEWARRAY= %d, populateVALUESIndex= %d",
                    valuesSizerIndex, indexANEWARRAY, populateVALUESIndex));
    }

    /**
     * This method uses JA bytecode to inject into the Enum's class initiator in order to expand the enum's $VALUES field.
     *
     * @param expansion int value, expand the $VALUES field's size this much.
     * @throws BadBytecode forwarded, Javassist stuff.
     */
    private static void resizeEnumVALUES(int expansion) throws BadBytecode, ClassNotFoundException, NotFoundException {
        CodeIterator codeIterator = initiatorCodeIterator;

        int valuesSize = getInteger(codeIterator, valuesSizerIndex);

        //Construct the find and replace Bytecode objects.
        Bytecode find = new Bytecode(enumConstPool);
        putInteger(find, valuesSize);
        // public void addAnewarray(CtClass clazz, int length) may not work as it assumes the size initiator is: "addIconst(length);:
        find.addAnewarray(className);

        Bytecode replace = new Bytecode(enumConstPool);
        putInteger(replace, valuesSize + expansion);
        replace.addAnewarray(className);

        // Find and replace requires equality. Other mods may use this to expand enum so the valuesSize and OpCode for it may vary.
        // Since replace uses found values + expansion it's also uncertain how large the bytecode instruction will be.
        int sizeDifference = find.getSize() - replace.getSize();
        if (sizeDifference < 0){
            // Insert gaps into staticInitiator's code after ANEWARRAY instruction.
            codeIterator.insertGap(indexANEWARRAY + 3, sizeDifference);
            IntStream.range(0, sizeDifference)
                    .forEach(value -> find.addOpcode(Opcode.NOP));

        } else if (sizeDifference > 0) {
            // Pad "replace" with NOP. Since this only expands the VALUES array this probably shouldn't happen.
            // Out comes should be either value "replace" equals what's in ClassInitializer or "replace" is larger because
            // the expansion amount pushed the integer storing bytecode into a larger opCode structure.
            IntStream.range(0, sizeDifference)
                    .forEach(value -> replace.addOpcode(Opcode.NOP));
        }
        codeIterator.write(replace.get(), valuesSizerIndex);
    }

    /**
     * This method builds bytecode to inject into the enum's initiator. The injected code initializes new enum entries and adds
     * a reference of that new object to the $VALUES array.
     *
     * @throws BadBytecode forwarded, JA stuff.
     * @throws ClassNotFoundException forwarded, JA stuff.
     * @throws NotFoundException forwarded, JA stuff.
     */
    static void initiateEnumEntries() throws BadBytecode, ClassNotFoundException, NotFoundException {
        initiatorParser();
        Bytecode enumInitiator = new Bytecode(enumConstPool);
        Bytecode populateVALUES = new Bytecode(enumConstPool);
        int extensionCounter = 0;
        int valuesSize = getInteger(initiatorCodeIterator, valuesSizerIndex);
        // Construct the two bytecode objects to be inserted.
        for (ExtendHerbEnum.EnumData enumData : toExtendEntries) {
            enumInitiator.addNew(enumCtClass);
            enumInitiator.addOpcode(Opcode.DUP);
            enumInitiator.addLdc(enumData.fieldName);
            putInteger(enumInitiator, valuesSize + extensionCounter);
            enumInitiator.addGetstatic("com/wurmonline/mesh/Tiles$Tile", enumData.tileType, "Lcom/wurmonline/mesh/Tiles$Tile;");
            enumInitiator.addGetfield("com/wurmonline/mesh/Tiles$Tile", "id","B");
            enumInitiator.addGetstatic("com/wurmonline/mesh/GrassData$GrowthStage", enumData.growthStage, "Lcom/wurmonline/mesh/GrassData$GrowthStage;");
            putInteger(enumInitiator, enumData.action);
            putInteger(enumInitiator, enumData.item);
            putInteger(enumInitiator, enumData.material);
            putInteger(enumInitiator, enumData.chanceAt1Skill);
            putInteger(enumInitiator, enumData.chanceAt100Skill);
            putInteger(enumInitiator, enumData.difficultyAt1Skill);
            putInteger(enumInitiator, enumData.difficultyAt100Skill);
            enumInitiator.addGetstatic("com/wurmonline/server/behaviours/ModifiedBy", enumData.modifiedByCategory, "Lcom/wurmonline/server/behaviours/ModifiedBy;");
            putInteger(enumInitiator, enumData.modifierValue);
            enumInitiator.addInvokespecial(className, "<init>",
                    "(Ljava/lang/String;IBLcom/wurmonline/mesh/GrassData$GrowthStage;SIBIIIILcom/wurmonline/server/behaviours/ModifiedBy;I)V");
            enumInitiator.addPutstatic(className, enumData.fieldName, "Lcom/wurmonline/server/behaviours/Herb;");

            populateVALUES.addOpcode(Opcode.DUP);
            putInteger(populateVALUES, valuesSize + extensionCounter);
            extensionCounter++;
            populateVALUES.addGetstatic(className, enumData.fieldName, "Lcom/wurmonline/server/behaviours/Herb;");
            populateVALUES.addOpcode(Opcode.AASTORE);
        }

        // Do bytecode changes from the bottom up so bytecode indexes don't change after every insert.
        initiatorCodeIterator.insert(populateVALUESIndex, populateVALUES.get());
        resizeEnumVALUES(toExtendEntries.size());
        initiatorCodeIterator.insert(valuesSizerIndex, enumInitiator.get());
        initiatorMethodInfo.rebuildStackMapIf6(classPool, enumClassFile);

    }

    /**
     * Encode the value for arg "integer" into the appropriate byteCode opCode + operand  for the java-int. Add the
     * encoded information to the byte code object "bytecode".
     *
     * @param bytecode JA bytecode object.
     * @param integer int value.
     */
    private static void putInteger(Bytecode bytecode, int integer) {
        switch (integer) {
            case -1:
                bytecode.add(Opcode.ICONST_M1);
                break;
            case 0:
                bytecode.add(Opcode.ICONST_0);
                break;
            case 1:
                bytecode.add(Opcode.ICONST_1);
                break;
            case 2:
                bytecode.add(Opcode.ICONST_2);
                break;
            case 3:
                bytecode.add(Opcode.ICONST_3);
                break;
            case 4:
                bytecode.add(Opcode.ICONST_4);
                break;
            case 5:
                bytecode.add(Opcode.ICONST_5);
                break;
            default:
                if (integer >= Byte.MIN_VALUE && integer <= Byte.MAX_VALUE) {
                    bytecode.add(Opcode.BIPUSH);
                    // integer bound to byte size.
                    bytecode.add(integer);
                } else if (integer >= Short.MIN_VALUE && integer <= Short.MAX_VALUE) {
                    bytecode.add(Opcode.SIPUSH);
                    // Since byte code requires byte sized blocks, break up integer with bitmask and shift.
                    bytecode.add((integer & 0xff00) >>> 8, integer & 0x00ff);
                } else {
                    // Appends LDC or LDC_W depending on constant pool size.
                    bytecode.addLdc(enumConstPool.addIntegerInfo(integer));
                }
        }
    }

    /**
     * Decode the byte code represented by a opCode + operand(s) at the position in arg "instructionIndex". Return
     * decoded data as java-int.
     *
     * @param codeIterator JA CodeIterator object.
     * @param instructionIndex int value, it is the codeIterator index of an opCode.
     * @return int value.
     */
    private static int getInteger(CodeIterator codeIterator, int instructionIndex) {
        int opCode = codeIterator.byteAt(instructionIndex);
        switch (opCode) {
            case Opcode.ICONST_M1:
                return -1;
            case Opcode.ICONST_0:
                return 0;
            case Opcode.ICONST_1:
                return 1;
            case Opcode.ICONST_2:
                return 2;
            case Opcode.ICONST_3:
                return 3;
            case Opcode.ICONST_4:
                return 4;
            case Opcode.ICONST_5:
                return 5;
            case Opcode.BIPUSH:
                return codeIterator.byteAt(instructionIndex + 1);
            case Opcode.SIPUSH:
                return codeIterator.s16bitAt(instructionIndex + 1);
            case Opcode.LDC:
                return enumConstPool.getIntegerInfo(codeIterator.byteAt(instructionIndex + 1));
            case Opcode.LDC_W:
                return enumConstPool.getIntegerInfo(codeIterator.u16bitAt(instructionIndex + 1));
            default:
                throw new RuntimeException(String.format("Failed to decode integer. Pos = %d, Bytecode = %d", instructionIndex, opCode));
        }
    }
}
