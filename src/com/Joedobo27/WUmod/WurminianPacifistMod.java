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


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class WurminianPacifistMod implements WurmMod, Initable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {

    private Field EMPTY_FIELD;
    private Logger logger = Logger.getLogger(WurminianPacifistMod.class.getName());
    private Class forageDataClazz;

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
    private int towelItemID;
    private boolean craftCottonBed = false;
    private int cottonBedID;
    private boolean redDyeFromMadder = false;
    private int madderID;
    private boolean cheeseDrillWithCloth = false;
    private int nettleTeaID;
    private int cheeseDrillID;
    private boolean craftGourdCanteen = false;
    private boolean gourdWax = false;
    private int waxGourdID;
    private boolean craftCottonToolBelt = false;
    private int cottonToolbeltID;
    //</editor-fold>

    //<editor-fold desc="Bytecode objects">
    private ClassPool pool;
    private CtClass ctcSelf;
    private CtClass ctcWurmColor;
    private ClassFile cfWurmColor;
    private ConstPool cpWurmColor;
    private CtClass ctcForage;
    private CtClass ctcForageJDB;
    private CtClass ctcForageData;
    private CtClass ctcForageDataJDB;
    private CtClass ctcServer;
    //</editor-fold>

    //<editor-fold desc="Javassist objects">
    private CodeAttribute getCompositeColorAttribute;
    private CodeIterator getCompositeColorIterator;
    private MethodInfo getCompositeColorMInfo;
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
        gourdWax = Boolean.valueOf(properties.getProperty("gourdWax", Boolean.toString(gourdWax)));
        craftCottonToolBelt = Boolean.valueOf(properties.getProperty("craftCottonToolBelt", Boolean.toString(craftCottonToolBelt)));
    }

    @Override
    public void onItemTemplatesCreated() {

        if (craftCottonPelt) {
            ItemTemplateBuilder towel = new ItemTemplateBuilder("jdbTowel");
            setTowelItemID(IdFactory.getIdFor("jdbTowel", IdType.ITEMTEMPLATE));
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
            setCottonBedID(IdFactory.getIdFor("jdbCottonBed", IdType.ITEMTEMPLATE));
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
            // for now, using lovage's graphic and woad's details.
            ItemTemplateBuilder madder = new ItemTemplateBuilder("jdbMadder");
            setMadderID(IdFactory.getIdFor("jdbMadder", IdType.ITEMTEMPLATE));
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
            setNettleTeaID(IdFactory.getIdFor("jdbNettleTea",IdType.ITEMTEMPLATE));
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
            setCheeseDrillID(IdFactory.getIdFor("jdbCheeseDrill", IdType.ITEMTEMPLATE));
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
            setCottonToolbeltID(IdFactory.getIdFor("jdbCottonToolBelt", IdType.ITEMTEMPLATE));
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
        if (craftGourdCanteen || gourdWax){
            ItemTemplateBuilder waxGourd = new ItemTemplateBuilder("jdbWaxGourd");
            setWaxGourdID(IdFactory.getIdFor("jdbWaxGourd", IdType.ITEMTEMPLATE));
            waxGourd.name("Wax gourd", "Wax gourds", "A hard shelled gourd with a narrow top and ball shaped bottom. Wax appears to be leaching out around it's stem.");
            waxGourd.size(3);
            //waxGourd.descriptions();
            waxGourd.itemTypes(new short[] { 146, 102, 129 });
            waxGourd.imageNumber((short) 501);
            waxGourd.behaviourType((short) 16);
            waxGourd.combatDamage(0);
            waxGourd.decayTime(28800L);
            waxGourd.dimensions(10, 10, 10);
            waxGourd.primarySkill(-10);
            //waxGourd.bodySpaces();
            waxGourd.modelName("model.food.pumpkin.");
            waxGourd.difficulty(200.0f);
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
        }
    }

    @Override
    public void onServerStarted() {
        if (craftCottonBed) {
            AdvancedCreationEntry bed = CreationEntryCreator.createAdvancedEntry(10044, 482, 483, getCottonBedID(), false, false, 0.0f, true, true, CreationCategories.FURNITURE);
                bed.addRequirement(new CreationRequirement(1, 485, 1, true));
                bed.addRequirement(new CreationRequirement(4, 486, 4, true));
                bed.addRequirement(new CreationRequirement(10, 144, 10, true));
            logger.log(Level.INFO, "Cotton bed created and a away to craft it added.");
        }
        if (craftCottonPelt) {
            CreationEntry towel = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.clothYard,
                    ItemList.clothString,getTowelItemID(),true,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            towel.setDepleteFromSource(squareGramsInTowel);
            towel.setDepleteFromTarget(stringGramsInTowel);
            CreationEntry pelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.scissors,
                    getTowelItemID(), ItemList.pelt, false, true, 0.0f, false, false, CreationCategories.TOOLS);
            pelt.setDepleteFromTarget(towelGrams);
            logger.log(Level.INFO, "Cotton Pelt created and a away to craft it added.");
        }

        if (cheeseDrillWithCloth) {
            AdvancedCreationEntry clothCheeseDrill = CreationEntryCreator.createAdvancedEntry(SkillList.CARPENTRY_FINE, ItemList.plank, ItemList.shaft,
                    getCheeseDrillID(), false, false, 0.0f, true, false, CreationCategories.TOOLS);
            clothCheeseDrill.addRequirement(new CreationRequirement(1, ItemList.plank, 4, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(2, ItemList.sprout, 2, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(3, ItemList.nailsIronSmall, 1, true));
            clothCheeseDrill.addRequirement(new CreationRequirement(4, ItemList.clothYard, 2, true));
            logger.log(Level.INFO, "CheeseDrill from cloth squares added and a way to craft it");
        }
        if (craftCottonToolBelt) {
            CreationEntry cottonToolBelt = CreationEntryCreator.createSimpleEntry(SkillList.CLOTHTAILORING, ItemList.metalHooks, ItemList.clothYard,
                    getCottonToolbeltID(), true, true, 0.0f, false, false, CreationCategories.CLOTHES);
            cottonToolBelt.setDepleteFromTarget(1500);
        }
        if (redDyeFromMadder || craftGourdCanteen) {
            try {
                ArrayList entries = ReflectionUtil.getPrivateField(forageDataClazz, ReflectionUtil.getField(forageDataClazz, "entries"));
                Constructor forageDataIni = forageDataClazz.getConstructor(String.class, Integer.TYPE, Byte.TYPE, GrassData.GrowthStage.class,
                        Short.TYPE, Integer.TYPE, Byte.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ModifiedBy.class,
                        Integer.TYPE);
                entries.add(forageDataIni.newInstance("GSHORT_WAX_GOURD", 1000, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.SHORT, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("GMED_WAX_GOURD", 1001, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.MEDIUM, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("GTALL_WAX_GOURD", 1002, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.TALL, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("GWILD_WAX_GOURD", 1003, Tiles.Tile.TILE_GRASS.id, GrassData.GrowthStage.WILD, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("STEPPE_WAX_GOURD", 1004, Tiles.Tile.TILE_STEPPE.id, GrassData.GrowthStage.SHORT, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("MARSH_WAX_GOURD", 1005, Tiles.Tile.TILE_MARSH.id, GrassData.GrowthStage.SHORT, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("TSHORT_WAX_GOURD", 1006, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.SHORT, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("TMED_WAX_GOURD", 1007, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.MEDIUM, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("TTALL_WAX_GOURD", 1008, Tiles.Tile.TILE_TREE.id, GrassData.GrowthStage.TALL, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("BSHORT_WAX_GOURD", 1009, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.SHORT, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("BMED_WAX_GOURD", 1010, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.MEDIUM, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));
                entries.add(forageDataIni.newInstance("BTALL_WAX_GOURD", 1011, Tiles.Tile.TILE_BUSH.id, GrassData.GrowthStage.TALL, (short) 570, getWaxGourdID(), (byte) 0, 15, 15, -5, -5, ModifiedBy.NOTHING, 0));


            } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }


            if (redDyeFromMadder) {
                CreationEntry redMadder = CreationEntryCreator.createSimpleEntry(SkillList.ALCHEMY_NATURAL, ItemList.water,
                        getMadderID(), ItemList.dyeRed, true, true, 0.0f, false, false, CreationCategories.DYES);

            }
        }

        if (toolInWSToBS){
            ArrayList<Integer> created = new ArrayList<>(Arrays.asList(ItemList.knifeButchering, ItemList.knifeBladeButchering,
                    ItemList.knifeCarving, ItemList.knifeBladeCarving, ItemList.sickle, ItemList.sickleBlade,
                    ItemList.scythe, ItemList.scytheBlade));
            ArrayList<Integer> targets = new ArrayList<>(Arrays.asList(ItemList.knifeBladeButchering, ItemList.knifeBladeCarving, ItemList.scytheBlade,
                    ItemList.sickleBlade, ItemList.ironBar, ItemList.steelBar, ItemList.adamantineBar, ItemList.glimmerSteelBar, ItemList.seryllBar));

            Map<Integer, List<CreationEntry>> simpleEntries = new HashMap<>();
            try {
                simpleEntries = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "simpleEntries"));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            Map<Integer, List<CreationEntry>> matrix = new HashMap<>();
            try {
                matrix = ReflectionUtil.getPrivateField(CreationMatrix.class, ReflectionUtil.getField(CreationMatrix.class, "matrix"));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

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

            // Remove all matrix entries for the combination of targets and created. Only those that match can be removed.
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

            CreationEntry knifeButchering = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.woodenHandleSword,
                    ItemList.knifeBladeButchering,ItemList.knifeButchering,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry knifeBladeButcheringIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.ironBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.steelBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.adamantineBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.glimmerSteelBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeButcheringSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.seryllBar,ItemList.knifeBladeButchering,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);

            CreationEntry KnifeCarving = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.woodenHandleSword,
                    ItemList.knifeBladeCarving,ItemList.knifeCarving,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry knifeBladeCarvingIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.ironBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.steelBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.adamantineBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.glimmerSteelBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry knifeBladeCarvingSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilSmall,
                    ItemList.seryllBar,ItemList.knifeBladeCarving,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);

            CreationEntry scythe = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.shaft,
                    ItemList.scytheBlade,ItemList.scythe,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry scytheBladeIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.ironBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scytheBladeSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.steelBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scythebladeAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.adamantineBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scythebladeGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.glimmerSteelBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry scytheBladeSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.seryllBar,ItemList.scytheBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);

            CreationEntry sickle = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.woodenHandleSword,
                    ItemList.sickleBlade,ItemList.sickle,true,true,0.0f,false,false,CreationCategories.TOOLS);
            CreationEntry sickleBladeIron = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.ironBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeSteel = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.steelBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeAdam = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.adamantineBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeGlim = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.glimmerSteelBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);
            CreationEntry sickleBladeSery = CreationEntryCreator.createSimpleEntry(SkillList.SMITHING_BLACKSMITHING,ItemList.anvilLarge,
                    ItemList.seryllBar,ItemList.sickleBlade,false,true,0.0f,false,false,CreationCategories.TOOL_PARTS);

            logger.log(Level.INFO, "Tools crafted with WS switch over to BS");
        }
    }

    @Override
    public void init() {
        String replaceByteResult;
        String printByteResult;
        jaseBT jbt;
        jaseBT jbt1;

        pool = HookManager.getInstance().getClassPool();
        setJSSelf();
        setJSWurmColor();
        setJSForage();
        setJSServer();

        if (redDyeFromMadder || craftGourdCanteen || gourdWax) {
            // Testing tool to make it so a tile can always be foraged or botanized.
            setJSAlwaysForage();
            // Convert WU's Forage.class from Enum to ArrayList of Forage-objects.
            setJSForageOverwrite();
            if (redDyeFromMadder) {
                setGetCompositeColor(cfWurmColor, "(IIIF)I", "getCompositeColor");
                try {
                    getGetCompositeColorIterator().insertGap(9, 7);
                } catch (BadBytecode badBytecode) {
                    badBytecode.printStackTrace();
                }
                jbt = new jaseBT();
                //<editor-fold desc="Change information.">
                // insert 7 wide gap at line 9.
                // this--if (itemTemplateId == 439) {
                // becomes-- if (itemTemplateId == 439 || itemTemplateID == ??) {
                // Where ?? is a value picked for Madder and inserted with with ConstantPool.addIntegerInfo()
                //</editor-fold>
                jbt.setOpCodeStructure(new ArrayList<>(Arrays.asList(Opcode.IF_ICMPEQ, Opcode.ILOAD_2, Opcode.LDC_W)));
                jbt.setOperandStructure(new ArrayList<>(Arrays.asList("000a", "",
                        String.format("%04X", cpWurmColor.addIntegerInfo(getMadderID())))));
                jbt.setOpcodeOperand();
                replaceByteResult = jaseBT.byteCodeFindReplace("00,00,00,00,00,00,00", "00,00,00,00,00,00,00", jbt.getOpcodeOperand(), getGetCompositeColorIterator(),
                        "getCompositeColor");
                try {
                    getGetCompositeColorMInfo().rebuildStackMapIf6(pool, cfWurmColor);
                } catch (BadBytecode badBytecode) {
                    badBytecode.printStackTrace();
                }
                logger.log(Level.INFO, replaceByteResult);
                jaseBT.byteCodePrint(getGetCompositeColorIterator(), "getCompositeColor",
                        "C:\\Program Files (x86)\\Steam\\SteamApps\\common\\Wurm Unlimited Dedicated Server\\byte code prints");
            }
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
    }
    //<editor-fold desc="Javassist and bytecode altering section.">
    private void setJSSelf() {
    try {
        this.ctcSelf = pool.get(this.getClass().getName());
    } catch (NotFoundException e) {
        e.printStackTrace();
    }
}

    private void setJSWurmColor() {
        try {
            this.ctcWurmColor = pool.get("com.wurmonline.server.items.WurmColor");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        this.cfWurmColor = ctcWurmColor.getClassFile();
        this.cpWurmColor = cfWurmColor.getConstPool();
    }

    private void setJSForage() {
        try {
            ctcForage = pool.get("com.wurmonline.server.behaviours.Forage");
            ctcForageJDB = pool.get("com.Joedobo27.WUmod.ForageJDB");
            ctcForageDataJDB = pool.get("com.Joedobo27.WUmod.ForageDataJDB");

        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setJSServer() {
        try {
            ctcServer = pool.get("com.wurmonline.server.Server");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setJSAlwaysForage(){
        try {
            CtMethod ctmIsForagable = ctcServer.getMethod("isForagable", "(II)Z");
            ctmIsForagable.setBody("{ return true; }");
            CtMethod ctmIsBotanizable = ctcServer.getMethod("isBotanizable", "(II)Z");
            ctmIsBotanizable.setBody("{ return true; }");
        } catch (NotFoundException | CannotCompileException e){
            e.printStackTrace();
        }

    }

    private void setJSForageOverwrite(){
        try {
            ctcForageData = pool.getAndRename("com.Joedobo27.WUmod.ForageDataJDB", "com.wurmonline.server.behaviours.ForageData");
            ClassMap cmForageData = new ClassMap();
            cmForageData.put("com.Joedobo27.WUmod.ForageDataJDB", "com.wurmonline.server.behaviours.ForageData");
            ctcForageData.replaceClassName(cmForageData);
            ctcForageData.rebuildClassFile();


            ClassMap cmForage = new ClassMap();
            cmForage.put("com.Joedobo27.WUmod.ForageJDB", "com.wurmonline.server.behaviours.Forage");
            CtClass ctcForageNew = pool.getAndRename("com.Joedobo27.WUmod.ForageJDB", "com.wurmonline.server.behaviours.Forage");
            ctcForageNew.replaceClassName(cmForage);
            ctcForageNew.replaceClassName(cmForageData);
            ctcForageNew.rebuildClassFile();

            ctcForageData.writeFile();
            ctcForageNew.writeFile();
            forageDataClazz = ctcForageData.toClass();


        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
        }
    }

    //</editor-fold>
    private void setWaxGourdID(int waxGourdID) {
        this.waxGourdID = waxGourdID;
    }

    private int getWaxGourdID() {
        return waxGourdID;
    }

    private void setTowelItemID(int towelItemID) {
        this.towelItemID = towelItemID;
    }

    private int getTowelItemID() {
        return towelItemID;
    }

    private void setCottonBedID(int cottonBedID) {
        this.cottonBedID = cottonBedID;
    }

    private int getCottonBedID() {
        return cottonBedID;
    }

    private void setMadderID(int madderID) {
        this.madderID = madderID;
    }

    private int getMadderID() {
        return madderID;
    }

    private void setCheeseDrillID(int cheeseDrillID) {
        this.cheeseDrillID = cheeseDrillID;
    }

    private int getCheeseDrillID() {
        return cheeseDrillID;
    }

    private void setNettleTeaID(int nettleTeaID) {
        this.nettleTeaID = nettleTeaID;
    }

    private int getNettleTeaID() {
        return nettleTeaID;
    }

    private void setCottonToolbeltID(int cottonToolbeltID) {
        this.cottonToolbeltID = cottonToolbeltID;
    }

    private int getCottonToolbeltID() {
        return cottonToolbeltID;
    }

    private void setGetCompositeColor(ClassFile cf, String desc, String name){
        if (this.getCompositeColorMInfo == null || this.getCompositeColorIterator == null || this.getCompositeColorAttribute == null){
            for (List a : new List[]{cf.getMethods()}){
                for (Object b : a){
                    MethodInfo MInfo = (MethodInfo) b;
                    if (Objects.equals(MInfo.getDescriptor(), desc) && Objects.equals(MInfo.getName(), name)){
                        this.getCompositeColorMInfo = MInfo;
                        break;
                    }
                }
            }
            if (this.getCompositeColorMInfo == null){
                throw new NullPointerException();
            }
            this.getCompositeColorAttribute = this.getCompositeColorMInfo.getCodeAttribute();
            this.getCompositeColorIterator = this.getCompositeColorAttribute.iterator();
        }
    }

    private CodeIterator getGetCompositeColorIterator() {
        return getCompositeColorIterator;
    }

    private CodeAttribute getGetCompositeColorAttribute() {
        return getCompositeColorAttribute;
    }

    private MethodInfo getGetCompositeColorMInfo() {
        return getCompositeColorMInfo;
    }
}
