package com.Joedobo27.wurminianpacifist;

import com.Joedobo27.common.Common;
import com.wurmonline.server.behaviours.Forage;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import javassist.*;
import javassist.bytecode.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static com.Joedobo27.wurminianpacifist.BytecodeTools.addConstantPoolReference;
import static com.Joedobo27.wurminianpacifist.BytecodeTools.findConstantPoolReference;
import static com.Joedobo27.wurminianpacifist.BytecodeTools.findReplaceCodeIterator;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WurminianPacifistMod implements WurmServerMod, Initable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {

    private static Logger logger;

    private static boolean craftCottonPelt = false;
    private static int towelX = 5;
    private static int towelY = 10;
    private static int towelZ = 10;
    private static int towelGrams = 2000;
    private static float towelDifficulty = 20.0f;
    private static int towelValue = 30000;
    private static boolean toolInWSToBS = false;
    private static int squareGramsInTowel = 1200;
    private static int stringGramsInTowel = 800;
    private static boolean craftCottonBed = false;
    private static boolean redDyeFromMadder = false;
    private static boolean cheeseDrillWithCloth = false;
    private static boolean craftGourdCanteen = false;
    private static boolean waxGourdToFat = false;
    private static boolean craftCottonToolBelt = false;

    private static int towelItemID;
    private static int cottonBedID;
    private static int madderID = 22764;
    private static int nettleTeaID;
    private static int cheeseDrillID;
    private static int waxGourdID = 22761;
    private static int gourdCanteenID;
    private static int waxID;
    private static int cottonToolbeltID;

    static {
        logger = Logger.getLogger(WurminianPacifistMod.class.getName());
        classPool = HookManager.getInstance().getClassPool();
    }


    private static ClassPool classPool;

    @Override
    public void configure(Properties properties) {
        craftCottonPelt = Boolean.valueOf(properties.getProperty("craftCottonPelt", Boolean.toString(craftCottonPelt)));
        towelX = Integer.valueOf(properties.getProperty("towel.x", Integer.toString(towelX)));
        towelY = Integer.valueOf(properties.getProperty("towel.y", Integer.toString(towelY)));
        towelZ = Integer.valueOf(properties.getProperty("towel.z", Integer.toString(towelZ)));
        towelGrams = Integer.valueOf(properties.getProperty("towel.grams", Integer.toString(towelGrams)));
        towelDifficulty = Float.valueOf(properties.getProperty("towel.difficulty", Float.toString(towelDifficulty)));
        towelValue = Integer.valueOf(properties.getProperty("towel.value", Integer.toString(towelValue)));
        squareGramsInTowel = Integer.valueOf(properties.getProperty("towel.squareGramWeight", Integer.toString(squareGramsInTowel)));
        stringGramsInTowel = Integer.valueOf(properties.getProperty("towel.stringGramsWeight", Integer.toString(stringGramsInTowel)));
        toolInWSToBS = Boolean.valueOf(properties.getProperty("toolInWSToBS", Boolean.toString(toolInWSToBS)));
        craftCottonBed = Boolean.valueOf(properties.getProperty("craftCottonBed", Boolean.toString(craftCottonBed)));
        redDyeFromMadder = Boolean.valueOf(properties.getProperty("redDyeFromMadder", Boolean.toString(redDyeFromMadder)));
        cheeseDrillWithCloth = Boolean.valueOf(properties.getProperty("cheeseDrillWithCloth", Boolean.toString(cheeseDrillWithCloth)));
        craftGourdCanteen = Boolean.valueOf(properties.getProperty("craftGourdCanteen", Boolean.toString(craftGourdCanteen)));
        waxGourdToFat = Boolean.valueOf(properties.getProperty("waxGourdToFat", Boolean.toString(waxGourdToFat)));
        craftCottonToolBelt = Boolean.valueOf(properties.getProperty("craftCottonToolBelt", Boolean.toString(craftCottonToolBelt)));
        waxGourdID = Integer.parseInt(properties.getProperty("waxGourdID", Integer.toString(waxGourdID)));
        madderID = Integer.parseInt(properties.getProperty("madderID", Integer.toString(madderID)));
    }

    @Override
    public void onItemTemplatesCreated() {
        if (craftCottonPelt) {
            ItemTemplateBuilder towel = new ItemTemplateBuilder("jdbTowel");
            towelItemID = IdFactory.getIdFor("jdbTowel", IdType.ITEMTEMPLATE);
            towel.name("towel", "towels", "A thick piece of cloth with many looped strings protruding from the surface.");
            towel.size(3);
            //towel.descriptions();
            towel.itemTypes(new short[]{ItemTypes.ITEM_TYPE_CLOTH, ItemTypes.ITEM_TYPE_BULK});
            towel.imageNumber((short) 640);
            towel.behaviourType((short) 1);
            towel.combatDamage(0);
            towel.decayTime(3024000L);
            towel.dimensions(towelX, towelY, towelZ);
            towel.primarySkill(-10);
            //towel.bodySpaces();
            towel.modelName("model.resource.yard.");
            towel.difficulty(towelDifficulty);
            towel.weightGrams(towelGrams);
            towel.material((byte) 17);
            towel.value(towelValue);
            towel.isTraded(true);
            //towel.armourType();
            try {
                towel.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (craftCottonBed) {
            ItemTemplateBuilder cottonBed = new ItemTemplateBuilder("jdbCottonBed");
            cottonBedID = IdFactory.getIdFor("jdbCottonBed", IdType.ITEMTEMPLATE);
            cottonBed.name("Bed", "Beds", "A cosy bed with a thick cotton comforter.");
            cottonBed.size(3);
            //cottonBed.descriptions();
            cottonBed.itemTypes(new short[]{109, 108, 21, 51, 52, 44, 86, 92, 31, 67, 135, 48, 110, 111, 176, 178, 157});
            cottonBed.imageNumber((short) 313);
            cottonBed.behaviourType((short) 1);
            cottonBed.combatDamage(0);
            cottonBed.decayTime(9072000L);
            cottonBed.dimensions(60, 60, 200);
            cottonBed.primarySkill(-10);
            //cottonBed.bodySpaces();
            cottonBed.modelName("model.furniture.bed.standard.");
            cottonBed.difficulty(20.0f);
            cottonBed.weightGrams(40000);
            cottonBed.material((byte) 14);
            cottonBed.value(10000);
            cottonBed.isTraded(true);
            //cottonBed.armourType();
            try {
                cottonBed.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (redDyeFromMadder) {
            // for now, using lovage graphic and MADDER's details.
            ItemTemplateBuilder madder = new ItemTemplateBuilder("jdbMadder");
            madderID = IdFactory.getIdFor("jdbMadder", IdType.ITEMTEMPLATE);
            madder.name("Madder", "Madders", "A plant with vibrant red roots.");
            madder.size(3);
            //madder.descriptions();
            madder.itemTypes(new short[]{46, 146, 164});
            madder.imageNumber((short) 711);
            madder.behaviourType((short) 1);
            madder.combatDamage(0);
            madder.decayTime(9072000L);
            madder.dimensions(3, 3, 3);
            madder.primarySkill(-10);
            //madder.bodySpaces();
            madder.modelName("model.herb.lovage.");
            madder.difficulty(100.0f);
            madder.weightGrams(50);
            madder.material((byte) 22);
            madder.value(100);
            madder.isTraded(true);
            //madder.armourType();
            try {
                madder.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cheeseDrillWithCloth) {
            // 1kg nettle + 1L watter is 1l Nettle tea (which is also a rennet)
            // 0.5L Nettle tea + 8L milk = 0.5 kg cheese. Use cheese's standard item stats.
            ItemTemplateBuilder nettleTea = new ItemTemplateBuilder("jdbNettleTea");
            nettleTeaID = IdFactory.getIdFor("jdbNettleTea",IdType.ITEMTEMPLATE);
            nettleTea.name("nettle tea", "nettle", "A slightly bitter tea of dark green color. It can make milk curdle.");
            nettleTea.size(3);
            //nettleTea.descriptions();
            nettleTea.itemTypes(new short[] { 26, 88, 90 });
            nettleTea.imageNumber((short) 540);
            nettleTea.behaviourType((short) 1);
            nettleTea.combatDamage(0);
            nettleTea.decayTime(86400L);
            nettleTea.dimensions(10, 10, 10);
            nettleTea.primarySkill(-10);
            //nettleTea.bodySpaces();
            nettleTea.modelName("model.liquid.");
            nettleTea.difficulty(1.0f);
            nettleTea.weightGrams(1000);
            nettleTea.material((byte) 26);
            nettleTea.value(0);
            nettleTea.isTraded(false);
            //nettleTea.armourType();
            try {
                nettleTea.build();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ItemTemplateBuilder cheeseDrill = new ItemTemplateBuilder("jdbCheeseDrill");
            cheeseDrillID = IdFactory.getIdFor("jdbCheeseDrill", IdType.ITEMTEMPLATE);
            cheeseDrill.name("cheese drill", "cheese drills", "A wooden press used to compress cheese curds and separate out whey.");
            cheeseDrill.size(3);
            //cheeseDrill.descriptions();
            cheeseDrill.itemTypes(new short[]{108, 44, 144, 38, 21, 92, 147, 51});
            cheeseDrill.imageNumber((short) 266);
            cheeseDrill.behaviourType((short) 1);
            cheeseDrill.combatDamage(0);
            cheeseDrill.decayTime(9072000L);
            cheeseDrill.dimensions(15, 15, 50);
            cheeseDrill.primarySkill(-10);
            //cheeseDrill.bodySpaces();
            cheeseDrill.modelName("model.tool.cheesedrill.");
            cheeseDrill.difficulty(30.0f);
            cheeseDrill.weightGrams(3000);
            cheeseDrill.material((byte) 14);
            cheeseDrill.value(10000);
            cheeseDrill.isTraded(true);
            //cheeseDrill.armourType();
            try {
                cheeseDrill.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (craftCottonToolBelt) {
            ItemTemplateBuilder cottonToolBelt = new ItemTemplateBuilder("jdbCottonToolBelt");
            cottonToolbeltID = IdFactory.getIdFor("jdbCottonToolBelt", IdType.ITEMTEMPLATE);
            cottonToolBelt.name("toolbelt", "toolbelts", "An ingenious system of pockets, pouches, hooks and holes designed to keep a wide array of common tools.");
            cottonToolBelt.size(3);
            //cottonToolBelt.descriptions();
            cottonToolBelt.itemTypes(new short[] { 108, 44, 24, 92, 147, 121, 97 });
            cottonToolBelt.imageNumber((short) 861);
            cottonToolBelt.behaviourType((short) 1);
            cottonToolBelt.combatDamage(0);
            cottonToolBelt.decayTime(3024000L);
            cottonToolBelt.dimensions(2, 5, 10);
            cottonToolBelt.primarySkill(-10);
            cottonToolBelt.bodySpaces(new byte[] { 34, 43 });
            cottonToolBelt.modelName("model.clothing.belt.toolbelt.");
            cottonToolBelt.difficulty(35.0f);
            cottonToolBelt.weightGrams(1000);
            cottonToolBelt.material((byte) 17);
            cottonToolBelt.value(10000);
            cottonToolBelt.isTraded(true);
            //cottonToolBelt.armourType();
            try {
                cottonToolBelt.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (craftGourdCanteen || waxGourdToFat){
            ItemTemplateBuilder waxGourd = new ItemTemplateBuilder("jdbWaxGourd");
            waxGourdID = IdFactory.getIdFor("jdbWaxGourd", IdType.ITEMTEMPLATE);
            waxGourd.name("Wax gourd", "Wax gourds", "A hard shelled gourd with a narrow top and ball shaped bottom. Wax appears to be leaching out around it's stem.");
            waxGourd.size(3);
            //waxGourd.descriptions();
            waxGourd.itemTypes(new short[] { 146, 102, 129 });
            waxGourd.imageNumber((short) 501);
            waxGourd.behaviourType((short) 16);
            waxGourd.combatDamage(0);
            waxGourd.decayTime(28800L);
            waxGourd.dimensions(10, 10, 20);
            waxGourd.primarySkill(-10);
            //waxGourd.bodySpaces();
            waxGourd.modelName("model.food.pumpkin.");
            waxGourd.difficulty(200.0f);
            waxGourd.weightGrams(100);
            waxGourd.material((byte)22);
            waxGourd.value(10);
            waxGourd.isTraded(true);
            //waxGourd.armourType();
            try {
                waxGourd.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ItemTemplateBuilder gourdCanteen = new ItemTemplateBuilder("jdbGourdCanteen");
            gourdCanteenID = IdFactory.getIdFor("jdbGourdCanteen", IdType.ITEMTEMPLATE);
            gourdCanteen.name("Gourd canteen", "Gourd canteens", "A hollowed out gourd for holding liquids.");
            gourdCanteen.size(3);
            //gourdCanteen.descriptions();
            gourdCanteen.itemTypes(new short[] { 108, 44, 21, 1, 33, 147 });
            gourdCanteen.imageNumber((short) 240);
            gourdCanteen.behaviourType((short) 1);
            gourdCanteen.combatDamage(0);
            gourdCanteen.decayTime(3024000L);
            gourdCanteen.dimensions(10, 10, 20);
            gourdCanteen.primarySkill(-10);
            //gourdCanteen.bodySpaces();
            gourdCanteen.modelName("model.tool.waterskin.");
            gourdCanteen.difficulty(20.0f);
            gourdCanteen.weightGrams(100);
            gourdCanteen.material((byte)14);
            gourdCanteen.value(10000);
            gourdCanteen.isTraded(true);
            //gourdCanteen.armourType();
            try {
                gourdCanteen.build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServerStarted() {
        try {
            cottonCreationSubstitutes();
            waxGourdReflection();
            madderReflection();
            toolInWSToBSReflection();
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
                InvocationTargetException e) {
            logger.log(Level.WARNING,e.getMessage(), e);
        }
    }

    @Override
    public void init() {
        try {
            redDyeFromMadderBytecode();
            waxGourdBytecode();

            // Testing tool to make it so a tile can always be foraged or botanized.
            jsAlwaysForage();

        } catch (NotFoundException | CannotCompileException | BadBytecode | ClassNotFoundException e) {
           logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private void jsAlwaysForage() throws NotFoundException, CannotCompileException {

        JAssistClassData server = new JAssistClassData("com.wurmonline.server.Server", classPool);
        JAssistMethodData isForagable = new JAssistMethodData(server, "(II)Z", "isForagable");
        JAssistMethodData isBotanizable = new JAssistMethodData(server, "(II)Z", "isBotanizable");
        isForagable.getCtMethod().setBody("{ return true; }");
        isBotanizable.getCtMethod().setBody("{ return true; }");
    }

    private static void redDyeFromMadderBytecode() throws NotFoundException, BadBytecode, CannotCompileException, ClassNotFoundException {
        if (!redDyeFromMadder)
            return;
        final int[] redDyeFromMadderSuccesses = new int[]{0};

        // In CreationEntry.checkSaneAmounts()
        // Byte code change: this.objectCreated != 73 to this.getObjectCreated() == 73
        // Do this because it's not possible to instrument on a field and have the replace function use a returned value from a hook method.
        JAssistClassData creationEntry = new JAssistClassData("com.wurmonline.server.items.CreationEntry", classPool);
        JAssistMethodData checkSaneAmounts = new JAssistMethodData(creationEntry,
                "(Lcom/wurmonline/server/items/Item;ILcom/wurmonline/server/items/Item;ILcom/wurmonline/server/items/ItemTemplate;Lcom/wurmonline/server/creatures/Creature;Z)V",
                "checkSaneAmounts");

        boolean isModifiedCheckSaneAmounts = true;
        byte[] findPoolResult;
        try {
            //noinspection UnusedAssignment
            findPoolResult = findConstantPoolReference(creationEntry.getConstPool(),
                    "// Method com/Joedobo27/common/Common.checkSaneAmountsExceptionsHook:(III)I");
        } catch (UnsupportedOperationException e) {
            isModifiedCheckSaneAmounts = false;
        }
        if (isModifiedCheckSaneAmounts)
            Arrays.fill(redDyeFromMadderSuccesses, 1);
        if (!isModifiedCheckSaneAmounts) {
            Bytecode find = new Bytecode(creationEntry.getConstPool());
            find.addOpcode(Opcode.ALOAD_0);
            find.addOpcode(Opcode.GETFIELD);
            findPoolResult = findConstantPoolReference(creationEntry.getConstPool(), "// Field objectCreated:I");
            find.add(findPoolResult[0], findPoolResult[1]);
            find.addOpcode(Opcode.BIPUSH);
            find.add(73);
            logger.log(Level.INFO, Arrays.toString(find.get()));

            Bytecode replace = new Bytecode(creationEntry.getConstPool());
            replace.addOpcode(Opcode.ALOAD_0);
            replace.addOpcode(Opcode.INVOKEVIRTUAL);
            findPoolResult = addConstantPoolReference(creationEntry.getConstPool(), "// Method getObjectCreated:()I");
            replace.add(findPoolResult[0], findPoolResult[1]);
            replace.addOpcode(Opcode.BIPUSH);
            replace.add(73);
            logger.log(Level.INFO, Arrays.toString(replace.get()));

            boolean replaceResult = findReplaceCodeIterator(checkSaneAmounts.getCodeIterator(), find, replace);
            redDyeFromMadderSuccesses[0] = replaceResult ? 1 : 0;
            logger.log(Level.FINE, "checkSaneAmounts find and replace: " + Boolean.toString(replaceResult));

            checkSaneAmounts.getCtMethod().instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (Objects.equals("getObjectCreated", methodCall.getMethodName())) {
                        methodCall.replace("$_ = com.Joedobo27.common.Common.checkSaneAmountsExceptionsHook( $0.getObjectCreated(), sourceMax, targetMax);");
                        logger.log(Level.FINE, "CreationEntry.class, checkSaneAmounts(), installed hook at line: " + methodCall.getLineNumber());
                        redDyeFromMadderSuccesses[1] = 1;
                    }
                }
            });
        }


        //<editor-fold desc="Within WurmColor change getCompositeColor()">
        // insert 7 wide gap at line 9.
        // this--if (itemTemplateId == 439) {
        // becomes-- if (itemTemplateId == 439 || itemTemplateID == ??) {
        // Where ?? is a value picked for Madder and inserted with with ConstantPool.addIntegerInfo()
        //</editor-fold>
        JAssistClassData wurmColor = new JAssistClassData("com.wurmonline.server.items.WurmColor", classPool);
        JAssistMethodData getCompositeColor = new JAssistMethodData(wurmColor, "(IIIF)I", "getCompositeColor");

        getCompositeColor.getCodeIterator().insertGap(9, 7);

        Bytecode find = new Bytecode(wurmColor.getConstPool());
        IntStream.range(1,8)
                .forEach(value -> find.addOpcode(Opcode.NOP));

        Bytecode replace = new Bytecode(wurmColor.getConstPool());
        replace.addOpcode(Opcode.IF_ICMPEQ);
        replace.add(0, 10);
        replace.addOpcode(Opcode.ILOAD_2);
        replace.addOpcode(Opcode.LDC);
        findPoolResult = addConstantPoolReference(wurmColor.getConstPool(), "// int " + madderID);
        replace.add(findPoolResult[1]);
        boolean findResult = findReplaceCodeIterator(getCompositeColor.getCodeIterator(), find, replace);
        getCompositeColor.getMethodInfo().rebuildStackMapIf6(classPool, wurmColor.getClassFile());

        // Add fields to herb enum.
        /*
        int fieldNumber
        byte TileType
        GrassData$GrowthStage growthStage
        Short action see behaviours.Actions. In Herb enum this is called "category".
        int item see item.ItemList. Aka, itemType.
        byte material if zero, then ItemFactory.createItem() will look up the template.getMaterial().
             Further, I'm not sure why this here since every entry for herb have a material of type 0.
        int chanceAt1Skill
        int chanceAt100Skill
        int difficultyAt1Skill
        int difficultyAt100Skill
        ModifiedBy modifiedByCategory
        int modifierValue
        */
        ExtendHerbEnum extendHerbEnum = new ExtendHerbEnum(classPool);
        extendHerbEnum.addExtendEntry("GSHORT_MADDER", 1000, "TILE_GRASS", "SHORT",(short) 575, madderID, (byte) 0, 1, 6, 20, 15, "NO_TREES", 10);
        extendHerbEnum.addExtendEntry("GMED_MADDER", 1001, "TILE_GRASS", "MEDIUM", (short) 575, madderID, (byte)0, 1, 10, 20, 10, "NO_TREES", 20);
        extendHerbEnum.addExtendEntry("GTALL_MADDER", 1002, "TILE_GRASS", "TALL", (short) 575, madderID, (byte) 0, 1, 20, 20, 5, "NO_TREES", 30);
        extendHerbEnum.addExtendEntry("GWILD_MADDER",1003, "TILE_GRASS", "WILD", (short) 575, madderID, (byte) 0, 1, 40, 20, 1, "NO_TREES", 40);
        extendHerbEnum.addExtendEntry("STEPPE_MADDER", 1004, "TILE_STEPPE", "SHORT", (short) 575, madderID, (byte) 0, 10, 46, 20, 1, "NO_TREES", 50);
        extendHerbEnum.addExtendEntry("MARSH_MADDER", 1005, "TILE_MARSH", "SHORT", (short) 575, madderID, (byte) 0, 6, 26, 20, 20, "NO_TREES", 20);
        extendHerbEnum.addExtendEntry("MOSS_MADDER", 1006, "TILE_MOSS", "SHORT", (short) 575, madderID, (byte) 0, 6, 26, 20, 20, "NO_TREES", 32);
        extendHerbEnum.addExtendEntry("PEAT_MADDER", 1007,"TILE_PEAT", "SHORT", (short) 575, madderID, (byte) 0, 6, 6, 20, 20, "NO_TREES", 40);
        extendHerbEnum.addExtendEntry("TSHORT_MADDER", 1008, "TILE_TREE", "SHORT", (short) 575, madderID, (byte) 0, 6, 16, 30, 20, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("TMED_MADDER", 1009, "TILE_TREE", "MEDIUM", (short) 575, madderID, (byte) 0, 6, 16, 30, 10, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("TTALL_MADDER", 1010, "TILE_TREE", "TALL", (short) 575, madderID, (byte) 0, 6, 16, 30, 1, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("BSHORT_MADDER", 1011, "TILE_BUSH", "SHORT", (short) 575, madderID, (byte) 0, 6, 16, 20, 10, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("BMED_MADDER", 1012, "TILE_BUSH", "MEDIUM", (short) 575, madderID, (byte) 0, 6, 16, 20, 5, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("BTALL_MADDER", 1013, "TILE_BUSH", "TALL", (short) 575, madderID, (byte) 0, 6, 16, 20, 1, "NOTHING", 0);

        ExtendHerbEnum.createFieldsInEnum();
        ExtendHerbEnum.initiateEnumEntries();
    }

    private static void waxGourdBytecode() throws ClassNotFoundException, NotFoundException, CannotCompileException, BadBytecode {
        if (!craftGourdCanteen && !waxGourdToFat)
            return;
        // Add fields to forage enum.
        ExtendForageEnum extendForageEnum = new ExtendForageEnum(classPool);
        extendForageEnum.addExtendEntry("GSHORT_WAX_GOURD", 1000, "TILE_GRASS", "SHORT", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("GMED_WAX_GOURD", 1001, "TILE_GRASS", "MEDIUM", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("GTALL_WAX_GOURD", 1002, "TILE_GRASS", "TALL", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("GWILD_WAX_GOURD", 1003, "TILE_GRASS", "WILD", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("STEPPE_WAX_GOURD", 1004, "TILE_STEPPE", "SHORT", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("MARSH_WAX_GOURD", 1005, "TILE_MARSH", "SHORT", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("TSHORT_WAX_GOURD", 1006, "TILE_TREE", "SHORT", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("TMED_WAX_GOURD", 1007, "TILE_TREE", "MEDIUM", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("TTALL_WAX_GOURD", 1008, "TILE_TREE", "TALL", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("BSHORT_WAX_GOURD", 1009, "TILE_BUSH", "SHORT", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("BMED_WAX_GOURD", 1010, "TILE_BUSH", "MEDIUM", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("BTALL_WAX_GOURD", 1011, "TILE_BUSH", "TALL", (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);

        ExtendForageEnum.createFieldsInEnum();
        ExtendForageEnum.initiateEnumEntries();
    }

    private static void cottonCreationSubstitutes() {
        if (craftCottonBed) {
            AdvancedCreationEntry bed = CreationEntryCreator.createAdvancedEntry(10044, 482, 483, cottonBedID, false, false, 0.0f, true, true, CreationCategories.FURNITURE);
            bed.addRequirement(new CreationRequirement(1, 485, 1, true));
            bed.addRequirement(new CreationRequirement(4, 486, 4, true));
            bed.addRequirement(new CreationRequirement(10, 144, 10, true));
            logger.log(Level.INFO, "Cotton bed created and a away to craft it added.");
        }
        if (craftCottonPelt) {
            CreationEntry towel = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.clothYard,
                    ItemList.clothString, towelItemID, true, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
            towel.setDepleteFromSource(squareGramsInTowel);
            towel.setDepleteFromTarget(stringGramsInTowel);
            CreationEntry pelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.scissors,
                    towelItemID, ItemList.pelt, false, true, 0.0f, false, false, CreationCategories.TOOLS);
            pelt.setDepleteFromTarget(towelGrams);
            logger.log(Level.INFO, "Cotton Pelt created and a away to craft it added.");
        }

        if (cheeseDrillWithCloth) {
            AdvancedCreationEntry clothCheeseDrill = CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.plank, ItemList.shaft,
                    cheeseDrillID, false, false, 0.0f, true, false, CreationCategories.TOOLS);
            clothCheeseDrill.addRequirement(new CreationRequirement(1, ItemList.plank, 4, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(2, ItemList.sprout, 2, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(3, ItemList.nailsIronSmall, 1, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(4, ItemList.clothYard, 2, true));
            logger.log(Level.INFO, "CheeseDrill from cloth squares added and a way to craft it");
        }
        if (craftCottonToolBelt) {
            CreationEntry cottonToolBelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.metalHooks, ItemList.clothYard,
                    cottonToolbeltID, true, true, 0.0f, false, false, CreationCategories.CLOTHES);
            cottonToolBelt.setDepleteFromTarget(1500);
        }
    }

    private static void waxGourdReflection() throws  ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    InvocationTargetException, NoSuchFieldException {
        if (!craftGourdCanteen && !waxGourdToFat)
            return;
        Class forageClass = Class.forName("com.wurmonline.server.behaviours.Forage");
        Forage[] values = ReflectionUtil.getPrivateField(forageClass, ReflectionUtil.getField(
                forageClass, "$VALUES"));

        for (Forage f : Forage.values()){
            String name = f == null ? "null" : f.name();
            logger.log(Level.INFO, String.format("name %s", name));
        }

        if (waxGourdToFat) {

        }
        if (craftGourdCanteen) {
            CreationEntry gourdCanteen = CreationEntryCreator.createSimpleEntry(SkillList.BUTCHERING, ItemList.knifeCarving, waxGourdID,
                    gourdCanteenID, false, true, 0.0f, false, false, CreationCategories.CONTAINER);
        }
    }


    private static void madderReflection() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    InvocationTargetException, NoSuchFieldException {
        if (redDyeFromMadder) {
            // Add red dye making recipe.
            CreationEntry redMadder = CreationEntryCreator.createSimpleEntry(SkillList.ALCHEMY_NATURAL, ItemList.water,
                    madderID, ItemList.dyeRed, true, true, 0.0f, false, false, CreationCategories.DYES);
            int[] exceptions = {madderID, ItemList.dyeRed, ItemList.dye, ItemList.lye};
            Common.addExceptions(exceptions);
        }
    }

    private static void toolInWSToBSReflection() throws NoSuchFieldException, IllegalAccessException {
        if (!toolInWSToBS)
            return;
        ArrayList<Integer> created = new ArrayList<>(Arrays.asList(ItemList.knifeButchering, ItemList.knifeBladeButchering,
                ItemList.knifeCarving, ItemList.knifeBladeCarving, ItemList.sickle, ItemList.sickleBlade,
                ItemList.scythe, ItemList.scytheBlade));
        ArrayList<Integer> targets = new ArrayList<>(Arrays.asList(ItemList.knifeBladeButchering, ItemList.knifeBladeCarving, ItemList.scytheBlade,
                ItemList.sickleBlade, ItemList.ironBar, ItemList.steelBar, ItemList.adamantineBar, ItemList.glimmerSteelBar, ItemList.seryllBar));
        Map<Integer, List<CreationEntry>> simpleEntries = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "simpleEntries"));
        Map<Integer, List<CreationEntry>> matrix = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "matrix"));

        //remove all simpleEntries matching the created items.
        HashMap<Integer, List<CreationEntry>> toDeleteSimple = new HashMap<>();
        simpleEntries.entrySet()
                .stream()
                .filter(simpleEntries1 -> created.contains(simpleEntries1.getKey()))
                .forEach(simpleEntries1 -> toDeleteSimple.put(simpleEntries1.getKey(), simpleEntries1.getValue()));

        toDeleteSimple.entrySet()
                .forEach(toDeleteSimple1 -> simpleEntries.remove(toDeleteSimple1.getKey(), toDeleteSimple1.getValue()));

        // Remove all matrix entries (which is a Map<int, List<CreationEntry>> objects) for the combination of target and created. Only those that match both can be removed.
        // For example, many items have ironBar as as a target but we must only remove those that also match created.
        HashMap<Integer, List<CreationEntry>> toDeleteMatrix = new HashMap<>();

        matrix.entrySet()
                .stream()
                .filter(matrix1 -> targets.contains(matrix1.getKey())) // Is matrix entries's key in targets list?
                .forEach(matrix1 -> {   // Is matrix key in toDeleteMatrix, else add it along with a blank List.
                    if (!toDeleteMatrix.containsKey(matrix1.getKey())){
                        toDeleteMatrix.put(matrix1.getKey(), new ArrayList<>());
                    }
                    matrix1.getValue().stream()
                            .filter(entry -> created.contains(entry.getObjectCreated())) // Is matrix[key]'s value in created list?
                            .forEach(entry -> toDeleteMatrix.get(matrix1.getKey()).add(entry) // Add value to toDeleteMatrix, should only be one left.
                            );
                });

        toDeleteMatrix.entrySet()
                .forEach(toDeleteMatrix1 -> { // cycle over all Maps<int, list>
                    toDeleteMatrix1.getValue().forEach(entry -> {
                        // cycle over the list in for Map's value.
                        if (matrix.get(toDeleteMatrix1.getKey()).contains(entry)) {
                            // remove all key,value matches from matrix.
                            matrix.get(toDeleteMatrix1.getKey()).remove(entry);
                        }
                    });
                });

        /*
        for (HashMap.Entry<Integer, List<CreationEntry>> matrix1 : matrix.entrySet()) {
            if (targets.contains(matrix1.getKey())) {
                if (!toDeleteMatrix.containsKey(matrix1.getKey())) {
                    entries = new ArrayList<>();
                    toDeleteMatrix.put(matrix1.getKey(), entries);
                }

                matrix1.getValue().stream()
                        .filter(entry -> created.contains(entry.getObjectCreated()))
                        .forEach(entry -> toDeleteMatrix.get(matrix1.getKey()).add(entry)
                        );
            }
        }

        for (HashMap.Entry<Integer, List<CreationEntry>> delete : toDeleteMatrix.entrySet()) {
            matrix.get(delete.getKey());
            for (CreationEntry entry : delete.getValue()) {
                if (matrix.get(delete.getKey()).contains(entry)) {
                    matrix.get(delete.getKey()).remove(entry);
                }
            }
        }
        */

        /*
        knifeButchering createSimpleEntry(1016, 99, 125, 93, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        createSimpleEntry(10010, 64, 46, 125, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        carver createSimpleEntry(1016, 99, 126, 8, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        carver blade createSimpleEntry(10010, 64, 46, 126, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        scythe createSimpleEntry(1016, 23, 270, 268, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        scythe blade createSimpleEntry(10010, 185, 46, 270, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        sickle createSimpleEntry(1016, 99, 269, 267, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        sickle blade  createSimpleEntry(10010, 185, 46, 269, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        */

        CreationEntry knifeButchering = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.woodenHandleSword,
                ItemList.knifeBladeButchering, ItemList.knifeButchering, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        CreationEntry knifeBladeButcheringIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.ironBar, ItemList.knifeBladeButchering, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeButcheringSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.steelBar, ItemList.knifeBladeButchering, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeButcheringAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.adamantineBar, ItemList.knifeBladeButchering, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeButcheringGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.glimmerSteelBar, ItemList.knifeBladeButchering, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeButcheringSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.seryllBar, ItemList.knifeBladeButchering, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);

        CreationEntry KnifeCarving = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.woodenHandleSword,
                ItemList.knifeBladeCarving, ItemList.knifeCarving, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        CreationEntry knifeBladeCarvingIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.ironBar, ItemList.knifeBladeCarving, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeCarvingSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.steelBar, ItemList.knifeBladeCarving, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeCarvingAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.adamantineBar, ItemList.knifeBladeCarving, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeCarvingGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.glimmerSteelBar, ItemList.knifeBladeCarving, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry knifeBladeCarvingSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilSmall,
                ItemList.seryllBar, ItemList.knifeBladeCarving, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);

        CreationEntry scythe = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.shaft,
                ItemList.scytheBlade, ItemList.scythe, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        CreationEntry scytheBladeIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.ironBar, ItemList.scytheBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry scytheBladeSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.steelBar, ItemList.scytheBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry scythebladeAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.adamantineBar, ItemList.scytheBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry scythebladeGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.glimmerSteelBar, ItemList.scytheBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry scytheBladeSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.seryllBar, ItemList.scytheBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);

        CreationEntry sickle = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.woodenHandleSword,
                ItemList.sickleBlade, ItemList.sickle, true, true, 0.0f, false, false, CreationCategories.TOOLS);
        CreationEntry sickleBladeIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.ironBar, ItemList.sickleBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry sickleBladeSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.steelBar, ItemList.sickleBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry sickleBladeAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.adamantineBar, ItemList.sickleBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry sickleBladeGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.glimmerSteelBar, ItemList.sickleBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);
        CreationEntry sickleBladeSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING, ItemList.anvilLarge,
                ItemList.seryllBar, ItemList.sickleBlade, false, true, 0.0f, false, false, CreationCategories.TOOL_PARTS);

        logger.log(Level.INFO, "Tools crafted with WS switch over to BS");

    }


    /*
    private static void addWaxGourdForageReflection() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException {

        ArrayList forageEntries = ReflectionUtil.getPrivateField(aaaJoeCommon.forageDataClazz, ReflectionUtil.getField(aaaJoeCommon.forageDataClazz, "forageEntries"));
        Constructor forageDataIni = aaaJoeCommon.forageDataClazz.getConstructor(String.class, Integer.TYPE, Byte.TYPE, GrassData.GrowthStage.class,
                Short.TYPE, Integer.TYPE, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ModifiedBy.class,
                Integer.TYPE);
        logger.log(Level.INFO, "Clazz " + aaaJoeCommon.forageDataClazz.getName());
        logger.log(Level.INFO, "Const " + forageDataIni.toString());
        forageEntries.add(forageDataIni.newInstance("GSHORT_WAX_GOURD", 1000, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("GMED_WAX_GOURD", 1001, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("GTALL_WAX_GOURD", 1002, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("GWILD_WAX_GOURD", 1003, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("STEPPE_WAX_GOURD", 1004, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("MARSH_WAX_GOURD", 1005, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("TSHORT_WAX_GOURD", 1006, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("TMED_WAX_GOURD", 1007, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("TTALL_WAX_GOURD", 1008, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("BSHORT_WAX_GOURD", 1009, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("BMED_WAX_GOURD", 1010, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
        forageEntries.add(forageDataIni.newInstance("BTALL_WAX_GOURD", 1011, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 570, waxGourdID, (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
    }

    @SuppressWarnings("unchecked")
    private static void addMadderHerbReflection() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ArrayList herbEntries = ReflectionUtil.getPrivateField(aaaJoeCommon.herbDataClazz, ReflectionUtil.getField(aaaJoeCommon.herbDataClazz, "herbEntries"));
        Constructor herbDataIni = aaaJoeCommon.herbDataClazz.getConstructor(String.class, Integer.TYPE, Byte.TYPE, GrassData.GrowthStage.class,
                Short.TYPE, Integer.TYPE, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ModifiedBy.class,
                Integer.TYPE);
        herbEntries.add(herbDataIni.newInstance("GSHORT_MADDER", 1000, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 1, 6, 20, 15, ModifiedBy.NO_TREES, 10));
        herbEntries.add(herbDataIni.newInstance("GMED_MADDER", 1001, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 575, madderID, (byte) 0, 1, 10, 20, 10, ModifiedBy.NO_TREES, 20));
        herbEntries.add(herbDataIni.newInstance("GTALL_MADDER", 1002, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 575, madderID, (byte) 0, 1, 20, 20, 5, ModifiedBy.NO_TREES, 30));
        herbEntries.add(herbDataIni.newInstance("GWILD_MADDER", 1003, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 575, madderID, (byte) 0, 1, 40, 20, 1, ModifiedBy.NO_TREES, 40));
        herbEntries.add(herbDataIni.newInstance("STEPPE_MADDER", 1004, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 10, 46, 20, 1, ModifiedBy.NO_TREES, 50));
        herbEntries.add(herbDataIni.newInstance("MARSH_MADDER", 1005, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 6, 26, 20, 20, ModifiedBy.NO_TREES, 20));
        herbEntries.add(herbDataIni.newInstance("MOSS_MADDER", 1006, Tiles.Tile.TILE_MOSS.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 6, 26, 20, 20, ModifiedBy.NO_TREES, 32));
        herbEntries.add(herbDataIni.newInstance("PEAT_MADDER", 1007, Tiles.Tile.TILE_PEAT.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 6, 6, 20, 20, ModifiedBy.NO_TREES, 40));
        herbEntries.add(herbDataIni.newInstance("TSHORT_MADDER", 1008, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 6, 16, 30, 20, ModifiedBy.NOTHING, 0));
        herbEntries.add(herbDataIni.newInstance("TMED_MADDER", 1009, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 575, madderID, (byte) 0, 6, 16, 30, 10, ModifiedBy.NOTHING, 0));
        herbEntries.add(herbDataIni.newInstance("TTALL_MADDER", 1010, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 575, madderID, (byte) 0, 6, 16, 30, 1, ModifiedBy.NOTHING, 0));
        herbEntries.add(herbDataIni.newInstance("BSHORT_MADDER", 1011, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 575, madderID, (byte) 0, 6, 16, 20, 10, ModifiedBy.NOTHING, 0));
        herbEntries.add(herbDataIni.newInstance("BMED_MADDER", 1012, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 575, madderID, (byte) 0, 6, 16, 20, 5, ModifiedBy.NOTHING, 0));
        herbEntries.add(herbDataIni.newInstance("BTALL_MADDER", 1013, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 575, madderID, (byte) 0, 6, 16, 20, 1, ModifiedBy.NOTHING, 0));
    }
    */
}
