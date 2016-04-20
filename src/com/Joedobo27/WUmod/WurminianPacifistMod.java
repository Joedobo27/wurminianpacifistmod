package com.Joedobo27.WUmod;

import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.ModifiedBy;
import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import javassist.*;
import javassist.bytecode.*;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WurminianPacifistMod implements WurmMod, Initable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {

    private static Logger logger = Logger.getLogger(WurminianPacifistMod.class.getName());

    //<editor-fold desc="Configure controls.">
    private boolean craftCottonPelt = false;
    private int towelX = 5;
    private int towelY = 10;
    private int towelZ = 10;
    private int towelGrams = 2000;
    private float towelDifficulty = 20.0f;
    private int towelValue = 30000;
    private boolean toolInWSToBS = false;
    private int squareGramsInTowel = 1200;
    private int stringGramsInTowel = 800;
    private boolean craftCottonBed = false;
    private boolean redDyeFromMadder = false;
    private boolean cheeseDrillWithCloth = false;
    private boolean craftGourdCanteen = false;
    private boolean craftGourdWax = false;
    private boolean craftCottonToolBelt = false;
    //</editor-fold>

    //<editor-fold desc="IDs for new items.">
    private static int towelItemID;
    private static int cottonBedID;
    private static int madderID;
    private static int nettleTeaID;
    private static int cheeseDrillID;
    private static int waxGourdID;
    private static int gourdCanteenID;
    private static int waxID;
    private static int cottonToolbeltID;
    //</editor-fold>

    //<editor-fold desc="Javassist objects">
    private static ClassPool pool;
    private static CtClass ctcSelf;
    private static CtClass ctcWurmColor;
    private static ClassFile cfWurmColor;
    private static ConstPool cpWurmColor;
    private static CtClass ctcServer;
    private static CodeAttribute getCompositeColorAttribute;
    private static CodeIterator getCompositeColorIterator;
    private static MethodInfo getCompositeColorMInfo;
    //</editor-fold>

    @Override
    public void configure(Properties properties) {
        craftCottonPelt = Boolean.valueOf(properties.getProperty("craftCottonPelt", Boolean.toString(craftCottonPelt)));
        towelX = Integer.valueOf(properties.getProperty("towelX", Integer.toString(towelX)));
        towelY = Integer.valueOf(properties.getProperty("towelY", Integer.toString(towelY)));
        towelZ = Integer.valueOf(properties.getProperty("towelZ", Integer.toString(towelZ)));
        towelGrams = Integer.valueOf(properties.getProperty("towelGrams", Integer.toString(towelGrams)));
        towelDifficulty = Float.valueOf(properties.getProperty("towelDifficulty", Float.toString(towelDifficulty)));
        towelValue = Integer.valueOf(properties.getProperty("towelValue", Integer.toString(towelValue)));
        squareGramsInTowel = Integer.valueOf(properties.getProperty("squareGramsInTowel", Integer.toString(squareGramsInTowel)));
        stringGramsInTowel = Integer.valueOf(properties.getProperty("stringGramsInTowel", Integer.toString(stringGramsInTowel)));
        toolInWSToBS = Boolean.valueOf(properties.getProperty("toolInWSToBS", Boolean.toString(toolInWSToBS)));
        craftCottonBed = Boolean.valueOf(properties.getProperty("craftCottonBed", Boolean.toString(craftCottonBed)));
        redDyeFromMadder = Boolean.valueOf(properties.getProperty("redDyeFromMadder", Boolean.toString(redDyeFromMadder)));
        cheeseDrillWithCloth = Boolean.valueOf(properties.getProperty("cheeseDrillWithCloth", Boolean.toString(cheeseDrillWithCloth)));
        craftGourdCanteen = Boolean.valueOf(properties.getProperty("craftGourdCanteen", Boolean.toString(craftGourdCanteen)));
        craftGourdWax = Boolean.valueOf(properties.getProperty("craftGourdWax", Boolean.toString(craftGourdWax)));
        craftCottonToolBelt = Boolean.valueOf(properties.getProperty("craftCottonToolBelt", Boolean.toString(craftCottonToolBelt)));
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
        if (craftGourdCanteen || craftGourdWax){
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
            if (craftGourdCanteen) {
                addWaxGourdForageReflection();
                CreationEntry gourdCanteen = CreationEntryCreator.createSimpleEntry(SkillList.BUTCHERING, ItemList.knifeCarving, waxGourdID,
                        gourdCanteenID, false, true, 0.0f, false, false, CreationCategories.CONTAINER);
            }
            if (redDyeFromMadder) {
                addMadderHerbReflection();
                CreationEntry redMadder = CreationEntryCreator.createSimpleEntry(SkillList.ALCHEMY_NATURAL, ItemList.water,
                        madderID, ItemList.dyeRed, true, true, 0.0f, false, false, CreationCategories.DYES);
                Field a[] = CreationEntry.class.getDeclaredFields();
                logger.log(Level.INFO, Arrays.toString(a));
                if (aaaJoeCommon.modifiedCheckSaneAmounts) {
                    ArrayList<Integer> abc = ReflectionUtil.getPrivateField(CreationEntry.class, ReflectionUtil.getField(CreationEntry.class,
                            "largeMaterialRatioDifferentials"));
                    abc.add(ItemList.dyeRed);
                    logger.log(Level.INFO, abc.toString());
                }
                /*
                ArrayList<Integer> largeMaterialRatioDifferentials = ReflectionUtil.getPrivateField(CreationEntry.class,
                        ReflectionUtil.getField(CreationEntry.class, "largeMaterialRatioDifferentials"));
                if (!largeMaterialRatioDifferentials.contains(ItemList.dyeRed))
                    largeMaterialRatioDifferentials.add(ItemList.dyeRed);
                */
            }
            if (toolInWSToBS) {
                ArrayList<Integer> created = new ArrayList<>(Arrays.asList(ItemList.knifeButchering, ItemList.knifeBladeButchering,
                        ItemList.knifeCarving, ItemList.knifeBladeCarving, ItemList.sickle, ItemList.sickleBlade,
                        ItemList.scythe, ItemList.scytheBlade));
                ArrayList<Integer> targets = new ArrayList<>(Arrays.asList(ItemList.knifeBladeButchering, ItemList.knifeBladeCarving, ItemList.scytheBlade,
                        ItemList.sickleBlade, ItemList.ironBar, ItemList.steelBar, ItemList.adamantineBar, ItemList.glimmerSteelBar, ItemList.seryllBar));
                Map<Integer, List<CreationEntry>> simpleEntries = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "simpleEntries"));
                Map<Integer, List<CreationEntry>> matrix = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "matrix"));

                //remove all simpleEntries matching the created items.
                HashMap<Integer, List<CreationEntry>> toDeleteSimple = new HashMap<>();
                //noinspection Convert2streamapi
                for (HashMap.Entry<Integer, List<CreationEntry>> simpleEntries1 : simpleEntries.entrySet()) {
                    if (created.contains(simpleEntries1.getKey())) {
                        toDeleteSimple.put(simpleEntries1.getKey(), simpleEntries1.getValue());
                    }
                }
                for (HashMap.Entry<Integer, List<CreationEntry>> delete : toDeleteSimple.entrySet()) {
                    simpleEntries.remove(delete.getKey(), delete.getValue());
                }

                // Remove all matrix forageEntries for the combination of targets and created. Only those that match can be removed.
                // For example, many items have ironBar as as a target but we must only remove those that also match created.
                HashMap<Integer, List<CreationEntry>> toDeleteMatrix = new HashMap<>();
                List<CreationEntry> entries;
                for (HashMap.Entry<Integer, List<CreationEntry>> matrix1 : matrix.entrySet()) {
                    if (targets.contains(matrix1.getKey())) {
                        if (!toDeleteMatrix.containsKey(matrix1.getKey())) {
                            entries = new ArrayList<>();
                            toDeleteMatrix.put(matrix1.getKey(), entries);
                        }
                        //noinspection Convert2streamapi
                        for (CreationEntry entry : matrix1.getValue()) {
                            if (created.contains(entry.getObjectCreated())) {
                                toDeleteMatrix.get(matrix1.getKey()).add(entry);
                            }
                        }
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
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {
            String replaceByteResult;
            String printByteResult;
            JDBByteCode jbt;
            JDBByteCode jbt1;

            pool = HookManager.getInstance().getClassPool();
            setJSSelf();

            // Testing tool to make it so a tile can always be foraged or botanized.
            setJSServer();
            jsAlwaysForage();

            if (redDyeFromMadder) {
                setJSWurmColor();
                jsAlterGetCompositeColor();
                if (!aaaJoeCommon.overwroteHerb)
                    aaaJoeCommon.jsHerbOverwrite();
                // change CheckSaneAmounts of CreationEntry so red dye making with madder dosen't give "not enough material" messages.
                if (!aaaJoeCommon.modifiedCheckSaneAmounts) {
                    aaaJoeCommon.jsCheckSaneAmountsExclusions();
                }
            }
            if (craftGourdCanteen || craftGourdWax) {
                if (!aaaJoeCommon.overwroteForage)
                    aaaJoeCommon.jsForageOverwrite();

            }
            if (cheeseDrillWithCloth) {
            /*
            Need to insert into SimpleCreationEntry#Run() and inside the "if (counter == 1.0f) {" section near the other heat checks.
            water (source) must be hot
            if (realSource.getTemplateId() == ItemList.water && realTarget.getTemplateId() == getNettleTeaID() &&
            realSource.getTemperature() < 2000){
                performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " must be hot to do this.");
                throw new NoSuchItemException("Too low temperature.");
            }
            */
            }
        } catch (NotFoundException | CannotCompileException | FileNotFoundException | BadBytecode e) {
            e.printStackTrace();
        }
    }

    //<editor-fold desc="Javassist and bytecode altering section.">
    private void setJSSelf() throws NotFoundException {
        ctcSelf = pool.get(this.getClass().getName());
    }

    private void setJSWurmColor() throws NotFoundException {
        ctcWurmColor = pool.get("com.wurmonline.server.items.WurmColor");
        cfWurmColor = ctcWurmColor.getClassFile();
        cpWurmColor = cfWurmColor.getConstPool();
    }

    private void setJSServer() throws NotFoundException {
        ctcServer = pool.get("com.wurmonline.server.Server");
    }

    private void jsAlwaysForage() throws NotFoundException, CannotCompileException {
        CtMethod ctmIsForagable = ctcServer.getMethod("isForagable", "(II)Z");
        ctmIsForagable.setBody("{ return true; }");
        CtMethod ctmIsBotanizable = ctcServer.getMethod("isBotanizable", "(II)Z");
        ctmIsBotanizable.setBody("{ return true; }");
    }

    private static void jsAlterGetCompositeColor() throws BadBytecode, NotFoundException, FileNotFoundException {
        JDBByteCode jbt;
        String replaceByteResult;
        setGetCompositeColor(cfWurmColor, "(IIIF)I", "getCompositeColor");
        getCompositeColorIterator.insertGap(9, 7);

        jbt = new JDBByteCode();
        //<editor-fold desc="Change information.">
        // insert 7 wide gap at line 9.
        // this--if (itemTemplateId == 439) {
        // becomes-- if (itemTemplateId == 439 || itemTemplateID == ??) {
        // Where ?? is a value picked for Madder and inserted with with ConstantPool.addIntegerInfo()
        //</editor-fold>
        jbt.setOpCodeStructure(new ArrayList<>(Arrays.asList(Opcode.IF_ICMPEQ, Opcode.ILOAD_2, Opcode.LDC_W)));
        jbt.setOperandStructure(new ArrayList<>(Arrays.asList("000a", "",
                String.format("%04X", cpWurmColor.addIntegerInfo(madderID)))));
        jbt.setOpcodeOperand();
        replaceByteResult = JDBByteCode.byteCodeFindReplace("00,00,00,00,00,00,00", "00,00,00,00,00,00,00", jbt.getOpcodeOperand(), getCompositeColorIterator,
                "getCompositeColor");

        getCompositeColorMInfo.rebuildStackMapIf6(pool, cfWurmColor);
        logger.log(Level.INFO, replaceByteResult);
        JDBByteCode.byteCodePrint(getCompositeColorIterator, "getCompositeColor",
                "C:\\Program Files (x86)\\Steam\\SteamApps\\common\\Wurm Unlimited Dedicated Server\\byte code prints");
    }

    private static void setGetCompositeColor(ClassFile cf, String desc, String name){
        if (getCompositeColorMInfo == null || getCompositeColorIterator == null || getCompositeColorAttribute == null){
            for (List a : new List[]{cf.getMethods()}){
                for (Object b : a){
                    MethodInfo MInfo = (MethodInfo) b;
                    if (Objects.equals(MInfo.getDescriptor(), desc) && Objects.equals(MInfo.getName(), name)){
                        getCompositeColorMInfo = MInfo;
                        break;
                    }
                }
            }
            if (getCompositeColorMInfo == null){
                throw new NullPointerException();
            }
            getCompositeColorAttribute = getCompositeColorMInfo.getCodeAttribute();
            getCompositeColorIterator = getCompositeColorAttribute.iterator();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Reflection methods section.">
    @SuppressWarnings("unchecked")
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
    //</editor-fold>
}
