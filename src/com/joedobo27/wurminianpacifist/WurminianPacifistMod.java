package com.joedobo27.wurminianpacifist;

import com.joedobo27.common.Common;
import com.wurmonline.server.behaviours.Forage;
import com.wurmonline.server.behaviours.Herb;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import javassist.*;
import javassist.Modifier;
import javassist.bytecode.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.CodeReplacer;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.joedobo27.wurminianpacifist.BytecodeTools.addConstantPoolReference;
import static com.joedobo27.wurminianpacifist.BytecodeTools.findConstantPoolReference;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WurminianPacifistMod implements WurmServerMod, Initable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {

    private static Logger logger;

    private static boolean craftCottonPelt = false;
    private static boolean cheeseDrillWithCloth = false;
    private static boolean craftCottonBed = false;
    private static boolean craftCottonToolBelt = false;
    private static boolean toolInWSToBS = false;
    private static boolean redDyeFromMadder = false;
    private static boolean craftGourdCanteen = false;
    private static boolean waxGourdToFat = false;
    private static boolean enableEssenceSystem = false;
    private static float gemQualityPer = 5.0f;
    private static int baseActionTime = 50; // tenths of a second.
    private static float alembicDifficulty = 70.0f;


    private static int cottonBedTemplateId;
    private static int madderTemplateId = Integer.MAX_VALUE - 8;
    private static int cheeseDrillTemplateId;
    private static int waxGourdTemplateId = Integer.MAX_VALUE - 8;
    private static int gourdCanteenTemplateId;
    private static int cottonToolBeltTemplateId;
    private static int dullGooTemplateId;
    private static int essenceTemplateId;
    private static int clayAlembicTemplateId;
    private static int potteryAlembicTemplateId;

    static {
        logger = Logger.getLogger(WurminianPacifistMod.class.getName());
        classPool = HookManager.getInstance().getClassPool();
    }


    private static ClassPool classPool;

    @Override
    public void configure(Properties properties) {
        craftCottonPelt = Boolean.parseBoolean(properties.getProperty("craftCottonPelt", Boolean.toString(craftCottonPelt)));
        cheeseDrillWithCloth = Boolean.parseBoolean(properties.getProperty("cheeseDrillWithCloth", Boolean.toString(cheeseDrillWithCloth)));
        craftCottonBed = Boolean.parseBoolean(properties.getProperty("craftCottonBed", Boolean.toString(craftCottonBed)));
        craftCottonToolBelt = Boolean.parseBoolean(properties.getProperty("craftCottonToolBelt", Boolean.toString(craftCottonToolBelt)));
        toolInWSToBS = Boolean.parseBoolean(properties.getProperty("toolInWSToBS", Boolean.toString(toolInWSToBS)));
        redDyeFromMadder = Boolean.parseBoolean(properties.getProperty("redDyeFromMadder", Boolean.toString(redDyeFromMadder)));
        craftGourdCanteen = Boolean.parseBoolean(properties.getProperty("craftGourdCanteen", Boolean.toString(craftGourdCanteen)));
        waxGourdToFat = Boolean.parseBoolean(properties.getProperty("waxGourdToFat", Boolean.toString(waxGourdToFat)));
        enableEssenceSystem = Boolean.parseBoolean(properties.getProperty("enableEssenceSystem", Boolean.toString(enableEssenceSystem)));
        gemQualityPer = Float.parseFloat(properties.getProperty("gemQualityPer", Float.toString(gemQualityPer)));
        baseActionTime = Integer.parseInt(properties.getProperty("baseActionTime", Integer.toString(baseActionTime)));
    }

    @Override
    public void init() {
        try {
            ModActions.init();
            redDyeFromMadderBytecode();
            waxGourdBytecode();
            // Testing tool to make it so a tile can always be foraged or botanized.
            jsAlwaysForage();

        } catch (NotFoundException | CannotCompileException | BadBytecode | ClassNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void onItemTemplatesCreated() {
        if (enableEssenceSystem) {
            ItemTemplateBuilder dullGoo = new ItemTemplateBuilder("jdbDullGoo");
            dullGooTemplateId = IdFactory.getIdFor("jdbDullGoo", IdType.ITEMTEMPLATE);
            dullGoo.name("Dull goo", "Dull goo", "It's gooey.");
            dullGoo.size(3);
            //cottonBed.descriptions();
            dullGoo.itemTypes(new short[]{6, 146});
            dullGoo.imageNumber((short) 588);
            dullGoo.behaviourType((short) 1);
            dullGoo.combatDamage(0);
            dullGoo.decayTime(9072000L);
            dullGoo.dimensions(1, 10, 10);
            dullGoo.primarySkill(-10);
            //cottonBed.bodySpaces();
            dullGoo.modelName("model.liquid.transmutation.");
            dullGoo.difficulty(40.0f);
            dullGoo.weightGrams(100);
            dullGoo.material((byte) 21);
            dullGoo.value(10000);
            dullGoo.isTraded(true);
            //cottonBed.armourType();
            try {
                dullGoo.build();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ItemTemplateBuilder essence = new ItemTemplateBuilder("jdbEssence");
            essenceTemplateId = IdFactory.getIdFor("jdbEssence", IdType.ITEMTEMPLATE);
            essence.name("Essence", "Essences", "An essence of rarity.");
            essence.size(3);
            //cottonBed.descriptions();
            essence.itemTypes(new short[]{6, 146});
            essence.imageNumber((short) 643);
            essence.behaviourType((short) 1);
            essence.combatDamage(0);
            essence.decayTime(Long.MAX_VALUE);
            essence.dimensions(1, 1, 1);
            essence.primarySkill(-10);
            //cottonBed.bodySpaces();
            essence.modelName("model.food.salt.source.");
            essence.difficulty(20.0f);
            essence.weightGrams(10);
            essence.material((byte) 21);
            essence.value(100000);
            essence.isTraded(true);
            //cottonBed.armourType();
            try {
                essence.build();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ItemTemplateBuilder clayAlembic = new ItemTemplateBuilder("jdbClayAlembic");
            clayAlembicTemplateId = IdFactory.getIdFor("jdbClayAlembic", IdType.ITEMTEMPLATE);
            //https://en.wikipedia.org/wiki/Alembic
            clayAlembic.name("Alembic", "Alembics", "An alchemical still.");
            clayAlembic.size(3);
            //clayAlembic.descriptions();
            clayAlembic.itemTypes(new short[]{ItemTypes.ITEM_TYPE_HOLLOW, ItemTypes.ITEM_TYPE_REPAIRABLE, ItemTypes.ITEM_TYPE_UNFIRED});
            clayAlembic.imageNumber((short)662);
            clayAlembic.behaviourType((short) 1);
            clayAlembic.combatDamage(0);
            clayAlembic.decayTime(172800L);
            clayAlembic.dimensions(75, 75, 150);
            clayAlembic.primarySkill(-10);
            //clayAlembic.bodySpaces();
            clayAlembic.modelName("model.decoration.amphora.large.");
            clayAlembic.difficulty(alembicDifficulty);
            clayAlembic.weightGrams(100000);
            clayAlembic.material((byte)18);
            clayAlembic.value(10000);
            clayAlembic.isTraded(true);
            //clayAlembic.armourType();
            try {
                clayAlembic.build();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ItemTemplateBuilder potteryAlembic = new ItemTemplateBuilder("jdbPotteryAlembic");
            potteryAlembicTemplateId = IdFactory.getIdFor("jdbPotteryAlembic", IdType.ITEMTEMPLATE);
            //https://en.wikipedia.org/wiki/Alembic
            potteryAlembic.name("Alembic", "Alembics", "An alchemical still.");
            potteryAlembic.size(3);
            //potteryAlembic.descriptions();
            potteryAlembic.itemTypes(new short[]{ItemTypes.ITEM_TYPE_HOLLOW, ItemTypes.ITEM_TYPE_CONTAINER_LIQUID,
                    ItemTypes.ITEM_TYPE_USE_GROUND_ONLY, ItemTypes.ITEM_TYPE_POTTERY, ItemTypes.ITEM_TYPE_REPAIRABLE,
                    ItemTypes.ITEM_TYPE_COLORABLE, ItemTypes.ITEM_TYPE_NOMOVE, ItemTypes.ITEM_TYPE_OWNER_MOVEABLE,
                    ItemTypes.ITEM_TYPE_OWNER_TURNABLE, ItemTypes.ITEM_TYPE_DECORATION,
                    ItemTypes.ITEM_TYPE_TRANSPORTABLE, ItemTypes.ITEM_TYPE_USES_SPECIFIED_CONTAINER_VOLUME});
            potteryAlembic.imageNumber((short)682);
            potteryAlembic.behaviourType((short) 1);
            potteryAlembic.combatDamage(0);
            potteryAlembic.decayTime(12096000L);
            potteryAlembic.dimensions(119, 119, 409); // large cart container is 120, 120, 410.
            potteryAlembic.primarySkill(-10);
            //potteryAlembic.bodySpaces();
            potteryAlembic.modelName("model.decoration.amphora.large.");
            potteryAlembic.difficulty(5.0f);
            potteryAlembic.weightGrams(100000);
            potteryAlembic.material((byte)19);
            potteryAlembic.value(10000);
            potteryAlembic.isTraded(true);
            //potteryAlembic.armourType();
            ItemTemplate potteryAlembicTemplate;
            try {
                potteryAlembicTemplate = potteryAlembic.build();
                potteryAlembicTemplate.setContainerSize(206,206,206); // Largest dimension appears to be 205 for a take item.
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (craftCottonBed) {
            ItemTemplateBuilder cottonBed = new ItemTemplateBuilder("jdbCottonBed");
            cottonBedTemplateId = IdFactory.getIdFor("jdbCottonBed", IdType.ITEMTEMPLATE);
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
            madderTemplateId = IdFactory.getIdFor("jdbMadder", IdType.ITEMTEMPLATE);
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
            ItemTemplateBuilder cheeseDrill = new ItemTemplateBuilder("jdbCheeseDrill");
            cheeseDrillTemplateId = IdFactory.getIdFor("jdbCheeseDrill", IdType.ITEMTEMPLATE);
            cheeseDrill.name("cheese drill", "cheese drills", "A wooden press used to compress cheese curds and separate out whey.");
            cheeseDrill.size(3);
            //cheeseDrill.descriptions();
            cheeseDrill.itemTypes(new short[]{108, 44, 144, 38, 21, 92, 147, 51, 210});
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
            cottonToolBeltTemplateId = IdFactory.getIdFor("jdbCottonToolBelt", IdType.ITEMTEMPLATE);
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
            waxGourdTemplateId = IdFactory.getIdFor("jdbWaxGourd", IdType.ITEMTEMPLATE);
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
            waxGourd.difficulty(2.0f);
            waxGourd.weightGrams(1000);
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
            gourdCanteenTemplateId = IdFactory.getIdFor("jdbGourdCanteen", IdType.ITEMTEMPLATE);
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
            ModActions.registerAction(new CreateEssenceAction());
            cottonCreationSubstitutes();
            addPotteryTempStates();
            essenceCreationEntries();
            waxGourdReflection();
            madderReflection();
            toolInWSToBSReflection();

            JAssistClassData.voidClazz();
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
                InvocationTargetException e) {
            logger.log(Level.WARNING,e.getMessage(), e);
        }
    }

    private void printHerbEnum() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException{
        Field itemType = ReflectionUtil.getField(Class.forName("com.wurmonline.server.behaviours.Forage"), "itemType");
        int itemT;
        CharSequence waxGourd = "WAX_GOURD";
        for (Forage forage:Forage.values()){
            if (forage.name().contains(waxGourd)) {
                itemT = ReflectionUtil.getPrivateField(forage,itemType);
                logger.log(Level.INFO, String.format("%s$  %d$  %f$", forage.name(), forage.getItem(), forage.getDifficultyAt(99)));
            }
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
        final int[] redDyeFromMadderSuccesses = new int[]{0, 0};

        redDyeFromMadderSuccesses[0] = checkSaneAmountsBytecodeAlter() ? 1 : 0;
        redDyeFromMadderSuccesses[1] = getCompositeColorBytecodeAlter() ? 1 : 0;


        //Remove final modifier from itemType field. I need to reflectively set it with an ID generated in onItemTemplatesCreated.
        JAssistClassData herb_class = JAssistClassData.getClazz("Herb");
        if (herb_class == null) {
            herb_class = new JAssistClassData("com.wurmonline.server.behaviours.Herb", classPool);
        }
        CtField ctField = herb_class.getCtClass().getDeclaredField("itemType", "I");
        ctField.setModifiers(ctField.getModifiers() & ~Modifier.FINAL);


        //<editor-fold desc="Add fields to herb enum.">
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
        //</editor-fold>
        ExtendHerbEnum extendHerbEnum = new ExtendHerbEnum(classPool);
        extendHerbEnum.addExtendEntry("GSHORT_MADDER", "TILE_GRASS", "SHORT",(short) 575, madderTemplateId, (byte) 0, 1, 6, 20, 15, "NO_TREES", 10);
        extendHerbEnum.addExtendEntry("GMED_MADDER", "TILE_GRASS", "MEDIUM", (short) 575, madderTemplateId, (byte)0, 1, 10, 20, 10, "NO_TREES", 20);
        extendHerbEnum.addExtendEntry("GTALL_MADDER", "TILE_GRASS", "TALL", (short) 575, madderTemplateId, (byte) 0, 1, 20, 20, 5, "NO_TREES", 30);
        extendHerbEnum.addExtendEntry("GWILD_MADDER", "TILE_GRASS", "WILD", (short) 575, madderTemplateId, (byte) 0, 1, 40, 20, 1, "NO_TREES", 40);
        extendHerbEnum.addExtendEntry("STEPPE_MADDER", "TILE_STEPPE", "SHORT", (short) 575, madderTemplateId, (byte) 0, 10, 46, 20, 1, "NO_TREES", 50);
        extendHerbEnum.addExtendEntry("MARSH_MADDER", "TILE_MARSH", "SHORT", (short) 575, madderTemplateId, (byte) 0, 6, 26, 20, 20, "NO_TREES", 20);
        extendHerbEnum.addExtendEntry("MOSS_MADDER", "TILE_MOSS", "SHORT", (short) 575, madderTemplateId, (byte) 0, 6, 26, 20, 20, "NO_TREES", 32);
        extendHerbEnum.addExtendEntry("PEAT_MADDER", "TILE_PEAT", "SHORT", (short) 575, madderTemplateId, (byte) 0, 6, 6, 20, 20, "NO_TREES", 40);
        extendHerbEnum.addExtendEntry("TSHORT_MADDER", "TILE_TREE", "SHORT", (short) 575, madderTemplateId, (byte) 0, 6, 16, 30, 20, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("TMED_MADDER", "TILE_TREE", "MEDIUM", (short) 575, madderTemplateId, (byte) 0, 6, 16, 30, 10, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("TTALL_MADDER", "TILE_TREE", "TALL", (short) 575, madderTemplateId, (byte) 0, 6, 16, 30, 1, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("BSHORT_MADDER", "TILE_BUSH", "SHORT", (short) 575, madderTemplateId, (byte) 0, 6, 16, 20, 10, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("BMED_MADDER", "TILE_BUSH", "MEDIUM", (short) 575, madderTemplateId, (byte) 0, 6, 16, 20, 5, "NOTHING", 0);
        extendHerbEnum.addExtendEntry("BTALL_MADDER", "TILE_BUSH", "TALL", (short) 575, madderTemplateId, (byte) 0, 6, 16, 20, 1, "NOTHING", 0);

        ExtendHerbEnum.createFieldsInEnum();
        ExtendHerbEnum.initiateEnumEntries();

        evaluateChangesArray(redDyeFromMadderSuccesses, "redDyeFromMadder");
    }

    /**
     * In CreationEntry.checkSaneAmounts()
     * Byte code change: this.objectCreated != 73 to this.getObjectCreated() == 73
     * Do this because it's not possible to instrument on a field and have the replace function use a returned value
     * form a hook method.
     * Then, expression editor hook into getObjectCreated and replace returned with checkSaneAmountsExceptionsHook.
     * Using the hook instead of straight bytecode because it lets me use variable names form WU code.
     *
     * This change needs to be made because it blocks using very small things that can be combined.
     *
     * @return boolean type. Was the change successful?
     * @throws BadBytecode JA related, forwarded
     * @throws NotFoundException JA related, forwarded
     * @throws CannotCompileException JA related, forwarded
     */
    private static boolean checkSaneAmountsBytecodeAlter() throws BadBytecode, NotFoundException, CannotCompileException {
        final boolean[] toReturn = {false};
        JAssistClassData creationEntry = JAssistClassData.getClazz("CreationEntry");
        if (creationEntry == null)
            creationEntry = new JAssistClassData("com.wurmonline.server.items.CreationEntry", classPool);
        JAssistMethodData checkSaneAmounts = new JAssistMethodData(creationEntry,
                "(Lcom/wurmonline/server/items/Item;ILcom/wurmonline/server/items/Item;ILcom/wurmonline/server/items/ItemTemplate;Lcom/wurmonline/server/creatures/Creature;Z)V",
                "checkSaneAmounts");

        boolean isModifiedCheckSaneAmounts = true;
        byte[] findPoolResult;
        try {
            findConstantPoolReference(creationEntry.getConstPool(),
                    "// Method com/joedobo27/common/Common.checkSaneAmountsExceptionsHook:(III)I");
        } catch (UnsupportedOperationException e) {
            isModifiedCheckSaneAmounts = false;
        }
        if (isModifiedCheckSaneAmounts)
            toReturn[0] = true;
        if (!isModifiedCheckSaneAmounts) {
            Bytecode find = new Bytecode(creationEntry.getConstPool());
            find.addOpcode(Opcode.ALOAD_0);
            find.addOpcode(Opcode.GETFIELD);
            findPoolResult = findConstantPoolReference(creationEntry.getConstPool(), "// Field objectCreated:I");
            find.add(findPoolResult[0], findPoolResult[1]);
            find.addOpcode(Opcode.BIPUSH);
            find.add(73);

            Bytecode replace = new Bytecode(creationEntry.getConstPool());
            replace.addOpcode(Opcode.ALOAD_0);
            replace.addOpcode(Opcode.INVOKEVIRTUAL);
            findPoolResult = addConstantPoolReference(creationEntry.getConstPool(), "// Method getObjectCreated:()I");
            replace.add(findPoolResult[0], findPoolResult[1]);
            replace.addOpcode(Opcode.BIPUSH);
            replace.add(73);

            CodeReplacer codeReplacer = new CodeReplacer(checkSaneAmounts.getCodeAttribute());
            try {
                codeReplacer.replaceCode(find.get(), replace.get());
            } catch (NotFoundException e){
                toReturn[0] = false;
            }

            checkSaneAmounts.getCtMethod().instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (Objects.equals("getObjectCreated", methodCall.getMethodName())) {
                        methodCall.replace("$_ = com.joedobo27.common.Common.checkSaneAmountsExceptionsHook( $0.getObjectCreated(), sourceMax, targetMax);");
                        logger.log(Level.FINE, "CreationEntry.class, checkSaneAmounts(), installed hook at line: " + methodCall.getLineNumber());
                        toReturn[0] = true;
                    }
                }
            });
        }
        return toReturn[0];
    }

    /**
     * Change getCompositeColor() in WurmColor so madder can be used as a dye creation material.
     * was-
     *  if (itemTemplateId == 439) {...}
     *
     * becomes-
     *  boolean isRed = com.joedobo27.wurminianpacifist.WurminianPacifistMod.isRedPaintItem(itemTemplateId);
     *  if (isRed) {...}
     *
     * @return boolean type, Does itemTemplate in arg0 make red dye?
     * @throws NotFoundException JA related, forwarded.
     */
    private static boolean getCompositeColorBytecodeAlter() throws NotFoundException, BadBytecode {
        boolean toReturn;
        JAssistClassData wurmColor = JAssistClassData.getClazz("WurmColor");
        if (wurmColor == null)
            wurmColor = new JAssistClassData("com.wurmonline.server.items.WurmColor", classPool);
        JAssistMethodData getCompositeColor = new JAssistMethodData(wurmColor, "(IIIF)I", "getCompositeColor");

        byte[] bytes;
        Bytecode find = new Bytecode(wurmColor.getConstPool());
        find.addOpcode(Opcode.ILOAD_2);
        find.addOpcode(Opcode.SIPUSH);
        bytes = BytecodeTools.intToByteArray(439, 2);
        find.add(bytes[0], bytes[1]);
        find.addOpcode(Opcode.IF_ICMPNE); // 9: if_icmpne     63
        bytes = BytecodeTools.intToByteArray(54, 2);
        find.add(bytes[0], bytes[1]);
        find.addOpcode(Opcode.ILOAD_0);
        find.addOpcode(Opcode.INVOKESTATIC);
        bytes = findConstantPoolReference(wurmColor.getConstPool(),"// Method getColorRed:(I)I");
        find.add(bytes[0], bytes[1]);

        Bytecode replace = new Bytecode(wurmColor.getConstPool());
        replace.addOpcode(Opcode.ILOAD_2);
        replace.addOpcode(Opcode.INVOKESTATIC);
        bytes = addConstantPoolReference(wurmColor.getConstPool(),
                "// Method com/joedobo27/wurminianpacifist/WurminianPacifistMod.isRedPaintItem:(I)Z");
        replace.add(bytes[0], bytes[1]);
        replace.addOpcode(Opcode.IFEQ);
        bytes = BytecodeTools.intToByteArray(54, 2);
        replace.add(bytes[0], bytes[1]);
        replace.addOpcode(Opcode.ILOAD_0);
        replace.addOpcode(Opcode.INVOKESTATIC);
        bytes = findConstantPoolReference(wurmColor.getConstPool(),"// Method getColorRed:(I)I");
        replace.add(bytes[0], bytes[1]);

        CodeReplacer codeReplacer = new CodeReplacer(getCompositeColor.getCodeAttribute());
        try {
            codeReplacer.replaceCode(find.get(), replace.get());
            toReturn = true;
        } catch (NotFoundException e) {
            toReturn = false;
        }
        if (toReturn) {
            getCompositeColor.getMethodInfo().rebuildStackMapIf6(classPool, wurmColor.getClassFile());
        }
        return toReturn;
    }

    public static boolean isRedPaintItem(int itemTemplateId) {
        return itemTemplateId == ItemList.cochineal || itemTemplateId == madderTemplateId;
    }

    private static void waxGourdBytecode() throws ClassNotFoundException, NotFoundException, CannotCompileException, BadBytecode {
        if (!craftGourdCanteen && !waxGourdToFat)
            return;
        //Remove final modifier from itemType field. I need to reflectively set it with an ID generated in onItemTemplatesCreated.
        JAssistClassData forage_class = JAssistClassData.getClazz("Forage");
        if (forage_class == null) {
            forage_class = new JAssistClassData("com.wurmonline.server.behaviours.Forage", classPool);
        }
        CtField ctField = forage_class.getCtClass().getDeclaredField("itemType", "I");
        ctField.setModifiers(ctField.getModifiers() & ~Modifier.FINAL);

        // Add fields to forage enum.
        ExtendForageEnum extendForageEnum = new ExtendForageEnum(classPool);
        extendForageEnum.addExtendEntry("GSHORT_WAX_GOURD", "TILE_GRASS", "SHORT", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("GMED_WAX_GOURD", "TILE_GRASS", "MEDIUM", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("GTALL_WAX_GOURD", "TILE_GRASS", "TALL", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("GWILD_WAX_GOURD", "TILE_GRASS", "WILD", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("STEPPE_WAX_GOURD", "TILE_STEPPE", "SHORT", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("MARSH_WAX_GOURD", "TILE_MARSH", "SHORT", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("TSHORT_WAX_GOURD", "TILE_TREE", "SHORT", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("TMED_WAX_GOURD", "TILE_TREE", "MEDIUM", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("TTALL_WAX_GOURD", "TILE_TREE", "TALL", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("BSHORT_WAX_GOURD", "TILE_BUSH", "SHORT", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("BMED_WAX_GOURD", "TILE_BUSH", "MEDIUM", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);
        extendForageEnum.addExtendEntry("BTALL_WAX_GOURD", "TILE_BUSH", "TALL", (short) 570, waxGourdTemplateId, (byte) 0, 15, 15, -5, -5, "NOTHING", 0);

        ExtendForageEnum.createFieldsInEnum();
        ExtendForageEnum.initiateEnumEntries();
    }

    private static void addPotteryTempStates() {
        if (enableEssenceSystem) {
            TempStates.addState(new TempState(clayAlembicTemplateId, potteryAlembicTemplateId, (short) 10000, true, true, false));
        }
    }

    private static void essenceCreationEntries() {
        if (enableEssenceSystem) {
            CreationEntry potteryAlembic = CreationEntryCreator.createSimpleEntry(SkillList.POTTERY, ItemList.bodyHand, ItemList.clay, clayAlembicTemplateId,
                    false, true, 0.0f, false, false, CreationCategories.POTTERY);
        }
    }

    private static void cottonCreationSubstitutes() {
        if (craftCottonBed) {
            AdvancedCreationEntry bed = CreationEntryCreator.createAdvancedEntry(10044, 482, 483, cottonBedTemplateId, false, false, 0.0f, true, true, CreationCategories.FURNITURE);
            bed.addRequirement(new CreationRequirement(1, 485, 1, true));
            bed.addRequirement(new CreationRequirement(4, 486, 4, true));
            bed.addRequirement(new CreationRequirement(10, 144, 10, true));
            logger.log(Level.INFO, "Cotton bed created and a away to craft it added.");
        }
        if (craftCottonPelt) {
            CreationEntry pelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.scissors,
                    ItemList.sheet, ItemList.pelt, false, true, 0.0f, false, false, CreationCategories.TOOLS);
            pelt.setDepleteFromTarget(1500);
            logger.log(Level.INFO, "Cotton Pelt created and a away to craft it added.");
        }

        if (cheeseDrillWithCloth) {
            AdvancedCreationEntry clothCheeseDrill = CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.plank, ItemList.shaft,
                    cheeseDrillTemplateId, false, false, 0.0f, true, false, CreationCategories.TOOLS);
            clothCheeseDrill.addRequirement(new CreationRequirement(1, ItemList.plank, 4, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(2, ItemList.sprout, 2, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(3, ItemList.nailsIronSmall, 1, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(4, ItemList.clothYard, 2, true));
            logger.log(Level.INFO, "CheeseDrill from cloth squares added and a way to craft it");
        }
        if (craftCottonToolBelt) {
            CreationEntry cottonToolBelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.metalHooks, ItemList.clothYard,
                    cottonToolBeltTemplateId, true, true, 0.0f, false, false, CreationCategories.CLOTHES);
            cottonToolBelt.setDepleteFromTarget(1500);
        }
    }

    private static void waxGourdReflection() throws  ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    InvocationTargetException, NoSuchFieldException {
        if (!craftGourdCanteen && !waxGourdToFat)
            return;
        Field itemType = ReflectionUtil.getField(Class.forName("com.wurmonline.server.behaviours.Forage"), "itemType");
        CharSequence waxGourd = "WAX_GOURD";
        for (Forage forage : Forage.values()){
            if (forage.name().contains(waxGourd)) {
                ReflectionUtil.setPrivateField(forage, itemType, waxGourdTemplateId);
            }
        }

        if (waxGourdToFat) {
            CreationEntry fat = CreationEntryCreator.createSimpleEntry(SkillList.BUTCHERING, ItemList.knifeButchering, waxGourdTemplateId,
                    ItemList.tallow, false, true, 0.0f, false, false, CreationCategories.RESOURCES);
            fat.setDepleteFromTarget(1000);
        }
        if (craftGourdCanteen) {
            CreationEntry gourdCanteen = CreationEntryCreator.createSimpleEntry(SkillList.BUTCHERING, ItemList.knifeCarving, waxGourdTemplateId,
                    gourdCanteenTemplateId, false, true, 0.0f, false, false, CreationCategories.CONTAINER);
            gourdCanteen.setDepleteFromTarget(1000);
        }
    }

    private static void madderReflection() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
    InvocationTargetException, NoSuchFieldException {
        if (redDyeFromMadder) {

            Field itemType = ReflectionUtil.getField(Class.forName("com.wurmonline.server.behaviours.Herb"), "itemType");
            CharSequence madder = "MADDER";
            for (Herb herb : Herb.values()){
                if (herb.name().contains(madder)) {
                    ReflectionUtil.setPrivateField(herb, itemType, madderTemplateId);
                }
            }

            // Add red dye making recipe.
            CreationEntry redMadder = CreationEntryCreator.createSimpleEntry(SkillList.ALCHEMY_NATURAL, ItemList.water,
                    madderTemplateId, ItemList.dyeRed, true, true, 0.0f, false, false, CreationCategories.DYES);
            int[] exceptions = {madderTemplateId, ItemList.dyeRed, ItemList.dye, ItemList.lye};
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

    static int getDullGooTemplateId() {
        return dullGooTemplateId;
    }

    static float getGemQualityPer() { return gemQualityPer;}

    static int getEssenceTemplateId() { return essenceTemplateId;}

    static int getBaseActionTime() { return baseActionTime;}

    static int getPotteryAlembicTemplateId() { return potteryAlembicTemplateId;}

    private static void evaluateChangesArray(int[] ints, String option) {
        boolean changesSuccessful = Arrays.stream(ints).noneMatch(value -> value == 0);
        if (changesSuccessful) {
            logger.log(Level.INFO, option + " option changes SUCCESSFUL");
        } else {
            logger.log(Level.INFO, option + " option changes FAILURE");
            logger.log(Level.FINE, Arrays.toString(ints));
        }
    }
}
